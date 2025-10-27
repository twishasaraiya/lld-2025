package org.example.BoundedQueue;

import java.util.LinkedList;
import java.util.Queue;

public class SynchronisedPC implements ProducerConsumer{

    private Queue<String> queue;
    private int maxSize;

    public SynchronisedPC(int maxSize) {
        this.maxSize = maxSize;
        this.queue = new LinkedList<>();
    }

    public static void main(String[] args) {

    }

    @Override
    public synchronized void produce(String msg)  {
            while (queue.size() >= this.maxSize) {
                System.out.println("waiting on queue to produce " + Thread.currentThread().getName() + " " + msg);
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("Produced message to queue " + Thread.currentThread().getName() + " " + msg);
            queue.add(msg);
            notifyAll();
    }

    @Override
    public synchronized void consume(){
            while (queue.isEmpty()){
                try {
                    System.out.println("waiting on queue to receive msg " + Thread.currentThread().getName());
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            String msg = queue.poll();
            System.out.println("Consumed message " + msg + " by " + Thread.currentThread().getName());
            notifyAll();
    }
}