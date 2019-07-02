package karbonator.collection;

public interface Deque<E> extends SequenceContainer<E> {
    public boolean isEmpty();
    
    public boolean isFull();

    public E peekFront();
    
    public E peekBack();

    public Deque<E> pushFront(E o);
    
    public Deque<E> pushBack(E o);
    
    public Deque<E> popFront(E [] pDest, int destIndex);
    
    public Deque<E> popBack(E [] pDest, int destIndex);
    
    public E popFront();
    
    public E popBack();
    
    public void clear();
}
