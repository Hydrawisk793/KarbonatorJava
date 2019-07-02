package karbonator.math.geom3d;

import karbonator.math.Utils;

public class Vector3D extends Tuple3D {

    public Vector3D getXVector() {
        return new Vector3D(values[0], 0.0f, 0.0f);
    }
    public Vector3D getYVector() {
        return new Vector3D(0.0f, values[1], 0.0f);
    }
    public Vector3D getZVector() {
        return new Vector3D(0.0f, 0.0f, values[2]);
    }
    public Vector3D getAxisVector(int axisIndex) {
        switch(axisIndex) {
        case 0:
            return getXVector();
        case 1:
            return getYVector();
        case 2:
            return getZVector();
        default:
            throw new IndexOutOfBoundsException();
        }
    }
    
    public boolean equals(Vector3D o) {
        return  Utils.isEqual(values[0], o.values[0]) && 
                Utils.isEqual(values[1], o.values[1]) && 
                Utils.isEqual(values[2], o.values[2]);
    }

    public float normSquared() {
        return values[0]*values[0] + values[1]*values[1] + values[2]*values[2];
    }
    public float norm() {
        return Utils.sqrt(normSquared());
    }
    public Vector3D normalize() {
        return new Vector3D(this).normalizeAssign();
    }
    public Vector3D negate() {
        return new Vector3D(this).negateAssign();
    }
    public Vector3D pointwiseAbsolute() {
        return new Vector3D(this).pointwiseAbsoluteAssign();
    }
    public Vector3D pointwiseMultiply(Vector3D o) {
        return new Vector3D(this).pointwiseMultiplyAssign(o);
    }
    public Vector3D add(Vector3D o) {
        return new Vector3D(this).addAssign(o);
    }
    public Vector3D subtract(Vector3D o) {
        return new Vector3D(this).subtractAssign(o);
    }
    public Vector3D multiply(float v) {
        return new Vector3D(this).multiplyAssign(v);
    }
    public Vector3D divide(float v) {
        return new Vector3D(this).divideAssign(v);
    }
    public float dot(Vector3D o) {
        return values[0]*o.values[0] + values[1]*o.values[1] + values[2]*o.values[2];
    }
    public Vector3D cross(Vector3D o) {
        return new Vector3D(this).crossAssign(o);
    }
    
    public Vector3D assign(Vector3D o) {
        values[0] = o.values[0];
        values[1] = o.values[1];
        values[2] = o.values[2];
            
        return this;        
    }
    public Vector3D normalizeAssign() {
        float thisNorm = norm();
        
        if(Utils.isZero(thisNorm)) {
            values[0] = 0.0f;
            values[1] = 0.0f;
            values[2] = 0.0f;
            
            return this;
        }
        else {
            return divideAssign(norm());
        }
    }
    public Vector3D negateAssign() {
        values[0] = -values[0];
        values[1] = -values[1];
        values[2] = -values[2];
    
        return this;
    }
    public Vector3D pointwiseAbsoluteAssign() {
        values[0] = Utils.abs(values[0]);
        values[1] = Utils.abs(values[1]);
        values[2] = Utils.abs(values[2]);
    
        return this;
    }
    public Vector3D pointwiseMultiplyAssign(Vector3D o) {
        values[0] *= o.values[0];
        values[1] *= o.values[1];
        values[2] *= o.values[2];
        
        return this;
    }
    public Vector3D addAssign(Vector3D o) {
        values[0] += o.values[0];
        values[1] += o.values[1];
        values[2] += o.values[2];
    
        return this;
    }
    public Vector3D subtractAssign(Vector3D o) {
        values[0] -= o.values[0];
        values[1] -= o.values[1];
        values[2] -= o.values[2];
    
        return this;
    }
    public Vector3D multiplyAssign(float v) {
        values[0] *= v;
        values[1] *= v;
        values[2] *= v;
    
        return this;
    }
    public Vector3D divideAssign(float v) {
        return multiplyAssign(1*(1/v));
    }
    public Vector3D crossAssign(Vector3D o) {
        float aX = values[0];
        float aY = values[1];
        float aZ = values[2];
        float bX = o.values[0];
        float bY = o.values[1];
        float bZ = o.values[2];
    
        values[0] = aY*bZ-aZ*bY;
        values[1] = aZ*bX-aX*bZ;
        values[2] = aX*bY-aY*bX;

        return this;
    }
    
    public Vector3D() {
        super();
    }
    public Vector3D(Vector3D o) {
        super(o);
    }
    public Vector3D(float x, float y, float z) {
        super(x, y, z);
    }

}
