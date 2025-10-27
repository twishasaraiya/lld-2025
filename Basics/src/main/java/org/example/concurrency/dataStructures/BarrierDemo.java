package org.example.concurrency.dataStructures;


import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A synchronization aid that allows a set of threads to all wait for each other to reach a common barrier point. CyclicBarriers are useful in programs involving a fixed sized party of threads that must occasionally wait for each other. The barrier is called cyclic because it can be re-used after the waiting threads are released.
 */
public class BarrierDemo {
    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
//        Java example
//        CyclicBarrier barrier = new CyclicBarrier(3);
//        barrier.await();

        CustomBarrier cb = new CustomBarrier(2);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 2; i++) {
            executorService.submit(new Worker1(cb));
        }

        executorService.shutdown();
        if(!executorService.awaitTermination(5000, TimeUnit.SECONDS)){
            executorService.shutdownNow();
        }
    }
}


interface IBarrier{
    void await();
}

class CustomBarrier implements IBarrier{
    private int totalCount;
    private int count;
    private Lock lock;
    private Condition waitCondition;
    private AtomicInteger version;

    public CustomBarrier(int count) {
        this.totalCount = count;
        this.count = count;
        this.lock = new ReentrantLock();
        this.waitCondition = lock.newCondition();
        this.version = new AtomicInteger(1);
    }


    @Override
    public void await() {
        System.out.println("Await called by " + Thread.currentThread().getName());
        try {
            lock.lock();
            count-=1;
            int currVersion = version.get();
            System.out.println("Count " + count + " decremented by " + Thread.currentThread().getName());
            while (count > 0){
                waitCondition.await();
            }
            waitCondition.signalAll(); // signal first so that condition check passes
            // reset count from last thread, other threads cannot update version once done
            if(version.compareAndSet(currVersion+1, currVersion)){
                this.count = totalCount;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            lock.unlock();
        }
    }
}

class Worker1 implements Runnable{
    private final CustomBarrier customBarrier;

    public Worker1(CustomBarrier customBarrier) {
        this.customBarrier = customBarrier;
    }

    @Override
    public void run() {
        try {
            System.out.println("Hi I am " + Thread.currentThread().getName());
            Thread.sleep(new Random().nextInt(10000));
            customBarrier.await(); // wait for others to reach this point
            Thread.sleep(1000);
            System.out.println("Hi I am done");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
