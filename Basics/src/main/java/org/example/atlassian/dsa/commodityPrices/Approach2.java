package org.example.atlassian.dsa.commodityPrices;

import com.sun.source.tree.Tree;

import java.sql.Timestamp;
import java.util.*;

public class Approach2 {
    public static void main(String[] args) {
        CommodityPrice r = new CommodityPrice();
        r.upsertCommodityPrice(4, 27);
        r.upsertCommodityPrice(6, 26);
        r.upsertCommodityPrice(9, 25);
        System.out.println(r.getMaxCommodityPrice());
        r.upsertCommodityPrice(4, 28);
        System.out.println(r.getMaxCommodityPrice());
    }
}

class CommodityPrice {
    // original
    private Map<Integer, Integer> timestampToPrice;
    private TreeMap<Integer, HashSet<Integer>> priceToTimestamp;
//    private Map<Integer, TreeMap<Integer, Integer>> timestampToPrice;
    private int checkpointCounter;

    public CommodityPrice() {
        this.timestampToPrice = new HashMap<>();
        this.priceToTimestamp = new TreeMap<>();
        this.checkpointCounter = 0;
    }


    public void upsertCommodityPrice(int timestamp, int commodityPrice) {
        Integer oldPrice = timestampToPrice.get(timestamp);
        if (oldPrice != null){
            priceToTimestamp.get(oldPrice).remove(timestamp);
            if(priceToTimestamp.get(oldPrice).isEmpty()){
                priceToTimestamp.remove(oldPrice);
            }
        }

        timestampToPrice.put(timestamp, commodityPrice);
        priceToTimestamp.computeIfAbsent(commodityPrice, k -> new HashSet<>())
                .add(timestamp);
    }

    public int getMaxCommodityPrice() {
        return priceToTimestamp.lastKey();
        }
//
//        public int getCommodityPrice(int timestamp, int checkpoint){
//        }
}
