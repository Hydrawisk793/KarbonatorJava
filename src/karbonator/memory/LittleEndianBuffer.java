package karbonator.memory;

import karbonator.collection.Array;
import karbonator.collection.Vector;

class LittleEndianBuffer implements Buffer {
    public LittleEndianBuffer() {
        buffer = new Vector<Byte>();
    }
    
    public LittleEndianBuffer(LittleEndianBuffer o) {
        this();
        
        final int SIZE = o.getSize();
        for(int r1=0;r1<SIZE;++r1) {
            buffer.enqueue(new Byte(o.buffer.at(r1)));
        }
    }
    
    public LittleEndianBuffer(byte [] bytes) {
        this();
        
        final int SIZE = bytes.length;
        for(int r1=0;r1<SIZE;++r1) {
            buffer.enqueue(bytes[r1]);
        }
    }
    
    public LittleEndianBuffer(Array<Byte> bytes) {
        this();

        final int SIZE = bytes.getSize();
        for(int r1=0;r1<SIZE;++r1) {
            buffer.enqueue(bytes.at(r1));
        }
    }
    
    @Override
    public int getSize() {
        return buffer.getSize();
    }
    
    @Override
    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    @Override
    public byte readInt8() { 
        return (byte)(buffer.dequeue()&0xFF);
    }
    
    @Override
    public short readInt16() {
        short result = (short)(readInt8()&0xFF);
        result |= ((readInt8()&0xFF)<<8);

        return result;
    }
    
    @Override
    public int readInt32() {
        int result = (readInt8()&0xFF);
        result |= ((readInt8()&0xFF)<<8);
        result |= ((readInt8()&0xFF)<<16);
        result |= ((readInt8()&0xFF)<<24);

        return result;
    }
    
    @Override
    public long readInt64() {
        long result = (readInt8()&0xFF);
        result |= ((readInt8()&0xFF)<<8);
        result |= ((readInt8()&0xFF)<<16);
        result |= ((readInt8()&0xFF)<<24);
        result |= ((readInt8()&0xFF)<<32);
        result |= ((readInt8()&0xFF)<<40);
        result |= ((readInt8()&0xFF)<<48);
        result |= ((readInt8()&0xFF)<<56);

        return result;
    }
    
    @Override
    public float readFloat() {
        int result = readInt32();

        return Float.intBitsToFloat(result);
    }
    
    @Override
    public double readDouble() {
        long result = readInt64();

        return Double.longBitsToDouble(result);
    }
    
    @Override
    public byte [] readBytes(int amount) {
        byte [] result = new byte [amount];
        
        for(int r1=0;r1<amount;++r1) {
            result[r1] = readInt8();
        }
        
        return result;
    }

    @Override
    public void writeInt8(int v) {
        buffer.enqueue((byte)(v&0xFF));
    }
    
    @Override
    public void writeInt16(int v) {
        writeInt8((v&0xFF));
        writeInt8(((v&0xFF00)>>>8));
    }
    
    @Override
    public void writeInt32(int v) {
        writeInt8((v&0xFF));
        writeInt8(((v&0xFF00)>>>8));
        writeInt8(((v&0xFF0000)>>>16));
        writeInt8(((v&0xFF000000)>>>24));
    }
    
    @Override
    public void writeInt64(long v) {
        writeInt8((int)(v&0xFF));
        writeInt8((int)((v&0xFF00)>>>8));
        writeInt8((int)((v&0xFF0000)>>>16));
        writeInt8((int)((v&0xFF000000)>>>24));
        writeInt8((int)((v&0xFF00000000L)>>>32));
        writeInt8((int)((v&0xFF0000000000L)>>>40));
        writeInt8((int)((v&0xFF000000000000L)>>>48));
        writeInt8((int)((v&0xFF00000000000000L)>>>56));
    }
    
    @Override
    public void writeFloat(float v) {
        writeInt32(Float.floatToIntBits(v));
    }
    
    @Override
    public void writeDouble(double v) {
        writeInt64(Double.doubleToLongBits(v));
    }
    
    @Override
    public void writeBytes(byte [] bytes) {
        for(int r1=0;r1<bytes.length;++r1) {
            writeInt8(bytes[r1]);
        }
    }

    @Override
    public byte [] toByteArray() {
        final int SIZE = buffer.getSize();
        byte [] result = new byte [SIZE];

        for(int r1=0;r1<SIZE;++r1) {
            result[r1] = buffer.at(r1);
        }
    
        return result;
    }
    
    private Vector<Byte> buffer;
}
