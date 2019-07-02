package karbonator.collection;

public interface RandomlyAccessible<E> extends BidirectionallyIterable<E> {
    public RandomAccessIterator<E> getRandomAccessIterator();
    
    public RandomAccessIterator<E> getRandomAccessIterator(int index);
}
