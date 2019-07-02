package karbonator.collection;

public interface RandomAccessIterator<E> extends BidirectionalIterator<E> {
    public boolean isPointingAt(int index);
    
    public void moveTo(int index);
    
    public void moveToBegin();
    
    public void moveToEnd();
}
