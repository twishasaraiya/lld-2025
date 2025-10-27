package org.example.Kafka;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
    public static void main(String[] args) {
        Broker broker = new Broker();
        String t1 = "topic1";
        String t2 = "topic2";
        broker.createTopic(t1);
        broker.createTopic(t2);

        ExecutorService executorService = Executors.newCachedThreadPool();

        try{
            Consumer c1 = new Consumer(t1, broker);
            Consumer c2 = new Consumer(t1, broker);
            Producer p1 = new Producer(t1, broker);
            Producer p2 = new Producer(t2, broker);

            executorService.execute(c1);
            executorService.execute(c2);
            Thread.sleep(2000);
            executorService.execute(p1);
//            executorService.execute(p2);

            executorService.shutdown();
            if(!executorService.awaitTermination(1000, TimeUnit.SECONDS)){
                broker.setShutdown(true);
                executorService.shutdownNow();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
        }

    }
}

/**
 * Entities
 *
 * Broker
 *  - Map<Topic, List<Subscribers>
 *  - createTopic()
 *  - publish(topicName, message)
 *  - subscribe(topicName)
 *  - getLatestOffset(topicName)
 * Topic
 *   - List<String> msg
 *
 * Subscriber
 *  - int offset
 *
 * Producer
 * Consumer
 *
 */
class Topic{
    String topicName;
    List<String> messages;
    ReentrantReadWriteLock readWriteLock;

    public Topic(String name) {
        this.topicName = name;
        this.messages = new ArrayList<>();
        this.readWriteLock = new ReentrantReadWriteLock(true);
    }
    public String getLatestMessage() throws InterruptedException {
        try {
            readWriteLock.readLock().lock();
            return this.messages.get(messages.size() - 1);
        }finally {
            readWriteLock.readLock().unlock();;
        }
    }

    public String getMessage(int offset) throws InterruptedException {
        try {
            readWriteLock.readLock().lock();
            return this.messages.get(offset);
        }finally {
            readWriteLock.readLock().unlock();
        }
    }
    public void publish(String msg) throws InterruptedException {
        try {
            readWriteLock.writeLock().lock();
            this.messages.add(msg);
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
class Subscriber{
    String id;
    String topicName;
    AtomicInteger offset;

    public Subscriber(String topicName, int offset) {
        this.topicName = topicName;
        this.offset = new AtomicInteger(offset);
        this.id = UUID.randomUUID().toString();
    }

    public void incrementOffset(){
        offset.incrementAndGet();
    }

    public int getOffset() {
        return offset.get();
    }
}
class Broker{
    Map<String, Topic> topicMap;
    Map<String, Subscriber> subscriberMap;
    Boolean isShutdown;

    public Broker() {
        this.topicMap = new ConcurrentHashMap<>();
        this.subscriberMap = new ConcurrentHashMap<>();
        this.isShutdown = false;
    }

    public void createTopic(String topicName){
        if (topicMap.containsKey(topicName)){
            throw new RuntimeException("Topic already exists");
        }
        Topic topic = new Topic(topicName);
        topicMap.put(topicName, topic);
    }
    public void publish(String topicName, String message){
        if (!topicMap.containsKey(topicName)){
            throw new RuntimeException("Invalid Topic name. Topic not found");
        }
        try {
            topicMap.get(topicName).publish(message);
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }
    public String subscribe(String topicName){
        if (!topicMap.containsKey(topicName)){
            throw new RuntimeException("Invalid Topic name. Topic not found");
        }
        int offset = topicMap.get(topicName).messages.size();
        Subscriber subscriber = new Subscriber(topicName, offset);
        subscriberMap.put(subscriber.id,subscriber);
        return subscriber.id;
    }

    public String pollMessage(String subscriberId, String topicName) throws InterruptedException {
        if(!subscriberMap.containsKey(subscriberId)) return null;
        int subscriberOffset = subscriberMap.get(subscriberId).getOffset();
        int topicSize = topicMap.get(topicName).messages.size();
        if(subscriberOffset >= topicSize) return null;
        String msg = topicMap.get(topicName).getMessage(subscriberOffset);
        subscriberMap.get(subscriberId).incrementOffset();
        return msg;
    }
    public Boolean getShutdown() {
        return isShutdown;
    }

    public void setShutdown(Boolean shutdown) {
        isShutdown = shutdown;
    }
}
class Producer implements Runnable{
    String topicName;
    Broker broker;
    private static final Random random = new Random();

    public Producer(String topicName, Broker broker) {
        this.topicName = topicName;
        this.broker = broker;
    }

    @Override
    public void run() {
        try {
            for(int i=0;i<100;i++){
                String msg = "Msg from " + topicName + " " + random.nextInt(100) + " thread ::" + Thread.currentThread().getName();
                System.out.println("Publishing " + msg);
                broker.publish(topicName, msg);
                Thread.sleep(2000);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }
}

class Consumer implements Runnable{
    String topicName;
    Broker broker;

    public Consumer(String topicName, Broker broker) {
        this.topicName = topicName;
        this.broker = broker;
    }

    @Override
    public void run() {
        String id = this.broker.subscribe(topicName);
        try {
            while(!broker.getShutdown()){
                String msg = broker.pollMessage(id, topicName);
                if(msg == null) {
                    Thread.sleep(1000);
                    continue;
                }
                System.out.println("Received message " + msg + " on topic " + topicName + " by " + Thread.currentThread().getName());
              }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
