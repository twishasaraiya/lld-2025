package org.example.atlassian.costExplorer.service.impl;

import org.example.atlassian.costExplorer.entities.Plan;
import org.example.atlassian.costExplorer.entities.Subscription;
import org.example.atlassian.costExplorer.service.IUserManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager implements IUserManager {

    private Map<String, Subscription> subscriptions;

    public UserManager() {
        this.subscriptions = new ConcurrentHashMap<>();
    }

    @Override
    public String createSubscription(String clientId, Plan plan) {
        Subscription newSubscription = new Subscription(clientId, plan);
        Subscription existing = subscriptions.putIfAbsent(clientId, newSubscription);
        if(newSubscription != null){
            throw new RuntimeException("A subscription already exists for client");
        }
        return newSubscription.getClientId();
    }

    @Override
    public boolean updateSubscription(String subscriptionId, Plan newPlan) {
        return false;
    }

    @Override
    public Subscription getSubscriptionByClientId(String clientId) {
        Subscription subscription = subscriptions.get(clientId);
        if(subscription == null){
            throw new RuntimeException("Subscription already exists");
        }
        return subscription;
    }
}
