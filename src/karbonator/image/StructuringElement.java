package karbonator.image;

import karbonator.math.geom2d.Point2D;

public class StructuringElement {

    public Point2D getPosition() {
        return new Point2D(pos);
    }
    public byte getPixel() {
        return value;
    }

    public void setPosition(Point2D o) {
        pos = new Point2D(o);
    }
    public void setPixel(byte v) {
        value = v;
    }

    public StructuringElement() {
        pos = new Point2D();
        value = 0;
    }
    public StructuringElement(StructuringElement o) {
        pos = new Point2D(o.pos);
        value = o.value;
    }
    public StructuringElement(Point2D pos, byte value) {
        this.pos = new Point2D(pos);
        this.value = value;
    }

    private Point2D pos;
    private byte value;

}
