package org.example.CircuitBreaker;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        CircuitBreakerConfig config = new CircuitBreakerConfig(5000, 2, 1000, 2);
        CircuitBreaker circuitBreaker = new CircuitBreaker(config);
        // happy state
//        for(int i = 0; i <10; i++) {
//            try {
//                circuitBreaker.intercept(() -> "Api call 200 OK " + System.currentTimeMillis());
//            }catch (Exception e){
//                System.err.println("Exception " + e);
//            }
//        }

        // should close
        for(int i = 0; i <10; i++) {
            try{
                Thread.sleep(1000);
                if(i < 5){
                    circuitBreaker.intercept(() -> {
                        throw new RuntimeException("API error ");
                    });
                }else{
                    circuitBreaker.intercept(() -> "Api call 200 OK " + System.currentTimeMillis());
                }
            }catch (Exception e){
                System.err.println("Exception " + e);
            }
        }

        Thread.sleep(10000);
    }
}

class CircuitBreakerConfig{
    int windowInMills;
    int maxFailureCalls;
    int coolOffPeriodInMillis;
    int maxHalfOpenFailCalls;

    public CircuitBreakerConfig(int windowInMills, int maxFailureCalls, int coolOffPeriodInMillis, int maxHalfOpenFailCalls) {
        this.windowInMills = windowInMills;
        this.maxFailureCalls = maxFailureCalls;
        this.coolOffPeriodInMillis = coolOffPeriodInMillis;
        this.maxHalfOpenFailCalls = maxHalfOpenFailCalls;
    }
}
class CircuitBreaker {
    enum State {
        OPEN,
        CLOSED,
        HALF_OPEN
    }

    private AtomicReference<State> currState;
    private CircuitBreakerConfig config;
    private AtomicReference<Long> lastOpenTime;
    private BlockingQueue<Long> errorWindow;
    private AtomicInteger maxHalfOpenCalls;

    public CircuitBreaker(CircuitBreakerConfig config) {
        this.currState = new AtomicReference<>(State.CLOSED);
        this.config = config;
        this.lastOpenTime = new AtomicReference<>(-1L);
        this.errorWindow = new LinkedBlockingDeque<>(config.maxFailureCalls);
        this.maxHalfOpenCalls = new AtomicInteger(0);
    }

    public <R> R intercept(Supplier<R> apiCall){
        State state = currState.get();
        System.out.println("Current State " + state);
        switch (state){
            case OPEN:
                long diff = System.currentTimeMillis() - lastOpenTime.get();
                if(diff >= config.coolOffPeriodInMillis){
                    if(currState.compareAndSet(State.OPEN, State.HALF_OPEN)) {
                        System.out.println("state change from open to half open");
                        maxHalfOpenCalls.set(0);
                        return makeApiCall(apiCall);
                    }
                }
                throw new RuntimeException("Circuit Breaker is open!");
            case HALF_OPEN:
                if(maxHalfOpenCalls.incrementAndGet() > config.maxHalfOpenFailCalls){
                    maxHalfOpenCalls.decrementAndGet();
                    if(currState.compareAndSet(State.HALF_OPEN, State.OPEN)){
                        lastOpenTime.set(System.currentTimeMillis());
                    }
                    throw new RuntimeException("Too many half open calls, moving back to open state");
                }
                maxHalfOpenCalls.decrementAndGet();
                // allow api call in half open state
                return makeApiCall(apiCall);
            case CLOSED:
                return makeApiCall(apiCall);
            default:
                throw new RuntimeException("Incorrect circuit breaker state " + state.toString());
        }
    }

    private <T> T makeApiCall(Supplier<T> apiCall){
        try {
            T result = apiCall.get();
            currState.compareAndSet(State.HALF_OPEN, State.CLOSED); // if success close again
            return result;
        }catch (Exception e){
            synchronized (this){
                long now = System.currentTimeMillis();
                long windowStart = now - config.windowInMills;
                // remove candidates from window
                while(!errorWindow.isEmpty() && errorWindow.peek() < windowStart){
                    errorWindow.poll();
                }
                errorWindow.offer(now);
                if(errorWindow.size() >= config.maxFailureCalls){
                    currState.set(State.OPEN);
                    lastOpenTime.set(now);
                    throw new RuntimeException("Open Circuit breaker");
                }
                throw new RuntimeException(e);
            }
        }
    }
}
