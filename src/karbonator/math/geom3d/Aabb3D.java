package karbonator.math.geom3d;

import karbonator.math.Utils;
import karbonator.util.Triplet;

/**
    @brief Axis Aligned Bounding Box in three-dimensional space.
    @date 2015/05/09
    @author Hydrawisk793
*/
public class Aabb3D {

    private static final int N_FACE = 6;
    private static final int N_VERTEX = 8;

    public Point3D getCenter() {
        return new Point3D(center);
    }
    public Aabb3D moveTo(Point3D o) {
        center.assign(o);
        
        return this;
    }
    public Aabb3D move(Vector3D o) {
        center.addAssign(o);
        
        return this;
    }
    
    public Vector3D getSize() {
        return new Vector3D(size);
    }
    public void setSize(Vector3D o) {
        size = new Vector3D(o);
    }
    
    public float getWidth() {
        return size.getX()*2;
    }
    public float getHeight() {
        return size.getY()*2;
    }
    public float getDepth() {
        return size.getZ()*2;
    }
    
    public Point3D getMinimumPoint() {
        return center.subtract(size);
    }
    public Point3D getMaximumPoint() {
        return center.add(size);
    }
    
    /**
        @brief Get all vertexes of AABB.
        @details The order of vertexes is defined as follows.<br/>
        &nbsp;&nbsp;&nbsp;2--------6<br/>
        &nbsp;&nbsp;/|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/|<br/>
        &nbsp;/&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/&nbsp;|<br/>
        /&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/&nbsp;&nbsp;|<br/>
        3--+----7&nbsp;&nbsp;&nbsp;|<br/>
        |&nbsp;&nbsp;0----|---4<br/>
        |&nbsp;/&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;/&nbsp;<br/>
        |/&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;/&nbsp;&nbsp;<br/>
        |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|/&nbsp;&nbsp;&nbsp;<br/>
        1-------5&nbsp;&nbsp;&nbsp;&nbsp;<br/>
    */
    public int getVertexCount() {
        return N_VERTEX;
    }
    public Point3D [] getVertexes() {
        Point3D min = getMinimumPoint();
        float minX = min.getX();
        float minY = min.getY();
        float minZ = min.getZ();
        Point3D max = getMaximumPoint();
        float maxX = max.getX();
        float maxY = max.getY();
        float maxZ = max.getZ();
        
        return new Point3D [] {
            new Point3D(minX, minY, minZ), 
            new Point3D(minX, minY, maxZ), 
            new Point3D(minX, maxY, minZ), 
            new Point3D(minX, maxY, maxZ), 
            new Point3D(maxX, minY, minZ), 
            new Point3D(maxX, minY, maxZ), 
            new Point3D(maxX, maxY, minZ), 
            new Point3D(maxX, maxY, maxZ), 
        };
    }
    public Point3D getVertexXnYnZn() {
        return getMinimumPoint();
    }
    public Point3D getVertexXnYnZp() {
        return new Point3D(center.getX()-size.getX(), center.getY()-size.getY(), center.getZ()+size.getZ());
    }
    public Point3D getVertexXnYpZn() {
        return new Point3D(center.getX()-size.getX(), center.getY()+size.getY(), center.getZ()-size.getZ());
    }
    public Point3D getVertexXnYpZp() {
        return new Point3D(center.getX()-size.getX(), center.getY()+size.getY(), center.getZ()+size.getZ());
    }
    public Point3D getVertexXpYnZn() {
        return new Point3D(center.getX()+size.getX(), center.getY()-size.getY(), center.getZ()-size.getZ());
    }
    public Point3D getVertexXpYnZp() {
        return new Point3D(center.getX()+size.getX(), center.getY()-size.getY(), center.getZ()+size.getZ());
    }
    public Point3D getVertexXpYpZn() {
        return new Point3D(center.getX()+size.getX(), center.getY()+size.getY(), center.getZ()-size.getZ());
    }
    public Point3D getVertexXpYpZp() {
        return getMaximumPoint();
    }

    public int getFaceCount() {
        return N_FACE;
    }
    /**
        @brief Get all faces of AABB.
        @details The order of faces is defined as follows.<br/>
        -yz, +yz, x-z, x+z, xy-, xy+
    */
    public Aabb3D [] getFaceAABBs() {
        Point3D [] vertexes = getVertexes();
    
        return new Aabb3D [] {
            new Aabb3D(vertexes[0], vertexes[3]), 
            new Aabb3D(vertexes[4], vertexes[7]), 
            new Aabb3D(vertexes[0], vertexes[5]), 
            new Aabb3D(vertexes[2], vertexes[7]), 
            new Aabb3D(vertexes[0], vertexes[6]), 
            new Aabb3D(vertexes[1], vertexes[7])
        };
    }
    /**
        @brief Get all faces of AABB.
        @details The order of faces is defined as follows.<br/>
        -yz, +yz, x-z, x+z, xy-, xy+
        @param epsilon Decrement amount of size.
    */
    public Aabb3D [] getFaceAABBs(float epsilon) {
        Vector3D yzSize = new Vector3D(0f, size.getY()-epsilon, size.getZ()-epsilon);
        Vector3D zxSize = new Vector3D(size.getX()-epsilon, 0f, size.getZ()-epsilon);
        Vector3D xySize = new Vector3D(size.getX()-epsilon, size.getY()-epsilon, 0f);
        
        return new Aabb3D [] {
            new Aabb3D(new Point3D(center.getX()-size.getX(), center.getY(), center.getZ()), yzSize), 
            new Aabb3D(new Point3D(center.getX()+size.getX(), center.getY(), center.getZ()), yzSize), 
            new Aabb3D(new Point3D(center.getX(), center.getY()-size.getY(), center.getZ()), zxSize), 
            new Aabb3D(new Point3D(center.getX(), center.getY()+size.getY(), center.getZ()), zxSize), 
            new Aabb3D(new Point3D(center.getX(), center.getY(), center.getZ()-size.getZ()), xySize), 
            new Aabb3D(new Point3D(center.getX(), center.getY(), center.getZ()+size.getZ()), xySize), 
        };
    }
    public Aabb3D getFaceXn() {
        Point3D min = getMinimumPoint();
        float minX = min.getX();
        float minY = min.getY();
        float minZ = min.getZ();
        Point3D max = getMaximumPoint();
        float maxY = max.getY();
        float maxZ = max.getZ();

        return new Aabb3D(new Point3D(minX, minY, minZ), new Point3D(minX, maxY, maxZ));
    }
    public Aabb3D getFaceXp() {
        Point3D min = getMinimumPoint();
        float minY = min.getY();
        float minZ = min.getZ();
        Point3D max = getMaximumPoint();
        float maxX = max.getX();
        float maxY = max.getY();
        float maxZ = max.getZ();
        
        return new Aabb3D(new Point3D(maxX, minY, minZ), new Point3D(maxX, maxY, maxZ));
    }
    public Aabb3D getFaceYn() {
        Point3D min = getMinimumPoint();
        float minX = min.getX();
        float minY = min.getY();
        float minZ = min.getZ();
        Point3D max = getMaximumPoint();
        float maxX = max.getX();
        float maxZ = max.getZ();
        
        return new Aabb3D(new Point3D(minX, minY, minZ), new Point3D(maxX, minY, maxZ));
    }
    public Aabb3D getFaceYp() {
        Point3D min = getMinimumPoint();
        float minX = min.getX();
        float minZ = min.getZ();
        Point3D max = getMaximumPoint();
        float maxX = max.getX();
        float maxY = max.getY();
        float maxZ = max.getZ();
        
        return new Aabb3D(new Point3D(minX, maxY, minZ), new Point3D(maxX, maxY, maxZ));
    }
    public Aabb3D getFaceZn() {
        Point3D min = getMinimumPoint();
        float minX = min.getX();
        float minY = min.getY();
        float minZ = min.getZ();
        Point3D max = getMaximumPoint();
        float maxX = max.getX();
        float maxY = max.getY();
        
        return new Aabb3D(new Point3D(minX, minY, minZ), new Point3D(maxX, maxY, minZ));
    }
    public Aabb3D getFaceZp() {
        Point3D min = getMinimumPoint();
        float minX = min.getX();
        float minY = min.getY();
        Point3D max = getMaximumPoint();
        float maxX = max.getX();
        float maxY = max.getY();
        float maxZ = max.getZ();
        
        return new Aabb3D(new Point3D(minX, minY, maxZ), new Point3D(maxX, maxY, maxZ));
    }
    
    public Parallelogram3D [] getFaceParallelograms(float epsilon) {
        Vector3D yzSize = new Vector3D(0f, size.getY()-epsilon, size.getZ()-epsilon);
        Vector3D zxSize = new Vector3D(size.getX()-epsilon, 0f, size.getZ()-epsilon);
        Vector3D xySize = new Vector3D(size.getX()-epsilon, size.getY()-epsilon, 0f);
        
        return new Parallelogram3D [] {
            new Parallelogram3D(new Point3D(center.getX()-size.getX(), center.getY(), center.getZ()), yzSize), 
            new Parallelogram3D(new Point3D(center.getX()+size.getX(), center.getY(), center.getZ()), yzSize), 
            new Parallelogram3D(new Point3D(center.getX(), center.getY()-size.getY(), center.getZ()), zxSize), 
            new Parallelogram3D(new Point3D(center.getX(), center.getY()+size.getY(), center.getZ()), zxSize), 
            new Parallelogram3D(new Point3D(center.getX(), center.getY(), center.getZ()-size.getZ()), xySize), 
            new Parallelogram3D(new Point3D(center.getX(), center.getY(), center.getZ()+size.getZ()), xySize), 
        };
    }
    public Vector3D [] getFaceNormals() {
        return new Vector3D [] {
            new Vector3D(-size.getX(), 0f, 0f), 
            new Vector3D(size.getX(), 0f, 0f), 
            new Vector3D(0f, -size.getY(), 0f), 
            new Vector3D(0f, size.getY(), 0f), 
            new Vector3D(0f, 0f, -size.getZ()), 
            new Vector3D(0f, 0f, size.getZ()), 
        };
    }
    public Parallelogram3D [] getFaceParallelograms() {
        Point3D [] vertexes = getVertexes();
        
        return new Parallelogram3D [] {
            new Parallelogram3D(vertexes[2], vertexes[6], vertexes[0]), //Plane XnYn
            new Parallelogram3D(vertexes[3], vertexes[1], vertexes[7]), //Plane XpYp
            new Parallelogram3D(vertexes[3], vertexes[2], vertexes[1]), //Plane YnZn
            new Parallelogram3D(vertexes[7], vertexes[5], vertexes[6]), //Plane YpZp
            new Parallelogram3D(vertexes[0], vertexes[4], vertexes[1]), //Plane ZnXn
            new Parallelogram3D(vertexes[7], vertexes[6], vertexes[3])  //Plane ZpXp
        };
    }
    public Plane3D [] getFacePlanes() {
        Point3D minPoint = getMinimumPoint();
        Point3D maxPoint = getMaximumPoint();
    
        return new Plane3D [] {
             new Plane3D(new Point3D(0.0f, 0.0f, minPoint.getZ()), new Vector3D(0.0f, 0.0f, -1.0f)), 
             new Plane3D(new Point3D(0.0f, 0.0f, maxPoint.getZ()), new Vector3D(0.0f, 0.0f, 1.0f)), 
             new Plane3D(new Point3D(minPoint.getX(), 0.0f, 0.0f), new Vector3D(-1.0f, 0.0f, 0.0f)), 
             new Plane3D(new Point3D(maxPoint.getX(), 0.0f, 0.0f), new Vector3D(1.0f, 0.0f, 0.0f)), 
             new Plane3D(new Point3D(0.0f, minPoint.getY(), 0.0f), new Vector3D(0.0f, -1.0f, 0.0f)), 
             new Plane3D(new Point3D(0.0f, maxPoint.getY(), 0.0f), new Vector3D(0.0f, 1.0f, 0.0f)) 
        };
    }
    /*
    public HPoint3D [] getFaceVertexes(int faceIndex) {
        HPoint3D [] vertexes = getVertexes();
    
        switch(faceIndex) {
        case 0:
            return new HPoint3D [] {
                vertexes[4], vertexes[0], vertexes[2], vertexes[6]
            };
        case 1:
            return new HPoint3D [] {
                vertexes[1], vertexes[5], vertexes[7], vertexes[3]
            };
        case 2:
             return new HPoint3D [] {
                vertexes[0], vertexes[1], vertexes[3], vertexes[2]
            };
        case 3:
            return new HPoint3D [] {
                vertexes[5], vertexes[4], vertexes[6], vertexes[7]
            };
        case 4:
            return new HPoint3D [] {
                vertexes[5], vertexes[1], vertexes[0], vertexes[4]
            };
        case 5:
            return new HPoint3D [] {
                vertexes[2], vertexes[3], vertexes[7], vertexes[6]
            };
        default:
            throw new HIndexOutOfBoundsException();
        }
    }
    */
    public LineSegment3D [] getEdges() {
        Point3D [] vertexes = getVertexes();
        
        return new LineSegment3D [] {
            new LineSegment3D(vertexes[0], vertexes[1]), 
            new LineSegment3D(vertexes[1], vertexes[3]), 
            new LineSegment3D(vertexes[3], vertexes[2]), 
            new LineSegment3D(vertexes[2], vertexes[0]),
            new LineSegment3D(vertexes[4], vertexes[5]), 
            new LineSegment3D(vertexes[5], vertexes[7]), 
            new LineSegment3D(vertexes[7], vertexes[6]), 
            new LineSegment3D(vertexes[6], vertexes[4]), 
            new LineSegment3D(vertexes[0], vertexes[4]), 
            new LineSegment3D(vertexes[1], vertexes[5]), 
            new LineSegment3D(vertexes[2], vertexes[6]), 
            new LineSegment3D(vertexes[3], vertexes[7])
        };
    }
    
    public Triplet<Boolean, Float, Float> intersects(Ray3D ray) {
        return ray.intersects(this);
    }
    public Triplet<Boolean, Float, Float> intersects(LineSegment3D lineSegment) {
        return lineSegment.intersects(this);
    }
    public boolean intersects(Aabb3D o, float epsilon) {
        Point3D minPointA = getMinimumPoint();
        Point3D maxPointA = getMaximumPoint();
        Point3D minPointB = o.getMinimumPoint();
        Point3D maxPointB = o.getMaximumPoint();
        float minA = 0.0f;
        float maxA = 0.0f;
        float minB = 0.0f;
        float maxB = 0.0f;

        ////////////////////////////////
        //Axis X

        if(minPointA.getX() < minPointB.getX()) {
            minA = minPointA.getX();
            maxA = maxPointA.getX();
            minB = minPointB.getX();
            maxB = maxPointB.getX();
        }
        else {
            minB = minPointA.getX();
            maxB = maxPointA.getX();
            minA = minPointB.getX();
            maxA = maxPointB.getX();
        }

        if(!Utils.intersects(minA, maxA, minB, maxB, epsilon)) {
            return false;
        }

        ////////////////////////////////

        ////////////////////////////////
        //Axis Y
        
        if(minPointA.getY() < minPointB.getY()) {
            minA = minPointA.getY();
            maxA = maxPointA.getY();
            minB = minPointB.getY();
            maxB = maxPointB.getY();
        }
        else {
            minB = minPointA.getY();
            maxB = maxPointA.getY();
            minA = minPointB.getY();
            maxA = maxPointB.getY();
        }
        
        if(!Utils.intersects(minA, maxA, minB, maxB, epsilon)) {
            return false;
        }
        
        ////////////////////////////////
        
        ////////////////////////////////
        //Axis Z
        
        if(minPointA.getZ() < minPointB.getZ()) {
            minA = minPointA.getZ();
            maxA = maxPointA.getZ();
            minB = minPointB.getZ();
            maxB = maxPointB.getZ();
        }
        else {
            minB = minPointA.getZ();
            maxB = maxPointA.getZ();
            minA = minPointB.getZ();
            maxA = maxPointB.getZ();
        }
        
        if(!Utils.intersects(minA, maxA, minB, maxB, epsilon)) {
            return false;
        }
        
        ////////////////////////////////
        
        return true;
    }
    public boolean intersects(Triangle3D triangle, float epsilon) {
        return triangle.intersects(this, epsilon);
    }
    
    public Aabb3D assign(Aabb3D o) {
        if(this != o) {
            center.assign(o.center);
            size.assign(o.size);
        }
        
        return this;
    }
    
    @Override
    public String toString() {
        return String.format("[%s, %s]", center, size);
    }
    
    public Aabb3D() {
        center = new Point3D();
        size = new Vector3D();
    }
    public Aabb3D(Aabb3D o) {
        this.center = new Point3D(o.center);
        this.size = new Vector3D(o.size);
    }
    public Aabb3D(Vector3D size) {
        this(new Point3D(), size);
    }
    public Aabb3D(Point3D center, Vector3D size) {
        this.center = new Point3D(center);
        this.size = size.pointwiseAbsolute();
    }
    public Aabb3D(Point3D minPoint, Point3D maxPoint) {
        center = new Point3D((maxPoint.getX()+minPoint.getX())*0.5f, (maxPoint.getY()+minPoint.getY())*0.5f, (maxPoint.getZ()+minPoint.getZ())*0.5f);
        size = maxPoint.subtract(center);
    }

    private Point3D center;
    private Vector3D size;

}
