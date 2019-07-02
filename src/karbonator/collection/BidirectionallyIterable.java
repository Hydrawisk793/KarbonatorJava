package karbonator.collection;

public interface BidirectionallyIterable<E> {
    public BidirectionalIterator<E> getBeginIterator();
    
    public BidirectionalIterator<E> getEndIterator();
}
