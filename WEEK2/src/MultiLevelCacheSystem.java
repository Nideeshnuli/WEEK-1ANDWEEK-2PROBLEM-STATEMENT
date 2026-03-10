import java.util.*;

class VideoData {
    String videoId;
    String content;

    public VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

public class MultiLevelCacheSystem {

    // L1 cache (LRU using LinkedHashMap)
    LinkedHashMap<String, VideoData> L1Cache =
            new LinkedHashMap<>(10000, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                    return size() > 10000;
                }
            };

    // L2 cache
    HashMap<String, VideoData> L2Cache = new HashMap<>();

    // L3 database
    HashMap<String, VideoData> database = new HashMap<>();

    // Access counter
    HashMap<String, Integer> accessCount = new HashMap<>();

    int l1Hits = 0;
    int l2Hits = 0;
    int l3Hits = 0;

    public MultiLevelCacheSystem() {

        // preload database
        for (int i = 1; i <= 20; i++) {
            database.put("video_" + i,
                    new VideoData("video_" + i, "Video Content " + i));
        }
    }

    public VideoData getVideo(String videoId) {

        long start = System.currentTimeMillis();

        // L1 check
        if (L1Cache.containsKey(videoId)) {
            l1Hits++;
            System.out.println("L1 Cache HIT (0.5ms)");
            return L1Cache.get(videoId);
        }

        System.out.println("L1 Cache MISS");

        // L2 check
        if (L2Cache.containsKey(videoId)) {
            l2Hits++;
            System.out.println("L2 Cache HIT (5ms)");

            VideoData data = L2Cache.get(videoId);

            promoteToL1(videoId, data);

            return data;
        }

        System.out.println("L2 Cache MISS");

        // L3 database
        if (database.containsKey(videoId)) {
            l3Hits++;
            System.out.println("L3 Database HIT (150ms)");

            VideoData data = database.get(videoId);

            addToL2(videoId, data);

            return data;
        }

        System.out.println("Video not found");
        return null;
    }

    // Add to L2
    private void addToL2(String videoId, VideoData data) {

        if (L2Cache.size() >= 100000) {
            Iterator<String> it = L2Cache.keySet().iterator();
            if (it.hasNext()) {
                L2Cache.remove(it.next());
            }
        }

        L2Cache.put(videoId, data);

        accessCount.put(videoId,
                accessCount.getOrDefault(videoId, 0) + 1);
    }

    // Promote to L1
    private void promoteToL1(String videoId, VideoData data) {

        L1Cache.put(videoId, data);

        accessCount.put(videoId,
                accessCount.getOrDefault(videoId, 0) + 1);
    }

    // Cache invalidation
    public void invalidate(String videoId) {

        L1Cache.remove(videoId);
        L2Cache.remove(videoId);

        System.out.println("Cache invalidated for " + videoId);
    }

    // Statistics
    public void getStatistics() {

        int total = l1Hits + l2Hits + l3Hits;

        System.out.println("\nCache Statistics:");

        if (total == 0) total = 1;

        System.out.println("L1 Hit Rate: " + (l1Hits * 100 / total) + "%");
        System.out.println("L2 Hit Rate: " + (l2Hits * 100 / total) + "%");
        System.out.println("L3 Hit Rate: " + (l3Hits * 100 / total) + "%");

        System.out.println("Overall Requests: " + total);
    }

    public static void main(String[] args) {

        MultiLevelCacheSystem cache = new MultiLevelCacheSystem();

        cache.getVideo("video_5");
        cache.getVideo("video_5");

        cache.getVideo("video_10");

        cache.invalidate("video_5");

        cache.getStatistics();
    }
}
