package org.example.atlassian.costExplorer;

import org.example.atlassian.costExplorer.entities.*;
import org.example.atlassian.costExplorer.service.IMetricsManager;
import org.example.atlassian.costExplorer.service.IPricingStrategy;
import org.example.atlassian.costExplorer.service.IUserManager;

public class CostExplorer {
    private IUserManager userManager;
    private IMetricsManager metricsManager;
    private IPricingStrategy pricingStrategy;

    public CostExplorer(IUserManager userManager, IMetricsManager metricsManager, IPricingStrategy pricingStrategy) {
        this.userManager = userManager;
        this.metricsManager = metricsManager;
        this.pricingStrategy = pricingStrategy;
    }

    public String createSubscription(String clientId, PlanType planType){
        Plan plan = PlanFactory.getPlan(planType);
        return this.userManager.createSubscription(clientId, plan);
    }

    public void recordMetrics(String clientId, Integer totalUsers, Integer currentStorage){
        ClientMetrics clientMetrics = new ClientMetrics(currentStorage, totalUsers);
        this.metricsManager.recordMetrics(clientId, clientMetrics);
    }

    public CostReport generateCostReport(String clientId, int year){
        Subscription subscription = userManager.getSubscriptionByClientId(clientId);
        ConsumptionMetrics consumptionMetrics = metricsManager.getMonthlyUsageByClient(clientId, year);
        return this.pricingStrategy.calculatePrice(subscription, consumptionMetrics);
    }
}
