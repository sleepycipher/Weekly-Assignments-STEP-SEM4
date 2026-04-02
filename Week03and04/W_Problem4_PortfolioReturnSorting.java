import java.util.*;

public class Problem4_PortfolioReturnSorting {

    static class Asset {
        String ticker;
        double returnRate;   // percentage
        double volatility;   // lower = less risky

        Asset(String ticker, double returnRate, double volatility) {
            this.ticker = ticker;
            this.returnRate = returnRate;
            this.volatility = volatility;
        }

        @Override
        public String toString() {
            return ticker + ":" + returnRate + "%";
        }
    }

    // ============================================================
    // MERGE SORT by returnRate ASC (stable - preserves order for ties)
    // ============================================================
    public static void mergeSort(Asset[] arr, int left, int right) {
        if (left >= right) return;
        int mid = (left + right) / 2;
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }

    private static void merge(Asset[] arr, int left, int mid, int right) {
        Asset[] temp = Arrays.copyOfRange(arr, left, right + 1);
        int i = 0, j = mid - left + 1, k = left;
        int rightLen = right - left;

        while (i <= mid - left && j <= rightLen) {
            if (temp[i].returnRate <= temp[j].returnRate) arr[k++] = temp[i++];
            else                                          arr[k++] = temp[j++];
        }
        while (i <= mid - left) arr[k++] = temp[i++];
        while (j <= rightLen)   arr[k++] = temp[j++];
    }

    // ============================================================
    // HYBRID QUICK SORT: Quick Sort + Insertion Sort for small partitions
    // Sorts by returnRate DESC, then volatility ASC on ties
    // ============================================================
    private static final int INSERTION_THRESHOLD = 5;

    public static void hybridQuickSort(Asset[] arr, int low, int high) {
        if (high - low < INSERTION_THRESHOLD) {
            insertionSortDesc(arr, low, high);
            return;
        }
        if (low < high) {
            int pivotIndex = medianOfThree(arr, low, high);
            swap(arr, pivotIndex, high);
            int p = partition(arr, low, high);
            hybridQuickSort(arr, low, p - 1);
            hybridQuickSort(arr, p + 1, high);
        }
    }

    // Median-of-3 pivot (random variant: pick 3 random, take middle)
    private static int medianOfThree(Asset[] arr, int low, int high) {
        int mid = (low + high) / 2;
        // Sort low, mid, high by returnRate, pick middle
        if (arr[low].returnRate < arr[mid].returnRate) swap(arr, low, mid);
        if (arr[low].returnRate < arr[high].returnRate) swap(arr, low, high);
        if (arr[mid].returnRate < arr[high].returnRate) swap(arr, mid, high);
        return mid; // median is now at mid
    }

    // Partition for DESC order; secondary: volatility ASC
    private static int partition(Asset[] arr, int low, int high) {
        Asset pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            boolean shouldSwap = arr[j].returnRate > pivot.returnRate ||
                (arr[j].returnRate == pivot.returnRate && arr[j].volatility < pivot.volatility);
            if (shouldSwap) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    // Insertion sort for small subarrays (DESC by returnRate, ASC volatility on tie)
    private static void insertionSortDesc(Asset[] arr, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            Asset key = arr[i];
            int j = i - 1;
            while (j >= low && (arr[j].returnRate < key.returnRate ||
                   (arr[j].returnRate == key.returnRate && arr[j].volatility > key.volatility))) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    private static void swap(Asset[] arr, int i, int j) {
        Asset t = arr[i]; arr[i] = arr[j]; arr[j] = t;
    }

    public static void main(String[] args) {
        System.out.println("=== Problem 4: Portfolio Return Sorting ===\n");

        Asset[] assets = {
            new Asset("AAPL",  12.0, 0.25),
            new Asset("TSLA",   8.0, 0.55),
            new Asset("GOOG",  15.0, 0.20),
            new Asset("MSFT",  12.0, 0.18),  // same return as AAPL, lower volatility
            new Asset("AMZN",  10.0, 0.30),
            new Asset("NVDA",  20.0, 0.60),
            new Asset("META",   5.0, 0.40),
        };

        System.out.println("Input: " + Arrays.toString(assets));

        // ---- Merge Sort ascending ----
        Asset[] mergeSorted = assets.clone();
        mergeSort(mergeSorted, 0, mergeSorted.length - 1);
        System.out.println("\nMerge Sort (returnRate ASC, stable): " + Arrays.toString(mergeSorted));
        System.out.println("  Note: AAPL and MSFT tie at 12% - original order preserved");

        // ---- Hybrid Quick Sort descending ----
        Asset[] quickSorted = assets.clone();
        hybridQuickSort(quickSorted, 0, quickSorted.length - 1);
        System.out.println("\nHybrid Quick Sort (returnRate DESC + volatility ASC): ");
        for (Asset a : quickSorted) {
            System.out.printf("  %-6s return=%.1f%%  volatility=%.2f%n",
                              a.ticker, a.returnRate, a.volatility);
        }

        System.out.println("\n--- Algorithm Notes ---");
        System.out.println("Merge Sort: O(n log n) always, stable - ideal for ties");
        System.out.println("Hybrid Quick: Quick Sort + Insertion for partitions < " + INSERTION_THRESHOLD);
        System.out.println("  -> Avoids recursion overhead on small subarrays");
        System.out.println("Median-of-3 pivot: reduces worst-case O(n²) risk");
    }
}
