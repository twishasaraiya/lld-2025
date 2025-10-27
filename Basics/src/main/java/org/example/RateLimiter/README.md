Problem Statement
You're building a Rate Limiting Service for a high-traffic API gateway that handles 1 million requests per second across 100+ microservices. The system must support multiple rate limiting algorithms, different granularities, and work across a distributed cluster of servers.

Business Context
Real-world scenario: You're at a company like Stripe, Netflix, or Amazon where API rate limiting is critical for:

Preventing abuse from malicious clients
Ensuring fair usage across all customers
Protecting downstream services from overload
Implementing tiered pricing (free vs premium limits)
Compliance requirements (PCI-DSS for financial APIs)


Requirements
Phase 1 - Core Rate Limiting (Must Complete - 30 minutes)
Functional Requirements:

Multiple Rate Limiting Algorithms:

Token Bucket - Allow bursts up to capacity
Fixed Window - Reset counter at fixed intervals
Sliding Window - More accurate but memory intensive


Multi-Granularity Support:

Per User - Different limits for different user tiers
Per IP Address - Prevent IP-based attacks
Per API Endpoint - Different limits per resource
Composite Keys - e.g., user+endpoint, IP+endpoint


Core Operations:

isAllowed(key, algorithm, limit, windowSize) - Check if request allowed
getRemainingQuota(key) - Get remaining allowance
reset(key) - Manual reset for admin operations



Non-Functional Requirements:

Ultra-low latency - < 1ms p99 latency for rate limit checks
High throughput - 1M+ requests/second per server
Thread-safe - Concurrent access from multiple threads
Memory efficient - Minimal overhead per tracked key