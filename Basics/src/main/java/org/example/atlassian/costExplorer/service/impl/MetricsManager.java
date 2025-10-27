package org.example.atlassian.costExplorer.service.impl;

import org.example.atlassian.costExplorer.entities.ClientMetrics;
import org.example.atlassian.costExplorer.entities.ConsumptionMetrics;
import org.example.atlassian.costExplorer.service.IMetricsManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MetricsManager implements IMetricsManager {
    Map<String, Set<ClientMetrics>> clientMetricsMap;

    public MetricsManager() {
        this.clientMetricsMap = new ConcurrentHashMap<>();
    }

    @Override
    public void recordMetrics(String clientId, ClientMetrics clientMetrics) {
        this.clientMetricsMap.computeIfAbsent(clientId, k -> new TreeSet<>(Comparator.comparing(ClientMetrics::getLocalDate)))
                .add(clientMetrics);
    }

    @Override
    public ConsumptionMetrics getMonthlyUsageByClient(String clientId, int year) {
        Map<Integer, ClientMetrics> yearMetrics = clientMetricsMap.get(clientId)
                .stream()
                .filter(clientMetrics -> clientMetrics.getLocalDate().getYear() == year)
                .collect(Collectors.toMap(ClientMetrics::getMonth, Function.identity()));

        return new ConsumptionMetrics(clientId, yearMetrics);
    }
}
