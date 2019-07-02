package karbonator.math.geom2d;

public class Vector2D {

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
    
    public Vector2D addAssign(Vector2D o) {
        x += o.x;
        y += o.y;
        
        return this;
    }
    public Vector2D subtractAssign(Vector2D o) {
        x -= o.x;
        y -= o.y;
        
        return this;
    }
    public Vector2D multiplyAssign(float v) {
        x *= v;
        y *= v;
        
        return this;
    }
    
    public Vector2D() {
        this(0, 0);
    }
    public Vector2D(Vector2D o) {
        this(o.x, o.y);
    }
    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    private float x;
    private float y;

}
