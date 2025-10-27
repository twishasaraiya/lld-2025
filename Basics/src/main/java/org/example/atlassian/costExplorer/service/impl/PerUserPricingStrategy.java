package org.example.atlassian.costExplorer.service.impl;

import org.example.atlassian.costExplorer.entities.*;
import org.example.atlassian.costExplorer.service.IPricingStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PerUserPricingStrategy implements IPricingStrategy {
    @Override
    public CostReport calculatePrice(Subscription subscription, ConsumptionMetrics metrics) {
        Plan plan = subscription.getPlan();
        Map<Integer, BigDecimal> monthlyCalcPricing = metrics.getMonthlyMetrics()
                .values()
                .stream()
                .collect(Collectors.toMap(ClientMetrics::getMonth, clientMetrics ->
                    calculatePrice(clientMetrics, plan)
                ));

        Optional<Integer> lastMonth = monthlyCalcPricing.keySet().stream().max(Integer::compareTo);
        if(lastMonth.isEmpty()){
            lastMonth = Optional.of(0);
        }
        double estimatedPricing = monthlyCalcPricing
                .values()
                .stream()
                .collect(Collectors.averagingDouble(BigDecimal::doubleValue));

        for (int i = lastMonth.get()+1; i <=12; i++) {
            monthlyCalcPricing.put(i, BigDecimal.valueOf(estimatedPricing));
        }
        BigDecimal yearlyCost = monthlyCalcPricing.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CostReport(monthlyCalcPricing, yearlyCost, subscription.getClientId());
    }
    private BigDecimal calculatePrice(ClientMetrics clientMetrics, Plan plan){
        return  BigDecimal.valueOf(clientMetrics.getRegisteredUsers()).multiply(plan.getQuota().getPricePerGBPerMonth());
    }
}
