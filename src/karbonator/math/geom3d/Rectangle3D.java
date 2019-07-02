package karbonator.math.geom3d;

public class Rectangle3D {

    public Point3D getStartPoint() {
        return new Point3D(start);
    }
    public Point3D getEndPoint() {
        return new Point3D(end);
    }
    public Point3D getCenterPoint() {
        return new Point3D(
            (start.getX()+end.getX())/2,
            (start.getY()+end.getY())/2,
            (start.getY()+end.getY())/2
        );
    }
    
    public void setStartPoint(Point3D o) {
        start = new Point3D(o);
    }
    public void setEndPoint(Point3D o) {
        end = new Point3D(o);
    }

    public void moveTo(Point3D position) {
        Vector3D size = end.subtract(start).multiply(0.5f);
        start = position.subtract(size);
        end = position.add(size);
    }
    public void move(Vector3D velocity) {
        start.add(velocity);
        end.add(velocity);
    }

    public void normalize() {
        float startX = start.getX();
        float endX = end.getX();
        float startY = start.getY();
        float endY = end.getY();
        float startZ = start.getZ();
        float endZ = end.getZ();
        float minX;
        float maxX;
        float minY;
        float maxY;
        float minZ;
        float maxZ;
        
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
        
        if(startZ < endZ) {
            minZ = startZ;
            maxZ = endZ;
        }
        else {
            minZ = endZ;
            maxZ = startZ;
        }
        
        start = new Point3D(minX, minY, minZ);
        end = new Point3D(maxX, maxY, maxZ);
    }

    public boolean collidesWith(Rectangle3D o) {
        Rectangle3D rectA;
        Rectangle3D rectB;

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
        
        ////////////////////////////////
        //Z Axis
        
        if(start.getZ() < o.start.getZ()) {
            rectA = this;
            rectB = o;
        }
        else {
            rectA = o;
            rectB = this;
        }
        
        minA = rectA.start.getZ();
        maxA = rectA.end.getZ();
        minB = rectB.start.getZ();
        maxB = rectB.end.getZ();
        
        if(minA > maxB || maxA < minB) {
            return false;
        }
        
        ////////////////////////////////
        
        return true;
    }
    
    public Rectangle3D() {
        start = new Point3D(0, 0, 0);
        end = new Point3D(0, 0, 0);
    }
    public Rectangle3D(Rectangle3D o) {
        start = new Point3D(o.start);
        end = new Point3D(o.end);
    }
    public Rectangle3D(Point3D topLeft, Point3D bottomRight) {
        start = new Point3D(topLeft);
        end = new Point3D(bottomRight);
    }
    public Rectangle3D(Point3D topLeft, Vector3D size) {
        start = new Point3D(topLeft);
        end = start.add(size);
    }

    private Point3D start;
    private Point3D end;

}
