package org.example.rippling;

import java.util.*;

/*
Implement HashMap Data Structure handling following:
Hashing
Handling collisions
Resizing
Load factors
Multi-threading/Concurrency

Implement this a hire software engineer in a 60 mins interview in java
 */
public class ConcurrentHashMap {
    public static void main(String[] args) {
        CustomMap customMap = new CustomMap(5);
        customMap.put("1", "1");
        System.out.println(customMap.get("1"));
        customMap.put("2", "2");
        customMap.put("3", "3");
        customMap.put("4", "4");
        customMap.put("1","2");
        System.out.println(customMap.get("2"));
    }

}

interface IMap{
    void put(String key, String val);
    String get(String key);
}

class CustomMap implements IMap{
    LinkedList<java.util.Map.Entry<String, String>>[] buckets;
    int maxCapacity;
    float loadFactor;
    int numKeys;

    public CustomMap(int intialCapacity) {
        this.maxCapacity = intialCapacity;
        this.buckets = new LinkedList[maxCapacity];
        for (int i = 0; i < maxCapacity; i++) {
            buckets[i] = new LinkedList<>();
        }
        this.loadFactor = 0.75f;
        this.numKeys = 0;
    }

    @Override
    public void put(String key, String val) {
        int bucketIdx = Objects.hash(key) % maxCapacity;
        LinkedList<java.util.Map.Entry<String,String>> linkedList = buckets[bucketIdx];
        for (java.util.Map.Entry<String, String> node : linkedList) {
            if (node.getKey().equals(key)) {
                node.setValue(val);
                return;
            }
        }
        linkedList.add(new AbstractMap.SimpleEntry<String, String>(key, val));
        ++numKeys;
        if(numKeys >= loadFactor*maxCapacity){
            resize();
        }
    }

    @Override
    public String get(String key) {
        int bucketIdx = Objects.hash(key) % maxCapacity;
        LinkedList<java.util.Map.Entry<String,String>> linkedList = buckets[bucketIdx];
        for (java.util.Map.Entry<String, String> node : linkedList) {
            if (node.getKey().equals(key)) {
                return node.getValue();
            }
        }
        return null;
    }

    private void resize(){
        System.out.println("Resize");
        int newCapacity = maxCapacity * 2;
        maxCapacity = newCapacity;
        LinkedList<java.util.Map.Entry<String,String>>[] newBuckets = new LinkedList[newCapacity];
        for (int i = 0; i < maxCapacity; i++) {
            newBuckets[i] = new LinkedList<>();
        }
        for (int i=0;i< buckets.length;i++){
            for (java.util.Map.Entry<String,String> node : buckets[i]){
                int newIdx = Objects.hash(node.getKey()) % newCapacity;
                newBuckets[newIdx].add(node);
            }
        }
        this.buckets = newBuckets;
    }
}