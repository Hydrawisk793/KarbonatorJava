package karbonator.test.memory;

import karbonator.collection.SequenceContainer;
import karbonator.memory.ByteBuffer;
import karbonator.memory.ByteOrder;

public interface HContainerSerializer< E, C extends SequenceContainer<E> > {

    public ByteBuffer serialize(C src, ByteOrder byteOrder);
    public C unserialize(ByteBuffer buffer, E elemSerializer);

}
