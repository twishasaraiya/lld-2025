package org.example.atlassian.costExplorer.entities;

import java.math.BigDecimal;

public class Quota {
    private final Integer storageQuota;
    private final Integer usersAllowed;
    private final BigDecimal pricePerUserPerMonth;
    private final BigDecimal pricePerGBPerMonth;

    public Quota(Integer storageQuota, Integer usersAllowed, BigDecimal pricePerUserPerMonth, BigDecimal pricePerGBPerMonth) {
        this.storageQuota = storageQuota;
        this.usersAllowed = usersAllowed;
        this.pricePerUserPerMonth = pricePerUserPerMonth;
        this.pricePerGBPerMonth = pricePerGBPerMonth;
    }

    public BigDecimal getPricePerGBPerMonth() {
        return pricePerGBPerMonth;
    }

    public BigDecimal getPricePerUserPerMonth() {
        return pricePerUserPerMonth;
    }
}
