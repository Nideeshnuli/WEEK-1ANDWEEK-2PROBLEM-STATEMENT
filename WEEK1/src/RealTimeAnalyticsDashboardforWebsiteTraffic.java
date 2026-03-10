import java.util.*;

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

public class RealTimeAnalyticsDashboardforWebsiteTraffic {

    // pageUrl -> visit count
    HashMap<String, Integer> pageVisits = new HashMap<>();

    // pageUrl -> unique visitors
    HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();

    // traffic source -> count
    HashMap<String, Integer> trafficSources = new HashMap<>();


    // Process incoming page event
    public void processEvent(PageViewEvent event) {

        // Count page visits
        pageVisits.put(event.url, pageVisits.getOrDefault(event.url, 0) + 1);

        // Track unique visitors
        uniqueVisitors.putIfAbsent(event.url, new HashSet<>());
        uniqueVisitors.get(event.url).add(event.userId);

        // Count traffic source
        trafficSources.put(event.source,
                trafficSources.getOrDefault(event.source, 0) + 1);
    }


    // Get top 10 pages
    public List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry : pageVisits.entrySet()) {

            pq.add(entry);

            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<Map.Entry<String, Integer>> result = new ArrayList<>(pq);
        result.sort((a, b) -> b.getValue() - a.getValue());

        return result;
    }


    // Display dashboard
    public void getDashboard() {

        System.out.println("===== REAL TIME DASHBOARD =====");

        System.out.println("\nTop Pages:");

        List<Map.Entry<String, Integer>> topPages = getTopPages();

        int rank = 1;

        for (Map.Entry<String, Integer> entry : topPages) {

            String page = entry.getKey();
            int visits = entry.getValue();
            int unique = uniqueVisitors.get(page).size();

            System.out.println(rank + ". " + page + " - " + visits
                    + " views (" + unique + " unique)");

            rank++;
        }


        System.out.println("\nTraffic Sources:");

        int total = 0;

        for (int count : trafficSources.values()) {
            total += count;
        }

        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {

            double percent = (entry.getValue() * 100.0) / total;

            System.out.println(entry.getKey() + ": "
                    + String.format("%.1f", percent) + "%");
        }
    }


    public static void main(String[] args) throws InterruptedException {

        RealTimeAnalyticsDashboardforWebsiteTraffic analytics =
                new RealTimeAnalyticsDashboardforWebsiteTraffic();


        // Simulated incoming events
        analytics.processEvent(
                new PageViewEvent("/article/breaking-news", "user_123", "google"));

        analytics.processEvent(
                new PageViewEvent("/article/breaking-news", "user_456", "facebook"));

        analytics.processEvent(
                new PageViewEvent("/sports/championship", "user_789", "direct"));

        analytics.processEvent(
                new PageViewEvent("/article/breaking-news", "user_789", "google"));

        analytics.processEvent(
                new PageViewEvent("/sports/championship", "user_111", "google"));

        analytics.processEvent(
                new PageViewEvent("/sports/championship", "user_222", "facebook"));


        // Dashboard updates every 5 seconds
        while (true) {

            analytics.getDashboard();

            Thread.sleep(5000); // 5 seconds update
        }
    }
}
