package org.example.atlassian.rateLimiter;

import org.example.atlassian.rateLimiter.entities.RateLimitConfig;
import org.example.atlassian.rateLimiter.entities.Request;

public interface IRateLimiter {
    boolean isAllowed(Request request);
    void setConfig(RateLimitConfig config);
}
