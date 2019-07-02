package karbonator.math.geom3d;

import karbonator.math.Utils;

public class Tuple3D {

    private static final int N_AXIS = 3;

    public static int getAxisCount() {
        return N_AXIS;
    }

    public float getX() {
        return values[0];
    }
    public float getY() {
        return values[1];
    }
    public float getZ() {
        return values[2];
    }
    public float get(int index) {
        return values[index];
    }
    
    public void setX(float v) {
        values[0] = v;
    }
    public void setY(float v) {
        values[1] = v;
    }
    public void setZ(float v) {
        values[2] = v;
    }
    public void set(int index, float v) {
        values[index] = v;
    }
    public void set(float x, float y, float z) {
        this.values[0] = x;
        this.values[1] = y;
        this.values[2] = z;
    }
    
    public boolean isXZero() {
        return Utils.isZero(values[0]);
    }
    public boolean isYZero() {
        return Utils.isZero(values[1]);
    }
    public boolean isZZero() {
        return Utils.isZero(values[2]);
    }
    public boolean isAxisZero(int index) {
        return Utils.isZero(values[index]);
    }
    public boolean isZero() {
        return isXZero() && isYZero() && isZZero();
    }
    public boolean equals(Tuple3D o) {
        return  Utils.isEqual(values[0], o.values[0]) && 
                Utils.isEqual(values[1], o.values[1]) && 
                Utils.isEqual(values[2], o.values[2]);
    }
    
    @Override
    public String toString() {
        return String.format("[%.2f, %.2f, %.2f]", values[0], values[1], values[2]);
    }
    public float [] toArray() {
        return new float [] {values[0], values[1], values[2]};
    }

    public Tuple3D assign(Tuple3D o) {
        this.values[0] = o.values[0];
        this.values[1] = o.values[1];
        this.values[2] = o.values[2];
        
        return this;
    }

    public Tuple3D() {
        this(0.0f, 0.0f, 0.0f);
    }
    public Tuple3D(Tuple3D o) {
        this(o.values[0], o.values[1], o.values[2]);
    }
    public Tuple3D(float x, float y, float z) {
        values = new float [] {
            x, y, z
        };
    }

    protected float [] values;

}
