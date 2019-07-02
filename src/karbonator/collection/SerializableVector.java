package karbonator.collection;

import karbonator.memory.ByteBuffer;
import karbonator.memory.ByteOrder;
import karbonator.memory.Serializable;

@SuppressWarnings("unchecked")
public class SerializableVector<E> extends Vector<E> implements Serializable {
    
    private void setElementClass(Class<E> elemClass) {
        if(elemClass == null) {
            throw new NullPointerException("Parameter 'elemClass' should not be null.");
        }
        this.elemClass = elemClass;
    }

    @Override
    public ByteBuffer serialize(ByteOrder byteOrder) {
        ByteBuffer buffer = new ByteBuffer(byteOrder);
        
        final int nElem = getSize();
        buffer.writeInt32(nElem);
        
        for(int r1=0; r1<nElem; ++r1) {
            buffer.writeBytes(((Serializable)at(r1)).serialize(byteOrder).toByteArray());
        }
        
        return buffer;
    }
    @Override
    public SerializableVector<E> unserialize(ByteBuffer buffer) {
        final int nElem = buffer.readInt32();
        clear();
        
        try {
            for(int r1=0; r1<nElem; ++r1) {
                pushBack((E)(((Serializable)(elemClass.newInstance())).unserialize(buffer)));
            }
        }
        catch(IllegalAccessException iae) {
            iae.printStackTrace();
            
            throw new RuntimeException(iae);
        }
        catch(InstantiationException ie) {
            ie.printStackTrace();
            
            throw new RuntimeException(ie);
        }
        
        return this;
    }

    public SerializableVector(Class<E> elemClass) {
        super();
        
        setElementClass(elemClass);
    }
    public SerializableVector(Class<E> elemClass, int elementCount) {
        super(elementCount);
        
        setElementClass(elemClass);
    }
    public SerializableVector(Class<E> elemClass, Vector<E> o) {
        super(o);
        
        setElementClass(elemClass);
    }
    public SerializableVector(Class<E> elemClass, E [] src) {
        super(src);
        
        setElementClass(elemClass);
    }
    public SerializableVector(Class<E> elemClass, Array<E> src) {
        super(src);
        
        setElementClass(elemClass);
    }
    
    private Class<E> elemClass;

}
