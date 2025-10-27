package org.example.Zerodha;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {

    }
}

enum OrderType{
    BUY,
    SELL
}
enum TransType{
    MARKET,
    LIMIT
}
class Order{
    String id;
    OrderType type;
    Stock stock;
    TransType transType;
    Double price;
    Integer qty;
    long time;
}

class Stock{
    String name;
    Double price;

    public Stock(String name) {
        this.name = name;
    }

    public Stock(String name, Double price) {
        this.name = name;
        this.price = price;
    }
}

interface IBroker{
    void addStock(String name, Double price);
    boolean executeOrder(Order order);
    void updateStockPrice(String name, Double price);
}

class Broker implements IBroker{
    private Map<String, Stock> stockMap;
    private Map<String, PriorityQueue<Order>> buyOrders;
    private Map<String, PriorityQueue<Order>> sellOrders;
    private Map<String, ReentrantLock> lockMap;

    public Broker() {
        this.stockMap = new ConcurrentHashMap<>();
        this.buyOrders = new ConcurrentHashMap<>();
        this.sellOrders = new ConcurrentHashMap<>();
    }

    @Override
    public void addStock(String name, Double price) {
        stockMap.put(name, new Stock(name, price));
        lockMap.put(name, new ReentrantLock());
    }

    @Override
    public boolean executeOrder(Order order) {
        if(!validateOrder(order)){
            throw new RuntimeException("Invalid order");
        }
        order.id = UUID.randomUUID().toString();
        String stockName = order.stock.name;
        order.time = System.currentTimeMillis();
        if(TransType.MARKET.equals(order.transType)){
            order.price = stockMap.get(stockName).price;
        }
        if(OrderType.BUY.equals(order.type)){
            buyOrders.computeIfAbsent(stockName, k -> new PriorityQueue<>((a,b) -> {
                if(a.price.equals(b.price)){
                    return Long.compare(a.time,b.time); // FIFO by time
                }
                return b.price.compareTo(a.price); // highest bidder first
            })).add(order);
        }else if(OrderType.SELL.equals(order.type)){
            sellOrders.computeIfAbsent(stockName, k -> new PriorityQueue<>((a,b) -> {
                if(a.price.equals(b.price)){
                    return Long.compare(a.time,b.time); // FIFO by time
                }
                return b.price.compareTo(a.price); // cheap seller price first
            })).add(order);
        }
        return matchOrders(stockName);
    }

    private boolean matchOrders(String stockName){
        lockMap.get(stockName).lock();
        try {
            Order buyTop = buyOrders.get(stockName).peek();
            Order sellTop = sellOrders.get(stockName).peek();
            if(buyTop == null || sellTop == null) return false;
            if(buyTop.price < sellTop.price) return false;
            buyOrders.get(stockName).poll();
            sellOrders.get(stockName).poll();
            if(buyTop.qty < sellTop.qty){
                // buy complete, partial sell
                System.out.println("Sell order " + sellTop.id + " completed by " + buyTop.id);
                sellTop.qty -= buyTop.qty;
                sellOrders.get(stockName).add(sellTop);
            }else{
                // sell complete
                System.out.println("Buy order " + buyTop.id + " completed by sell " + sellTop.id);
                buyTop.qty -= sellTop.qty;
                buyOrders.get(stockName).add(buyTop);
            }
            return true;
        }finally {
            lockMap.get(stockName).unlock();
        }
    }
    @Override
    public void updateStockPrice(String name, Double price) {
        Stock stock;
        if(stockMap.containsKey(name)){
            stock = stockMap.get(name);
        }else{
            stock = new Stock(name);
        }
        stock.price = price;
    }

    private boolean validateOrder(Order order){
        if(order.type == null || (order.type != OrderType.BUY && order.type != OrderType.SELL)) return false;
        if(TransType.LIMIT.equals(order.transType) && order.price == null) return false;
        if(order.qty == null) return false;
        return true;
    }
}
