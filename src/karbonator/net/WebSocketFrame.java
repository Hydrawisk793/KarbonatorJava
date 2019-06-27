package karbonator.net;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import karbonator.BitUtils;

/**
 * A web socket frame that implements web socket data framing described in RFC 6455.
 * 
 * @author Hydrawisk793
 * @since 2017-03-15
 */
public class WebSocketFrame {
    /**
     * The maximum available payload length in this implemention.
     */
    public static final long MAXIMUM_SUPPORTED_PAYLOAD_LENGTH = 0x7FFFFFFFL;
    
    /**
     * An enum of possible web socket frame opcode values.
     */
    public enum Opcode {
        CONTINUATION(0, false),
        TEXT(1, false),
        BINARY(2, false),
        RESERVED_03(3, false),
        RESERVED_04(4, false),
        RESERVED_05(5, false),
        RESERVED_06(6, false),
        RESERVED_07(7, false),
        CLOSE_CONNECTION(8, true),
        PING(9, true),
        PONG(10, true),
        RESERVED_CONTROLL_0B(11, true),
        RESERVED_CONTROLL_0C(12, true),
        RESERVED_CONTROLL_0D(13, true),
        RESERVED_CONTROLL_0E(14, true),
        RESERVED_CONTROLL_0F(15, true);
        
        private Opcode(int opcode, boolean control) {
            opcode_ = opcode;
            control_ = control;
        }
        
        public int getOpcode() {
            return opcode_;
        }
        
        public boolean isControl() {
            return control_;
        }
        
        private int opcode_;
        
        private boolean control_;
    }
    
    /**
     * A web socket frame reader that can be used in asynchronous buffering environment.
     * 
     * @author Hydrawisk793
     * @since 2017-09-28
     */
    public static class AsynchronousFrameReader {
        public AsynchronousFrameReader() {
            this(1024 * 8);
        }
        
        public AsynchronousFrameReader(int bufferSize) {
            byteBuffer_ = ByteBuffer.allocate(bufferSize);
            frame_ = new WebSocketFrame();
            state_ = 0;
        }
        
        /**
         * Enqueues bytes.
         * 
         * @param bytes
         * @param offset
         * @param byteCount
         */
        public void consume(byte[] bytes) {
            byteBuffer_.put(bytes, 0, bytes.length);
        }
        
        /**
         * Enqueues bytes.
         * 
         * @param bytes
         * @param offset
         * @param byteCount
         */
        public void consume(byte[] bytes, int offset, int byteCount) {
            byteBuffer_.put(bytes, offset, byteCount);
        }
        
        /**
         * Reads bytes from the specified channel and enqueues them.
         * 
         * @param chan
         */
        public void consume(ReadableByteChannel chan) throws IOException {
            while(chan.read(byteBuffer_) > 0);
        }
        
        /**
         * Dequeues bytes from the buffer and create web socket frames as many as possible.
         * 
         * @return A web socket frame list. the list may not contain any web socket frames.
         */
        public List<WebSocketFrame> createWebSocketFrames() {
            List<WebSocketFrame> frames = new LinkedList<>();
            
            byteBuffer_.flip();
            for(boolean loop = true; loop; ) {
                switch(state_) {
                case 0:
                    if(byteBuffer_.remaining() >= 1) {
                        byte firstByte = byteBuffer_.get();
                        frame_.finalFragment_ = (firstByte & 0x80) != 0;
                        frame_.reservedBits_ = (firstByte & 0x70) >>> 4;
                        if(frame_.reservedBits_ != 0) {
                            throw new RuntimeException("The reserved bits of the first byte must be zero.");
                        }
                        frame_.opcode_ = Opcode.values()[(firstByte & 0x0F)];
                        
                        state_ = 1;
                    }
                    else {
                        loop = false;
                    }
                break;
                case 1:
                    if(byteBuffer_.remaining() >= 1) {
                        byte secondByte = byteBuffer_.get();
                        frame_.mask_ = (secondByte & 0x80) != 0;
                        payloadLength_ = secondByte & 0x7F;
                        
                        extendedLengthByteCount_ = 0;
                        if(payloadLength_ == 126){
                            extendedLengthByteCount_ = 2;
                        }
                        else if(payloadLength_ == 127) {
                            extendedLengthByteCount_ = 8;
                        }
                        
                        state_ = (extendedLengthByteCount_ > 0 ? 2 : 3);
                    }
                    else {
                        loop = false;
                    }
                break;
                case 2:
                    if(byteBuffer_.remaining() >= extendedLengthByteCount_) {
                        payloadLength_ = 0;
                        for(int i = extendedLengthByteCount_; i > 0;) {
                            --i;
                            payloadLength_ <<= 8;
                            payloadLength_ |= (byteBuffer_.get() & 0xFF);
                        }
                    }
                    else {
                        loop = false;
                    }
                break;
                case 3:
                    if(payloadLength_ < 0) {
                        throw new RuntimeException("The payload length cannot exceed 2^63.");
                    }
                    if(payloadLength_ >= 0x80000000L) {
                        throw new RuntimeException("Payload lengths that exceeds 2^31 is not supported yet...");
                    }
                    intPayloadLength_ = (int)payloadLength_;
                    
                    frame_.maskingKey_ = 0xFFFFFFFF;
                    
                    state_ = (frame_.mask_ ? 4 : 5);
                break;
                case 4:
                    if(byteBuffer_.remaining() >= 4) {
                        frame_.maskingKey_ = byteBuffer_.getInt();
                        state_ = 5;
                    }
                    else {
                        loop = false;
                    }
                break;
                case 5:
                    if(byteBuffer_.remaining() >= intPayloadLength_) {
                        frame_.payload_ = new byte[intPayloadLength_];
                        byteBuffer_.get(frame_.payload_);
                        if(frame_.mask_) {
                            frame_.maskPayload();
                        }
                        
                        frames.add(frame_);
                        frame_ = new WebSocketFrame();
                        
                        state_ = 0;
                    }
                    else {
                        loop = false;
                    }
                break;
                }
            }
            byteBuffer_.compact();
            
            return frames;
        }
        
        /**
         * 
         */
        public void reset() {
            byteBuffer_.clear();
            frame_ = new WebSocketFrame();
            state_ = 0;
        }
        
        private ByteBuffer byteBuffer_;
        
        private WebSocketFrame frame_;
        
        private int state_;
        
        private int extendedLengthByteCount_;
        
        private int intPayloadLength_;
        
        private long payloadLength_;
    }
    
    /**
     * Creates a close connection control frame.
     * 
     * @param statusCode An 16-bit unsigned integer that represents the state code.
     * @param mask whether the payload will be masked.
     * @param maskingKey 
     * @param reasonPayload 
     * @return a close connection control frame.
     */
    public static WebSocketFrame createCloseConnectionFrame(
        int statusCode,
        boolean mask,
        int maskingKey,
        byte[] reasonPayload
    ) {
        //TODO : Validate statusCode.
        if(statusCode >= 65536 || statusCode < 0) {
            throw new IllegalArgumentException("The status codes must be in range [0, 65536).");
        }
        
        return createControlFrame(
            Opcode.CLOSE_CONNECTION,
            mask, maskingKey,
            BitUtils.concat(
                new byte[] {
                    (byte)((statusCode & 0xFF00) >>> 8),
                    (byte)(statusCode & 0xFF)
                },
                reasonPayload
            )
        );
    }
    
    /**
     * Creates a ping frame.
     * 
     * @param mask
     * @param maskingKey
     * @param payload
     * @return a ping frame.
     */
    public static WebSocketFrame createPingFrame(
        boolean mask,
        int maskingKey,
        byte[] payload
    ) {
        return createControlFrame(Opcode.PING, mask, maskingKey, payload);
    }
    
    /**
     * Creates a pong frame that is response of ping frames.
     * 
     * @param mask
     * @param maskingKey
     * @param payload
     * @return a pong frame.
     */
    public static WebSocketFrame createPongFrame(
        boolean mask,
        int maskingKey,
        byte[] payload
    ) {
        return createControlFrame(Opcode.PONG, mask, maskingKey, payload);
    }
    
    /**
     * Splits a payload if needed, and wraps them with web socket frames.
     * 
     * @param isBinary whether the payload is binary.
     * @param mask whether the payload will be masked.
     * @param maskingKey 
     * @param payload 
     * @param fragmentSize the size of fragments. if it is zero, the payload WILL NOT be fragmented.
     * @return an array of web socket frames.
     */
    public static WebSocketFrame[] encode(
        boolean isBinary,
        boolean mask,
        int maskingKey,
        byte[] payload,
        int fragmentSize
    ) {
        if(payload == null) {
            payload = new byte[0];
        }
        if(payload.length > MAXIMUM_SUPPORTED_PAYLOAD_LENGTH) {
            throw new IllegalArgumentException("");
        }
        if(fragmentSize < 0) {
            throw new IllegalArgumentException("");
        }
        
        List<WebSocketFrame> frames = new ArrayList<WebSocketFrame>();
        WebSocketFrame frame = null;
        frame = new WebSocketFrame();
        frame.opcode_ = (isBinary ? Opcode.BINARY : Opcode.TEXT);
        frame.setMasking(mask, maskingKey);
        
        if(fragmentSize < 1 || fragmentSize >= payload.length) {
            frame.finalFragment_ = true;
            frame.payload_ = payload;
            frames.add(frame);
        }
        else {
            frame.finalFragment_ = false;
            frame.payload_ = Arrays.copyOfRange(payload, 0, fragmentSize);
            frames.add(frame);
            
            int payloadOff = fragmentSize;
            int maxContinuationOffset = payload.length - fragmentSize;
            for(; payloadOff < maxContinuationOffset; payloadOff += fragmentSize) {
                frame = new WebSocketFrame();
                frame.opcode_ = Opcode.CONTINUATION;
                frame.finalFragment_ = false;
                frame.setMasking(mask, maskingKey);
                frame.payload_ = Arrays.copyOfRange(payload, payloadOff, payloadOff + fragmentSize);
                frames.add(frame);
            }
            
            frame = new WebSocketFrame();
            frame.opcode_ = Opcode.CONTINUATION;
            frame.finalFragment_ = true;
            frame.setMasking(mask, maskingKey);
            frame.payload_ = Arrays.copyOfRange(payload, payloadOff, payloadOff + fragmentSize);
            frames.add(frame);
        }
        
        WebSocketFrame[] frameArray = new WebSocketFrame[frames.size()];
        return frames.toArray(frameArray);
    }
    
    /**
     * Consumes bytes from a byte buffer and constructs web socket frames.<br/>
     * The payload WILL BE UNMASKED if the mask bit is set.
     * 
     * @param byteBuffer  A byte buffer to be consumed.
     * @return a web socket frame buffer array.
     * @throws java.nio.BufferUnderflowException If the number of bytes to consume is less than required.
     */
    public static WebSocketFrame[] parse(
        ByteBuffer byteBuffer
    ) {
        List<WebSocketFrame> frames = new ArrayList<>();
        while(byteBuffer.remaining() > 0) {
            frames.add(new WebSocketFrame(byteBuffer));
        }
        
        return (WebSocketFrame[])frames.toArray();
    }
    
    /**
     * Merges web socket frame fragments into a web socket message.
     * 
     * @param fragmentFrames
     * @return the web socket message created from the fragments.
     */
    public static WebSocketMessage mergeFragments(List<WebSocketFrame> fragmentFrames) {
        WebSocketMessage message = null;
        WebSocketFrame frame = null;
        
        switch(fragmentFrames.size()) {
        case 0:
            message = new WebSocketMessage(true, new byte[0]);
        break;
        case 1:
            frame = fragmentFrames.get(0);
            if(!frame.isFinalFragment()) {
                throw new IllegalArgumentException("");
            }
            
            message = new WebSocketMessage(
                (frame.getOpcode() == Opcode.BINARY),
                frame.getPayload()
            );
        break;
        default:
            List<byte[]> payloadFragments = new ArrayList<>();
            frame = fragmentFrames.get(0);
            if(frame.isFinalFragment()) {
                throw new IllegalArgumentException("");
            }
            boolean isBinary = (frame.getOpcode() == Opcode.BINARY);
            payloadFragments.add(frame.getPayload());
            
            int i = 1;
            for(; i < fragmentFrames.size() - 1; ++i) {
                frame = fragmentFrames.get(i);
                if(!frame.getOpcode().isControl()) {
                    if(!frame.isFinalFragment()) {
                        if(frame.getOpcode() != Opcode.CONTINUATION) {
                            throw new IllegalArgumentException("");
                        }
                        
                        payloadFragments.add(frame.getPayload());
                    }
                }
                else if(!frame.isFinalFragment()) {
                    throw new IllegalArgumentException("Control frames cannot be fragmented.");
                }
            }
            
            frame = fragmentFrames.get(i);
            if(!frame.isFinalFragment() || frame.getOpcode() != Opcode.CONTINUATION) {
                throw new IllegalArgumentException("");
            }
            payloadFragments.add(frame.getPayload());
            
            byte[][] byteArrays = new byte[payloadFragments.size()][];
            payloadFragments.toArray(byteArrays);
            message = new WebSocketMessage(isBinary, BitUtils.concat(byteArrays));
        }
        
        return message;
    }
    
    private WebSocketFrame() {
        finalFragment_ = true;
        reservedBits_ = 0;
        opcode_ = Opcode.BINARY;
        mask_ = false;
        maskingKey_ = 0xFFFFFFFF;
        payload_ = new byte[0];
    }
    
    /**
     * Consumes bytes from a byte buffer and constructs a web socket frame.<br/>
     * The payload WILL BE UNMASKED if the mask bit is set.
     * 
     * @param byteBuffer A byte buffer to be consumed.
     * @throws java.nio.BufferUnderflowException If the number of bytes to consume is less than required.
     */
    public WebSocketFrame(
        ByteBuffer byteBuffer
    ) {
        byte firstByte = byteBuffer.get();
        finalFragment_ = (firstByte & 0x80) != 0;
        reservedBits_ = (firstByte & 0x70) >>> 4;
        opcode_ = Opcode.values()[(firstByte & 0x0F)];
        
        byte secondByte = byteBuffer.get();
        mask_ = (secondByte & 0x80) != 0;
        
        int payloadLength = secondByte & 0x7F;
        byte extendedLengthByteCount = 0;
        if(payloadLength == 126) {
            extendedLengthByteCount = 2;
        }
        else if(payloadLength == 127) {
            extendedLengthByteCount = 8;
        }
        if(extendedLengthByteCount > 0) {
            payloadLength = 0;
            for(int i = extendedLengthByteCount; i > 0; ) {
                --i;
                payloadLength <<= 8;
                payloadLength = (byteBuffer.get() & 0xFF);
            }
        }
        if(payloadLength >= 0x8000000000000000L) {
            throw new RuntimeException("The payload length cannot exceed 2^63.");
        }
        if(payloadLength >= 0x80000000L) {
            throw new RuntimeException("Payload lengths that exceeds 2^31 is not supported yet...");
        }
        
        maskingKey_ = (mask_ ? byteBuffer.getInt() : 0xFFFFFFFF);
        
        payload_ = new byte[payloadLength];
        byteBuffer.get(payload_);
        
        if(mask_) {
            maskPayload();
        }
    }
    
    /**
     * Consumes bytes from a input stream and constructs a web socket frame.<br/>
     * The payload WILL BE UNMASKED if the mask bit is set.
     * 
     * @param is An input stream as byte producer.
     * @throws java.io.IOException If any I/O errors occur.
     * @throws karbonator.karbonator.net.StreamUnderflowException If the number of bytes to consume is less than required.
     */
    public WebSocketFrame(
        InputStream is
    ) throws IOException {
        byte[] buffer = new byte [8];
        if(is.read(buffer, 0, 2) < 2) {
            throw new StreamUnderflowException();
        }
        
        byte firstByte = buffer[0];
        finalFragment_ = (firstByte & 0x80) != 0;
        reservedBits_ = (firstByte & 0x70) >>> 4;
        if(reservedBits_ != 0) {
            throw new RuntimeException("What???");
        }
        opcode_ = Opcode.values()[(firstByte & 0x0F)];
        
        byte secondByte = buffer[1];
        mask_ = (secondByte & 0x80) != 0;
        long payloadLength = secondByte & 0x7F;
        
        byte extendedLengthByteCount = 0;
        if(payloadLength == 126) {
            extendedLengthByteCount = 2;
        }
        else if(payloadLength == 127) {
            extendedLengthByteCount = 8;
        }
        if(extendedLengthByteCount > 0) {
            if(is.read(buffer, 0, extendedLengthByteCount) < extendedLengthByteCount) {
                throw new StreamUnderflowException("");
            }
            
            payloadLength = 0;
            for(int i = extendedLengthByteCount, j = 0; i > 0; ++j) {
                --i;
                payloadLength <<= 8;
                payloadLength |= (buffer[j] & 0xFF);
            }
        }
        if(payloadLength < 0) {
            throw new RuntimeException("The payload length cannot exceed 2^63.");
        }
        if(payloadLength >= 0x80000000L) {
            throw new RuntimeException("Payload lengths that exceeds 2^31 is not supported yet...");
        }
        int intPayloadLength = (int)payloadLength;

        maskingKey_ = 0xFFFFFFFF;
        if(mask_) {
            if(is.read(buffer, 0, 4) < 4) {
                throw new StreamUnderflowException("");
            }
            
            maskingKey_ = ((buffer[0] & 0xFF) << 24)
                | ((buffer[1] & 0xFF) << 16)
                | ((buffer[2] & 0xFF) << 8)
                | (buffer[3] & 0xFF)
            ;
        }
        
        payload_ = consumeByteBlockFromStream(is, intPayloadLength);
        
        if(mask_) {
            maskPayload();
        }
    }
    
    /**
     * Retrieves whether it is the final fragment.
     * 
     * @return true if it is the final fragment, false otherwise.
     */
    public boolean isFinalFragment() {
        return finalFragment_;
    }
    
    public int getReservedBits() {
        return reservedBits_;
    }
    
    public Opcode getOpcode() {
        return opcode_;
    }
    
    /**
     * Retrieves whether the payload is masked.
     * 
     * @return true if the payload is masked, false otherwise.
     */
    public boolean getMask() {
        return mask_;
    }
    
    /**
     * Retrieves the mask key used by masking the payload.
     * 
     * @return the masking key. if the payload is not masked, all bits of the masking key is one.
     */
    public int getMaskingKey() {
        return maskingKey_;
    }
    
    /**
     * Set payload making information.<br/>
     * This action WILL NOT MASK payload immediately.
     * 
     * @param mask Whether the frame will mask the payload.
     * @param maskingKey The masking key to be used making the payload.<br/>
     * If the parameter 'mask' is false, this parameter will be ignored.
     */
    public void setMasking(
        boolean mask,
        int maskingKey
    ) {
        mask_ = mask;
        maskingKey_ = (mask_ ? maskingKey : 0xFFFFFFFF);
    }
    
    /**
     * Retrieves the actual byte count of the frame header.
     * The possible range of the size is [2, 14].
     * 
     * @return the byte count of the frame header.
     */
    public int getHeaderSize() {
        int headerSize = 2;
        
        
        
        if(mask_) {
            headerSize += 4;
        }
        
        return headerSize;
    }    
    
    /**
     * Retrieves the UNMASKED payload byte array.
     * 
     * @return the unmasked payload byte array.
     */
    public byte[] getPayload() {
        return payload_;
    }
    
    /**
     * Replaces the payload.<br/>
     * This action WILL NOT MASK the payload immediately.
     * 
     * @param payload A new payload to replace the exisiting one.
     */
    public void setPayload(byte[] payload) {
        payload_ = payload;
    }
    
    /**
     * Converts the web socket frame to byte array.<br/>
     * The palyload WILL BE MASKED if the mask bit is set.
     * 
     * @return the byte array representaion of the web socket frame.
     */
    public byte[] toByteArray() {
        int byteCount = 0;
        ByteBuffer byteBuffer = ByteBuffer.allocate(16 + payload_.length);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        
        byteBuffer.put((byte)(
            (finalFragment_ ? 0x80 : 0x00)
            | ((reservedBits_ & 0x03) << 4)
            | (opcode_.ordinal() & 0x0F)
        ));
        ++byteCount;
        
        byte secondByte = (byte)(mask_ ? 0x80 : 0x00);
        if(payload_.length < 126) {
            secondByte |= (byte)(payload_.length & 0x7F);
            byteBuffer.put(secondByte);
            ++byteCount;
        }
        else if(payload_.length < 65536) {
            secondByte |= (byte)126;
            byteBuffer.put(secondByte);
            
            byte[] payloadLengthBytes = payloadLengthToByteArray();
            byteBuffer.put(payloadLengthBytes);
            byteCount += 1 + payloadLengthBytes.length;
        }
        else {
            secondByte |= (byte)127;
            byteBuffer.put(secondByte);
            
            byte[] payloadLengthBytes = payloadLengthToByteArray();
            byteBuffer.put(payloadLengthBytes);
            byteCount += 1 + payloadLengthBytes.length;
        }
        
        if(mask_) {
            byteBuffer.putInt(maskingKey_);
            maskPayload();
            byteCount += 4;
        }
        
        byteBuffer.put(payload_);
        byteCount += payload_.length;
        
        byte[] bytes = new byte[byteCount];
        byteBuffer.position(0);
        byteBuffer.get(bytes, 0, bytes.length);
        return bytes;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append('{');
        
        sb.append("finalFragment");
        sb.append(':');
        sb.append(finalFragment_);

        sb.append(',');
        sb.append("opcode");
        sb.append(':');
        sb.append(opcode_);
        
        sb.append(',');
        sb.append("payloadLength");
        sb.append(':');
        sb.append(payload_.length);
        
        sb.append('}');
        
        return sb.toString();
    }
    
    public String toDebugString() {
        byte[] byteArray = toByteArray();
        
        StringBuilder sb = new StringBuilder();
        int i;
        for (i = 0; i < byteArray.length - 4;) {
            for (int j = 0; j < 4; ++j, ++i) {
                sb.append(Integer.toHexString(byteArray[i]));
                sb.append(' ');
            }
            sb.append("\r\n");
        }

        for (int j = 0; j < 4 && i < byteArray.length; ++j, ++i) {
            sb.append(Integer.toHexString(byteArray[i]));
            sb.append(' ');
        }
        sb.append("\r\n");

        return sb.toString();
    }
    
    private static WebSocketFrame createControlFrame(
        Opcode opcode,
        boolean mask,
        int maskingKey,
        byte[] payload
    ) {
        if(opcode.ordinal() < Opcode.CLOSE_CONNECTION.ordinal()) {
            throw new IllegalArgumentException("Must specify one of the control opcodes.");
        }
        
        if(payload == null) {
            payload = new byte[0];
        }
        else if(payload.length >= 126) {
            throw new RuntimeException("Control frames cannot hold payloads whose length are greather or equal than 126 bytes.");
        }
        
        WebSocketFrame frame = new WebSocketFrame();
        frame.opcode_ = opcode;
        
        frame.setMasking(mask, maskingKey);
        frame.setPayload(payload);
        
        return frame;
    }
    
    private byte[] consumeByteBlockFromStream(
        InputStream is,
        int expectedByteCount
    ) throws IOException {
//        byte[] bytes = new byte[expectedByteCount];
//        if(expectedByteCount > 0) {
//            int i = 0;
//            for(int byteValue = 0; i < expectedByteCount && (byteValue = is.read()) >= 0; ++i) {
//                bytes[i] = (byte)(byteValue & 0xFF);
//            }
//            
//            if(i < expectedByteCount) {
//                throw new StreamUnderflowException("");
//            }
//        }
//        
//        return bytes;
        
        List<byte[]> byteArrayList = new ArrayList<>();
        int bufferSize = 1024;
        int remainingByteCount = expectedByteCount;
        int readByteCount = 0;
        
        while(true) {
            byte[] buffer = new byte[bufferSize];
            if(remainingByteCount < bufferSize) {
                readByteCount = is.read(buffer, 0, remainingByteCount);
            }
            else {
                readByteCount = is.read(buffer, 0, bufferSize);
            }
            if(readByteCount < 0) {
                break;
            }
            
            if(readByteCount < bufferSize) {
                byteArrayList.add(Arrays.copyOfRange(buffer, 0, readByteCount));
            }
            else {
                byteArrayList.add(buffer);
            }

            remainingByteCount -= readByteCount;
            if(remainingByteCount < 1) {
                break;
            }
        }
        
        if(remainingByteCount > 0) {
            throw new StreamUnderflowException("");
        }
        
        byte[][] arrayOfByteArray = new byte[byteArrayList.size()][];
        byteArrayList.toArray(arrayOfByteArray);
        return BitUtils.concat(arrayOfByteArray);
    }
    
    private byte[] payloadLengthToByteArray() {
        if(payload_.length < 126) {
            return new byte[]{
                (byte)(payload_.length & 0xFF)
            };
        }
        
        if(payload_.length < 65536) {
            return BitUtils.toBytes((short)(payload_.length & 0xFFFF), false);
        }
        
        return BitUtils.toBytes(payload_.length, false);
    }
    
    /**
     * Masks or unmasks the payload with the masking key property.
     */
    private void maskPayload() {
        byte[] maskingKeyOctects = BitUtils.toBytes(maskingKey_, false);
        for(int i = 0; i < payload_.length; ++i) {
            payload_[i] = (byte)((payload_[i] & 0xFF) ^ (maskingKeyOctects[i & 0x03] & 0xFF));
        }
    }
    
    private boolean finalFragment_;
    
    private int reservedBits_;
    
    private Opcode opcode_;
    
    private boolean mask_;
    
    private int maskingKey_;
    
    private byte[] payload_;
}
