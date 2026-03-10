import java.util.*;

class TokenBucket {

    int maxTokens;
    int tokens;
    long lastRefillTime;

    public TokenBucket(int maxTokens) {
        this.maxTokens = maxTokens;
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }

    // Refill tokens every hour
    public synchronized void refill() {

        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastRefillTime;

        long hour = 3600000;

        if (elapsed >= hour) {
            tokens = maxTokens;
            lastRefillTime = currentTime;
        }
    }

    public synchronized boolean allowRequest() {

        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }

        return false;
    }

    public synchronized int getRemainingTokens() {
        return tokens;
    }

    public long getResetTime() {
        return lastRefillTime + 3600000;
    }
}

public class DistributedRateLimiter {

    static final int LIMIT = 1000;

    HashMap<String, TokenBucket> clients = new HashMap<>();


    public synchronized String checkRateLimit(String clientId) {

        clients.putIfAbsent(clientId, new TokenBucket(LIMIT));

        TokenBucket bucket = clients.get(clientId);

        if (bucket.allowRequest()) {

            return "Allowed (" + bucket.getRemainingTokens()
                    + " requests remaining)";
        }

        long retryAfter = (bucket.getResetTime() - System.currentTimeMillis()) / 1000;

        return "Denied (0 requests remaining, retry after "
                + retryAfter + " seconds)";
    }


    public String getRateLimitStatus(String clientId) {

        TokenBucket bucket = clients.get(clientId);

        if (bucket == null) {
            return "Client not found";
        }

        int used = LIMIT - bucket.getRemainingTokens();

        return "{used: " + used +
                ", limit: " + LIMIT +
                ", reset: " + bucket.getResetTime() + "}";
    }


    public static void main(String[] args) {

        DistributedRateLimiter limiter = new DistributedRateLimiter();

        String client = "abc123";

        System.out.println(limiter.checkRateLimit(client));
        System.out.println(limiter.checkRateLimit(client));
        System.out.println(limiter.checkRateLimit(client));

        System.out.println(limiter.getRateLimitStatus(client));
    }
}
