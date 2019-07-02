package karbonator.collection;

public interface Array<E> extends SequenceContainer<E> {
    public int getCapacity();
    
    public E at(int index);
    
    public void set(int index, E o);
}
