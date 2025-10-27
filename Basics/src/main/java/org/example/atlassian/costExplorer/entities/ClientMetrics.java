package org.example.atlassian.costExplorer.entities;

import java.time.LocalDate;

public class ClientMetrics {
    private Integer storageUsed;
    private Integer registeredUsers;
    private LocalDate localDate;

    public ClientMetrics(Integer storageUsed, Integer registeredUsers) {
        this.storageUsed = storageUsed;
        this.registeredUsers = registeredUsers;
        this.localDate = localDate.now();
    }

    public Integer getStorageUsed() {
        return storageUsed;
    }

    public Integer getRegisteredUsers() {
        return registeredUsers;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public Integer getMonth(){
        return localDate.getMonthValue();
    }
}
