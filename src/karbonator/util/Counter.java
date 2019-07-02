package karbonator.util;

public class Counter {

    public int getMinimum() {
        return min;
    }
    public int getMaximum() {
        return max;
    }
    public int getValue() {
        return value;
    }
    public boolean isMaximum() {
        return value == max;
    }
    public boolean isMinimum() {
        return value == min;
    }
    public boolean hasOverflowed() {
        return value > max;
    }
    public boolean hasUnderflowed() {
        return value < min;
    }

    public void setValue(int v) {
        value = v;
    }
    public void setToMaximum() {
        value = max;
    }
    public void setToMinumim() {
        value = min;
    }
    public boolean increase() {
        ++value;
        
        return hasOverflowed();
    }
    public boolean decrease() {
        --value;
        
        return hasUnderflowed();
    }

    public Counter() {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
    }
    public Counter(Counter o) {
        this(o.min, o.max, o.value);
    }
    public Counter(int min, int max, int value) {
        this.min = min;
        this.max = max;
        this.value = value;
    }

    private int min;
    private int max;
    private int value;

}
