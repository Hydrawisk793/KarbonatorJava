package karbonator.image;

public class RgbBitmap extends ArgbBitmap {
    
    @Override
    public void setPixel(Integer v) {
        super.setPixel(0xFF000000|v);
    }
    @Override
    public void setPixel(int index, Integer v) {
        super.setPixel(index, 0xFF000000|v);
    }
    @Override
    public void setPixel(int x, int y, Integer v) {
        setPixel(x + getOffsetY(y), v);
    }
    
    public RgbBitmap() {
        super();
        
        setPixel(0);
    }
    public RgbBitmap(int width, int height) {
        super(width, height);

        setPixel(0);
    }
    public RgbBitmap(RgbBitmap o) {
        super(o);
    }

}
