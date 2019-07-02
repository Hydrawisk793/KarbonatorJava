package karbonator.collection;

import karbonator.ContainerOverflowException;
import karbonator.ContainerUnderflowException;

@SuppressWarnings("unchecked")
public class ArrayQueue<E> implements Array<E>, Queue<E> {
    public ArrayQueue() {
        this(32);
    }
    
    public ArrayQueue(ArrayQueue<E> o) {
        rear_ = o.rear_;
        front_ = o.front_;
        elements_ = new Object [o.elements_.length];
        for(int r1=0;r1<elements_.length;++r1) {
            elements_[r1] = o.elements_[r1];
        }
    }
    
    public ArrayQueue(int elementCount) {
        rear_ = 0;
        front_ = 0;
        elements_ = new Object [elementCount+1];
        for(int r1=0;r1<elements_.length;++r1) {
            elements_[r1] = null;
        }
    }
    
    public int getSize() {
        int result;
    
        if(rear_ >= front_) {
            result = rear_ - front_;
        }
        else {
            result = elements_.length - (front_ - rear_);
        }
    
        return result;
    }
    
    @Override
    public int getCapacity() {
        return elements_.length;
    }
    
    @Override
    public boolean isEmpty() {
        return (front_ == rear_);
    }
    
    @Override
    public boolean isFull() {
        return (((rear_+1)%elements_.length) == front_);
    }
    
    @Override
    public E at(int index) {
        assertIsNotEmpty();
        assertIndexIsInRange(index);
    
        return (E)elements_[(front_+index)%elements_.length];
    }
    
    @Override
    public void set(int index, E o) {
        assertIsNotEmpty();
        assertIndexIsInRange(index);
    
        elements_[(front_+index)%elements_.length] = o;
    }
    
    @Override
    public E peekFront() {
        assertIsNotEmpty();
    
        return (E)elements_[front_];
    }
    
    @Override
    public E peekBack() {
        assertIsNotEmpty();
    
        return (E)elements_[(rear_-1)%elements_.length];
    }

    @Override
    public ArrayQueue<E> enqueue(E o) {
        assertIsNotFull();
    
        elements_[rear_++] = o;
    
        rear_ %= elements_.length;
    
        return this;
    }
    
    @Override
    public ArrayQueue<E> dequeue(E [] pDest, int destIndex) {
        pDest[destIndex] = dequeue();
        
        return this;
    }
    
    @Override
    public E dequeue() {
        assertIsNotEmpty();
    
        E result = (E)elements_[front_];
        elements_[front_] = null;
    
        ++front_;
        front_ %= elements_.length;
    
        return result;
    }
    
    @Override
    public void clear() {
        front_ = rear_ = 0;
    }
    
    private void assertIndexIsInRange(int index) {
        if(index < 0 || index >= getSize()) {
            throw new IndexOutOfBoundsException();
        }
    }
    
    private void assertIsNotFull() {
        if(isFull()) {
            throw new ContainerOverflowException();
        }
    }
    
    private void assertIsNotEmpty() {
        if(isEmpty()) {
            throw new ContainerUnderflowException();
        }
    }
    
    private Object [] elements_;
    
    private int front_;
    
    private int rear_;    
}
