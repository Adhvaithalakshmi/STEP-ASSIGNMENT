import java.util.*;
import java.util.concurrent.*;

class PageViewEvent {
    String url;
    String userId;
    String source;

    public PageViewEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

class RealTimeAnalytics {

    // page -> visit count
    private Map<String, Integer> pageViews = new ConcurrentHashMap<>();

    // page -> unique users
    private Map<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();

    // traffic source -> count
    private Map<String, Integer> trafficSources = new ConcurrentHashMap<>();

    // process incoming event
    public void processEvent(PageViewEvent event) {

        // increment page views
        pageViews.merge(event.url, 1, Integer::sum);

        // track unique visitors
        uniqueVisitors
                .computeIfAbsent(event.url, k -> ConcurrentHashMap.newKeySet())
                .add(event.userId);

        // track traffic sources
        trafficSources.merge(event.source, 1, Integer::sum);
    }

    // return top 10 pages
    public List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {

            pq.offer(entry);

            if (pq.size() > 10)
                pq.poll();
        }

        List<Map.Entry<String, Integer>> result = new ArrayList<>(pq);

        result.sort((a, b) -> b.getValue() - a.getValue());

        return result;
    }

    // display dashboard
    public void printDashboard() {

        System.out.println("\n===== REAL TIME DASHBOARD =====");

        System.out.println("\nTop Pages:");

        List<Map.Entry<String, Integer>> topPages = getTopPages();

        int rank = 1;

        for (Map.Entry<String, Integer> entry : topPages) {

            String page = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.getOrDefault(page, Collections.emptySet()).size();

            System.out.println(rank++ + ". " + page +
                    " - " + views + " views (" + unique + " unique)");
        }

        System.out.println("\nTraffic Sources:");

        int total = trafficSources.values().stream().mapToInt(i -> i).sum();

        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {

            double percent = (entry.getValue() * 100.0) / total;

            System.out.printf("%s: %.2f%%\n", entry.getKey(), percent);
        }
    }
}

public class AnalyticsDashboardDemo {

    public static void main(String[] args) {

        RealTimeAnalytics analytics = new RealTimeAnalytics();

        ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);

        // dashboard refresh every 5 seconds
        scheduler.scheduleAtFixedRate(() -> {
            analytics.printDashboard();
        }, 5, 5, TimeUnit.SECONDS);

        // simulate incoming events
        Random random = new Random();

        String[] pages = {
                "/article/breaking-news",
                "/sports/championship",
                "/tech/ai-future",
                "/politics/election",
                "/health/fitness"
        };

        String[] sources = {"google", "facebook", "direct", "twitter"};

        for (int i = 0; i < 5000; i++) {

            String page = pages[random.nextInt(pages.length)];
            String source = sources[random.nextInt(sources.length)];
            String user = "user_" + random.nextInt(1000);

            PageViewEvent event = new PageViewEvent(page, user, source);

            analytics.processEvent(event);

            try {
                Thread.sleep(10);
            } catch (Exception ignored) {}
        }
    }
}