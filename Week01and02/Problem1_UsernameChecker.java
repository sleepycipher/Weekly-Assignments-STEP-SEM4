import java.util.*;

public class Problem1_UsernameChecker {

    // HashMap to store username -> userId mapping
    private HashMap<String, Integer> registeredUsers = new HashMap<>();

    // HashMap to track how many times each username was attempted
    private HashMap<String, Integer> attemptFrequency = new HashMap<>();

    // Simulate pre-registered users
    public Problem1_UsernameChecker() {
        registeredUsers.put("john_doe", 1001);
        registeredUsers.put("admin", 1002);
        registeredUsers.put("user123", 1003);
        registeredUsers.put("jane_smith_official", 1004);
    }

    // Check if username is available - O(1)
    public boolean checkAvailability(String username) {
        // Track attempt frequency
        attemptFrequency.put(username, attemptFrequency.getOrDefault(username, 0) + 1);
        return !registeredUsers.containsKey(username);
    }

    // Register a new username
    public String registerUsername(String username, int userId) {
        if (checkAvailability(username)) {
            registeredUsers.put(username, userId);
            return "SUCCESS: Username '" + username + "' registered for userId " + userId;
        } else {
            return "FAILED: Username '" + username + "' is already taken.";
        }
    }

    // Suggest alternatives if username is taken
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        // Append numbers
        for (int i = 1; i <= 3; i++) {
            String suggestion = username + i;
            if (!registeredUsers.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }
        // Replace underscore with dot
        String dotVersion = username.replace("_", ".");
        if (!registeredUsers.containsKey(dotVersion)) {
            suggestions.add(dotVersion);
        }
        // Add underscore at end
        String underscoreVersion = username + "_";
        if (!registeredUsers.containsKey(underscoreVersion)) {
            suggestions.add(underscoreVersion);
        }
        return suggestions;
    }

    // Get the most attempted username
    public String getMostAttempted() {
        String mostAttempted = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostAttempted = entry.getKey();
            }
        }
        return mostAttempted + " (" + maxCount + " attempts)";
    }

    public static void main(String[] args) {
        Problem1_UsernameChecker checker = new Problem1_UsernameChecker();

        System.out.println("=== Problem 1: Social Media Username Availability Checker ===\n");

        System.out.println("checkAvailability(\"john_doe\") -> " + checker.checkAvailability("john_doe"));
        System.out.println("checkAvailability(\"jane_smith\") -> " + checker.checkAvailability("jane_smith"));

        System.out.println("\nSuggestions for 'john_doe': " + checker.suggestAlternatives("john_doe"));

        // Simulate multiple attempts on "admin"
        for (int i = 0; i < 10; i++) checker.checkAvailability("admin");

        System.out.println("\nMost attempted username: " + checker.getMostAttempted());

        System.out.println("\n" + checker.registerUsername("jane_smith", 2001));
        System.out.println(checker.registerUsername("john_doe", 2002));
    }
}
