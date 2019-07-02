package karbonator.collection;

public interface BidirectionalIterator<E> extends ForwardIterator<E> {
    public void moveToPrevious();
}
