import java.util.*;

public class Problem10_MultiLevelCache {

    // Inner class for video data
    static class VideoData {
        String videoId;
        String title;
        String content; // simulated video content placeholder

        VideoData(String videoId, String title) {
            this.videoId = videoId;
            this.title = title;
            this.content = "Video data for " + videoId;
        }
    }

    // --- L1: In-memory LinkedHashMap (access-order = LRU) ---
    private static final int L1_CAPACITY = 5; // small for demo
    private LinkedHashMap<String, VideoData> l1Cache;

    // --- L2: Simulated SSD cache ---
    private static final int L2_CAPACITY = 10;
    private LinkedHashMap<String, VideoData> l2Cache;

    // --- L3: Simulated database (all videos) ---
    private HashMap<String, VideoData> l3Database = new HashMap<>();

    // --- Access count tracking (for promotion decisions) ---
    private HashMap<String, Integer> accessCount = new HashMap<>();
    private static final int PROMOTION_THRESHOLD = 3; // promote L2->L1 after 3 accesses

    // --- Stats ---
    private int l1Hits = 0, l2Hits = 0, l3Hits = 0, l1Misses = 0;
    private int totalRequests = 0;

    public Problem10_MultiLevelCache() {
        // L1: LRU via accessOrder=true
        l1Cache = new LinkedHashMap<String, VideoData>(L1_CAPACITY, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                if (size() > L1_CAPACITY) {
                    System.out.println("  [L1 Eviction] Evicted: " + eldest.getKey());
                    // Demote to L2 on eviction
                    l2Cache.put(eldest.getKey(), eldest.getValue());
                    return true;
                }
                return false;
            }
        };

        // L2: LRU
        l2Cache = new LinkedHashMap<String, VideoData>(L2_CAPACITY, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                if (size() > L2_CAPACITY) {
                    System.out.println("  [L2 Eviction] Evicted from L2: " + eldest.getKey());
                    return true;
                }
                return false;
            }
        };

        // Populate L3 database
        for (int i = 1; i <= 20; i++) {
            String id = "video_" + String.format("%03d", i);
            l3Database.put(id, new VideoData(id, "Title for " + id));
        }
    }

    // Main get method - checks L1 -> L2 -> L3
    public VideoData getVideo(String videoId) {
        totalRequests++;
        System.out.println("\n>> getVideo(\"" + videoId + "\")");

        // --- Check L1 ---
        if (l1Cache.containsKey(videoId)) {
            l1Hits++;
            System.out.println("   L1 Cache HIT (0.5ms)");
            accessCount.put(videoId, accessCount.getOrDefault(videoId, 0) + 1);
            return l1Cache.get(videoId);
        }

        // --- Check L2 ---
        if (l2Cache.containsKey(videoId)) {
            l2Hits++;
            System.out.println("   L1 Cache MISS");
            System.out.println("   L2 Cache HIT (5ms)");

            VideoData data = l2Cache.get(videoId);
            int count = accessCount.getOrDefault(videoId, 0) + 1;
            accessCount.put(videoId, count);

            // Promote to L1 if access count exceeds threshold
            if (count >= PROMOTION_THRESHOLD) {
                System.out.println("   -> Promoted to L1 (access count = " + count + ")");
                l1Cache.put(videoId, data);
            }
            return data;
        }

        // --- Check L3 (database) ---
        l1Misses++;
        System.out.println("   L1 Cache MISS");
        System.out.println("   L2 Cache MISS");

        if (l3Database.containsKey(videoId)) {
            l3Hits++;
            System.out.println("   L3 Database HIT (150ms)");

            VideoData data = l3Database.get(videoId);
            accessCount.put(videoId, accessCount.getOrDefault(videoId, 0) + 1);

            // Load into L2
            l2Cache.put(videoId, data);
            System.out.println("   -> Added to L2");
            return data;
        }

        System.out.println("   NOT FOUND in any level.");
        return null;
    }

    // Invalidate a video from all cache levels (content update)
    public void invalidate(String videoId) {
        l1Cache.remove(videoId);
        l2Cache.remove(videoId);
        accessCount.remove(videoId);
        System.out.println("Invalidated \"" + videoId + "\" from all cache levels.");
    }

    // Print statistics
    public void getStatistics() {
        int total = l1Hits + l2Hits + l3Hits + (totalRequests - l1Hits - l2Hits - l3Hits);
        System.out.println("\n========== Cache Statistics ==========");
        System.out.printf("L1 Hits : %3d  (%.1f%%)  ~0.5ms avg%n",
                          l1Hits, safePercent(l1Hits, totalRequests));
        System.out.printf("L2 Hits : %3d  (%.1f%%)  ~5ms avg%n",
                          l2Hits, safePercent(l2Hits, totalRequests));
        System.out.printf("L3 Hits : %3d  (%.1f%%)  ~150ms avg%n",
                          l3Hits, safePercent(l3Hits, totalRequests));
        int overallHits = l1Hits + l2Hits + l3Hits;
        System.out.printf("Overall : %3d  (%.1f%%) hit rate%n",
                          overallHits, safePercent(overallHits, totalRequests));
        System.out.println("L1 size : " + l1Cache.size() + "/" + L1_CAPACITY);
        System.out.println("L2 size : " + l2Cache.size() + "/" + L2_CAPACITY);
        System.out.println("======================================");
    }

    private double safePercent(int part, int total) {
        return total > 0 ? part * 100.0 / total : 0;
    }

    public static void main(String[] args) {
        Problem10_MultiLevelCache cache = new Problem10_MultiLevelCache();

        System.out.println("=== Problem 10: Multi-Level Cache System (L1/L2/L3) ===");

        // First access - goes to L3
        cache.getVideo("video_001");
        cache.getVideo("video_002");
        cache.getVideo("video_003");

        // Repeat accesses - should hit L2, then promote to L1
        cache.getVideo("video_001"); // L2 hit (count=2)
        cache.getVideo("video_001"); // L2 hit -> promoted to L1 (count=3)
        cache.getVideo("video_001"); // L1 hit

        // Access more videos to trigger L1 eviction
        cache.getVideo("video_004");
        cache.getVideo("video_005");
        cache.getVideo("video_006"); // L1 full - oldest evicted to L2

        // Test cache invalidation (content update scenario)
        System.out.println("\n--- Content Update ---");
        cache.invalidate("video_001");
        cache.getVideo("video_001"); // should miss all levels, go to L3

        cache.getStatistics();
    }
}
