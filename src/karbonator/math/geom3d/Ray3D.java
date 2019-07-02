package karbonator.math.geom3d;

import karbonator.math.Utils;
import karbonator.util.Pair;
import karbonator.util.Triplet;

public class Ray3D {

    public Point3D getStartPoint() {
        return new Point3D(start);
    }
    public Vector3D getDirection() {
        return new Vector3D(direction);
    }
    public Point3D getPoint(float parameter) {
        return start.add(direction.multiply(parameter));
    }
    
    public void setStartPoint(Point3D o) {
        start = new Point3D(o);
    }
    public void setDirection(Vector3D o) {
        direction = new Vector3D(o);
    }
    
    public void moveTo(Point3D o) {
        start = new Point3D(o);
    }
    public void move(Vector3D o) {
        start.addAssign(o);
    }
    
    public Triplet<Boolean, Float, Float> intersects(Aabb3D aabb) {
        float maxMinParam = -Float.MAX_VALUE;
        float minMaxParam = Float.MAX_VALUE;
        
        Point3D minAABBPoint = aabb.getMinimumPoint();
        Point3D maxAABBPoint = aabb.getMaximumPoint();
        
        float [] lineAxisStartPoints = start.toArray();
        float [] lineAxisDirections = direction.toArray();
        float [] minAABBAxisPositions = minAABBPoint.toArray();
        float [] maxAABBAxisPositions = maxAABBPoint.toArray();
        
        float minParam = 0.0f;
        float maxParam = 0.0f;
        
        ////////////////////////////////
        //Axis r1
        
        for(int r1=0; r1<3; ++r1) {
            if(Utils.isZero(lineAxisDirections[r1])) {
                if(lineAxisStartPoints[r1] < minAABBAxisPositions[r1] || lineAxisStartPoints[r1] > maxAABBAxisPositions[r1]) {
                    return new Triplet<Boolean, Float, Float>(false, 0.0f, 0.0f);
                }
            }
            else {
                float invLineAxisDirection = Utils.inverse(lineAxisDirections[r1]);
                minParam = (minAABBAxisPositions[r1] - lineAxisStartPoints[r1])*invLineAxisDirection;
                maxParam = (maxAABBAxisPositions[r1] - lineAxisStartPoints[r1])*invLineAxisDirection;
                
                if(minParam > maxParam) {
                    float temp = minParam;
                    minParam = maxParam;
                    maxParam = temp;
                }
                
                if(maxMinParam < minParam) {
                    maxMinParam  = minParam;
                }
                if(minMaxParam > maxParam) {
                    minMaxParam  = maxParam;
                }
                
                if(minMaxParam < 0.0f || maxMinParam > minMaxParam) {
                    return new Triplet<Boolean, Float, Float>(false, 0.0f, 0.0f);
                }
            }
        }
        
        ////////////////////////////////
    
        return new Triplet<Boolean, Float, Float>(true, maxMinParam, minMaxParam);
    }
    public Pair<Boolean, Float> intersects(Triangle3D triangle) {
        return triangle.intersects(this);
    }
    public Pair<Boolean, Float> intersects(Parallelogram3D parallelogram) {
        return parallelogram.intersects(this);
    }
    
    public Ray3D(Ray3D o) {
        this(o.start, o.direction);
    }
    public Ray3D(Vector3D direction) {
        this.start = new Point3D();
        this.direction = new Vector3D(direction);
    }
    public Ray3D(Point3D start, Vector3D direction) {
        this.start = new Point3D(start);
        this.direction = new Vector3D(direction);
    }
    public Ray3D(Point3D start, Point3D end) {
        this.start = new Point3D(start);
        this.direction = end.subtract(start);
    }

    private Point3D start;
    private Vector3D direction;

}
