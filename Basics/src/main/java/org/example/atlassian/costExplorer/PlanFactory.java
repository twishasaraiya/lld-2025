package org.example.atlassian.costExplorer;

import org.example.atlassian.costExplorer.entities.Plan;
import org.example.atlassian.costExplorer.entities.PlanType;
import org.example.atlassian.costExplorer.entities.Quota;

import java.util.Map;

public class PlanFactory {

    private static final Map<PlanType, Plan> plans = Map.of();
    private final Map<PlanType, Quota> quotas;
    public PlanFactory(Map<PlanType, Quota> quotas) {
        this.quotas = quotas;
        for (PlanType type : PlanType.values()){
            plans.put(type, new Plan(type, quotas.get(type)));
        }
    }

    public static Plan getPlan(PlanType planType){
        if(!plans.containsKey(planType)){
            throw new RuntimeException("Invalid plan");
        }
        return plans.get(planType);
    }
}
