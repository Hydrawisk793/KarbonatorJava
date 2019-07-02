package karbonator.image;

import karbonator.collection.Vector;
import karbonator.math.geom2d.Rectangle2D;

public class LabeledBitmap extends ArgbBitmap {
 
    public ObjectLabel getLabel(int id) {
        return new ObjectLabel(labels.at(id));
    }
    public int getLabelCount() {
        return labels.getSize();
    }
    public GrayBitmap getObjectImage(int labelId) {
        final int width = getWidth();
        ObjectLabel label = new ObjectLabel(labels.at(labelId++));
        Rectangle2D rect = label.getRectangle();
        final int OBJECT_WIDTH = (int)rect.getWidth();
        final int OBJECT_HEIGHT = (int)rect.getHeight();
        GrayBitmap objectImage = new GrayBitmap(OBJECT_WIDTH, OBJECT_HEIGHT);
        int absX;
        int absY = (int)rect.getTop();
    
        objectImage.setPixel((byte)0);
    
        for(int y=0;y<OBJECT_HEIGHT;++y) {
            absX = (int)rect.getLeft();
    
            for(int x=0;x<OBJECT_WIDTH;++x) {
                if( absX >= 0 && absX < width &&
                    absY >= 0 && absY < width)
                {
                    objectImage.setPixel(x, y, (byte)((getPixel(absX, absY) == labelId)?0xFF:0x00));
                }
    
                ++absX;
            }
    
            ++absY;
        }
    
        return objectImage;
    }

    public void setPixel(int v) {
        final int SIZE = getSize();
    
        for(int r1=0;r1<SIZE;++r1) {
            setPixel(r1, v);
        }
    }
    public void setLabel(int id, ObjectLabel o) {
        labels.set(id, new ObjectLabel(o));
    }

    public void addLabel(ObjectLabel o) {
        labels.pushBack(new ObjectLabel(o));
    }
    public void removeLabel(int id) {
        labels.remove(id);
    }

    public LabeledBitmap() {
        this(1, 1);
    }
    public LabeledBitmap(int width, int height) {
        super(width, height);

        labels = new Vector<ObjectLabel>();
    }

    private Vector<ObjectLabel> labels;

}
