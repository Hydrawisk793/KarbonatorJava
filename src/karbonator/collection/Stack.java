package karbonator.collection;

public interface Stack<E> extends SequenceContainer<E> {
    public boolean isEmpty();
    
    public boolean isFull();

    public E peek();

    public Stack<E> push(E o);
    
    public Stack<E> pop(E [] pDest, int destIndex);
    
    public E pop();
    
    public void clear();
}
