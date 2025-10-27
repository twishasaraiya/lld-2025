package org.example.atlassian.costExplorer.entities;

public class Plan {
    private final PlanType planType;
    private final Quota quota;

    public Plan(PlanType planType, Quota quota) {
        this.planType = planType;
        this.quota = quota;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public Quota getQuota() {
        return quota;
    }
}
