package org.example.RateLimiter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {

    public static void main(String[] args) {
        RateLimiterFacade rl = new RateLimiterFacade();
        ClientConfig clientConfig = ClientConfig.Builder.builder()
                .setGranularity(Granularity.PER_USER, new LimitConfig(RateLimitingAlgorithm.TOKEN_BUCKET, 10, 1))
                .setGranularity(Granularity.PER_IP, new LimitConfig(RateLimitingAlgorithm.TOKEN_BUCKET, 0, 0)) // block
                .build();
        String clientKey = rl.registerClient(clientConfig);
        Request request = new Request( "user123", "192.168.1.1", "/api/endpoint");
        rl.isAllowed(clientKey, request);
    }
}

/**
 *
 * RateLimitingAlgorithm
 * GranularityLevel
 */

enum RateLimitingAlgorithm{
    TOKEN_BUCKET,
    SLIDING_WINDOW
}

class Request{
    String userId;
    String ipAddress;
    String endpoint;

    public Request(String userId, String ipAddress, String endpoint) {
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.endpoint = endpoint;
    }

    public String getIdentifier(Granularity granularity){
        switch (granularity){
            case PER_IP: return ipAddress;
            case PER_USER: return userId;
            case PER_ENDPOINT: return endpoint;
        }
        throw new RuntimeException("Invalid granualarity");
    }
}
interface RateLimiter{
    boolean isAllowed(Request request);
    long getRemainingQuota();
}

enum Granularity{
    PER_USER("userId", 1),
    PER_IP("ipAddress", 2),
    PER_ENDPOINT("endpoint", 3),
    COMPOSITE("", 4);

    String requestIdentifierKey;
    int priority;
    Granularity(String requestIdentifierKey, int priority) {
        this.requestIdentifierKey = requestIdentifierKey;
        this.priority = priority;
    }

    public void setComposite(String requestIdentifierKey){
        if(this.equals(COMPOSITE)){
            this.requestIdentifierKey = requestIdentifierKey;
        }
    }

}
class ClientConfig {
    Map<Granularity, LimitConfig> limitConfigMap;

    public ClientConfig(Builder builder) {
        this.limitConfigMap = builder.limitConfigMap;
    }

    public static class Builder {
        private Map<Granularity, LimitConfig> limitConfigMap;

        private Builder() {
            this.limitConfigMap = new HashMap<>();
        }

        public static Builder builder(){
             return new Builder();
         }
         public Builder setGranularity(Granularity granularity, LimitConfig limitConfig){
            this.limitConfigMap.put(granularity, limitConfig);
            return this;
         }

         public ClientConfig build(){
            return new ClientConfig(this);
         }
    }
}

class LimitConfig{
    RateLimitingAlgorithm rateLimitingAlgorithm;
    long maxTokens;
    long refillRate;

    public LimitConfig(RateLimitingAlgorithm rateLimitingAlgorithm, long maxTokens, long refillRate) {
        this.rateLimitingAlgorithm = rateLimitingAlgorithm;
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
    }
}

class RateLimiterFactory{
    public static RateLimiter getRateLimiter(LimitConfig clientConfig){

        RateLimitingAlgorithm algorithm = clientConfig.rateLimitingAlgorithm;
        switch (algorithm){
            case TOKEN_BUCKET: return new TokenBucketRateLimiter(clientConfig);
            default:
                throw new IllegalArgumentException("Algorithm " + algorithm + " not supported");
        }

    }
}
class RateLimiterFacade{
    Map<String, Map<Granularity, LimitConfig>> clients;
    Map<String, RateLimiter> rateLimiterMap;

    public RateLimiterFacade() {
        this.clients = new ConcurrentSkipListMap<>();
        this.rateLimiterMap = new ConcurrentHashMap<>();
    }

    String registerClient(ClientConfig clientConfig){
        String key = UUID.randomUUID().toString();
        Map<Granularity, LimitConfig> configs = new ConcurrentSkipListMap<>((a,b) -> a.priority - b.priority);
        for(Map.Entry<Granularity, LimitConfig> entry: clientConfig.limitConfigMap.entrySet()){
            configs.put(entry.getKey(), entry.getValue());
        }
        this.clients.put(key, configs);
        return key;
    }

    public boolean isAllowed(String clientKey, Request request){
        if(!clients.containsKey(clientKey)){
            throw new RuntimeException("Not registered. Please register first");
        }
        for(Map.Entry<Granularity, LimitConfig> config:  clients.get(clientKey).entrySet()){
            Granularity granularity = config.getKey();
            String identifier = request.getIdentifier(granularity);

            String rateLimiterkey = clientKey + ":" + granularity.name() + ":" + identifier;
            if(!rateLimiterMap.containsKey(rateLimiterkey)){
                rateLimiterMap.put(rateLimiterkey, RateLimiterFactory.getRateLimiter(config.getValue()));
            }
            if(!rateLimiterMap.get(rateLimiterkey).isAllowed(request)){
                return false; // break if it is not allowed at lowest priority
            }
        }
        return true;
    }

    public long getRemainingQuota(String clientKey, String identifierVal){
        if(!clients.containsKey(clientKey)){
            throw new RuntimeException("Not registered. Please register first");
        }
        Granularity granularity = Granularity.valueOf(identifierVal);
        if(granularity == null || !clients.get(clientKey).containsKey(granularity)){
            throw new RuntimeException("Invalid request indentifier");
        }
        Optional<Map.Entry<Granularity, LimitConfig>> finalConfig = clients.get(clientKey).entrySet()
                .stream()
                .filter(entry -> identifierVal.equals(entry.getKey().requestIdentifierKey))
                .findFirst();

        if(finalConfig.isEmpty()){
            throw new RuntimeException("Invalid identifier");
        }

        return rateLimiterMap.get(identifierVal).getRemainingQuota();
    }

}

class TokenBucketRateLimiter implements RateLimiter{

    LimitConfig clientConfig;
    AtomicReference<State> state;
    class State{
        long currTokens;
        long lastRefillTime;

        public State(long currTokens, long lastRefillTime) {
            this.currTokens = currTokens;
            this.lastRefillTime = lastRefillTime;
        }
    }
    public TokenBucketRateLimiter(LimitConfig clientConfig) {
        this.clientConfig = clientConfig;
        this.state = new AtomicReference<>(new State(clientConfig.maxTokens, System.nanoTime()));
    }

    @Override
    public boolean isAllowed(Request request) {
        while (true){
            State currState = state.get();
            State newState = refill(currState);
            if(newState.currTokens <= 0) {
                state.compareAndSet(currState, new State(0, System.nanoTime()));
                return false;
            }
            State finalState = new State(newState.currTokens - 1, newState.lastRefillTime);
            if(state.compareAndSet(currState, finalState)){
                return true;
            }
        }
    }

    private State refill(State state){
        long time = System.nanoTime();
        long timeDiff = time - state.lastRefillTime;
        long newTokens = Math.min(state.currTokens + (timeDiff/1000000000L) * clientConfig.refillRate, clientConfig.maxTokens);
        return new State(newTokens, time);
    }
    @Override
    public long getRemainingQuota() {
        return state.get().currTokens;
    }
}