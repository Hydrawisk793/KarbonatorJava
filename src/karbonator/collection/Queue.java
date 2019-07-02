package karbonator.collection;

public interface Queue<E> extends SequenceContainer<E> {
    public boolean isEmpty();
    
    public boolean isFull();

    public E peekFront();
    
    public E peekBack();
    
    public Queue<E> enqueue(E o);
    
    public Queue<E> dequeue(E [] pDest, int destIndex);
    
    public E dequeue();
    
    public void clear();
}
