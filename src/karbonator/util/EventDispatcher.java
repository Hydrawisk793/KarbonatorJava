package karbonator.util;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import karbonator.util.EventDelegate;
import karbonator.util.Pair;

/**
 * 
 * @author Hydarwisk793
 * @since 2017-10-09
 */
public class EventDispatcher<K, E> {
    private static class Agent<K, E> implements Runnable {
        public Agent(EventDispatcher<K, E> dispatcher) {
            dispatcher_ = dispatcher;
            thread_ = null;
            queuedEvents_ = new LinkedList<>();
        }
        
        public void start() {
            thread_ = new Thread(this);
            thread_.setDaemon(true);
            thread_.start();
        }
        
        public void waitForTermination() {
            try {
                thread_.join();
            }
            catch(InterruptedException e) {}
        }
        
        public boolean isQueueEmpty() {
            synchronized(queuedEvents_) {
                return queuedEvents_.isEmpty();
            }
        }
        
        public void enqueue(Pair<K, E> pair) {
            synchronized(queuedEvents_) {
                queuedEvents_.add(pair);
            }
        }
        
        @Override
        public void run() {
            try {
                while(dispatcher_.running_) {
                    if(!isQueueEmpty()) {
                        Pair<K, E> pair = dequeue();
                        dispatcher_.fire(pair.left, pair.right);
                    }
                    
                    Thread.sleep(1);
                }
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                dispatcher_.running_ = false;
            }
        }
        
        private Pair<K, E> dequeue() {
            synchronized(queuedEvents_) {
                Pair<K, E> pair = queuedEvents_.get(0);
                queuedEvents_.remove(0);
                
                return pair;
            }
        }
        
        private EventDispatcher<K, E> dispatcher_;
        
        private Thread thread_;
        
        private List<Pair<K, E>> queuedEvents_;
    }
    
    public EventDispatcher() {
        running_ = false;
        agent_ = null;
        events_ = new Hashtable<>();
    }
    
    public boolean isRunning() {
        return running_;
    }
    
    public boolean hasQueuedEvents() {
        return isRunning() && !agent_.isQueueEmpty();
    }
    
    public synchronized void start() {
        if(!isRunning()) {
            running_ = true;
            
            agent_ = new Agent<K, E>(this);
            agent_.start();
        }
    }
    
    public synchronized void stop(boolean waitForTermination) {
        if(isRunning()) {
            running_ = false;
            
            if(waitForTermination) {
                agent_.waitForTermination();
            }
        }
    }
    
    public void add(K key, EventListener<E> listener) {
        EventDelegate<E> eventDelegate = getDelegate(key);
        synchronized(eventDelegate) {
            eventDelegate.add(listener);
        }
    }
    
    public void remove(K key, EventListener<E> listener) {
        EventDelegate<E> eventDelegate = getDelegate(key);
        synchronized(eventDelegate) {
            eventDelegate.remove(listener);
        }
    }
    
    public void removeAllOf(K key) {
        EventDelegate<E> eventDelegate = getDelegate(key);
        synchronized(eventDelegate) {
            eventDelegate.clear();
        }
    }
    
    public void removeAll() {
        synchronized(events_) {
            for(K key : events_.keySet()) {
                removeAllOf(key);
            }
        }
    }
    
    public void notifyListeners(K key, E e) {
        agent_.enqueue(new Pair<K, E>(key, e));
    }
    
    private EventDelegate<E> getDelegate(K key) {
        synchronized(events_) {
            EventDelegate<E> eventDelegate = null;
            if(events_.containsKey(key)) {
                eventDelegate = events_.get(key);
            }
            else {
                eventDelegate = new EventDelegate<>();
                events_.put(key, eventDelegate);
            }
            
            return eventDelegate;
        }
    }
    
    private void fire(K key, E e) {
        for(EventListener<E> listener : new EventDelegate<E>(getDelegate(key))) {
            listener.invoke(e);
        }
    }
    
    private volatile boolean running_;
    
    private Agent<K, E> agent_;
    
    private Map<K, EventDelegate<E>> events_;
}
