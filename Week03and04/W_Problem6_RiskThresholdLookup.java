import java.util.*;

public class Problem6_RiskThresholdLookup {

    // ============================================================
    // LINEAR SEARCH on unsorted risk bands
    // ============================================================
    public static int[] linearSearch(int[] bands, int target) {
        int comparisons = 0;
        for (int i = 0; i < bands.length; i++) {
            comparisons++;
            if (bands[i] == target) return new int[]{i, comparisons};
        }
        return new int[]{-1, comparisons};
    }

    // ============================================================
    // BINARY SEARCH - exact match
    // ============================================================
    public static int[] binarySearch(int[] sorted, int target) {
        int low = 0, high = sorted.length - 1, comparisons = 0;
        while (low <= high) {
            int mid = (low + high) / 2;
            comparisons++;
            if (sorted[mid] == target)      return new int[]{mid, comparisons};
            else if (sorted[mid] < target)  low = mid + 1;
            else                            high = mid - 1;
        }
        return new int[]{-1, comparisons};
    }

    // ============================================================
    // FLOOR: largest value <= target (lower_bound variant)
    // ============================================================
    public static int[] findFloor(int[] sorted, int target) {
        int low = 0, high = sorted.length - 1;
        int floorVal = Integer.MIN_VALUE, floorIdx = -1;
        int comparisons = 0;

        while (low <= high) {
            int mid = (low + high) / 2;
            comparisons++;
            if (sorted[mid] <= target) {
                floorVal = sorted[mid];
                floorIdx = mid;
                low = mid + 1;   // try to find a larger value still <= target
            } else {
                high = mid - 1;
            }
        }
        return new int[]{floorVal, floorIdx, comparisons};
    }

    // ============================================================
    // CEILING: smallest value >= target (upper_bound variant)
    // ============================================================
    public static int[] findCeiling(int[] sorted, int target) {
        int low = 0, high = sorted.length - 1;
        int ceilVal = Integer.MAX_VALUE, ceilIdx = -1;
        int comparisons = 0;

        while (low <= high) {
            int mid = (low + high) / 2;
            comparisons++;
            if (sorted[mid] >= target) {
                ceilVal = sorted[mid];
                ceilIdx = mid;
                high = mid - 1;  // try to find a smaller value still >= target
            } else {
                low = mid + 1;
            }
        }
        return new int[]{ceilVal, ceilIdx, comparisons};
    }

    // ============================================================
    // INSERTION POINT: index where target should be inserted to keep array sorted
    // ============================================================
    public static int[] findInsertionPoint(int[] sorted, int target) {
        int low = 0, high = sorted.length, comparisons = 0;
        while (low < high) {
            int mid = (low + high) / 2;
            comparisons++;
            if (sorted[mid] < target) low = mid + 1;
            else                      high = mid;
        }
        return new int[]{low, comparisons};
    }

    // ============================================================
    // ASSIGN RISK BAND: which band does this client fall into?
    // Bands: [0-25) = LOW, [25-50) = MEDIUM, [50-100) = HIGH, [100+] = CRITICAL
    // ============================================================
    public static String assignRiskBand(int[] bandThresholds, String[] bandNames, int riskScore) {
        // Use ceiling to find which band the score falls into
        for (int i = 0; i < bandThresholds.length; i++) {
            if (riskScore < bandThresholds[i]) return bandNames[i];
        }
        return bandNames[bandNames.length - 1];
    }

    public static void main(String[] args) {
        System.out.println("=== Problem 6: Risk Threshold Binary Lookup ===\n");

        int[] sortedRisks = {10, 25, 50, 75, 100};
        int[] unsortedRisks = {50, 10, 100, 25, 75};  // for linear search demo

        System.out.println("Sorted risks  : " + Arrays.toString(sortedRisks));
        System.out.println("Unsorted risks: " + Arrays.toString(unsortedRisks));

        // ---- Linear search on unsorted ----
        System.out.println("\n--- Linear Search (unsorted) ---");
        int[] targets = {30, 50, 99};
        for (int t : targets) {
            int[] res = linearSearch(unsortedRisks, t);
            if (res[0] == -1) {
                System.out.println("threshold=" + t + " -> NOT FOUND (" + res[1] + " comparisons)");
            } else {
                System.out.println("threshold=" + t + " -> index=" + res[0] + " (" + res[1] + " comparisons)");
            }
        }

        // ---- Binary search exact ----
        System.out.println("\n--- Binary Search Exact (sorted) ---");
        for (int t : targets) {
            int[] res = binarySearch(sortedRisks, t);
            if (res[0] == -1) {
                System.out.println("target=" + t + " -> NOT FOUND (" + res[1] + " comparisons)");
            } else {
                System.out.println("target=" + t + " -> index=" + res[0] + " (" + res[1] + " comparisons)");
            }
        }

        // ---- Floor and Ceiling ----
        System.out.println("\n--- Floor and Ceiling ---");
        int[] queries = {30, 50, 5, 110, 75};
        for (int q : queries) {
            int[] floor   = findFloor(sortedRisks, q);
            int[] ceiling = findCeiling(sortedRisks, q);
            String floorStr   = floor[1]   == -1 ? "NONE" : String.valueOf(floor[0]);
            String ceilingStr = ceiling[1] == -1 ? "NONE" : String.valueOf(ceiling[0]);
            System.out.printf("query=%3d -> floor=%4s, ceiling=%4s (%d comps)%n",
                              q, floorStr, ceilingStr,
                              Math.max(floor[2], ceiling[2]));
        }

        // ---- Insertion point for new client ----
        System.out.println("\n--- Insertion Point for New Client ---");
        int[] newScores = {30, 10, 77, 100, 0};
        for (int s : newScores) {
            int[] ins = findInsertionPoint(sortedRisks, s);
            System.out.println("New client score=" + s + " -> insert at index " + ins[0]
                               + " (" + ins[1] + " comparisons)");
        }

        // ---- Band assignment ----
        System.out.println("\n--- Risk Band Assignment ---");
        int[] thresholds = {25, 50, 100};
        String[] bands = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};
        int[] clientScores = {15, 30, 60, 95, 100};
        for (int score : clientScores) {
            System.out.println("Score=" + score + " -> Band: " + assignRiskBand(thresholds, bands, score));
        }

        // ---- Complexity ----
        System.out.println("\n--- Complexity ---");
        System.out.println("Linear Search : O(n)     - no sorting required");
        System.out.println("Binary Search : O(log n) - requires sorted array");
        System.out.println("Floor/Ceiling : O(log n) - binary search variants");
    }
}
