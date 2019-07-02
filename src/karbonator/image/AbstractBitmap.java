package karbonator.image;

public abstract class AbstractBitmap<T> {

    public final int getWidth() {
        return width;
    }
    public final int getHeight() {
        return height;
    }
    public int getSize() {
        return width*height;
    }
    
    public abstract int getPixel(int index);
    public abstract int getPixel(int x, int y);
    
    public abstract void setPixel(T v);
    public abstract void setPixel(int index, T v);
    public abstract void setPixel(int x, int y, T v);
    
    public AbstractBitmap() {
        this(1, 1);
    }
    public AbstractBitmap(int width, int height) {
        this.width = (width<1?1:width);
        this.height = (height<1?1:height);
    }
    public AbstractBitmap(AbstractBitmap<T> o) {
        this(o.width, o.height);
    }
    
    protected final int getOffsetY(int y) {
        return y*width;
    }

    private int width;
    private int height;
    
}
