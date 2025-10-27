package org.example;

import java.security.Key;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 */
public class Main {
    public static void main(String[] args) {

    }
}
/**
 * Cache
 *  - evictionPolicy
 *
 * CacheEntry
 *  - TTL
 *  - lastAccessTime
 *
 * EvictionPolicy - Strategy pattern
 *
 */

interface Cache<K,V>{
    V get(K key);
    void put(K key, V value, long ttlInMillis);
    void remove(K key);
    void clear();
}

enum EvictionPolicy{
    LRU,
    LFU,
    FIFO
}

class CacheEntry<K,V> {
    K key;
    V value;
    long lastAccessTime;
    long ttlInMillis;
    CacheEntry<K,V> prev, next;

    public CacheEntry(K key, V value, long ttlInMillis) {
        this.key = key;
        this.value = value;
        this.lastAccessTime = System.currentTimeMillis();
        this.ttlInMillis = ttlInMillis;
    }

    public CacheEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }
}

class CacheFactory<K,V>{
    public Cache<K,V> getCache(int capacity, EvictionPolicy policy){
        switch (policy){
            case LRU: return new LRU<K,V>(capacity);
            default: throw new IllegalArgumentException("Eviction policy " + policy + " not supported");
        }
    }
}
class LRU<K,V> implements Cache<K,V>{
    int maxCapacity;
    Map<K, CacheEntry<K,V>> keyMap;
    ReentrantReadWriteLock readWriteLock;
    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    CacheEntry<K,V> head, tail;
    public LRU(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.keyMap = new ConcurrentHashMap<>();
        this.head = null;
        this.tail = null;
        this.readWriteLock = new ReentrantReadWriteLock();
        scheduledExecutorService.scheduleWithFixedDelay(() -> cleanup(), 60, 60, TimeUnit.SECONDS);
    }

    private void moveFront(CacheEntry<K,V> entry){
        readWriteLock.writeLock().lock();
        try {
            entry.lastAccessTime = System.currentTimeMillis();
            if(head == null && tail == null){
                head = tail = entry;
                return;
            }
            if(entry.prev != null) entry.prev.next = entry.next;
            if(entry.next != null) entry.next.prev = entry.prev;
            if(head != null){
                entry.next = head;
                head.prev = entry;
            }
            head = entry;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void addKey(K key, V value, long ttl){
        readWriteLock.writeLock().lock();
        try {
            CacheEntry<K,V> entry = new CacheEntry<>(key, value,ttl);
            if(keyMap.keySet().size() >= maxCapacity){
                // remove last accessed entry from Doubly linked list
                K tailKey = tail.key;
                keyMap.remove(tailKey);
                tail = tail.prev;
                if(tail == null) head = null; // list is empty
                else tail.next = null;
            }
            moveFront(entry);
            keyMap.put(key, entry);
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public V get(K key) {
        readWriteLock.readLock().lock();
        try {
            if(keyMap.containsKey(key)){
                CacheEntry<K,V> entry = keyMap.get(key);
                if(entry.lastAccessTime + entry.ttlInMillis < System.currentTimeMillis()){
                    return null;
                }
                moveFront(entry);
                return entry.value;
            }
            return null;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void put(K key, V value, long ttlInMillis) {
        readWriteLock.writeLock().lock();

        try {
            if(keyMap.containsKey(key)) { // already exists
                CacheEntry<K, V> entry = keyMap.get(key);
                moveFront(entry);
                entry.value = value;
                entry.ttlInMillis = ttlInMillis;
                return;
            }
            addKey(key, value, ttlInMillis);
        } finally {
            readWriteLock.writeLock().unlock();

        }
    }

    @Override
    public void remove(K key) {
        readWriteLock.writeLock().lock();
        try {
            if(!keyMap.containsKey(key)) return;
            CacheEntry<K,V> entry = keyMap.get(key);
            if(head == entry){
                head = head.next;
                head.prev = null;
            }
            else if(tail == entry){
                tail = tail.prev;
                tail.next = null;
            }else{
                entry.prev.next = entry.next;
                entry.next.prev = entry.prev;
            }
            keyMap.remove(key);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void clear() {

    }

    private void cleanup() {
        readWriteLock.writeLock().lock();

        /**
         * !!!!!!!! Wrong approach
         * Never modify collection while traversing through it will cause ConcurrentModificationExceptiob
         *
         */
        try {
//            for (K key: keyMap.keySet()){
//                CacheEntry<K,V> entry = keyMap.get(key);
//                if(entry.lastAccessTime + entry.ttlInMillis < System.currentTimeMillis()){
//                    remove(key);
//                }
//            }

            Iterator<Map.Entry<K,CacheEntry<K,V>>> itr = keyMap.entrySet().iterator();
            while (itr.hasNext()){
                Map.Entry<K, CacheEntry<K,V>> entry = itr.next();
                CacheEntry<K,V> cacheEntry = entry.getValue();
                if(cacheEntry.lastAccessTime + cacheEntry.ttlInMillis < System.currentTimeMillis()){
                    itr.remove();
                }
            }

        }finally {
            readWriteLock.writeLock().unlock();

        }
    }
}