import java.util.*;

public class Problem3_TradeVolumeAnalysis {

    static class Trade {
        String id;
        int volume;

        Trade(String id, int volume) {
            this.id = id;
            this.volume = volume;
        }

        @Override
        public String toString() {
            return id + ":" + volume;
        }
    }

    // ============================================================
    // MERGE SORT - ascending, stable, O(n log n)
    // ============================================================
    public static void mergeSort(Trade[] arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    private static void merge(Trade[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Trade[] leftArr  = new Trade[n1];
        Trade[] rightArr = new Trade[n2];

        System.arraycopy(arr, left,      leftArr,  0, n1);
        System.arraycopy(arr, mid + 1,   rightArr, 0, n2);

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            // Stable: use <= so equal elements keep original order
            if (leftArr[i].volume <= rightArr[j].volume) {
                arr[k++] = leftArr[i++];
            } else {
                arr[k++] = rightArr[j++];
            }
        }
        while (i < n1) arr[k++] = leftArr[i++];
        while (j < n2) arr[k++] = rightArr[j++];
    }

    // ============================================================
    // QUICK SORT - descending, in-place, average O(n log n)
    // Uses Lomuto partition scheme
    // ============================================================
    public static void quickSortDesc(Trade[] arr, int low, int high) {
        if (low < high) {
            // Median-of-3 pivot
            int pivotIndex = medianOfThree(arr, low, high);
            swap(arr, pivotIndex, high);

            int partIndex = lomutoPartitionDesc(arr, low, high);
            quickSortDesc(arr, low, partIndex - 1);
            quickSortDesc(arr, partIndex + 1, high);
        }
    }

    // Median-of-three pivot selection
    private static int medianOfThree(Trade[] arr, int low, int high) {
        int mid = (low + high) / 2;
        if (arr[low].volume < arr[mid].volume) swap(arr, low, mid);
        if (arr[low].volume < arr[high].volume) swap(arr, low, high);
        if (arr[mid].volume < arr[high].volume) swap(arr, mid, high);
        return mid;
    }

    // Lomuto partition for descending order
    private static int lomutoPartitionDesc(Trade[] arr, int low, int high) {
        int pivot = arr[high].volume;
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr[j].volume >= pivot) {  // >= for descending
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private static void swap(Trade[] arr, int i, int j) {
        Trade temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // ============================================================
    // Merge two sorted lists (e.g., morning + afternoon sessions)
    // ============================================================
    public static Trade[] mergeSortedLists(Trade[] a, Trade[] b) {
        Trade[] result = new Trade[a.length + b.length];
        int i = 0, j = 0, k = 0;

        while (i < a.length && j < b.length) {
            if (a[i].volume <= b[j].volume) result[k++] = a[i++];
            else                            result[k++] = b[j++];
        }
        while (i < a.length) result[k++] = a[i++];
        while (j < b.length) result[k++] = b[j++];
        return result;
    }

    // ============================================================
    // Compute total volume
    // ============================================================
    public static long totalVolume(Trade[] arr) {
        long total = 0;
        for (Trade t : arr) total += t.volume;
        return total;
    }

    public static void main(String[] args) {
        System.out.println("=== Problem 3: Historical Trade Volume Analysis ===\n");

        Trade[] trades = {
            new Trade("trade3", 500),
            new Trade("trade1", 100),
            new Trade("trade2", 300),
            new Trade("trade5", 100),  // duplicate volume for stability test
            new Trade("trade4", 750),
        };

        System.out.println("Input: " + Arrays.toString(trades));

        // ---- Merge Sort ascending ----
        Trade[] mergeSorted = trades.clone();
        mergeSort(mergeSorted, 0, mergeSorted.length - 1);
        System.out.println("\nMerge Sort (ASC, stable): " + Arrays.toString(mergeSorted));
        System.out.println("Total Volume: " + totalVolume(mergeSorted));

        // ---- Quick Sort descending ----
        Trade[] quickSorted = trades.clone();
        quickSortDesc(quickSorted, 0, quickSorted.length - 1);
        System.out.println("\nQuick Sort (DESC, median-of-3 pivot): " + Arrays.toString(quickSorted));

        // ---- Merge two sorted sessions ----
        Trade[] morning = {
            new Trade("m1", 100), new Trade("m2", 300), new Trade("m3", 500)
        };
        Trade[] afternoon = {
            new Trade("a1", 200), new Trade("a2", 400)
        };
        Trade[] merged = mergeSortedLists(morning, afternoon);
        System.out.println("\nMorning session   : " + Arrays.toString(morning));
        System.out.println("Afternoon session : " + Arrays.toString(afternoon));
        System.out.println("Merged (sorted)   : " + Arrays.toString(merged));
        System.out.println("Combined total    : " + totalVolume(merged));

        // ---- Complexity ----
        System.out.println("\n--- Complexity ---");
        System.out.println("Merge Sort: O(n log n) always, O(n) space, STABLE");
        System.out.println("Quick Sort: O(n log n) avg, O(n²) worst, O(log n) space, NOT stable");
    }
}
