package org.example.concurrency.dataStructures;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBuffer {
    public static void main(String[] args) throws InterruptedException {
        CustomeBuffer cb = new CustomeBuffer(2);
        Random random = new Random();
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 3; i++) {
            Producer p = new Producer(cb);
            executorService.submit(p);
        }
        for (int i = 0; i < 1; i++) {
            Consumer c = new Consumer(cb);
            executorService.submit(c);
        }


        executorService.shutdown();
        if (!executorService.awaitTermination(1000, TimeUnit.SECONDS)){
            executorService.shutdownNow();
        }
    }
}

class Producer implements Runnable{

    CustomeBuffer cb;

    public Producer(CustomeBuffer cb) {
        this.cb = cb;
    }

    @Override
    public void run() {
        cb.add(1);
        System.out.println("Added by " + Thread.currentThread().getName());
    }
}

class Consumer implements Runnable{
    CustomeBuffer cb;

    public Consumer(CustomeBuffer cb) {
        this.cb = cb;
    }

    @Override
    public void run() {
        cb.consume();
        System.out.println("Consumed by " + Thread.currentThread().getName());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
class CustomeBuffer{
    private final int maxCapacity;
    private Queue<Integer> queue;
    private ReentrantLock lock;
    private Condition notFull;
    private Condition notEmpty;

    public CustomeBuffer(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.queue = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
    }

    public void add(Integer element){
        try {
            lock.lock();
            while (queue.size() == maxCapacity){
                notFull.await();
            }
            queue.add(element);
            notEmpty.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public Integer consume(){
        try {
            lock.lock();
            while (queue.isEmpty()){
                notEmpty.await();
            }
            int element = queue.poll();
            notFull.signalAll();
            return element;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
