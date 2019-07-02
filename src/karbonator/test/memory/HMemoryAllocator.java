package karbonator.test.memory;

public abstract class HMemoryAllocator<E> {
    
    public final Object [] allocate(int size) {
        return new Object [size];
    }
    
    public abstract void construct(E [] dest, int destOffset, E o);
    public abstract E construct(E o);

}
