package org.example.atlassian.costExplorer.entities;

import java.time.LocalDate;
import java.util.UUID;

public class Subscription {
    private String id;
    private String clientId;
    private Plan plan;
    private LocalDate startDate;

    public Subscription(String clientId, Plan plan) {
        this.id = UUID.randomUUID().toString();
        this.clientId = clientId;
        this.plan = plan;
        this.startDate = LocalDate.now();
    }

    public String getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public Plan getPlan() {
        return plan;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
}
