package karbonator.math.geom3d;

public class RectangleRegion3D {

    public Vector3D getTopLeft() {
        return new Vector3D(topLeft);
    }
    public Vector3D getBottomRight() {
        return new Vector3D(bottomRight);
    }

    public void setTopLeft(Vector3D o) {
        topLeft = new Vector3D(o);
    }
    public void setBottomRight(Vector3D o) {
        bottomRight = new Vector3D(o);
    }

    public void set(RectangleRegion3D other) {
        topLeft = other.topLeft;
        bottomRight = other.bottomRight;
    }
    
    public void set(Vector3D topLeft, Vector3D bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    public void normalize() {
        float startX = topLeft.getX();
        float endX = bottomRight.getX();
        float startY = topLeft.getY();
        float endY = bottomRight.getY();
        float startZ = topLeft.getZ();
        float endZ = bottomRight.getZ();
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
        
        topLeft = new Vector3D(minX, minY, minZ);
        bottomRight = new Vector3D(maxX, maxY, maxZ);
    }

    public Rectangle3D getRectangleAt(Point3D pos) {
        return new Rectangle3D(pos.add(topLeft), pos.add(bottomRight));
    }

    public RectangleRegion3D() {
        topLeft = new Vector3D(0, 0, 0);
        bottomRight = new Vector3D(0, 0, 0);
    }
    public RectangleRegion3D(RectangleRegion3D o) {
        topLeft = new Vector3D(o.topLeft);
        bottomRight = new Vector3D(o.bottomRight);
    }
    public RectangleRegion3D(Vector3D topLeft, Vector3D bottomRight) {
        this.topLeft = new Vector3D(topLeft);
        this.bottomRight = new Vector3D(bottomRight);
    }

    private Vector3D topLeft;
    private Vector3D bottomRight;

}
