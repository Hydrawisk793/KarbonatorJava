package karbonator.collection;

public interface ForwardlyIterable<E> {
    public ForwardIterator<E> getForwardIterator();
}
