package org.example.atlassian.costExplorer;

public class Main {
}

/**
 *
 * Customer
 * Product (Jira)
 * Bill
 *  - Map<Month, Cost> monthlyBills;
 *  - double yearlyCost;
 *  - user
 *
 * - Cost
 *  - double price
 *  - long unitsUsed // number of hits
 *
 * Quota
 *   - long storageQuota
 *   - long usersAllowed
 *   - long apiCallsAllowed
 *
 *  ConsumptionMetrics
 *  - long numberOfApiCalls
 *  - numberOfUsers;
 *  - storageConsumption
 *
 *  enum PlanType {
 *      BASIC,
 *      PREMIUM,
 *      ENTERPRISE
 *
 *     - Quota for each plan type is defined in a pricing
 *  }
 *
 *
 *  PricingStrategy
 *  - double calculatePrice(Plan currentPlan, ConsumptionMetrics currMetrics));
 *
 *
 * IUserPlanManager
 * - Map<clientId, PlanType> userPlans;
 * - registerPlanForClient(clientId, PlanType)
 *
 * IMetricsManager
 *  - Map<clientId, Map<Month, ConsumptionMetrics>> userConsumption;
 *  - void addConsumptionMetrics(User clientId, ConsumptionMetrics metrics, LocalDate date);
 *
 * CostExplorer (Interface)
 *
 * - Bill getEstimateCost(LocalDate from, LocalDate to);
 *
 */