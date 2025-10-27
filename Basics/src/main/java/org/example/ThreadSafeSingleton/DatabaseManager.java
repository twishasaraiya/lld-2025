package org.example.ThreadSafeSingleton;

public class DatabaseManager {
    private volatile static DatabaseManager dbManager;
    private DatabaseManager(){
        System.out.println(Thread.currentThread().getName());
    }

    public static DatabaseManager getDatabaseManager(){
        if (dbManager == null){
            synchronized (DatabaseManager.class){
                if (dbManager == null){
                    dbManager = new DatabaseManager();
                }
            }
        }
        return dbManager;
    }
}
