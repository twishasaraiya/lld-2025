package org.example.rippling;

import javax.xml.stream.events.Comment;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * `
 * Design a key value cache system and implement set, get, delete methods to be expected. Input would be as list of strings.
 * ["SET key1 value1"]
 * ["GET key1"]
 * ["DELETE key1"]
 *
 * Follow up: How do we handle transactions?
 * A transaction starts with BEGIN and operations like SET, GET, DELETE can take place during this time. After these operation, the transaction is ended by either a COMMIT that commits everything permanently in the data store or ROLLBACK that reverts everything that was performed during the transaction window.
 * We need to implement commit and rollback methods
 */
public class KeyValueStore implements IKVStore{

    Map<String, String> kv;
    Stack<Transaction> transactionStack;

    public KeyValueStore() {
        this.kv = new ConcurrentHashMap<>();
        this.transactionStack = new Stack<>();
    }

    @Override
    public void set(String key, String val) {
        if(transactionStack.isEmpty()){
            kv.put(key, val);
            return;
        }
        Transaction t = transactionStack.peek();
        t.snapshot.put(key,val);

    }

    @Override
    public String get(String key) {
        if(transactionStack.isEmpty()){
            return kv.get(key);
        }
        Transaction t = transactionStack.peek();
        return t.snapshot.get(key);
    }

    @Override
    public void delete(String key) {
        if(transactionStack.isEmpty()){
            kv.remove(key); // how would delete work, how do we mark keys for deletion
            return;
        }
        Transaction t = transactionStack.peek();
        t.snapshot.put(key, null);
    }

    @Override
    public void begin() {
        Transaction t = new Transaction(kv);
        transactionStack.add(t);
    }

    @Override
    public void commit() {
        if(!transactionStack.isEmpty()){
            Transaction t = transactionStack.pop();
            kv.putAll(t.snapshot);
        }
    }

    @Override
    public void rollback() {
        if(!transactionStack.isEmpty()){
            transactionStack.pop();
        }
    }
}

class Transaction{
    Map<String, String> snapshot;

    public Transaction(Map<String,String> kv) {
        this.snapshot = Map.copyOf(kv);
    }
}
interface IKVStore{
    void set(String key, String val);
    String get(String key);
    void delete(String key);
    void begin();
    void commit();
    void rollback();
}
