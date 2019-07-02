package karbonator.memory;

interface Buffer {
    public int getSize();
    
    public boolean isEmpty();

    public byte readInt8();
    
    public short readInt16();
    
    public int readInt32();
    
    public long readInt64();
    
    public float readFloat();
    
    public double readDouble();
    
    public byte [] readBytes(int amount);
    
    public void writeInt8(int v);
    
    public void writeInt16(int v);
    
    public void writeInt32(int v);
    
    public void writeInt64(long v);
    
    public void writeFloat(float v);
    
    public void writeDouble(double v);
    
    public void writeBytes(byte [] bytes);
    
    public byte [] toByteArray();
}
