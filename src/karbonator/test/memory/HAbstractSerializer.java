package karbonator.test.memory;

import karbonator.memory.ByteBuffer;
import karbonator.memory.ByteOrder;

public interface HAbstractSerializer<E> {

    public ByteBuffer serialize(E src, ByteOrder byteOrder);
    public E unserialize(ByteBuffer buffer);

}
