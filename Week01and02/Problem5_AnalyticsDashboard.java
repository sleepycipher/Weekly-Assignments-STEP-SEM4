import java.util.*;

public class Problem5_AnalyticsDashboard {

    // HashMap<pageUrl, visitCount>
    private HashMap<String, Integer> pageViews = new HashMap<>();

    // HashMap<pageUrl, Set<userId>> for unique visitors
    private HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();

    // HashMap<trafficSource, count>
    private HashMap<String, Integer> trafficSources = new HashMap<>();

    // Total events processed
    private int totalEvents = 0;

    // Process a single page view event
    public void processEvent(String url, String userId, String source) {
        totalEvents++;

        // Track page views
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);

        // Track unique visitors
        uniqueVisitors.putIfAbsent(url, new HashSet<>());
        uniqueVisitors.get(url).add(userId);

        // Track traffic sources
        trafficSources.put(source, trafficSources.getOrDefault(source, 0) + 1);
    }

    // Get top N pages by visit count
    public List<Map.Entry<String, Integer>> getTopPages(int n) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(pageViews.entrySet());
        list.sort((a, b) -> b.getValue() - a.getValue());
        return list.subList(0, Math.min(n, list.size()));
    }

    // Get traffic source percentages
    public void printTrafficSources() {
        System.out.println("\nTraffic Sources:");
        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {
            double pct = entry.getValue() * 100.0 / totalEvents;
            System.out.printf("  %-12s: %5.1f%% (%d visits)%n", entry.getKey(), pct, entry.getValue());
        }
    }

    // Print full dashboard
    public void getDashboard() {
        System.out.println("\n========== REAL-TIME ANALYTICS DASHBOARD ==========");
        System.out.println("Total Events Processed: " + totalEvents);

        System.out.println("\nTop 5 Pages:");
        List<Map.Entry<String, Integer>> topPages = getTopPages(5);
        int rank = 1;
        for (Map.Entry<String, Integer> entry : topPages) {
            int uniqueCount = uniqueVisitors.getOrDefault(entry.getKey(), new HashSet<>()).size();
            System.out.printf("  %d. %-35s %6d views  (%d unique)%n",
                              rank++, entry.getKey(), entry.getValue(), uniqueCount);
        }

        printTrafficSources();
        System.out.println("===================================================");
    }

    // Get unique visitor count for a specific page
    public int getUniqueVisitors(String url) {
        return uniqueVisitors.getOrDefault(url, new HashSet<>()).size();
    }

    public static void main(String[] args) {
        Problem5_AnalyticsDashboard dashboard = new Problem5_AnalyticsDashboard();

        System.out.println("=== Problem 5: Real-Time Analytics Dashboard ===\n");

        // Simulate page view events
        String[] pages = {"/article/breaking-news", "/sports/championship", "/tech/ai-update",
                          "/lifestyle/recipes", "/politics/election"};
        String[] sources = {"google", "facebook", "direct", "twitter", "other"};

        Random rand = new Random(42);
        for (int i = 0; i < 500; i++) {
            String page = pages[rand.nextInt(pages.length)];
            String userId = "user_" + (rand.nextInt(200) + 1);
            String source = sources[rand.nextInt(sources.length)];
            dashboard.processEvent(page, userId, source);
        }

        // Additional burst on breaking news
        for (int i = 0; i < 300; i++) {
            dashboard.processEvent("/article/breaking-news", "user_" + (i + 1000), "google");
        }

        dashboard.getDashboard();

        System.out.println("\nUnique visitors on /article/breaking-news: " +
                           dashboard.getUniqueVisitors("/article/breaking-news"));
    }
}
