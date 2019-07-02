package karbonator.collection;

@SuppressWarnings("unchecked")
public class FixedArray<E> implements Array<E> {
    public FixedArray(FixedArray<E> o) {
        this(o.elements_.length);
        
        for(int r1=0;r1<elements_.length;++r1) {
            elements_[r1] = o.elements_[r1];
        }
    }
    
    public FixedArray(int elementCount) {
        elements_ = new Object [elementCount];
        
        for(int r1=0;r1<elements_.length;++r1) {
            elements_[r1] = null;
        }
    }
    
    public int getSize() {
        return elements_.length;
    }
    
    @Override
    public int getCapacity() {
        return elements_.length;
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
    
    public FixedArray<E> assign(FixedArray<E> o) {
        if(this != o) {
            if(elements_.length != o.elements_.length) {
                throw new IllegalArgumentException("Both arrays must have same size.");
            }
            
            System.arraycopy(o.elements_, 0, elements_, 0, o.elements_.length);
        }
        
        return this;
    }
    
    @Override
    public String toString() {
        final int SIZE = getSize();
        StringBuilder result = new StringBuilder();
        
        result.append('[');
        
        if(SIZE > 0) {
            result.append(elements_[0].toString());
        }
        
        for(int r1 = 1; r1 < SIZE; ++r1) {
            result.append(',');
            result.append(elements_[r1].toString());
        }
        
        result.append(']');
        
        return result.toString();
    }
    
    private void assertIndexIsInRange(int index) {
        if(index < 0 || index >= elements_.length) {
            throw new IndexOutOfBoundsException();
        }
    }
    
    private Object [] elements_;
}
