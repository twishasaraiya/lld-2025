
At Databricks, we have multiple microservices communicating with each other via APIs. We need to implement a Circuit Breaker pattern to handle service failures gracefully and prevent cascading failures.
Requirements:

Failure Detection:

Circuit breaker should open the circuit and fail fast if API error count for the last X minutes >= Y errors
Track errors in a sliding time window


Recovery Mechanism:

Circuit should close and start receiving requests after a cooldown period of Z minutes
Gradual recovery (half-open state) to test service health


Thread Safety:

Multiple threads will be making API calls concurrently
Circuit breaker state must be consistent across all threads+


## Entities

- Service
  - endpoint[]
- CircuitBreaker
  - config
    - windowInMins
    - failureRate
    - coolOffPeriod