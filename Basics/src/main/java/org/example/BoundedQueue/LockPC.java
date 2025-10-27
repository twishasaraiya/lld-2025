package org.example.BoundedQueue;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LockPC implements ProducerConsumer{

    private ReentrantLock lock;
    private int maxSize;
    private Condition isFull;
    private Condition isEmpty;

    private Queue<String> queue;

    public LockPC(int maxSize) {
        this.queue = new LinkedList<>();
        this.maxSize = maxSize;
        this.lock = new ReentrantLock(true);
        this.isFull = lock.newCondition();
        this.isEmpty = lock.newCondition();
    }

    @Override
    public void produce(String msg) {
        lock.lock();
        try {
            while (queue.size() >= maxSize) {
                System.out.println("waiting on queue to produce " + Thread.currentThread().getName() + " " + msg);
                isFull.await();
            }
            queue.add(msg);
            System.out.println("Produced message to queue " + Thread.currentThread().getName() + " " + msg);
            isEmpty.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void consume() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                System.out.println("waiting on queue to receive msg " + Thread.currentThread().getName());
                isEmpty.await();
            }
            String msg = queue.poll();
            System.out.println("Consumed message " + msg + " by " + Thread.currentThread().getName());
            isFull.signalAll();
        }catch (InterruptedException e){

        }finally {
            lock.unlock();
        }
    }
}
