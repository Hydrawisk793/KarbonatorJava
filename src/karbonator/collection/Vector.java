package karbonator.collection;

import karbonator.ContainerUnderflowException;

@SuppressWarnings("unchecked")
public class Vector<E> implements Array<E>, Stack<E>, Queue<E>, Deque<E> {
    public Vector() {
        this(DEFAULT_CAPACITY);
    }
    
    public Vector(Vector<E> o) {
        this(o.elements_.length);
        
        final int SIZE = o.getSize();
        System.arraycopy(o.elements_, 0, elements_, 0, SIZE);
        nextSlotIndex_ = SIZE;
    }
    
    public Vector(int elementCount) {
        nextSlotIndex_ = 0;
        
        resize(elementCount);
    }
    
    public Vector(E [] src) {
        this(src.length);
        
        System.arraycopy(src, 0, elements_, 0, src.length);
        nextSlotIndex_ = src.length;
    }
    
    public Vector(Array<E> src) {
        this(src.getCapacity());
        
        final int SIZE = src.getSize();
        for(int r1=0;r1<SIZE;++r1) {
            elements_[r1] = src.at(r1);
        }
        
        nextSlotIndex_ = SIZE;
    }
    
    @Override
    public int getSize() {
        return nextSlotIndex_;
    }
    
    @Override
    public int getCapacity() {
        return elements_.length;
    }
    
    @Override
    public boolean isEmpty() {
        return nextSlotIndex_ == 0;
    }
    
    @Override
    public boolean isFull() {
        return nextSlotIndex_ == elements_.length;
    }
    
    @Override
    public E at(int index) {
        assertIndexIsInRange(index);
        
        return (E)elements_[index];
    }
    
    @Override
    public void set(int index, E o) {
        assertIndexIsInRange(index);

        elements_[index] = o;
    }
    
    @Override
    public Vector<E> push(E o) {
        return pushBack(o);
    }
    
    @Override
    public Vector<E> pop(E[] pDest, int destIndex) {
        return popBack(pDest, destIndex);
    }
    
    @Override
    public E pop() {
        return popBack();
    }
    
    @Override
    public E peek() {
        return peekBack();
    }

    @Override
    public Vector<E> enqueue(E o) {
        return pushBack(o);
    }
    
    @Override
    public Vector<E> dequeue(E[] pDest, int destIndex) {
        return popFront(pDest, destIndex);
    }
    
    @Override
    public E dequeue() {
        return popFront();
    }
    
    @Override
    public E peekFront() {
        assertIsNotEmpty();
    
        return (E)elements_[0];
    }
    
    @Override
    public E peekBack() {
        assertIsNotEmpty();
    
        return (E)elements_[nextSlotIndex_-1];
    }
    
    @Override
    public Vector<E> pushFront(E o) {
        retainEmptySlot();
        
        shiftElementsRight(0, nextSlotIndex_, 1);
        ++nextSlotIndex_;
        
        elements_[0] = o;
        
        return this;
    }
    
    @Override
    public Vector<E> pushBack(E o) {
        retainEmptySlot();
        
        elements_[nextSlotIndex_] = o;
        ++nextSlotIndex_;
    
        return this;
    }
    
    @Override
    public Vector<E> popFront(E[] pDest, int destIndex) {
        assertIsNotEmpty();
        
        pDest[destIndex] = (E)elements_[0];
        
        shiftElementsLeft(1, nextSlotIndex_, 1);
        --nextSlotIndex_;

        return this;
    }
    
    @Override
    public E popFront() {
        assertIsNotEmpty();
        
        E element = (E)elements_[0];
        
        shiftElementsLeft(1, nextSlotIndex_, 1);
        --nextSlotIndex_;
        
        return element;
    }
    
    @Override
    public Vector<E> popBack(E[] pDest, int destIndex) {
        assertIsNotEmpty();
        
        pDest[destIndex] = (E)elements_[--nextSlotIndex_];
        
        elements_[nextSlotIndex_] = null;
    
        return this;
    }
    
    @Override
    public E popBack() {
        assertIsNotEmpty();
        
        E element = (E)elements_[--nextSlotIndex_];
        
        elements_[nextSlotIndex_] = null;
        
        return element;
    }

    public void insert(int index, E o) {
        if(index < 0) {
            throw new IndexOutOfBoundsException();
        }
        else if(index >= getSize()) {
            pushBack(o);
        }
        else {
            retainEmptySlot();
            
            shiftElementsRight(index, nextSlotIndex_, 1);
            ++nextSlotIndex_;
        
            elements_[index] = o;
        }
    }
    
    public E remove(int index) {
        assertIsNotEmpty();
        
        E element = (E)elements_[index];
        
        shiftElementsLeft(index+1, nextSlotIndex_, 1);
        --nextSlotIndex_;
        
        return element;
    }
    
    @Override
    public void clear() {
        if(elements_ != null) {
            for(int r1=0;r1<nextSlotIndex_;++r1) {
                elements_[r1] = null;
            }
        }
        
        nextSlotIndex_ = 0;
    }
    
    public void swap(int elem1Idx, int elem2Idx) {
        //assertIndexIsInRange(elem1Idx);
        //assertIndexIsInRange(elem2Idx);
    
        E temp = (E)elements_[elem1Idx];
        elements_[elem1Idx] = elements_[elem2Idx];
        elements_[elem2Idx] = temp;
    }
    
    public void trim() {
        resize(getSize());
    }
    
    public void resize(int newSize) {
        if(newSize == 0) {
            clear();
            elements_ = null;
        }
        else {
            final int N_ELEMENT = getSize();
            Object [] newContainer = new Object [newSize];

            if(elements_ != null) {
                if(newSize >= elements_.length) {
                    System.arraycopy(elements_, 0, newContainer, 0, N_ELEMENT);
                }
                else if(newSize >= N_ELEMENT) {
                    System.arraycopy(elements_, 0, newContainer, 0, N_ELEMENT);
                }
                else {
                    System.arraycopy(elements_, 0, newContainer, 0, newSize);
                }            

                if(N_ELEMENT> 0) {
                    clear();
                }
                elements_ = null;
            }
            
            elements_ = newContainer;
            nextSlotIndex_ = N_ELEMENT;
        }
    }

    public Vector<E> assign(Array<E> o) {
        if(this != o) {
            clear();
            
            final int nElem = o.getSize();
            for(int r1=0; r1<nElem; ++r1) {
                pushBack(o.at(r1));
            }
        }
    
        return this;
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        final int SIZE = getSize();
        
        result.append('[');

        if(SIZE > 0) {
            result.append(elements_[0].toString());
        }

        for(int r1=1;r1<SIZE;++r1) {
            result.append(", ");
            E element = (E)elements_[r1];
            if(element != null) {
                result.append(elements_[r1].toString());
            }
            else {
                result.append("null");
            }
        }

        result.append(']');
        
        return result.toString();
    }
    
    private void retainEmptySlot() {
        if(isFull()) {
            if(elements_ == null) {
                resize(DEFAULT_CAPACITY);
            }
            else {
                resize(elements_.length<<1);
            }
        }
    }
    
    private void shiftElementsLeft(int startIdx, int endIdx, int amount)  {
        int srcIdx = startIdx;
        int destIdx = startIdx - amount;
        
        for(;srcIdx<endIdx;) {
            elements_[destIdx] = elements_[srcIdx];
            elements_[srcIdx] = null;
    
            ++destIdx;
            ++srcIdx;
        }
    }
    
    private void shiftElementsRight(int startIdx, int endIdx, int amount) {
        int srcIdx = endIdx;
        int destIdx = endIdx + amount;
        
        for(;srcIdx>startIdx;) {
            --destIdx;
            --srcIdx;
    
            elements_[destIdx] = elements_[srcIdx];
            elements_[srcIdx] = null;
        }
    }
    
    private void assertIndexIsInRange(int index) {
        if(index < 0 || index >= getSize()) {
            throw new IndexOutOfBoundsException();
        }
    }
    
    private void assertIsNotEmpty() {
        if(isEmpty()) {
            throw new ContainerUnderflowException();
        }
    }
    
    private static final int DEFAULT_CAPACITY = 32;
    
    private Object [] elements_;
    
    private int nextSlotIndex_;
}
