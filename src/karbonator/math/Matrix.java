package karbonator.math;

public class Matrix {

    public int getRowSize() {
        return nRow;
    }
    public int getColumnSize() {
        return nCol;
    }
    public int getSize() {
        return nRow*nCol;
    }
    public double at(int row, int col) {
        assertRowColIndexInRange(row, col);
    
        return elements[col + row*nCol];
    }
    public double get(int row, int col) {
        assertRowColIndexInRange(row, col);
    
        return elements[col + row*nCol];
    }
    public double get(int index) {
        return elements[index];
    }
    public double [] getElementArray() {
        double [] result = new double [getSize()];
        
        System.arraycopy(elements, 0, result, 0, result.length);
        
        return result;
    }

    public void resize(int row, int col) {
        if(getSize() != row*col) {
            throw new RuntimeException();
        }
    
        nRow = row;
        nCol = col;
    }
    public void set(double v) {
        final int SIZE = getSize();
    
        for(int r1=0;r1<SIZE;++r1) {
            elements[r1] = v;
        }
    }
    public void set(int index, double v) {
        elements[index] = v;
    }
    public void set(int row, int col, double v) {
        assertRowColIndexInRange(row, col);
    
        elements[col + row*nCol] = v;
    }
    
    public Matrix getTranspose() {
        Matrix result = new Matrix(nCol, nRow);
    
        for(int row=0;row<result.nRow;++row) {
            for(int col=0;col<result.nCol;++col) {
                result.set(row, col, get(col, row));
            }
        }
    
        return result;
    }
    public Matrix getRowVector(int row) {
        if(row >= nRow) {
            throw new RuntimeException();
        }
    
        Matrix result = new Matrix(1, nCol);
    
        for(int col=0;col<nCol;++col) {
            result.set(0, col, at(row, col));
        }
    
        return result;
    }
    public Matrix getColumnVector(int col) {
        if(col >= nCol) {
            throw new RuntimeException();
        }
    
        Matrix result = new Matrix(nRow, 1);
    
        for(int row=0;row<nRow;++row) {
            result.set(row, 0, at(row, col));
        }
    
        return result;
    }

    public Matrix add(Matrix o) {
        assertSizeIsSame(o);

        Matrix result = new Matrix(nRow, nCol);
        
        for(int row=0;row<nRow;++row) {
            for(int col=0;col<nCol;++col) {
                result.set(row, col, get(row,col)+o.get(row, col));
            }
        }
        
        return result;
    }
    public Matrix sub(Matrix o) {
        assertSizeIsSame(o);
        
        Matrix result = new Matrix(nRow, nCol);
        
        for(int row=0;row<nRow;++row) {
            for(int col=0;col<nCol;++col) {
                result.set(row, col, get(row,col)-o.get(row, col));
            }
        }
        
        return result;
    }
    public Matrix multiply(Matrix o) {
        if(nCol != o.nRow) {
            throw new RuntimeException();
        }
    
        Matrix result = new Matrix(nRow, o.nCol);
    
        double sum = 0.0f;
        int n = nCol;
    
        for(int row=0;row<result.nRow;++row) {
            for(int col=0;col<result.nCol;++col) {            
                sum = 0.0f;
    
                for(int r1=0;r1<n;++r1) {
                    sum += at(row, r1)*o.at(r1, col);
                }
    
                result.set(row, col, sum);
            }
        }
    
        return result;
    }
    public Matrix multiply(double v) {
        Matrix result = new Matrix(nRow, nCol);
        
        for(int row=0;row<nRow;++row) {
            for(int col=0;col<nCol;++col) {
                result.set(row, col, get(row,col)*v);
            }
        }
        
        return result;
    }
    public Matrix pointMultiply(Matrix o) {
        assertSizeIsSame(o);
        
        Matrix result = new Matrix(nRow, nCol);
        
        for(int row=0;row<nRow;++row) {
            for(int col=0;col<nCol;++col) {
                result.set(row, col, get(row,col)*o.get(row, col));
            }
        }
        
        return result;
    }
    public Matrix divide(double v) {
        Matrix result = new Matrix(nRow, nCol);
        
        for(int row=0;row<nRow;++row) {
            for(int col=0;col<nCol;++col) {
                result.set(row, col, get(row,col)/v);
            }
        }
        
        return result;
    }

    public Matrix getSumOfRowVectors() {
        Matrix result = new Matrix(1, nCol);
        
        for(int row=0;row<nRow;++row) {
            result = result.add(getRowVector(row));
        }
        
        return result;
    }

    public Matrix() {
        this(1, 1);
    }
    public Matrix(Matrix o) {
        nRow = o.nRow;
        nCol = o.nCol;

        constructContainer(getSize());

        System.arraycopy(o.elements, 0, elements, 0, getSize());
    }
    public Matrix(int row, int col) {
        nRow = row;
        nCol = col;
        
        constructContainer(getSize());
        
        set(0);
    }
    public Matrix(int row, int col, int [] elements) {
        nRow = row;
        nCol = col;
        
        constructContainer(getSize());
        
        for(int r1=0;r1<getSize();++r1) {
            set(r1, elements[r1]);
        }
    }
    public Matrix(int row, int col, double [] elements) {
        nRow = row;
        nCol = col;
        
        constructContainer(getSize());
        
        System.arraycopy(elements, 0, this.elements, 0, getSize());
    }

    private void assertRowColIndexInRange(int row, int col) {
        if( row < 0 || row >= nRow ||
            col < 0 || col >= nCol)
        {
            throw new RuntimeException();
        }
    }
    private void assertSizeIsSame(Matrix o) {
        if(nRow != o.nRow || nCol != o.nCol) {
            throw new RuntimeException();
        }
    }

    private void constructContainer(int size) {
        elements = new double [size];
    }

    private int nRow;
    private int nCol;
    private double [] elements;

}
