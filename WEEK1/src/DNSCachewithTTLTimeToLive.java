import java.util.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;

    public DNSEntry(String domain, String ipAddress, int ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class DNSCachewithTTLTimeToLive {

    private final int MAX_CACHE_SIZE = 5;

    // LRU cache using LinkedHashMap
    LinkedHashMap<String, DNSEntry> cache = new LinkedHashMap<>(16, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };

    int cacheHits = 0;
    int cacheMisses = 0;

    // Simulate upstream DNS lookup
    private String queryUpstreamDNS(String domain) {
        return "172.217." + new Random().nextInt(255) + "." + new Random().nextInt(255);
    }

    public String resolve(String domain) {

        DNSEntry entry = cache.get(domain);

        if (entry != null) {
            if (!entry.isExpired()) {
                cacheHits++;
                return "Cache HIT → " + entry.ipAddress;
            } else {
                cache.remove(domain);
                System.out.println("Cache EXPIRED → querying upstream...");
            }
        }

        cacheMisses++;

        String ip = queryUpstreamDNS(domain);
        DNSEntry newEntry = new DNSEntry(domain, ip, 10); // TTL 10 seconds
        cache.put(domain, newEntry);

        return "Cache MISS → " + ip;
    }

    public void getCacheStats() {
        int total = cacheHits + cacheMisses;
        double hitRate = total == 0 ? 0 : ((double) cacheHits / total) * 100;

        System.out.println("Cache Hits: " + cacheHits);
        System.out.println("Cache Misses: " + cacheMisses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }

    public static void main(String[] args) throws Exception {

        DNSCachewithTTLTimeToLive dns = new DNSCachewithTTLTimeToLive();

        System.out.println(dns.resolve("google.com"));
        System.out.println(dns.resolve("google.com"));

        Thread.sleep(11000); // wait for TTL expiry

        System.out.println(dns.resolve("google.com"));

        dns.getCacheStats();
    }
}
