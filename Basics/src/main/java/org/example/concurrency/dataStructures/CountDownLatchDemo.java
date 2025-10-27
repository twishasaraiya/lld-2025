package org.example.concurrency.dataStructures;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A synchronization aid that allows one or more threads to wait until a set of operations being performed in other threads completes.
 * A CountDownLatch is initialized with a given count. The await methods block until the current count reaches zero due to invocations of the countDown method, after which all waiting threads are released and any subsequent invocations of await return immediately. This is a one-shot phenomenon -- the count cannot be reset.
 */

/**
 * Interface
 * await() -> block threads till count = 0
 * countDown() -> decrement count
 */
public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
//        CountDownLatch countDownLatch = new CountDownLatch(2);
//        countDownLatch.await();
//        countDownLatch.countDown();

        ICountDownLatch countDownLatch = new CustomCountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 2; i++) {
            Worker worker = new Worker(countDownLatch);
            executorService.submit(worker);
        }

        Thread.sleep(1000); // some work
        countDownLatch.countDown();
        countDownLatch.countDown();

        executorService.shutdown();
        if(!executorService.awaitTermination(1000, TimeUnit.SECONDS)){
            executorService.shutdownNow();
        }
    }
}

interface ICountDownLatch{
    void await();
    void countDown();
}

class CustomCountDownLatch implements ICountDownLatch{
    private AtomicInteger count;
    private Lock lock;
    private Condition waitCondition;

    public CustomCountDownLatch(int n) {
        this.count = new AtomicInteger(n);
        this.lock = new ReentrantLock();
        this.waitCondition = lock.newCondition();
    }

    @Override
    public void await() {
        try {
            lock.lock();
            while (count.get() > 0){
                waitCondition.await();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void countDown() {
        try {
            lock.lock();
            int cnt = count.decrementAndGet();
            System.out.println(cnt);
            if(cnt == 0){
                waitCondition.signalAll();
            }
        }finally {
            lock.unlock();
        }

    }
}

class Worker implements Runnable{
    private ICountDownLatch latch;

    public Worker(ICountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        System.out.println("Hi I am " + Thread.currentThread().getName());
        this.latch.await();
        try {
            System.out.println("Hi I am here " + Thread.currentThread().getName());
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}