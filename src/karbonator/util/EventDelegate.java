package karbonator.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * An event listener set that is inspired by C# event delegates.
 * 
 * @author Hydarwisk793
 * @since 2017-03-10
 */
public class EventDelegate<E> implements Iterable<EventListener<E>> {
    /**
     * Creates an empty delegate.
     */
    public EventDelegate() {
        listeners_ = new HashSet<>();
    }
    
    /**
     * Creates a clone of another delegate.
     * 
     * @param o a delegate to be cloned.
     */
    public EventDelegate(
        EventDelegate<E> o
    ) {
        this();
        listeners_.addAll(o.listeners_);
    }
    
    /**
     * Tests if the delegate has no listeners.
     * 
     * @return true if the delegate has no listeners, false otherwise.
     */
    public boolean isEmpty() {
        return listeners_.isEmpty();
    }
    
    /**
     * Retrieves the number of listeners.
     * 
     * @return the number of listeners.
     */
    public int getCount() {
        return listeners_.size();
    }
    
    @Override
    public Iterator<EventListener<E>> iterator() {
        return listeners_.iterator();
    }
    
    /**
     * Adds a new listener.
     * 
     * @param listener
     * @return this delegate object.
     */
    public EventDelegate<E> add(EventListener<E> listener) {
        listeners_.add(listener);
        
        return this;
    }
    
    /**
     * Removes specified listener.
     * 
     * @param listener
     * @return this delegate object.
     */
    public EventDelegate<E> remove(EventListener<E> listener) {
        listeners_.remove(listener);
        
        return this;
    }
    
    /**
     * Removes all listeners. the delegate will be empty.
     */
    public void clear() {
        listeners_.clear();
    }
    
    /**
     * Notifies all listeners.
     * 
     * @param event
     */
    public void invoke(E event) {
        if(isEmpty()) {
            throw new RuntimeException("Cannot invoke an empty delegate.");
        }
        
        for(EventListener<E> listener : listeners_) {
            listener.invoke(event);
        }
    }
    
    private Set<EventListener<E>> listeners_;
}
