package org.example.atlassian.rateLimiter;

import org.example.atlassian.rateLimiter.entities.RateLimitConfig;
import org.example.atlassian.rateLimiter.entities.Request;

import java.util.concurrent.atomic.AtomicLong;

public class TokenBucketStrategy implements IRateLimiter{
    private RateLimitConfig rateLimitConfig;
    private AtomicLong lastAccessTime;
    private AtomicLong currTokens;

    public TokenBucketStrategy(RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
    }

    @Override
    public boolean isAllowed(Request request) {
        /**
         * 1. refill bucket
         * 2. check if token exists
         */
        long currTime = System.currentTimeMillis();
        long timeDiff = currTime - lastAccessTime.get();
        long extraTokens = timeDiff * rateLimitConfig.getRefillRateInSeconds()/1000;
        if(lastAccessTime.compareAndSet(currTime, lastAccessTime.get())){
           currTokens.getAndUpdate(curr -> Math.min(curr + extraTokens, rateLimitConfig.getMaxRequestsInTimePeriod()));
        }

        long curr = currTokens.get();
        if(curr > 0 && currTokens.compareAndSet(curr, curr - 1)){
            return true;
        }
        return false;
    }

    @Override
    public void setConfig(RateLimitConfig config) {

    }
}
