package karbonator.collection;

import java.util.Iterator;

public interface Iterable<E> {
    public Iterator<E> getBeginIterator();
    
    public Iterator<E> getEndIterator();
}
