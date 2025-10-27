package org.example.atlassian.costExplorer.service;

import org.example.atlassian.costExplorer.entities.ClientMetrics;
import org.example.atlassian.costExplorer.entities.ConsumptionMetrics;

import java.util.Map;

public interface IMetricsManager {
    void recordMetrics(String clientId, ClientMetrics clientMetrics);
    ConsumptionMetrics getMonthlyUsageByClient(String clientId, int year);
}
