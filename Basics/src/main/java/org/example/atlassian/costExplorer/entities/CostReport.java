package org.example.atlassian.costExplorer.entities;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CostReport {
    private Map<Integer, BigDecimal> monthlyBills;
    private BigDecimal yearlyCost;
    private String clientId;

    public CostReport(Map<Integer, BigDecimal> monthlyBills, BigDecimal yearlyCost, String clientId) {
        this.monthlyBills = monthlyBills;
        this.yearlyCost = yearlyCost;
        this.clientId = clientId;
    }
}
