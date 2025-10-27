Problem Statement
Design and implement an In-Memory Cache System that supports multiple eviction policies, TTL (Time-To-Live) expiration, and high-concurrency operations. This cache will be used in a high-traffic distributed system serving millions of requests per second.

Requirements
Phase 1 - Core LRU Cache (Must Complete - 25 minutes)
Functional Requirements:

Basic Operations:

get(key) - Retrieve value, return null if not found or expired
put(key, value) - Insert/update key-value pair
remove(key) - Explicitly remove key
clear() - Remove all entries


LRU Eviction Policy:

When cache reaches max capacity, evict least recently used item
Both get() and put() operations update access order
O(1) time complexity for all operations


TTL Support:

Each entry has configurable TTL (time-to-live)
Expired entries should be automatically cleaned up
Lazy expiration during get() operations
Background cleanup for proactive memory management



Non-Functional Requirements:

Thread-safe for concurrent read/write operations
O(1) time complexity for get/put/remove operations
Memory efficient - no memory leaks from expired entries
Configurable capacity - runtime configuration support

Phase 2 - Advanced Features (If time permits - 25 minutes)

Multiple Eviction Policies:

LRU (Least Recently Used)
LFU (Least Frequently Used)
FIFO (First In, First Out)
Runtime policy switching capability


Enhanced TTL Features:

Sliding expiration - TTL resets on access
Absolute expiration - Fixed expiration regardless of access
Custom TTL per entry - Override default TTL


Monitoring & Statistics:

Cache hit/miss ratio
Eviction counts by policy
Memory usage statistics
Performance metrics (avg response time)