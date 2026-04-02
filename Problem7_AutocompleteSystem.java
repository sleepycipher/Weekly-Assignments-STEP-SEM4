import java.util.*;

public class Problem7_AutocompleteSystem {

    // HashMap<query, frequency> for all stored queries
    private HashMap<String, Integer> queryFrequency = new HashMap<>();

    // HashMap<prefix, sorted list of suggestions> - cache
    private HashMap<String, List<String>> prefixCache = new HashMap<>();

    public Problem7_AutocompleteSystem() {
        // Pre-load with common search queries
        String[][] seedData = {
            {"java tutorial", "1234567"},
            {"javascript", "987654"},
            {"java download", "456789"},
            {"java 21 features", "112233"},
            {"java vs python", "334455"},
            {"javascript tutorial", "789012"},
            {"javascript frameworks", "567890"},
            {"python tutorial", "1100000"},
            {"python download", "450000"},
            {"python for beginners", "330000"},
            {"machine learning", "900000"},
            {"machine learning python", "650000"},
        };
        for (String[] pair : seedData) {
            queryFrequency.put(pair[0], Integer.parseInt(pair[1]));
        }
    }

    // Search for autocomplete suggestions based on a prefix
    public List<String> search(String prefix) {
        String lowerPrefix = prefix.toLowerCase();

        // Check prefix cache first
        if (prefixCache.containsKey(lowerPrefix)) {
            System.out.println("[Cache HIT for prefix: \"" + lowerPrefix + "\"]");
            return prefixCache.get(lowerPrefix);
        }

        // Find all matching queries
        List<Map.Entry<String, Integer>> matches = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : queryFrequency.entrySet()) {
            if (entry.getKey().startsWith(lowerPrefix)) {
                matches.add(entry);
            }
        }

        // Sort by frequency descending
        matches.sort((a, b) -> b.getValue() - a.getValue());

        // Extract top 5 suggestions
        List<String> suggestions = new ArrayList<>();
        int limit = Math.min(5, matches.size());
        for (int i = 0; i < limit; i++) {
            suggestions.add(matches.get(i).getKey());
        }

        // Cache the result
        prefixCache.put(lowerPrefix, suggestions);
        return suggestions;
    }

    // Update frequency when a full query is searched
    public void updateFrequency(String query) {
        String lowerQuery = query.toLowerCase();
        int oldFreq = queryFrequency.getOrDefault(lowerQuery, 0);
        queryFrequency.put(lowerQuery, oldFreq + 1);

        // Invalidate cache for all prefixes of this query
        for (int i = 1; i <= lowerQuery.length(); i++) {
            prefixCache.remove(lowerQuery.substring(0, i));
        }

        System.out.println("Updated frequency for \"" + lowerQuery + "\": " + oldFreq + " -> " + (oldFreq + 1));
    }

    // Print suggestions nicely
    public void printSuggestions(String prefix) {
        List<String> suggestions = search(prefix);
        System.out.println("\nAutocomplete for \"" + prefix + "\":");
        if (suggestions.isEmpty()) {
            System.out.println("  No suggestions found.");
        } else {
            int rank = 1;
            for (String s : suggestions) {
                System.out.printf("  %d. %-35s (%,d searches)%n",
                                  rank++, s, queryFrequency.get(s));
            }
        }
    }

    // Add a brand new query to the system
    public void addQuery(String query, int initialFrequency) {
        queryFrequency.put(query.toLowerCase(), initialFrequency);
        // Invalidate relevant cache
        String lower = query.toLowerCase();
        for (int i = 1; i <= lower.length(); i++) {
            prefixCache.remove(lower.substring(0, i));
        }
    }

    public static void main(String[] args) {
        Problem7_AutocompleteSystem autocomplete = new Problem7_AutocompleteSystem();

        System.out.println("=== Problem 7: Autocomplete System for Search Engine ===\n");

        autocomplete.printSuggestions("jav");
        autocomplete.printSuggestions("java");
        autocomplete.printSuggestions("python");
        autocomplete.printSuggestions("machine");

        System.out.println("\n--- Updating frequencies ---");
        autocomplete.updateFrequency("java 21 features");
        autocomplete.updateFrequency("java 21 features");
        autocomplete.updateFrequency("java 21 features");

        System.out.println("\n--- After updates ---");
        autocomplete.printSuggestions("java");

        System.out.println("\n--- Adding new trending query ---");
        autocomplete.addQuery("java spring boot tutorial", 5000);
        autocomplete.printSuggestions("java s");
    }
}
