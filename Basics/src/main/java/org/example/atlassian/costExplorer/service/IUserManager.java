package org.example.atlassian.costExplorer.service;

import org.example.atlassian.costExplorer.entities.Plan;
import org.example.atlassian.costExplorer.entities.Subscription;

public interface IUserManager {
    String createSubscription(String clientId, Plan plan);
    boolean updateSubscription(String subscriptionId, Plan newPlan);
    Subscription getSubscriptionByClientId(String clientId);
}
