import java.util.*;

public class MultiLevelCacheSystem {

    static class VideoData {
        String videoId;
        String content;
        int accessCount;

        VideoData(String videoId, String content) {
            this.videoId = videoId;
            this.content = content;
            this.accessCount = 0;
        }
    }

    // L1 Cache (10,000 capacity) – Memory
    static LinkedHashMap<String, VideoData> L1 =
            new LinkedHashMap<>(10000, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                    return size() > 10000;
                }
            };

    // L2 Cache (100,000 capacity) – SSD simulation
    static LinkedHashMap<String, VideoData> L2 =
            new LinkedHashMap<>(100000, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                    return size() > 100000;
                }
            };

    // L3 Database simulation
    static HashMap<String, VideoData> L3 = new HashMap<>();

    // Statistics
    static int L1Hits = 0, L2Hits = 0, L3Hits = 0;
    static int totalRequests = 0;

    // Initialize database with videos
    static {
        for (int i = 1; i <= 1000; i++) {
            String id = "video_" + i;
            L3.put(id, new VideoData(id, "Video Content " + i));
        }
    }

    // Get video method
    static VideoData getVideo(String videoId) {

        totalRequests++;

        // ---------- L1 Cache ----------
        if (L1.containsKey(videoId)) {

            L1Hits++;
            VideoData v = L1.get(videoId);
            v.accessCount++;

            System.out.println("L1 Cache HIT (0.5ms)");

            return v;
        }

        System.out.println("L1 Cache MISS (0.5ms)");

        // ---------- L2 Cache ----------
        if (L2.containsKey(videoId)) {

            L2Hits++;

            VideoData v = L2.get(videoId);
            v.accessCount++;

            System.out.println("L2 Cache HIT (5ms)");

            // Promote to L1
            L1.put(videoId, v);
            System.out.println("Promoted to L1");

            return v;
        }

        System.out.println("L2 Cache MISS");

        // ---------- L3 Database ----------
        if (L3.containsKey(videoId)) {

            L3Hits++;

            VideoData v = L3.get(videoId);
            v.accessCount++;

            System.out.println("L3 Database HIT (150ms)");

            // Add to L2
            L2.put(videoId, v);

            return v;
        }

        System.out.println("Video not found.");
        return null;
    }

    // Cache invalidation (when content updated)
    static void invalidateVideo(String videoId) {

        L1.remove(videoId);
        L2.remove(videoId);

        if (L3.containsKey(videoId)) {
            L3.put(videoId, new VideoData(videoId, "Updated Content"));
        }

        System.out.println("Cache invalidated for " + videoId);
    }

    // Print statistics
    static void getStatistics() {

        double l1Rate = (L1Hits * 100.0) / totalRequests;
        double l2Rate = (L2Hits * 100.0) / totalRequests;
        double l3Rate = (L3Hits * 100.0) / totalRequests;

        System.out.println("\nCache Statistics");
        System.out.println("---------------------");

        System.out.println("L1 Hit Rate: " + String.format("%.2f", l1Rate) + "% (0.5ms)");
        System.out.println("L2 Hit Rate: " + String.format("%.2f", l2Rate) + "% (5ms)");
        System.out.println("L3 Hit Rate: " + String.format("%.2f", l3Rate) + "% (150ms)");

        double overall = ((L1Hits + L2Hits + L3Hits) * 100.0) / totalRequests;

        System.out.println("Overall Hit Rate: " + String.format("%.2f", overall) + "%");
    }

    public static void main(String[] args) {

        System.out.println("Request video_123");
        getVideo("video_123");

        System.out.println("\nRequest video_123 again");
        getVideo("video_123");

        System.out.println("\nRequest video_999");
        getVideo("video_999");

        System.out.println("\nInvalidate video_123");
        invalidateVideo("video_123");

        getStatistics();
    }
}