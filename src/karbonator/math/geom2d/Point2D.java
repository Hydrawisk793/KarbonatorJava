package karbonator.math.geom2d;

public class Point2D {

    public float getX() {
        return x;
    }
    public void setX(float v) {
        x = v;
    }
    public float getY() {
        return y;
    }
    public void setY(float v) {
        y = v;
    }
    
    public Point2D() {
        this(0, 0);
    }
    public Point2D(Point2D o) {
        this(o.x, o.y);
    }
    public Point2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    private float x;
    private float y;

}
