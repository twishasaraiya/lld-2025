package org.example.atlassian.costExplorer.entities;

import java.util.Map;

public class ConsumptionMetrics {
    private String clientId;
    private Map<Integer, ClientMetrics> monthlyMetrics;

    public ConsumptionMetrics(String clientId, Map<Integer, ClientMetrics> monthlyMetrics) {
        this.clientId = clientId;
        this.monthlyMetrics = monthlyMetrics;
    }

    public String getClientId() {
        return clientId;
    }

    public Map<Integer, ClientMetrics> getMonthlyMetrics() {
        return monthlyMetrics;
    }
}
