import java.util.*;

public class Problem1_TransactionFeeSorting {

    static class Transaction {
        String id;
        double fee;
        String timestamp;

        Transaction(String id, double fee, String timestamp) {
            this.id = id;
            this.fee = fee;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return id + ":$" + fee + "@" + timestamp;
        }
    }

    // ---- Bubble Sort by fee ascending ----
    public static int[] bubbleSort(List<Transaction> list) {
        int n = list.size();
        int passes = 0, swaps = 0;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (list.get(j).fee > list.get(j + 1).fee) {
                    Transaction temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                    swaps++;
                    swapped = true;
                }
            }
            passes++;
            if (!swapped) break; // Early termination
        }
        return new int[]{passes, swaps};
    }

    // ---- Insertion Sort by fee + timestamp (stable) ----
    public static void insertionSort(List<Transaction> list) {
        int n = list.size();
        for (int i = 1; i < n; i++) {
            Transaction key = list.get(i);
            int j = i - 1;
            // Sort by fee asc, then by timestamp asc for ties
            while (j >= 0 && (list.get(j).fee > key.fee ||
                   (list.get(j).fee == key.fee && list.get(j).timestamp.compareTo(key.timestamp) > 0))) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

    // ---- Flag high-fee outliers ----
    public static List<Transaction> flagHighFee(List<Transaction> list, double threshold) {
        List<Transaction> outliers = new ArrayList<>();
        for (Transaction t : list) {
            if (t.fee > threshold) outliers.add(t);
        }
        return outliers;
    }

    public static void main(String[] args) {
        System.out.println("=== Problem 1: Transaction Fee Sorting for Audit Compliance ===\n");

        // ---- Small batch: Bubble Sort ----
        List<Transaction> batch1 = new ArrayList<>(Arrays.asList(
            new Transaction("id1", 10.5,  "10:00"),
            new Transaction("id2", 25.0,  "09:30"),
            new Transaction("id3",  5.0,  "10:15"),
            new Transaction("id4", 55.0,  "08:45"),
            new Transaction("id5", 10.5,  "11:00")  // duplicate fee to test stability
        ));

        System.out.println("Input: " + batch1);

        int[] stats = bubbleSort(batch1);
        System.out.println("\nBubble Sort (fee ASC): " + batch1);
        System.out.println("Passes: " + stats[0] + ", Swaps: " + stats[1]);

        // ---- Medium batch: Insertion Sort ----
        List<Transaction> batch2 = new ArrayList<>(Arrays.asList(
            new Transaction("id1", 10.5,  "10:00"),
            new Transaction("id2", 25.0,  "09:30"),
            new Transaction("id3",  5.0,  "10:15"),
            new Transaction("id4", 55.0,  "08:45"),
            new Transaction("id5", 10.5,  "11:00")
        ));

        insertionSort(batch2);
        System.out.println("\nInsertion Sort (fee + timestamp ASC): " + batch2);

        // ---- Flag outliers > $50 ----
        List<Transaction> outliers = flagHighFee(batch1, 50.0);
        if (outliers.isEmpty()) {
            System.out.println("\nHigh-fee outliers (>$50): none");
        } else {
            System.out.println("\nHigh-fee outliers (>$50): " + outliers);
        }

        // ---- Complexity summary ----
        System.out.println("\n--- Time Complexity Summary ---");
        System.out.println("Bubble Sort: Best O(n) [sorted], Worst O(n²) [reverse]");
        System.out.println("Insertion Sort: Best O(n) [sorted], Worst O(n²) [reverse]");
        System.out.println("Both are stable sorts (preserve relative order of equal elements).");
    }
}
