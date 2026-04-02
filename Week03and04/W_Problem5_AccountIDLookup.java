import java.util.*;

public class Problem5_AccountIDLookup {

    // ============================================================
    // LINEAR SEARCH - O(n)
    // ============================================================

    // Find FIRST occurrence
    public static int[] linearSearchFirst(String[] logs, String target) {
        int comparisons = 0;
        for (int i = 0; i < logs.length; i++) {
            comparisons++;
            if (logs[i].equals(target)) {
                return new int[]{i, comparisons};
            }
        }
        return new int[]{-1, comparisons};
    }

    // Find LAST occurrence
    public static int[] linearSearchLast(String[] logs, String target) {
        int comparisons = 0;
        int lastIndex = -1;
        for (int i = 0; i < logs.length; i++) {
            comparisons++;
            if (logs[i].equals(target)) {
                lastIndex = i;
            }
        }
        return new int[]{lastIndex, comparisons};
    }

    // Count all occurrences via linear search
    public static int linearCount(String[] logs, String target) {
        int count = 0;
        for (String s : logs) if (s.equals(target)) count++;
        return count;
    }

    // ============================================================
    // BINARY SEARCH - O(log n) — requires sorted array
    // ============================================================

    // Find ANY occurrence
    public static int[] binarySearch(String[] sorted, String target) {
        int low = 0, high = sorted.length - 1, comparisons = 0;

        while (low <= high) {
            int mid = (low + high) / 2;
            comparisons++;
            int cmp = sorted[mid].compareTo(target);
            if (cmp == 0)      return new int[]{mid, comparisons};
            else if (cmp < 0)  low = mid + 1;
            else               high = mid - 1;
        }
        return new int[]{-1, comparisons};
    }

    // Find FIRST occurrence (lower bound)
    public static int[] binarySearchFirst(String[] sorted, String target) {
        int low = 0, high = sorted.length - 1, result = -1, comparisons = 0;

        while (low <= high) {
            int mid = (low + high) / 2;
            comparisons++;
            int cmp = sorted[mid].compareTo(target);
            if (cmp == 0) {
                result = mid;
                high = mid - 1;  // keep searching left
            } else if (cmp < 0) low = mid + 1;
            else                high = mid - 1;
        }
        return new int[]{result, comparisons};
    }

    // Find LAST occurrence (upper bound)
    public static int[] binarySearchLast(String[] sorted, String target) {
        int low = 0, high = sorted.length - 1, result = -1, comparisons = 0;

        while (low <= high) {
            int mid = (low + high) / 2;
            comparisons++;
            int cmp = sorted[mid].compareTo(target);
            if (cmp == 0) {
                result = mid;
                low = mid + 1;   // keep searching right
            } else if (cmp < 0) low = mid + 1;
            else                high = mid - 1;
        }
        return new int[]{result, comparisons};
    }

    // Count occurrences using binary search (last - first + 1)
    public static int binaryCount(String[] sorted, String target) {
        int first = binarySearchFirst(sorted, target)[0];
        if (first == -1) return 0;
        int last = binarySearchLast(sorted, target)[0];
        return last - first + 1;
    }

    public static void main(String[] args) {
        System.out.println("=== Problem 5: Account ID Lookup in Transaction Logs ===\n");

        // Unsorted logs (for linear search)
        String[] unsorted = {"accB", "accC", "accA", "accB", "accD", "accB", "accA"};
        System.out.println("Unsorted logs: " + Arrays.toString(unsorted));

        // Sort for binary search
        String[] sorted = unsorted.clone();
        Arrays.sort(sorted);
        System.out.println("Sorted logs  : " + Arrays.toString(sorted));

        // ---- Linear Search ----
        System.out.println("\n--- Linear Search ---");
        String target = "accB";

        int[] firstResult = linearSearchFirst(unsorted, target);
        System.out.println("First '" + target + "': index=" + firstResult[0]
                           + " (" + firstResult[1] + " comparisons)");

        int[] lastResult = linearSearchLast(unsorted, target);
        System.out.println("Last  '" + target + "': index=" + lastResult[0]
                           + " (" + lastResult[1] + " comparisons)");

        System.out.println("Count of '" + target + "': " + linearCount(unsorted, target));

        // ---- Binary Search ----
        System.out.println("\n--- Binary Search (on sorted array) ---");

        int[] anyResult = binarySearch(sorted, target);
        System.out.println("Any   '" + target + "': index=" + anyResult[0]
                           + " (" + anyResult[1] + " comparisons)");

        int[] bFirst = binarySearchFirst(sorted, target);
        System.out.println("First '" + target + "': index=" + bFirst[0]
                           + " (" + bFirst[1] + " comparisons)");

        int[] bLast = binarySearchLast(sorted, target);
        System.out.println("Last  '" + target + "': index=" + bLast[0]
                           + " (" + bLast[1] + " comparisons)");

        System.out.println("Count of '" + target + "' (binary): " + binaryCount(sorted, target));

        // ---- Not found test ----
        String missing = "accZ";
        int[] notFound = binarySearch(sorted, missing);
        System.out.println("\nSearch '" + missing + "': index=" + notFound[0]
                           + " (" + notFound[1] + " comparisons) -> NOT FOUND");

        // ---- Complexity comparison ----
        System.out.println("\n--- Time Complexity ---");
        System.out.println("Linear Search: O(n) - works on unsorted data");
        System.out.println("Binary Search: O(log n) - requires sorted input");
        System.out.printf("For n=%d: Linear max=%d comps, Binary max=%d comps%n",
                          sorted.length, sorted.length,
                          (int)(Math.log(sorted.length) / Math.log(2)) + 1);
    }
}
