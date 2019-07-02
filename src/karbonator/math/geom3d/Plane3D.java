package karbonator.math.geom3d;

import karbonator.math.Utils;
import karbonator.util.Pair;

public class Plane3D {

    public Point3D getVertexZero() {
        return new Point3D(p0);
    }
    public Vector3D getNormal() {
        return new Vector3D(normal);
    }
    public Vector3D getUnitNormal() {
        return new Vector3D(unitNormal);
    }

    public float distance(Point3D o) {
        return o.subtract(p0).dot(unitNormal);
    }

    public Pair<Boolean, Float> intersects(Ray3D o) {
        Vector3D negRayDir = o.getDirection().negate();
        float det = negRayDir.dot(normal);

        if(Utils.isZero(det)) { 
            return new Pair<Boolean, Float>(false, 0.0f);
        }
        
        float invDet = Utils.inverse(det);
        Vector3D vecT = o.getStartPoint().subtract(p0);

        float lineParam = vecT.dot(normal)*invDet;

        return new Pair<Boolean, Float>(lineParam >= 0.0f, lineParam);
    }
    public Pair<Boolean, Float> intersects(LineSegment3D o) {
        Vector3D negRayDir = o.getDirection().negate();
        float det = negRayDir.dot(normal);

        if(Utils.isZero(det)) { 
            return new Pair<Boolean, Float>(false, 0.0f);
        }
        
        float invDet = Utils.inverse(det);
        Vector3D vecT = o.getStartPoint().subtract(p0);

        float lineParam = vecT.dot(normal)*invDet;

        return new Pair<Boolean, Float>((lineParam >= 0.0f && lineParam <= 1.0f), lineParam);
    }

    public Plane3D(Plane3D o) {
        this(o.p0, o.normal);
    }
    public Plane3D(Vector3D normal) {
        this(new Point3D(), normal);
    }
    public Plane3D(Point3D point, Vector3D normal) {
        p0 = new Point3D(point);
        this.normal = new Vector3D(normal);
        
        if(normal.isZero()) {
            unitNormal = new Vector3D();
        }
        else {
            unitNormal = normal.normalize();
        }
    }
    
    protected Point3D p0;
    protected Vector3D normal;
    protected Vector3D unitNormal;

}
