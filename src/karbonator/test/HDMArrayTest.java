package karbonator.test;

import karbonator.collection.FixedArray;

public class HDMArrayTest {

    public static void main(String [] args) {
        FixedArray<Integer> array = new FixedArray<Integer>(5);
        
        array.set(0, 24);
        array.set(1, 53);
        array.set(2, 78);
        array.set(3, 80);
        array.set(4, 39);
        
        for(int r1=0;r1<array.getSize();++r1) {
            System.out.printf("%d ", array.at(r1));
        }
        System.out.println();
        
    }

}
