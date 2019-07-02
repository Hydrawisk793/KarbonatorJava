package karbonator.image;

public class BitmapBpp32 {
    
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getPixel(int index) {
        return pixels[index];
    }
    public int getPixel(int x, int y) {
        return pixels[y*width + x];
    }
    
    public void setPixel(int index, int pixel) {
        pixels[index] = pixel;
    }
    public void setPixel(int x, int y, int pixel) {
        pixels[y*width + x] = pixel;
    }
    public void setPixels(int destOffset, int [] src, int srcOffset, int srcLength) {
        System.arraycopy(src, srcOffset, pixels, destOffset, srcLength);
    }
    public void blitBitmap(int startX, int startY, BitmapBpp32 src) {
        final int srcWidth = src.getWidth();
        final int srcHeight = src.getHeight();
        
        int srcOff = 0;
        int destOff = startY*width + startX;
        for(int y=0; y<srcHeight; ++y) {
            System.arraycopy(src.pixels, srcOff, pixels, destOff, srcWidth);
            
            srcOff += srcWidth;
            destOff += width;
        }
    }

    public int [] toInt32Array() {
        int [] results = new int [pixels.length];
        System.arraycopy(pixels, 0, results, 0, pixels.length);
        
        return results;
    }
    
    public BitmapBpp32(int width, int height) {
        this.width = width;
        this.height = height;        
        pixels = new int [width*height];
    }
    public BitmapBpp32(BitmapBpp32 o) {
        this(o.width, o.height);
        
        System.arraycopy(o.pixels, 0, pixels, 0, o.width*o.height);
    }

    private int width;
    private int height;
    private int [] pixels;
    
}
