package karbonator.memory;

import karbonator.ArgumentNullException;

public class ByteBuffer implements Buffer {
    public ByteBuffer() {
        this(ByteOrder.BIG_ENDIAN);
    }
    
    public ByteBuffer(ByteBuffer src) {
        if(null == src) {
            throw new ArgumentNullException("'src' is null.");
        }
        
        setBuffer(src.byteOrder_, src.toByteArray());
    }
    
    public ByteBuffer(ByteOrder byteOrder) {
        setByteOrder(byteOrder);
    }
    
    public ByteBuffer(ByteOrder byteOrder, byte [] bytes) {
        setBuffer(byteOrder, bytes);
    }
    
    @Override
    public int getSize() {
        return impl_.getSize();
    }
    
    @Override
    public boolean isEmpty() {
        return impl_.isEmpty();
    }
    
    public ByteOrder getByteOrder() {
        return byteOrder_;
    }
    
    public void setByteOrder(ByteOrder byteOrder) {
        setBuffer(byteOrder, impl_.toByteArray());
    }
    
    @Override
    public byte readInt8() {
        return impl_.readInt8();
    }
    
    @Override
    public short readInt16() {
        return impl_.readInt16();
    }
    
    @Override
    public int readInt32() {
        return impl_.readInt32();
    }
    
    @Override
    public long readInt64() {
        return impl_.readInt64();
    }
    
    @Override
    public float readFloat() {
        return impl_.readFloat();
    }
    
    @Override
    public double readDouble() {
        return impl_.readDouble();
    }
    
    @Override
    public byte [] readBytes(int amount) {
        return impl_.readBytes(amount);
    }
    
    @Override
    public void writeInt8(int v) {
        impl_.writeInt8(v);
    }
    
    @Override
    public void writeInt16(int v) {
        impl_.writeInt16(v);
    }
    
    @Override
    public void writeInt32(int v) {
        impl_.writeInt32(v);
    }
    
    @Override
    public void writeInt64(long v) {
        impl_.writeInt64(v);
    }
    
    @Override
    public void writeFloat(float v) {
        impl_.writeFloat(v);
    }
    
    @Override
    public void writeDouble(double v) {
        impl_.writeDouble(v);
    }
    
    @Override
    public void writeBytes(byte[] bytes) {
        impl_.writeBytes(bytes);
    }
    
    @Override
    public byte [] toByteArray() {
        return impl_.toByteArray();
    }
    
    private void setBuffer(ByteOrder byteOrder, byte [] bytes) {
        this.byteOrder_ = byteOrder;
        
        impl_ = (byteOrder.equals(ByteOrder.BIG_ENDIAN) ? new BigEndianBuffer() : new LittleEndianBuffer());
        
        if(null != bytes) {
            impl_.writeBytes(bytes);
        }
    }
    
    private Buffer impl_;
    
    private ByteOrder byteOrder_;
}
