package karbonator.math.geom3d;

import karbonator.math.Utils;
import karbonator.util.Pair;

public class Parallelogram3D {

    public Point3D getVertexZero() {
        return new Point3D(p0);
    }
    public Point3D getVertexFirst() {
        return new Point3D(p1);
    }
    public Point3D getVertexSecond() {
        return new Point3D(p2);
    }
    public Point3D [] getVertexes() {
        return new Point3D [] {
            new Point3D(p0), 
            new Point3D(p1), 
            new Point3D(p2), 
            p1.subtract(e20)
        };
    }
    public Vector3D getZeroToFirst() {
        return new Vector3D(e01);
    }
    public Vector3D getSecondToZero() {
        return new Vector3D(e20);
    }
    public Vector3D getZeroToSecond() {
        return e20.negate();
    }
    public Vector3D getFirstToZero() {
        return e01.negate();
    }
    public Vector3D getNormal() {
        return new Vector3D(normal);
    }
    public Vector3D getUnitNormal() {
        return new Vector3D(unitNormal);
    }

    public void move(Vector3D o) {
        p0.add(o);
        p1.add(o);
        p2.add(o);
    }
    
    public Pair<Boolean, Float> intersects(Ray3D o) {
        Vector3D negRayDir = o.getDirection().negateAssign();
        float det = negRayDir.dot(normal);

        if(Utils.isZero(det)) {
            return new Pair<Boolean, Float>(false, 0.0f);
        }
        
        float invDet = Utils.inverse(det);
        Vector3D vecT = o.getStartPoint().subtract(p0);
        Vector3D vecTCrossNegRayDir = vecT.cross(negRayDir);

        float e01Param = getSecondToZero().dot(vecTCrossNegRayDir)*invDet;
        if(e01Param < 0.0f || e01Param > 1.0f) {
            return new Pair<Boolean, Float>(false, 0.0f);
        }
        
        float e02Param = e01.dot(vecTCrossNegRayDir)*invDet;
        if( e02Param < 0.0f || e02Param > 1.0f) {
            return new Pair<Boolean, Float>(false, 0.0f);
        }

        float lineParam = vecT.dot(normal)*invDet;

        return new Pair<Boolean, Float>((lineParam >= 0.0f), lineParam);
    }
    public Pair<Boolean, Float> intersects(LineSegment3D o) {
        Vector3D negRayDir = o.getDirection().negateAssign();
        float det = negRayDir.dot(normal);

        if(Utils.isZero(det)) {
            return new Pair<Boolean, Float>(false, 0.0f);
        }
        
        float invDet = Utils.inverse(det);
        Vector3D vecT = o.getStartPoint().subtract(p0);
        Vector3D vecTCrossNegRayDir = vecT.cross(negRayDir);

        float e01Param = getSecondToZero().dot(vecTCrossNegRayDir)*invDet;
        if(e01Param < 0.0f || e01Param > 1.0f) {
            return new Pair<Boolean, Float>(false, 0.0f);
        }
        
        float e02Param = e01.dot(vecTCrossNegRayDir)*invDet;
        if( e02Param < 0.0f || e02Param > 1.0f) {
            return new Pair<Boolean, Float>(false, 0.0f);
        }

        float lineParam = vecT.dot(normal)*invDet;

        return new Pair<Boolean, Float>((lineParam >= 0.0f && lineParam <= 1.0f), lineParam);
    }
    
    public Parallelogram3D(Parallelogram3D o) {
        this(o.p0, o.p1, o.p2);
    }
    public Parallelogram3D(Vector3D toFirst, Vector3D toSecond) {
        this(new Point3D(), toFirst, toSecond);
    }
    public Parallelogram3D(Point3D zero, Vector3D size) {
        this(new Point3D(zero), size.negate(), new Vector3D(size));
    }
    public Parallelogram3D(Point3D zero, Vector3D toFirst, Vector3D toSecond) {
        this(new Point3D(zero), zero.add(toFirst), zero.add(toSecond));
    }
    public Parallelogram3D(Point3D first, Point3D second) {
        this(new Point3D(), first, second);
    }
    public Parallelogram3D(Point3D zero, Point3D first, Point3D second) {
        this.p0 = new Point3D(zero);
        this.p1 = new Point3D(first);
        this.p2 = new Point3D(second);
        e01 = p1.subtract(p0);
        e20 = p0.subtract(p2);
        normal = e01.cross(getZeroToSecond());
        if(normal.isZero()) {
            unitNormal = new Vector3D();
        }
        else {
            unitNormal = normal.normalize();
        }
    }
    
    private Point3D p0;
    private Point3D p1;
    private Point3D p2;
    
    private Vector3D e01;
    private Vector3D e20;
    private Vector3D normal;
    private Vector3D unitNormal;
    
}
