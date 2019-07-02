package karbonator.math.geom2d;

public class Rectangle2D {

    public Point2D getStartPoint() {
        return new Point2D(start);
    }
    public Point2D getEndPoint() {
        return new Point2D(end);
    }
    public Point2D getCenterPoint() {
        float centerX = start.getX()+(getWidth()/2);
        float centerY = start.getY()+(getHeight()/2);
        return new Point2D(centerX, centerY);
    }
    public float getTop() {
        return start.getY();
    }
    public float getLeft() {
        return start.getX();
    }
    public float getBottom() {
        return end.getY();
    }
    public float getRight() {
        return end.getX();
    }
    public float getWidth() {
        return end.getX()-start.getX();
    }
    public float getHeight() {
        return end.getY()-start.getY();
    }
    
    public void setStartPoint(Point2D o) {
        start = new Point2D(o);
    }
    public void setEndPoint(Point2D o) {
        end = new Point2D(o);
    }

    /*
    public void moveTo(Point2D position) {
        Vector3D size = end.sub(start).multiply(0.5f);
        start = position.sub(size);
        end = position.add(size);
    }
    public void move(Vector3D velocity) {
        start.add(velocity);
        end.add(velocity);
    }
    */

    public void normalize() {
        float startX = start.getX();
        float endX = end.getX();
        float startY = start.getY();
        float endY = end.getY();
        float minX;
        float maxX;
        float minY;
        float maxY;
        
        if(startX < endX) {
            minX = startX;
            maxX = endX;
        }
        else {
            minX = endX;
            maxX = startX;
        }

        if(startY < endY) {
            minY = startY;
            maxY = endY;
        }
        else {
            minY = endY;
            maxY = startY;
        }
        
        start = new Point2D(minX, minY);
        end = new Point2D(maxX, maxY);
    }

    public boolean collidesWith(Rectangle2D o) {
        Rectangle2D rectA;
        Rectangle2D rectB;

        float minA;
        float maxA;
        float minB;
        float maxB;
        
        ////////////////////////////////
        //X Axis

        if(start.getX() < o.start.getX()) {
            rectA = this;
            rectB = o;
        }
        else {
            rectA = o;
            rectB = this;
        }

        minA = rectA.start.getX();
        maxA = rectA.end.getX();
        minB = rectB.start.getX();
        maxB = rectB.end.getX();

        if(minA > maxB || maxA < minB) {
            return false;
        }

        ////////////////////////////////

        ////////////////////////////////
        //Y Axis
        
        if(start.getY() < o.start.getY()) {
            rectA = this;
            rectB = o;
        }
        else {
            rectA = o;
            rectB = this;
        }

        minA = rectA.start.getY();
        maxA = rectA.end.getY();
        minB = rectB.start.getY();
        maxB = rectB.end.getY();
        
        if(minA > maxB || maxA < minB) {
            return false;
        }
        
        ////////////////////////////////
        
        return true;
    }
    
    public Rectangle2D() {
        start = new Point2D(0, 0);
        end = new Point2D(0, 0);
    }
    public Rectangle2D(Rectangle2D o) {
        start = new Point2D(o.start);
        end = new Point2D(o.end);
    }
    public Rectangle2D(Point2D topLeft, Point2D bottomRight) {
        start = new Point2D(topLeft);
        end = new Point2D(bottomRight);
    }
    /*
    public Rectangle2D(Point2D topLeft, Vector3D size) {
        start = new Point2D(topLeft);
        end = start.add(size);
    }
    */

    private Point2D start;
    private Point2D end;

}
