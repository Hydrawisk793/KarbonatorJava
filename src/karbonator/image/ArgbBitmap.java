package karbonator.image;

public class ArgbBitmap extends AbstractBitmap<Integer> {

    @Override
    public int getPixel(int index) {
        return pixels[index];
    }
    @Override
    public int getPixel(int x, int y) {
        return getPixel(x + getOffsetY(y));
    }
    public int [] accessPixels() {
        return pixels;
    }
    
    @Override
    public void setPixel(Integer v) {
        for(int r1=0;r1<pixels.length;++r1) {
            pixels[r1] = v;
        }
    }
    @Override
    public void setPixel(int index, Integer v) {
        pixels[index] = v;
    }
    @Override
    public void setPixel(int x, int y, Integer v) {
        setPixel(x + getOffsetY(y), v);
    }
    
    public void drawBitmap(ArgbBitmap o, int x, int y) {
        final int srcHeight = o.getHeight();
        final int srcWidth = o.getWidth();
        
        for(int srcY=0, destY=y;srcY<srcHeight;++srcY, ++destY) {
            for(int srcX=0, destX=x;srcX<srcWidth;++srcX, ++destX) {
                setPixel(destX, destY, o.getPixel(srcX, srcY));
            }
        }
    }

    public ArgbBitmap() {
        this(1, 1);
    }
    public ArgbBitmap(int width, int height) {
        super(width, height);
        
        final int SIZE = getSize();
        pixels = new int [SIZE];
    }
    public ArgbBitmap(ArgbBitmap o) {
        super(o);
        
        System.arraycopy(o.pixels, 0, pixels, 0, getSize());
    }

    private int [] pixels;

}
