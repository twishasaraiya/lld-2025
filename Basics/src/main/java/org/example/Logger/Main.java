package org.example.Logger;

import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Logger logger = Logger.getInstance();
        Producer p1 = new Producer();
        Producer p2 = new Producer();
        Consumer c1 = new Consumer("test.log");
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.execute(c1);
        executorService.execute(p1);
        executorService.execute(p2);

        Thread.sleep(1000);
        logger.shutdown();
        executorService.shutdown();
        if(!executorService.awaitTermination(2000, TimeUnit.SECONDS)){
            executorService.shutdownNow();
        }
    }
}

enum LogLevel {
    DEBUG(0),
    INFO(1),
    WARN(2),
    ERROR(3);

    int priority;

    LogLevel(int priority) {
        this.priority = priority;
    }
}

class LogMessage{
    String message;
    LogLevel logLevel;
    Long timestamp;

    public LogMessage(String message, LogLevel logLevel) {
        this.message = message;
        this.logLevel = logLevel;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "LogMessage{" +
                "message='" + message + '\'' +
                ", logLevel=" + logLevel +
                ", timestamp=" + timestamp +
                '}';
    }
}

class Logger{
    private static final int maxCapacity = 5;
    private BlockingQueue<LogMessage> messageQueue;
    private AtomicReference<LogLevel> currLevel;
    private volatile static Logger logger;
    private volatile boolean isShutdown;


    private Logger() throws IOException {
        this.messageQueue = new ArrayBlockingQueue<>(maxCapacity);
        this.currLevel = new AtomicReference<>(LogLevel.DEBUG);
        this.isShutdown = false;
    }

    public static Logger getInstance() throws IOException {
        if(logger == null){
            synchronized (Logger.class){
                if (logger == null){
                    logger = new Logger();
                }
                return logger;
            }
        }
        return logger;
    }

    public boolean log(LogMessage message){
        if(message.logLevel.priority < currLevel.get().priority || isShutdown){
            return false;
        }

        /**
         * offer is non-blocking and allows request to fail if capacity is exceeded
         */
        return messageQueue.offer(message);
    }

    public LogMessage getMessage() throws InterruptedException {
        return messageQueue.poll(1,TimeUnit.SECONDS);
    }



    public void setLevel(LogLevel level){
        currLevel.set(level);
    }

    public void shutdown() {
        this.isShutdown = true;
    }

    public boolean isShutdown() {
        return isShutdown;
    }

    public boolean isEmpty(){
        return messageQueue.isEmpty();
    }
}

class Producer implements Runnable{
    private Logger logger;

    public Producer() throws IOException {
        this.logger = Logger.getInstance();
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            String msg = "Hi this is producer thread " + Thread.currentThread().getName() + " cnt " + (i+1);
            System.out.println("Produce " + msg);
            LogMessage logMessage = new LogMessage(msg, LogLevel.INFO);
            logger.log(logMessage);
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class FileWriter {

    private ReentrantReadWriteLock reentrantLock;
    private PrintWriter printWriter;
    private volatile static FileWriter writer;

    private FileWriter(String filename) throws IOException {
        this.printWriter = new PrintWriter(new BufferedWriter(new java.io.FileWriter(filename, true)), true);
        this.reentrantLock = new ReentrantReadWriteLock();
    }

    public static FileWriter getInstance(String filename) throws IOException {
        if(writer == null){
            synchronized (FileWriter.class){
                if (writer == null){
                    writer = new FileWriter(filename);
                }
                return writer;
            }
        }
        return writer;
    }

    public boolean writeToFile(String message){
        reentrantLock.writeLock().lock();
        try {
            if(message != null){
                System.out.println("Writing to file " + message);
                printWriter.println(message);
                return true;
            }
            return false;
        } finally {
            reentrantLock.writeLock().unlock();
        }
    }
}
class Consumer implements Runnable{
    private Logger logger;
    private FileWriter fileWriter;


    public Consumer(String fileName) throws IOException {
        this.logger = Logger.getInstance();
        this.fileWriter = FileWriter.getInstance(fileName);
    }

    @Override
    public void run() {
        while (!logger.isShutdown() || !logger.isEmpty()){
            LogMessage message = null;
            try {
                message = logger.getMessage();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            if(message != null){
                fileWriter.writeToFile(message.toString());
            }
        }
    }


}