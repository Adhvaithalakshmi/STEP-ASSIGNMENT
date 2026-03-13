import java.util.concurrent.ConcurrentHashMap;

public class DistributedRateLimiter {

    // Token Bucket Class
    static class TokenBucket {

        private int tokens;
        private final int maxTokens;
        private final double refillRate; // tokens per second
        private long lastRefillTime;

        public TokenBucket(int maxTokens, double refillRate) {
            this.maxTokens = maxTokens;
            this.tokens = maxTokens;
            this.refillRate = refillRate;
            this.lastRefillTime = System.currentTimeMillis();
        }

        // refill tokens based on time passed
        private void refill() {
            long now = System.currentTimeMillis();
            double tokensToAdd = ((now - lastRefillTime) / 1000.0) * refillRate;

            if (tokensToAdd > 0) {
                tokens = Math.min(maxTokens, tokens + (int) tokensToAdd);
                lastRefillTime = now;
            }
        }

        // check if request allowed
        public synchronized boolean allowRequest() {
            refill();

            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }

        public synchronized int getRemainingTokens() {
            refill();
            return tokens;
        }

        public int getLimit() {
            return maxTokens;
        }
    }

    // Rate limiter map
    private static ConcurrentHashMap<String, TokenBucket> clientBuckets =
            new ConcurrentHashMap<>();

    private static final int LIMIT = 1000; // per hour
    private static final double REFILL_RATE = LIMIT / 3600.0; // tokens per second

    // Rate limit check
    public static void checkRateLimit(String clientId) {

        TokenBucket bucket = clientBuckets.computeIfAbsent(
                clientId,
                k -> new TokenBucket(LIMIT, REFILL_RATE)
        );

        boolean allowed = bucket.allowRequest();

        if (allowed) {
            System.out.println("Allowed (" +
                    bucket.getRemainingTokens() +
                    " requests remaining)");
        } else {
            System.out.println("Denied (0 requests remaining)");
        }
    }

    // Rate limit status
    public static void getRateLimitStatus(String clientId) {

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket == null) {
            System.out.println("No record for client");
            return;
        }

        int used = bucket.getLimit() - bucket.getRemainingTokens();

        System.out.println("{used: " + used +
                ", limit: " + bucket.getLimit() + "}");
    }

    // Main simulation
    public static void main(String[] args) {

        String client = "abc123";

        // simulate requests
        for (int i = 0; i < 1005; i++) {
            checkRateLimit(client);
        }

        System.out.println("\nRate Limit Status:");
        getRateLimitStatus(client);
    }
}