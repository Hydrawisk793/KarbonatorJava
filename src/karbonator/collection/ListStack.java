package karbonator.collection;

import karbonator.ContainerUnderflowException;

@SuppressWarnings("unchecked")
public class ListStack<E> implements Stack<E> {
    public ListStack() {
        head_ = null;
        elementCount_ = 0;
    }
    
    public ListStack(ListStack<E> o) {
        copyList(o);
    }
    
    @Override
    public int getSize() {
        return elementCount_;
    }
    
    @Override
    public boolean isEmpty() {
        return (elementCount_ == 0);
    }
    
    @Override
    public boolean isFull() {
        return false;
    }
    
    @Override
    public E peek() {
        if(isEmpty()) {
            throw new ContainerUnderflowException();
        }
    
        return (E)head_.element;
    }

    @Override 
    public ListStack<E> push(E o) {
        Node oldHead = head_;
        head_ = new Node(o, null);
        head_.next = oldHead;
        
        ++elementCount_;
    
        return this;
    }
    
    @Override
    public ListStack<E> pop(E [] pDest, int destIndex) {
        pDest[destIndex] = pop();
        
        return this;
    }
    
    @Override
    public E pop() {
        if(isEmpty()) {
            throw new ContainerUnderflowException();
        }
        
        Object result = head_.element;
        Node nextHead = head_.next;
        head_ = nextHead;
        
        --elementCount_;
        
        return (E)result;
    }
    
    @Override
    public void clear() {
        for(Node target=head_, next=null;target!=null;) {
            next = target.next;
            target = next;
        }
        head_ = null;
        
        elementCount_ = 0;
    }    
    
    private static class Node {
        
        public Node(Node o) {
            element = o.element;
            next = null;
        }
        public Node(Object element, Node next) {
            this.element = element;
            this.next = next;
        }

        public Object element;
        public Node next;
        
    }
    
    private void copyList(ListStack<E> o) {
        clear();
        
        Node src=o.head_;
        
        if(src != null) {
            head_ = new Node(src);
            
            src=src.next;
        }
        
        for(Node current = head_; src != null; src = src.next, current = current.next) {
            current.next = new Node(src);
        }
        
        elementCount_ = o.elementCount_;
    }
    
    private Node head_;
    
    private int elementCount_;
}
