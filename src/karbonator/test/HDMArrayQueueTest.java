package karbonator.test;

import karbonator.collection.ArrayQueue;

public class HDMArrayQueueTest {

    public static void main(String [] args) {
        ArrayQueue<Integer> queue = new ArrayQueue<Integer>(5);
    
        queue.enqueue(82);
        queue.enqueue(30);
        queue.enqueue(90);
        queue.enqueue(56);
        queue.enqueue(73);

        System.out.printf("isFull? == %b, isEmpty? == %b\n", queue.isFull(), queue.isEmpty());
        
        for(int r1=0;r1<queue.getSize();++r1) {
            System.out.printf("%d ", queue.at(r1));
        }
        System.out.println();
        
        for(;!queue.isEmpty();) {
            System.out.printf("%d ", queue.dequeue());
        }
        System.out.println();
        
        System.out.printf("isFull? == %b, isEmpty? == %b\n", queue.isFull(), queue.isEmpty());
    }

}
