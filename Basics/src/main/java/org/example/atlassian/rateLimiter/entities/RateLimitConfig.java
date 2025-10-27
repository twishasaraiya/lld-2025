package org.example.atlassian.rateLimiter.entities;

public class RateLimitConfig {
    private Integer maxRequestsInTimePeriod;
    private Long timePeriodInSeconds;
    private Long refillRateInSeconds;

    public RateLimitConfig(Integer maxRequestsInTimePeriod, Long timePeriodInSeconds, Long refillRateInSeconds) {
        this.maxRequestsInTimePeriod = maxRequestsInTimePeriod;
        this.timePeriodInSeconds = timePeriodInSeconds;
        this.refillRateInSeconds = refillRateInSeconds;
    }

    public Integer getMaxRequestsInTimePeriod() {
        return maxRequestsInTimePeriod;
    }

    public Long getTimePeriodInSeconds() {
        return timePeriodInSeconds;
    }

    public Long getRefillRateInSeconds() {
        return refillRateInSeconds;
    }
}
