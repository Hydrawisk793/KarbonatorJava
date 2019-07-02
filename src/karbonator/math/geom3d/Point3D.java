package karbonator.math.geom3d;

import karbonator.math.Utils;

public class Point3D extends Tuple3D {

    public boolean equals(Point3D o) {
        return  Utils.isEqual(values[0], o.values[0]) && 
                Utils.isEqual(values[1], o.values[1]) && 
                Utils.isEqual(values[2], o.values[2]);
    }

    public Point3D add(Vector3D o) {
        return new Point3D(this).addAssign(o);
    }
    public Vector3D subtract(Point3D start) {
        return new Vector3D(   values[0]-start.values[0], 
                                values[1]-start.values[1], 
                                values[2]-start.values[2]);
    }
    public Point3D subtract(Vector3D o) {
        return new Point3D(this).subtractAssign(o);
    }

    public Point3D assign(Point3D o) {
        values[0] = o.values[0];
        values[1] = o.values[1];
        values[2] = o.values[2];
            
        return this;        
    }
    public Point3D addAssign(Vector3D o) {
        values[0] += o.getX();
        values[1] += o.getY();
        values[2] += o.getZ();
        
        return this;
    }
    public Point3D subtractAssign(Vector3D o) {
        values[0] -= o.getX();
        values[1] -= o.getY();
        values[2] -= o.getZ();
        
        return this;
    }

    public Point3D() {
        super();
    }
    public Point3D(Point3D o) {
        super(o);
    }
    public Point3D(float x, float y, float z) {
        super(x, y, z);
    }

}
