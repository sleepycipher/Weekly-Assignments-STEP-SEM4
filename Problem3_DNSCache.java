import java.util.*;

public class Problem3_DNSCache {

    // Inner class to represent a DNS cache entry
    static class DNSEntry {
        String domain;
        String ipAddress;
        long timestamp;       // when it was cached
        long expiryTimeMs;    // TTL in milliseconds

        DNSEntry(String domain, String ipAddress, long ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.timestamp = System.currentTimeMillis();
            this.expiryTimeMs = ttlSeconds * 1000;
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > expiryTimeMs;
        }
    }

    private HashMap<String, DNSEntry> cache = new HashMap<>();
    private int hits = 0;
    private int misses = 0;
    private int expirations = 0;

    // Simulated upstream DNS responses
    private HashMap<String, String> upstreamDNS = new HashMap<>();

    public Problem3_DNSCache() {
        // Simulate real upstream DNS data
        upstreamDNS.put("google.com", "172.217.14.206");
        upstreamDNS.put("facebook.com", "157.240.22.35");
        upstreamDNS.put("github.com", "140.82.121.4");
        upstreamDNS.put("youtube.com", "216.58.215.78");
        upstreamDNS.put("amazon.com", "176.32.98.166");
    }

    // Resolve a domain - checks cache first
    public String resolve(String domain) {
        // Check if in cache
        if (cache.containsKey(domain)) {
            DNSEntry entry = cache.get(domain);
            if (!entry.isExpired()) {
                hits++;
                System.out.println("resolve(\"" + domain + "\") -> Cache HIT -> " + entry.ipAddress);
                return entry.ipAddress;
            } else {
                expirations++;
                cache.remove(domain);
                System.out.println("resolve(\"" + domain + "\") -> Cache EXPIRED -> querying upstream...");
            }
        } else {
            misses++;
            System.out.println("resolve(\"" + domain + "\") -> Cache MISS -> querying upstream...");
        }

        // Query upstream DNS
        String ip = upstreamDNS.getOrDefault(domain, "NXDOMAIN");
        if (!ip.equals("NXDOMAIN")) {
            // Cache with 300 second TTL
            cache.put(domain, new DNSEntry(domain, ip, 300));
            System.out.println("  Cached: " + domain + " -> " + ip + " (TTL: 300s)");
        }
        return ip;
    }

    // Add entry with custom TTL
    public void addEntry(String domain, String ip, long ttlSeconds) {
        cache.put(domain, new DNSEntry(domain, ip, ttlSeconds));
        System.out.println("Added: " + domain + " -> " + ip + " (TTL: " + ttlSeconds + "s)");
    }

    // Remove expired entries manually
    public void cleanExpiredEntries() {
        int removed = 0;
        Iterator<Map.Entry<String, DNSEntry>> iterator = cache.entrySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getValue().isExpired()) {
                iterator.remove();
                removed++;
            }
        }
        System.out.println("Cleaned " + removed + " expired entries.");
    }

    // Get cache statistics
    public void getCacheStats() {
        int total = hits + misses + expirations;
        double hitRate = total > 0 ? (hits * 100.0 / total) : 0;
        System.out.println("\n=== Cache Statistics ===");
        System.out.printf("Hits: %d | Misses: %d | Expirations: %d%n", hits, misses, expirations);
        System.out.printf("Hit Rate: %.1f%%%n", hitRate);
        System.out.println("Current cache size: " + cache.size() + " entries");
    }

    public static void main(String[] args) throws InterruptedException {
        Problem3_DNSCache dns = new Problem3_DNSCache();

        System.out.println("=== Problem 3: DNS Cache with TTL ===\n");

        dns.resolve("google.com");           // MISS -> cached
        dns.resolve("google.com");           // HIT
        dns.resolve("facebook.com");         // MISS -> cached
        dns.resolve("github.com");           // MISS -> cached
        dns.resolve("google.com");           // HIT
        dns.resolve("unknown-site.xyz");     // MISS -> NXDOMAIN

        // Simulate TTL expiry by adding a very short TTL entry
        System.out.println("\n--- Adding short TTL entry (1 second) ---");
        dns.addEntry("test.com", "192.168.1.1", 1);
        dns.resolve("test.com");             // HIT

        System.out.println("\nWaiting 2 seconds for TTL expiry...");
        Thread.sleep(2000);
        dns.resolve("test.com");             // EXPIRED -> re-resolves (NXDOMAIN here)

        dns.getCacheStats();
    }
}
