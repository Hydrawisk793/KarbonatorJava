package karbonator.test;

import karbonator.collection.Vector;
import karbonator.memory.ByteBuffer;
import karbonator.memory.ByteOrder;
import karbonator.memory.Serializable;

public class HVectorTest {

    public static class Foo {}
    public static class Bar implements Serializable {

        @Override
        public ByteBuffer serialize(ByteOrder byteOrder) {            
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Bar unserialize(ByteBuffer buffer) {
            // TODO Auto-generated method stub
            return this;
        }
        
        public int baz;
        public short bazbaz;
        
    }

    public static void main(String [] args) {
        Integer [] foo = {5, 4, 3, 2, 1};
        Vector<Integer> vector = new Vector<Integer>(foo);
    
        for(int r1=0;r1<vector.getSize();++r1) {
            System.out.printf("%d ", vector.at(r1));
        }
        System.out.println();
        
        vector.insert(2, 9);
        for(int r1=0;r1<vector.getSize();++r1) {
            System.out.printf("%d ", vector.at(r1));
        }
        System.out.println();
    }

}
