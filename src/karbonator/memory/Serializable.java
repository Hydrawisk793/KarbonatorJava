package karbonator.memory;

public interface Serializable {

    public ByteBuffer serialize(ByteOrder byteOrder);
    public Serializable unserialize(ByteBuffer buffer);
    
}
