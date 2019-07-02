package karbonator.image;

import java.util.Arrays;

import karbonator.collection.ListStack;
import karbonator.collection.Vector;
import karbonator.math.geom2d.Point2D;
import karbonator.math.geom2d.Rectangle2D;

public class GrayBitmap extends AbstractBitmap<Byte> {

    public int getPixel(int index) {
        return (pixels[index]&0xFF);
    }
    public int getPixel(int x, int y) {
        return getPixel(x + getOffsetY(y));
    }
    public int [] getRGBBitmapBits() {
        int [] result = new int [getSize()];
    
        int v = 0;
        for(int r1=0;r1<result.length;++r1) {
            v = (pixels[r1]&0xFF);
            result[r1] = 0xFF000000 | (v<<16) | (v<<8) | v;
        }
    
        return result;
    }
    public int [] getBitmapBits() {
        int [] result = new int [getSize()];
    
        for(int r1=0;r1<result.length;++r1) {
            result[r1] = (pixels[r1]&0xFF);
        }
    
        return result;
    }
    
    public void setPixel(Byte v) {
        for(int r1=0;r1<pixels.length;++r1) {
            pixels[r1] = (byte)(v&0xFF);
        }
    }
    public void setPixel(int index, Byte v) {
        pixels[index] = (byte)(v&0xFF);
    }
    public void setPixel(int x, int y, Byte v) {
        setPixel(x + getOffsetY(y), v);
    }
    
    public GrayBitmap inverse() {
        final int width = getWidth();
        final int height = getHeight();
        final int S_IMAGE = getSize();
        GrayBitmap destImage = new GrayBitmap(width, height);
        
        for(int r1=0;r1<S_IMAGE;++r1) {
            setPixel(r1, (byte)(getPixel(r1)^0xFF));
        }
    
        return destImage;
    }
    public GrayBitmap binarizeBySort(float threashold) {
        int [] sortedPixels = new int [getSize()];

        if(threashold < 0) {
            threashold = 0f;
        }
        else if(threashold > 1){
            threashold = 1f;
        }
        
        System.arraycopy(pixels, 0, sortedPixels, 0, getSize());
        Arrays.sort(sortedPixels);
        byte threshold = (byte)(sortedPixels[(int)(getSize()*threashold)]&0xFF);
        
        return binarize(threshold);
    }
    public GrayBitmap binarizeByMean() {
        final int WIDTH = getWidth();
        final int HEIGHT = getHeight();
        final int S_IMAGE = getSize();
        GrayBitmap destImage = new GrayBitmap(WIDTH, HEIGHT);
        int sum = 0;
        float mean = 0.0f;
    
        for(int r1=0;r1<S_IMAGE;++r1) {
            sum += getPixel(r1);
        }
        mean = sum/(float)S_IMAGE;
    
        for(int r1=0;r1<S_IMAGE;++r1) {
            destImage.setPixel(r1, (byte)(getPixel(r1)<mean?0:0xFF));
        }
    
        return destImage;
    }
    public GrayBitmap binarize(byte threshold) {
        final int WIDTH = getWidth();
        final int HEIGHT = getHeight();
        final int S_IMAGE = getSize();
        GrayBitmap destImage = new GrayBitmap(WIDTH, HEIGHT);
        
        for(int r1=0;r1<S_IMAGE;++r1) {
            destImage.setPixel(r1, (byte)(getPixel(r1)<threshold?0:0xFF));
        }
    
        return destImage;
    }
    public GrayBitmap scale(int destWidth, int destHeight) {
        final float wFactor = destWidth/(float)getWidth();
        final float hFactor = destHeight/(float)getHeight();
        GrayBitmap destImage = new GrayBitmap(destWidth, destHeight);
        float inversedWFactor = 1/wFactor;
        float inversedHFactor = 1/hFactor;
    
        float transformedPosX, transformedPosY;
    
        for(int y=0;y<destHeight;++y) {
            for(int x=0;x<destWidth;++x) {
                transformedPosX = (x*inversedWFactor);
                transformedPosY = (y*inversedHFactor);
    
                destImage.setPixel(x, y, interpolate(transformedPosX, transformedPosY));
            }
        }
    
        return destImage;
    }
    public GrayBitmap doBinaryMorphology(Vector<StructuringElement> structElems, boolean positiveBackground) {
        final int width = getWidth();
        final int height = getHeight();
        GrayBitmap destImage = new GrayBitmap(width, height);
        byte BACKGROUND;
        byte FOREGROUND;    
        int nElem = structElems.getSize();
        Point2D elemPos = null;
        int elemAbsX;
        int elemAbsY;
        byte newValue;
        
        if(!positiveBackground) {
            BACKGROUND = 0x00;
            FOREGROUND = (byte)0xFF;
        }
        else {
            BACKGROUND = (byte)0xFF;
            FOREGROUND = 0x00;
        };
    
        for(int y=0, offsetY=0;y<height;++y, offsetY += width) {
            for(int x=0;x<width;++x) {
                newValue = FOREGROUND;
    
                for(int elemIdx=0;elemIdx<nElem;++elemIdx) {
                    elemPos = structElems.at(elemIdx).getPosition();
                    elemAbsX = x + (int)elemPos.getX();
                    elemAbsY = y + (int)elemPos.getY();
                    
                    if( elemAbsX >= 0 && elemAbsX < width &&
                        elemAbsY >= 0 && elemAbsY < height &&
                        getPixel(elemAbsX + elemAbsY*width) == BACKGROUND) 
                    {
                        newValue = BACKGROUND;
                        break;
                    }
                }
                
                destImage.setPixel(x + offsetY, newValue);
            }
        }
        
        return destImage;
    }
    public GrayBitmap doGrayDilation(Vector<StructuringElement> structElems) {
        final int WIDTH = getWidth();
        final int HEIGHT = getHeight();
        final int N_ELEM = structElems.getSize();
        GrayBitmap destImage = new GrayBitmap(WIDTH, HEIGHT);
        int absX, absY;
        int sum, maxValue;
        
        destImage.setPixel((byte)0xFF);
    
        for(int y=0, offsetY=0;y<HEIGHT;++y, offsetY+=WIDTH) {
            for(int x=0;x<WIDTH;++x) {
                maxValue = getPixel(x+offsetY);
    
                for(int elemIdx=0;elemIdx<N_ELEM;++elemIdx) {
                    StructuringElement pElem = structElems.at(elemIdx);
                    Point2D pos = pElem.getPosition();
    
                    absX = x + (int)pos.getX();
                    absY = y + (int)pos.getY();
    
                    if( absX >= 0 && absX < WIDTH &&
                        absY >= 0 && absY < HEIGHT) {
                        sum = getPixel(absX+absY*WIDTH) + pElem.getPixel();
    
                        if(sum > maxValue) {
                            maxValue = sum;
                        }
                    }
                }
    
                destImage.setPixel(x+offsetY, clipValue(maxValue));
            }
        }
    
        return destImage;
    }
    public LabeledBitmap doLabeling(int minThreshold, int maxThreshold) {
        final int WIDTH = getWidth();
        final int HEIGHT = getHeight();
        LabeledBitmap labeledImage = new LabeledBitmap(WIDTH, HEIGHT);
        ListStack<Point2D> centerPointStack = new ListStack<Point2D>();
        int labelArea = 0;
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = 0, maxY = 0;
        int labelNo = 1;
        int labelX, labelY;
        int targetX, targetY;
        int targetStartX, targetStartY;        
    
        for(int scanY=0;scanY<HEIGHT;++scanY) {
            for(int scanX=0;scanX<WIDTH;++scanX) {
                if( getPixel(scanX, scanY) != 0 &&
                    labeledImage.getPixel(scanX, scanY) == 0)
                {
                    targetStartX = scanX-1;
                    targetStartY = scanY-1;
    
                    for(boolean fire=true;fire;) {
                        for(labelY=0, targetY=targetStartY;labelY<3;++labelY, ++targetY) {
                            for(labelX=0, targetX=targetStartX;labelX<3;++labelX, ++targetX) {
                                if( targetX >= 0 && targetX < WIDTH &&
                                    targetY >= 0 && targetY < HEIGHT &&
                                    getPixel(targetX, targetY) != 0 &&
                                    labeledImage.getPixel(targetX, targetY) == 0) 
                                {
                                    labeledImage.setPixel(targetX, targetY, labelNo);
                                    
                                    ++labelArea;
    
                                    if(targetX < minX) {
                                        minX = targetX;
                                    }
                                    if(targetX > maxX) {
                                        maxX = targetX;
                                    }
                                    if(targetY < minY) {
                                        minY = targetY;
                                    }
                                    if(targetY > maxY) {
                                        maxY = targetY;
                                    }
    
                                    centerPointStack.push(new Point2D(targetX, targetY));
                                }
                            }
                        }
    
                        if(!centerPointStack.isEmpty()) {
                            Point2D centerPoint = centerPointStack.pop();
    
                            targetStartX = (int)(centerPoint.getX()-1);
                            targetStartY = (int)(centerPoint.getY()-1);
                        }
                        else {
                            fire = false;
                        }
                    }
    
                    if(labelArea >= minThreshold & labelArea < maxThreshold) {
                        ObjectLabel label = new ObjectLabel();
                        label.setArea(labelArea);
                        label.setRectangle(new Rectangle2D(new Point2D(minX, minY), new Point2D(maxX, maxY)));
                        labeledImage.addLabel(label);
    
                        ++labelNo;
                    }
    
                    labelArea = 0;
                    minX = minY = Integer.MAX_VALUE;
                    maxX = maxY = 0;
                }
            }
        }
    
        return labeledImage;
    }
    
    public GrayBitmap() {
        this(1, 1);
    }
    public GrayBitmap(int width, int height) {
        super(width, height);
        
        pixels = new byte [getSize()];
    }
    public GrayBitmap(int width, int height, int [] srcBits) {
        this(width, height, srcBits, false);
    }
    public GrayBitmap(int width, int height, int [] srcBits, boolean rgbBits) {
        this(width, height);
        
        pixels = new byte [getSize()];

        if(!rgbBits) {
            for(int r1=0;r1<getSize();++r1) {
                setPixel(r1, clipValue(srcBits[r1]));
            }
        }
        else {
            for(int r1=0, newValue = 0;r1<getSize();++r1) {
                newValue =  ((
                                ((srcBits[r1]&0xFF0000)>>>16) + 
                                ((srcBits[r1]&0xFF00)>>>8) + 
                                (srcBits[r1]&0xFF)
                            )/3);
            
                setPixel(r1, (byte)newValue);
            }
        }
    }
    public GrayBitmap(int width, int height, double [] srcBits, boolean rgbBits) {
        this(width, height);
        
        pixels = new byte [getSize()];

        if(!rgbBits) {
            for(int r1=0;r1<getSize();++r1) {
                setPixel(r1, clipValue(srcBits[r1]));
            }
        }
        else {
            for(int r1=0, newValue = 0;r1<getSize();++r1) {
                newValue =  ((((int)srcBits[r1]&0xFF0000)>>>16) + 
                            (((int)srcBits[r1]&0xFF00)>>>8) + 
                            ((int)srcBits[r1]&0xFF))/3;
            
                setPixel(r1, (byte)newValue);
            }
        }
    }
    public GrayBitmap(GrayBitmap o) {
        this(o.getWidth(), o.getHeight());
        
        System.arraycopy(o.pixels, 0, pixels, 0, getSize());
    }
    
    private byte interpolate(float transformedPosX, float transformedPosY) {
        final int width = getWidth();
        final int height = getHeight();
        int targetPosX = (int)transformedPosX;
        int targetPosY = (int)transformedPosY;
        float valueTL, valueTR, valueBL, valueBR;
        byte newValue = 0;
    
        if( targetPosX >= 0 && targetPosX < width-1 &&
            targetPosY >= 0 && targetPosY < height-1) {
            valueTL = getPixel(targetPosX, targetPosY);
            valueTR = getPixel(targetPosX+1, targetPosY);
            valueBL = getPixel(targetPosX, targetPosY+1);
            valueBR = getPixel(targetPosX+1, targetPosY+1);
                    
            newValue = (byte)(
                        valueTL*(targetPosX+1-transformedPosX)*(targetPosY+1-transformedPosY) + 
                        valueTR*(transformedPosX-targetPosX)*(targetPosY+1-transformedPosY) + 
                        valueBL*(targetPosX+1-transformedPosX)*(transformedPosY-targetPosY) + 
                        valueBR*(transformedPosX-targetPosX)*(transformedPosY-targetPosY)
                        );
        }
    
        return newValue;
    }
    private static byte clipValue(int v) {
        if(v < 0) {
            v = 0;
        }
        else if(v > 0xFF) {
            v = 0xFF;
        }
    
        return (byte)(v&0xFF);
    }
    private static byte clipValue(double v) {
        byte result;
    
        if(v < 0) {
            result = 0;
        }
        else if(v > 0xFF) {
            result = (byte)0xFF;
        }
        else {
            result = (byte)v;
        }
    
        return result;
    }

    private byte [] pixels;

}
