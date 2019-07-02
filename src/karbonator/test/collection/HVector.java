package karbonator.test.collection;

import karbonator.ContainerUnderflowException;
import karbonator.collection.Array;
import karbonator.collection.Deque;
import karbonator.collection.Queue;
import karbonator.collection.Stack;
import karbonator.test.memory.HMemoryAllocator;

@SuppressWarnings("unchecked")
public class HVector<E> 
implements Array<E>, Stack<E>, Queue<E>, Deque<E> {

    private static final int DEFAULT_CAPACITY = 32;

    @Override
    public int getSize() {
        return nextSlotNdx;
    }
    @Override
    public int getCapacity() {
        return container.length;
    }
    @Override
    public boolean isEmpty() {
        return nextSlotNdx == 0;
    }
    @Override
    public boolean isFull() {
        return nextSlotNdx == container.length;
    }
    
    @Override
    public E at(int index) {
        //assertIndexIsInRange(index);

        return (E)container[index];
    }
    @Override
    public void set(int index, E o) {
        //assertIndexIsInRange(index);

        allocator.construct((E [])container, index, o);
    }
    
    @Override
    public HVector<E> push(E o) {
        return pushBack(o);
    }
    @Override
    public HVector<E> pop(E[] pDest, int destIndex) {
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
    public HVector<E> enqueue(E o) {
        return pushBack(o);
    }
    @Override
    public HVector<E> dequeue(E[] pDest, int destIndex) {
        return popFront(pDest, destIndex);
    }
    @Override
    public E dequeue() {
        return popFront();
    }
    @Override
    public E peekFront() {
        assertIsNotEmpty();
    
        return (E)container[0];
    }
    @Override
    public E peekBack() {
        assertIsNotEmpty();
    
        return (E)container[nextSlotNdx-1];
    }
    
    @Override
    public HVector<E> pushFront(E o) {
        retainEmptySlot();
        
        shiftElementsRight(0, nextSlotNdx, 1);
        ++nextSlotNdx;
        
        allocator.construct((E [])container, 0, o);
        
        return this;
    }
    @Override
    public HVector<E> popFront(E[] pDest, int destIndex) {
        assertIsNotEmpty();
        
        pDest[destIndex] = (E)container[0];
        
        shiftElementsLeft(1, nextSlotNdx, 1);
        --nextSlotNdx;

        return this;
    }
    @Override
    public E popFront() {
        assertIsNotEmpty();
        
        E element = (E)container[0];
        
        shiftElementsLeft(1, nextSlotNdx, 1);
        --nextSlotNdx;
        
        return element;
    }
    @Override
    public HVector<E> pushBack(E o) {
        retainEmptySlot();
        
        allocator.construct((E [])container, nextSlotNdx, o);
        ++nextSlotNdx;
    
        return this;
    }
    @Override
    public HVector<E> popBack(E[] pDest, int destIndex) {
        assertIsNotEmpty();
        
        pDest[destIndex] = (E)container[--nextSlotNdx];
        
        container[nextSlotNdx] = null;
    
        return this;
    }
    @Override
    public E popBack() {
        assertIsNotEmpty();
        
        E element = (E)container[--nextSlotNdx];
        
        container[nextSlotNdx] = null;
        
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
            
            shiftElementsRight(index, nextSlotNdx, 1);
            ++nextSlotNdx;
        
            allocator.construct((E [])container, index, o);
        }
    }
    public E remove(int index) {
        assertIsNotEmpty();
        
        E element = (E)container[index];
        
        shiftElementsLeft(index+1, nextSlotNdx, 1);
        --nextSlotNdx;
        
        return element;
    }
    @Override
    public void clear() {
        if(container != null) {
            for(int r1=0;r1<nextSlotNdx;++r1) {
                container[r1] = null;
            }
        }
        
        nextSlotNdx = 0;
    }
    public void swap(int elem1Idx, int elem2Idx) {
        //assertIndexIsInRange(elem1Idx);
        //assertIndexIsInRange(elem2Idx);
    
        E temp = (E)container[elem1Idx];
        container[elem1Idx] = container[elem2Idx];
        container[elem2Idx] = temp;
    }
    
    public void trim() {
        resize(getSize());
    }
    public void resize(int newSize) {
        if(newSize < 0) {
            throw new IndexOutOfBoundsException();
        }
        else if(newSize == 0) {
            clear();
            
            container = null;
            nextSlotNdx = -1;
        }
        else if(newSize != container.length) {
            final int N_ELEMENT = getSize();
            Object [] newContainer = new Object [newSize];

            if(container != null) {
                if(newSize > container.length || newSize >= N_ELEMENT) {
                    System.arraycopy(container, 0, newContainer, 0, N_ELEMENT);
                }
                else {
                    System.arraycopy(container, 0, newContainer, 0, newSize);
                }            

                if(N_ELEMENT> 0) {
                    clear();
                }
                container = null;
            }
            
            container = newContainer;
            nextSlotNdx = N_ELEMENT;
        }
    }
    private void retainEmptySlot() {
        if(isFull()) {
            if(container == null) {
                resize(DEFAULT_CAPACITY);
            }
            else {
                resize(container.length<<1);
            }
        }
    }

    private void shiftElementsLeft(int startIdx, int endIdx, int amount)  {
        int srcIdx = startIdx;
        int destIdx = startIdx - amount;
        
        for(;srcIdx<endIdx;) {
            container[destIdx] = container[srcIdx];
            container[srcIdx] = null;
    
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
    
            container[destIdx] = container[srcIdx];
            container[srcIdx] = null;
        }
    }
    
    public HVector<E> assign(Array<E> o) {
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
            result.append(container[0].toString());
        }

        for(int r1=1;r1<SIZE;++r1) {
            result.append(", ");
            result.append(container[r1].toString());
        }

        result.append(']');
        
        return result.toString();
    }
    
    public HVector(HMemoryAllocator<E> allocator) {
        this(DEFAULT_CAPACITY, allocator);
    }
    public HVector(int elementCount, HMemoryAllocator<E> allocator) {
        resize(elementCount);

        nextSlotNdx = 0;
        
        this.allocator = allocator;
    }
    public HVector(HVector<E> o, HMemoryAllocator<E> allocator) {
        this((E [])o.container, allocator);
    }
    public HVector(E [] src, HMemoryAllocator<E> allocator) {
        this(src.length, allocator);
        
        for(int r1=0; r1<src.length; ++r1) {
            allocator.construct((E [])container, r1, src[r1]);
        }
        
        nextSlotNdx = src.length;
    }
    public HVector(Array<E> src, HMemoryAllocator<E> allocator) {
        this(src.getCapacity(), allocator);
        
        final int SIZE = src.getSize();
        for(int r1=0;r1<SIZE;++r1) {
            allocator.construct((E [])container, r1, src.at(r1));
        }
        
        nextSlotNdx = src.getCapacity();
    }
    
    /*
    private void assertIndexIsInRange(int index) {
        if(index < 0 || index >= getSize()) {
            throw new HIndexOutOfBoundsException();
        }
    }
    */
    private void assertIsNotEmpty() {
        if(isEmpty()) {
            throw new ContainerUnderflowException();
        }
    }

    private Object [] container;
    private int nextSlotNdx;
    private HMemoryAllocator<E> allocator;

}
