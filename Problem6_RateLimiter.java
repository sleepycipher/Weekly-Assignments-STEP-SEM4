import java.util.*;

public class Problem6_RateLimiter {

    // Inner class: Token Bucket per client
    static class TokenBucket {
        int tokens;
        long lastRefillTime;
        int maxTokens;
        long refillIntervalMs; // interval for full refill in ms

        TokenBucket(int maxTokens, long refillIntervalMs) {
            this.maxTokens = maxTokens;
            this.tokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
            this.refillIntervalMs = refillIntervalMs;
        }

        // Refill tokens based on elapsed time
        void refill() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefillTime;
            if (elapsed >= refillIntervalMs) {
                tokens = maxTokens;
                lastRefillTime = now;
            }
        }

        // Try to consume one token
        synchronized boolean consume() {
            refill();
            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }

        long getResetTimeSeconds() {
            long elapsed = System.currentTimeMillis() - lastRefillTime;
            return Math.max(0, (refillIntervalMs - elapsed) / 1000);
        }
    }

    // HashMap<clientId, TokenBucket>
    private HashMap<String, TokenBucket> clientBuckets = new HashMap<>();

    private final int MAX_REQUESTS;
    private final long REFILL_INTERVAL_MS;

    public Problem6_RateLimiter(int maxRequestsPerHour) {
        this.MAX_REQUESTS = maxRequestsPerHour;
        this.REFILL_INTERVAL_MS = 3600 * 1000L; // 1 hour in ms
    }

    // Register a new client
    private void registerClient(String clientId) {
        clientBuckets.putIfAbsent(clientId, new TokenBucket(MAX_REQUESTS, REFILL_INTERVAL_MS));
    }

    // Check rate limit for a client - O(1)
    public String checkRateLimit(String clientId) {
        registerClient(clientId);
        TokenBucket bucket = clientBuckets.get(clientId);
        boolean allowed = bucket.consume();

        if (allowed) {
            return "ALLOWED: " + clientId + " (" + bucket.tokens + " requests remaining)";
        } else {
            long retryAfter = bucket.getResetTimeSeconds();
            return "DENIED: " + clientId + " (0 remaining, retry after " + retryAfter + "s)";
        }
    }

    // Get current rate limit status for a client
    public void getRateLimitStatus(String clientId) {
        registerClient(clientId);
        TokenBucket bucket = clientBuckets.get(clientId);
        bucket.refill();
        System.out.println("\nRate Limit Status for [" + clientId + "]:");
        System.out.println("  Used    : " + (MAX_REQUESTS - bucket.tokens));
        System.out.println("  Remaining: " + bucket.tokens);
        System.out.println("  Limit   : " + MAX_REQUESTS);
        System.out.println("  Resets in: " + bucket.getResetTimeSeconds() + "s");
    }

    // Get all clients and their remaining tokens
    public void printAllClients() {
        System.out.println("\n--- All Registered Clients ---");
        for (Map.Entry<String, TokenBucket> entry : clientBuckets.entrySet()) {
            entry.getValue().refill();
            System.out.printf("  %-15s : %d/%d tokens remaining%n",
                              entry.getKey(), entry.getValue().tokens, MAX_REQUESTS);
        }
    }

    public static void main(String[] args) {
        // 10 requests per "hour" for demo (actually resets after 1 hour)
        Problem6_RateLimiter limiter = new Problem6_RateLimiter(10);

        System.out.println("=== Problem 6: Distributed Rate Limiter for API Gateway ===\n");
        System.out.println("(Max 10 requests per client for demo)\n");

        // Client abc123 - use up tokens
        for (int i = 0; i < 12; i++) {
            System.out.println(limiter.checkRateLimit("abc123"));
        }

        // Client xyz789 - separate bucket
        System.out.println("\n--- Client xyz789 (fresh bucket) ---");
        System.out.println(limiter.checkRateLimit("xyz789"));
        System.out.println(limiter.checkRateLimit("xyz789"));

        limiter.getRateLimitStatus("abc123");
        limiter.getRateLimitStatus("xyz789");

        limiter.printAllClients();
    }
}
