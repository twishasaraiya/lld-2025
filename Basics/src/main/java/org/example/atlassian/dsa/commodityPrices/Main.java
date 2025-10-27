package org.example.atlassian.dsa.commodityPrices;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static void main(String[] args) {
        WrongApproach commodityPrice = new WrongApproach();
        System.out.println(commodityPrice.getMaxPrice());
        commodityPrice.addOrUpdatePrice(1L, 1800);
        System.out.println(commodityPrice.getMaxPrice());
        commodityPrice.addOrUpdatePrice(2L, 1900);
        System.out.println(commodityPrice.getMaxPrice());
        commodityPrice.addOrUpdatePrice(2L, 1700);
        System.out.println(commodityPrice.getMaxPrice());
        commodityPrice.addOrUpdatePrice(1L, 1650);
        System.out.println(commodityPrice.getMaxPrice());
    }
}

class WrongApproach {
    private Map<Long, Price> commodityPrices;
    private PriorityQueue<Price> maxPriceQueue;

    public WrongApproach() {
        this.commodityPrices = new ConcurrentHashMap<>();
        this.maxPriceQueue = new PriorityQueue<>((p1, p2) -> Double.compare(p2.price, p1.price));
    }

    public void addOrUpdatePrice(long timestamp, double price){
        if(commodityPrices.containsKey(timestamp)){
            Price oldPrice = commodityPrices.get(timestamp);
            maxPriceQueue.remove(oldPrice);
            oldPrice.price = price;
            maxPriceQueue.add(oldPrice);
        } else {
            Price newPrice = new Price(timestamp, price);
            maxPriceQueue.add(newPrice);
            commodityPrices.put(timestamp, newPrice);
        }
    }

    public double getMaxPrice(){
        if(maxPriceQueue.isEmpty()) {
            return -1;
        }
        return maxPriceQueue.peek().price;
    }

    class Price{
        long timestamp;
        double price;
        public Price(long timestamp, double price) {
            this.timestamp = timestamp;
            this.price = price;
        }
    }
}