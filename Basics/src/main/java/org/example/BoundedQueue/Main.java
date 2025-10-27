package org.example.BoundedQueue;

public class Main {
    public static void main(String[] args) {
//        ProducerConsumer pc = new SynchronisedPC(10);
        ProducerConsumer pc = new LockPC(10);

        Runnable producer = () -> {
            for(int i=0;i<10;i++){
                pc.produce("I want " + Math.random() + " points");
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e) {
                   throw new RuntimeException(e);
                }
            }
        };

        Runnable consumer = () -> {
            while (true){
                pc.consume();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        };

        int numProducers = 2;
        int numConsumers = 1;
        Thread[] producers = new Thread[numProducers];
        Thread[] consumers = new Thread[numConsumers];
        for (int i = 0; i < numProducers; i++) {
            producers[i] = new Thread(producer);
            producers[i].start();
        }

        for (int i = 0; i < numConsumers; i++) {
            consumers[i] = new Thread(consumer);
            consumers[i].start();
        }
    }
}
