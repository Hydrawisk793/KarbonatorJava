package karbonator.collection;

public interface ForwardIterator<E> {
    public E dereference();

    public void moveToNext();
    
    public boolean isBegin();
    
    public boolean isEnd();
    
    public boolean isNull();
}
