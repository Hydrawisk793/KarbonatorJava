package karbonator.image;

public class PriorityBitmap extends ArgbBitmap {

    public int getPriority(int index) {
        return priorities[index];
    }
    public int getPriority(int x, int y) {
        return getPriority(x + getOffsetY(y));
    }
    public int [] accessPriorities() {
        return priorities;
    }

    public void setPriority(int index, int v) {
        priorities[index] = v;
    }
    public void setPriority(int x, int y, int v) {
        setPriority(x+getOffsetY(y), v);
    }
    public void set(int x, int y, int priority, int argbValue) {
        final int OFFSET = x + getOffsetY(y);
        setPriority(OFFSET, priority);
        setPixel(OFFSET, argbValue);
    }
    
    public PriorityBitmap() {
        super();
    }
    public PriorityBitmap(int width, int height) {
        super(width, height);
        
        final int SIZE = getSize();
        priorities = new int [SIZE];
    }
    public PriorityBitmap(PriorityBitmap o) {
        super(o);
        
        System.arraycopy(o.priorities, 0, priorities, 0, getSize());
    }
        
    private int [] priorities;

}
