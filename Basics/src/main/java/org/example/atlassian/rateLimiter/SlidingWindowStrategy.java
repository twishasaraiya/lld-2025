package org.example.atlassian.rateLimiter;

import org.example.atlassian.rateLimiter.entities.RateLimitConfig;
import org.example.atlassian.rateLimiter.entities.Request;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SlidingWindowStrategy implements IRateLimiter{
    private RateLimitConfig rateLimitConfig;
    private Deque<Long> slidingWindowQueue;

    public SlidingWindowStrategy(RateLimitConfig config) {
        this.rateLimitConfig = config;
        this.slidingWindowQueue = new ConcurrentLinkedDeque<>();
    }

    @Override
    public boolean isAllowed(Request request) {
        // remove elements from left side of the queue
        long currTime = System.currentTimeMillis();
        long windowStart = currTime - rateLimitConfig.getTimePeriodInSeconds()*1000;
        while(!slidingWindowQueue.isEmpty() && slidingWindowQueue.getFirst() < windowStart){
            slidingWindowQueue.pollFirst();
        }
        if(slidingWindowQueue.size() < rateLimitConfig.getMaxRequestsInTimePeriod()){
            slidingWindowQueue.addLast(currTime);
            return true;
        }
        return false;
    }

    @Override
    public void setConfig(RateLimitConfig config) {
        this.rateLimitConfig = config;
    }
}
