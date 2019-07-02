package karbonator.test;

import karbonator.math.Matrix;

public class HMatrixTest {

    public static String matrixToString(Matrix o) {
        String result = "[";
        int nRow = o.getRowSize();
        int nCol = o.getColumnSize();
    
        if(nRow > 0) {
            for(int col=0;col<nCol;++col) {
                result += String.format("%.2f ", o.at(0, col));
            }
        }
    
        for(int row=1;row<nRow;++row) {
            result += "; ";
    
            for(int col=0;col<nCol;++col) {
                result += String.format("%.2f ", o.at(row, col));
            }
        }
    
        result += "]";
    
        return result;
    }

    public static void main(String [] args) {
        double foo1 [] = {1, 2, 3, 4, 5, 6};
        double foo2 [] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        //double foo3 [] = {0, 0, 1, 5, 5, 0};
        Matrix mat1 = new Matrix(2, 3, foo1);
        Matrix mat2 = new Matrix(3, 4, foo2);
        //HMatrix mat3 = new HMatrix(2, 3, foo3);
    
        //System.out.println(matrixToString(mat1));
        //System.out.println(matrixToString(mat1.getRowVector(0)));
        //System.out.println(matrixToString(mat1.getRowVector(1)));
        //System.out.println(matrixToString(mat1.getColumnVector(0)));
        //System.out.println(matrixToString(mat1.getColumnVector(1)));
        //System.out.println(matrixToString(mat1.getColumnVector(2)));
        //System.out.println(matrixToString(mat1.getTranspose()));
    
        //System.out.println(matrixToString(mat2));
    
        System.out.println(matrixToString(mat1.multiply(mat2)));
        System.out.println(matrixToString(mat1.pointMultiply(mat1)));
        System.out.println(matrixToString(mat2.getSumOfRowVectors()));
    
        //System.out.println(matrixToString(mat1.add(mat3)));
        //System.out.println(matrixToString(mat1.sub(mat3)));
    }

}
