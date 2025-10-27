package org.example.atlassian.costExplorer.service;

import org.example.atlassian.costExplorer.entities.ConsumptionMetrics;
import org.example.atlassian.costExplorer.entities.CostReport;
import org.example.atlassian.costExplorer.entities.Subscription;

public interface IPricingStrategy {
    CostReport calculatePrice(Subscription subscription, ConsumptionMetrics metrics);
}
