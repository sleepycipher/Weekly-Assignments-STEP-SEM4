import java.util.*;

public class Problem2_ClientRiskRanking {

    static class Client {
        String name;
        int riskScore;
        double accountBalance;

        Client(String name, int riskScore, double accountBalance) {
            this.name = name;
            this.riskScore = riskScore;
            this.accountBalance = accountBalance;
        }

        @Override
        public String toString() {
            return name + "(" + riskScore + ")";
        }
    }

    // ---- Bubble Sort ascending by riskScore (in-place, visualize swaps) ----
    public static int bubbleSortAsc(Client[] arr) {
        int n = arr.length;
        int totalSwaps = 0;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j].riskScore > arr[j + 1].riskScore) {
                    // Visualize the swap
                    System.out.println("  Swap: " + arr[j].name + "(" + arr[j].riskScore + ") <-> "
                                       + arr[j + 1].name + "(" + arr[j + 1].riskScore + ")");
                    Client temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    totalSwaps++;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
        return totalSwaps;
    }

    // ---- Insertion Sort descending by riskScore, then by accountBalance desc ----
    public static void insertionSortDesc(Client[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            Client key = arr[i];
            int j = i - 1;
            // Sort by riskScore DESC; on tie, sort by accountBalance DESC
            while (j >= 0 && (arr[j].riskScore < key.riskScore ||
                   (arr[j].riskScore == key.riskScore && arr[j].accountBalance < key.accountBalance))) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    // ---- Get top N highest risk clients ----
    public static void printTopN(Client[] arr, int n) {
        System.out.println("Top " + n + " highest risk clients:");
        for (int i = 0; i < Math.min(n, arr.length); i++) {
            System.out.printf("  %d. %s - Risk: %d, Balance: $%.2f%n",
                              i + 1, arr[i].name, arr[i].riskScore, arr[i].accountBalance);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Problem 2: Client Risk Score Ranking ===\n");

        Client[] clients = {
            new Client("clientC", 80,  15000.0),
            new Client("clientA", 20,  50000.0),
            new Client("clientB", 50,  30000.0),
            new Client("clientD", 95,   5000.0),
            new Client("clientE", 50,  45000.0),  // same risk as B, higher balance
            new Client("clientF", 10, 100000.0),
        };

        System.out.println("Input: " + Arrays.toString(clients));

        // ---- Bubble Sort ascending ----
        Client[] bubbleArr = clients.clone();
        System.out.println("\n--- Bubble Sort (riskScore ASC) ---");
        int swaps = bubbleSortAsc(bubbleArr);
        System.out.println("Result: " + Arrays.toString(bubbleArr));
        System.out.println("Total Swaps: " + swaps);

        // ---- Insertion Sort descending ----
        Client[] insertArr = clients.clone();
        System.out.println("\n--- Insertion Sort (riskScore DESC + accountBalance DESC) ---");
        insertionSortDesc(insertArr);
        System.out.println("Result: " + Arrays.toString(insertArr));

        // ---- Top 3 risk clients ----
        System.out.println();
        printTopN(insertArr, 3);

        // ---- Complexity notes ----
        System.out.println("\n--- Complexity ---");
        System.out.println("Both sorts: Space O(1) in-place");
        System.out.println("Insertion Sort advantage: O(n) on nearly-sorted data");
    }
}
