package karbonator.test;

import karbonator.math.geom3d.Aabb3D;
import karbonator.math.geom3d.Point3D;
import karbonator.math.geom3d.Vector3D;

public class HAABB3DTest {

    public static void test1() {
        Aabb3D aabb = new Aabb3D(new Point3D(), new Vector3D(3.0f, 1.0f, 8.0f));
        Point3D [] aabbVertexes = aabb.getVertexes();
        Aabb3D [] aabbFaces = aabb.getFaceAABBs();
        int vertexIdxes [][] = {
            new int [] {0, 1, 2, 3, 0, 1, 2, 3},
            new int [] {4, 5, 6, 7, 4, 5, 6, 7},
            new int [] {0, 1, 0, 1, 4, 5, 4, 5},
            new int [] {2, 3, 2, 3, 6, 7, 6, 7},
            new int [] {0, 0, 2, 2, 4, 4, 6, 6},
            new int [] {1, 1, 3, 3, 5, 5, 7, 7}
        };

        for(int r1=0;r1<aabbVertexes.length;++r1) {
            System.out.printf("%s ", aabbVertexes[r1].toString());
        }
        System.out.println();

        for(int r1=0;r1<aabbFaces.length;++r1) {
            Point3D [] faceVertexes = aabbFaces[r1].getVertexes();

            System.out.printf("Face %d : ", r1);
            for(int r2=0;r2<faceVertexes.length;++r2) {
                System.out.printf("%b ", faceVertexes[r2].equals(aabbVertexes[vertexIdxes[r1][r2]]));
            }
            System.out.println();
        }
    }
    public static void test2() {
        Aabb3D aabb = new Aabb3D(new Vector3D(12, 8, 0));
        
        System.out.printf("width == %f\r\n", aabb.getWidth());
        System.out.printf("height == %f\r\n", aabb.getHeight());
        System.out.printf("depth == %f\r\n", aabb.getDepth());
    }

    public static void main(String [] args) {
        test2();
    }

}
