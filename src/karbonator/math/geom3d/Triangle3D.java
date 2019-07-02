package karbonator.math.geom3d;

import karbonator.math.Utils;
import karbonator.util.Pair;
import karbonator.util.Triplet;

public class Triangle3D {

    public Point3D [] getVertexes() {
        return new Point3D [] {
            new Point3D(p0), 
            new Point3D(p1), 
            new Point3D(p2)
        };
    }
    public Point3D getVertexZero() {
        return new Point3D(p0);
    }
    public Point3D getVertexFirst() {
        return new Point3D(p1);
    }
    public Point3D getVertexSecond() {
        return new Point3D(p2);
    }
    public Vector3D getZeroToFirst() {
        return new Vector3D(e01);
    }
    public Vector3D getFirstToSecond() {
        return new Vector3D(e12);
    }
    public Vector3D getSecondToZero() {
        return new Vector3D(e20);
    }
    public Vector3D getZeroToSecond() {
        return e20.negate();
    }
    public Vector3D getSecondToFirst() {
        return e12.negate();
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
    public Aabb3D getAABB() {
        float minX = p0.getX();
        float minY = p0.getY();
        float minZ = p0.getZ();
        float maxX = p0.getX();
        float maxY = p0.getY();
        float maxZ = p0.getZ();

        float currentX = p1.getX();
        float currentY = p1.getY();
        float currentZ = p1.getZ();
        
        if(minX > currentX) {
            minX = currentX;
        }
        if(minY > currentY) {
            minY = currentY;
        }
        if(minZ > currentZ) {
            minZ = currentZ;
        }

        if(maxX < currentX) {
            maxX = currentX;
        }
        if(maxY < currentY) {
            maxY = currentY;
        }
        if(maxZ < currentZ) {
            maxZ = currentZ;
        }

        currentX = p2.getX();
        currentY = p2.getY();
        currentZ = p2.getZ();
        
        if(minX > currentX) {
            minX = currentX;
        }
        if(minY > currentY) {
            minY = currentY;
        }
        if(minZ > currentZ) {
            minZ = currentZ;
        }

        if(maxX < currentX) {
            maxX = currentX;
        }
        if(maxY < currentY) {
            maxY = currentY;
        }
        if(maxZ < currentZ) {
            maxZ = currentZ;
        }
        
        return new Aabb3D(new Point3D(minX, minY, minZ), new Point3D(maxX, maxY, maxZ));
    }
    
    public void move(Vector3D o) {
        p0.add(o);
        p1.add(o);
        p2.add(o);
    }
    
    public Triplet<Boolean, Float, Float> intersects(Point3D point) {
        if(!Utils.isZero(point.subtract(p0).dot(normal))) {
            return new Triplet<Boolean, Float, Float>(false, 0.0f, 0.0f);
        }
        
        Vector3D zeroToFirst = getZeroToFirst();
        Vector3D zeroToSecond = getZeroToSecond();
        Vector3D zeroToPoint = point.subtract(p0);
        
        if(normal.isZero()) {
            return new Triplet<Boolean, Float, Float>(false, 0.0f, 0.0f);
        }
        
        float invNormalDotNormal = Utils.inverse(normal.dot(normal));
        float paramFirst = zeroToFirst.cross(zeroToPoint).dot(normal) * invNormalDotNormal;
        if(paramFirst < 0.0f || paramFirst > 1.0f) {
            return new Triplet<Boolean, Float, Float>(false, 0.0f, 0.0f);
        }
        
        float paramSecond = zeroToPoint.cross(zeroToSecond).dot(normal) * invNormalDotNormal;
        if(paramSecond < 0.0f || paramSecond > 1.0f) {
            return new Triplet<Boolean, Float, Float>(false, 0.0f, 0.0f);
        }
        
        return new Triplet<Boolean, Float, Float>((paramFirst+paramSecond) <= 1.0f, paramFirst, paramSecond);
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
        if( e02Param < 0.0f || e02Param > 1.0f || ((e01Param+e02Param) > 1.0f)) {
            return new Pair<Boolean, Float>(false, 0.0f);
        }
        
        float lineParam = vecT.dot(normal)*invDet;
        
        return new Pair<Boolean, Float>(lineParam >= 0.0f, lineParam);
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
        if( e02Param < 0.0f || e02Param > 1.0f || ((e01Param+e02Param) > 1.0f)) {
            return new Pair<Boolean, Float>(false, 0.0f);
        }
        
        float lineParam = vecT.dot(normal)*invDet;
        
        return new Pair<Boolean, Float>((lineParam >= 0.0f && lineParam <= 1.0f), lineParam);
    }
    public boolean intersects(Aabb3D aabb, float epsilon) {
        ////////////////////////////////
        //Axis X, Y, Z

        if(!aabb.intersects(getAABB(), epsilon)) {
            return false;//new HPair<Boolean, Float>(false, 0.0f);
        }
        
        ////////////////////////////////

        ////////////////////////////////
        //Axis N = e01 x e02, 

        final Vector3D aabbSize = aabb.getSize();
        final Point3D aabbCenter = aabb.getCenter();
        final Vector3D triV0 = p0.subtract(aabbCenter);
        final Vector3D triV1 = p1.subtract(aabbCenter);
        final Vector3D triV2 = p2.subtract(aabbCenter);
        final Vector3D triUnitNormal = getUnitNormal();
        
        float aabbRadius = 0.0f;
        float projectedTriV0 = 0.0f;
        float projectedTriV1 = 0.0f;
        float projectedTriV2 = 0.0f;
        float triMin = 0.0f;
        float triMax = 0.0f;
        
        //크기만 비교하면 되므로 '축의 norm 제곱'으로 나눌 필요가 없음.
        //따라서 dot Product만 사용하면 됨.

        //Project AABB(Get radius of AABB)
        //Radius를 양수로 만들기 위해서 축 벡터 원소들을 양수로 만듦
        aabbRadius = aabbSize.dot(triUnitNormal.pointwiseAbsolute());
        
        //Project Triangle
        projectedTriV0 = triV0.dot(triUnitNormal);
        projectedTriV1 = triV1.dot(triUnitNormal);
        projectedTriV2 = triV2.dot(triUnitNormal);
    
        //Find minimum and maximum
        triMin = projectedTriV0;
        triMax = projectedTriV0;
        
        if(triMin > projectedTriV1) {
            triMin = projectedTriV1;
        }
        if(triMax < projectedTriV1) {
            triMax = projectedTriV1;
        }

        if(triMin > projectedTriV2) {
            triMin = projectedTriV2;
        }
        if(triMax < projectedTriV2) {
            triMax = projectedTriV2;
        }
        
        //Test Collision
        if(!Utils.intersects(triMin, triMax, -aabbRadius, aabbRadius, epsilon)) {
            return false;
        }

        ///final float resultParam = aabbRadius+projectedTriV0;

        ////////////////////////////////

        ////////////////////////////////
        //Axis Axy(x = {ex, ey, ez}, y = {e01, e12, e20})

        final Vector3D [] axes = {
            new Vector3D(0.0f, -e01.getZ(), e01.getY()), 
            new Vector3D(0.0f, -e12.getZ(), e12.getY()), 
            new Vector3D(0.0f, -e20.getZ(), e20.getY()), 
            new Vector3D(e01.getZ(), 0.0f, -e01.getX()), 
            new Vector3D(e12.getZ(), 0.0f, -e12.getX()), 
            new Vector3D(e20.getZ(), 0.0f, -e20.getX()), 
            new Vector3D(-e01.getY(), e01.getX(), 0.0f), 
            new Vector3D(-e12.getY(), e12.getX(), 0.0f), 
            new Vector3D(-e20.getY(), e20.getX(), 0.0f)
        };

        for(Vector3D axis : axes) {
            //두 Edge가 서로 평행한 경우는 무시
            if(!axis.isZero()) {
                //크기만 비교하면 되므로 '축의 norm 제곱'으로 나눌 필요가 없음.
                //따라서 dot Product만 사용하면 됨.
    
                //Project AABB(Get radius of AABB)
                //Radius를 양수로 만들기 위해서 축 벡터 원소들을 양수로 만듦
                aabbRadius = aabbSize.dot(axis.pointwiseAbsolute());
                
                //Project Triangle
                projectedTriV0 = triV0.dot(axis);
                projectedTriV1 = triV1.dot(axis);
                projectedTriV2 = triV2.dot(axis);
                
                //Find minimum and maximum
                triMin = projectedTriV0;
                triMax = projectedTriV0;
                
                if(triMin > projectedTriV1) {
                    triMin = projectedTriV1;
                }
                if(triMax < projectedTriV1) {
                    triMax = projectedTriV1;
                }
    
                if(triMin > projectedTriV2) {
                    triMin = projectedTriV2;
                }
                if(triMax < projectedTriV2) {
                    triMax = projectedTriV2;
                }
                
                //Test Collision
                if(!Utils.intersects(triMin, triMax, -aabbRadius, aabbRadius, epsilon)) {
                    return false;
                }
            }
        }
        
        ////////////////////////////////
        
        ////////////////////////////////
        //리액션 파라미터 계산 후 반환

        return true;//new HPair<Boolean, Float>(true, resultParam);

        ////////////////////////////////
    }
    
    public Triangle3D assign(Triangle3D o) {
        this.p0 = new Point3D(o.p0);
        this.p1 = new Point3D(o.p1);
        this.p2 = new Point3D(o.p2);
        
        cacheVectors();
        
        return this;
    }
    
    public Triangle3D(Triangle3D o) {
        this(o.p0, o.p1, o.p2);
    }
    public Triangle3D(Vector3D toFirst, Vector3D toSecond) {
        this(new Point3D(), toFirst, toSecond);
    }
    public Triangle3D(Point3D zero, Vector3D toFirst, Vector3D toSecond) {
        this(new Point3D(zero), zero.add(toFirst), zero.add(toSecond));
    }
    public Triangle3D(Point3D first, Point3D second) {
        this(new Point3D(), first, second);
    }
    public Triangle3D(Point3D zero, Point3D first, Point3D second) {
        this.p0 = new Point3D(zero);
        this.p1 = new Point3D(first);
        this.p2 = new Point3D(second);

        cacheVectors();
    }
    
    private void cacheVectors() {
        e01 = p1.subtract(p0);
        e12 = p2.subtract(p1);
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
    private Vector3D e12;
    private Vector3D e20;
    private Vector3D normal;
    private Vector3D unitNormal;

}
