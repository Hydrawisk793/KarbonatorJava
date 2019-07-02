package karbonator.image;

import karbonator.math.geom2d.Rectangle2D;

public class ObjectLabel {

    public int getArea() {
        return area;
    }
    public Rectangle2D getRectangle() {
        return new Rectangle2D(rect);
    }
    
    public void setArea(int v) {
        area = v;
    }
    public void setRectangle(Rectangle2D o) {
        rect = new Rectangle2D(o);
    }

    public ObjectLabel() {
        area = 0;
        rect = new Rectangle2D();
    }
    public ObjectLabel(ObjectLabel o) {
        area = o.area;
        rect = new Rectangle2D(o.rect);
    }

    private int area;
    private Rectangle2D rect;

}
