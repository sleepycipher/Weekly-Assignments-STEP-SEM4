import java.util.*;

public class Problem2_InventoryManager {

    // HashMap<productId, stockCount>
    private HashMap<String, Integer> inventory = new HashMap<>();

    // LinkedHashMap for waiting list (FIFO ordering)
    private LinkedHashMap<String, Queue<Integer>> waitingList = new LinkedHashMap<>();

    public Problem2_InventoryManager() {
        // Pre-load inventory
        inventory.put("IPHONE15_256GB", 100);
        inventory.put("SAMSUNG_S24", 50);
        inventory.put("PIXEL_8_PRO", 30);
    }

    // Check current stock - O(1)
    public String checkStock(String productId) {
        int stock = inventory.getOrDefault(productId, 0);
        return stock + " units available";
    }

    // Purchase item - synchronized for thread safety
    public synchronized String purchaseItem(String productId, int userId) {
        int stock = inventory.getOrDefault(productId, 0);

        if (stock > 0) {
            inventory.put(productId, stock - 1);
            return "SUCCESS: User " + userId + " purchased. " + (stock - 1) + " units remaining.";
        } else {
            // Add to waiting list
            waitingList.putIfAbsent(productId, new LinkedList<>());
            Queue<Integer> queue = waitingList.get(productId);
            queue.add(userId);
            return "OUT OF STOCK: User " + userId + " added to waiting list. Position #" + queue.size();
        }
    }

    // Restock a product
    public void restock(String productId, int quantity) {
        inventory.put(productId, inventory.getOrDefault(productId, 0) + quantity);
        System.out.println("Restocked " + productId + " with " + quantity + " units.");

        // Notify waiting users
        if (waitingList.containsKey(productId)) {
            Queue<Integer> queue = waitingList.get(productId);
            int notified = Math.min(quantity, queue.size());
            System.out.println("Notifying " + notified + " users from waiting list...");
            for (int i = 0; i < notified; i++) {
                System.out.println("  -> Notified userId: " + queue.poll());
            }
        }
    }

    // Get waiting list position
    public String getWaitingListPosition(String productId, int userId) {
        if (!waitingList.containsKey(productId)) return "Not in waiting list.";
        Queue<Integer> queue = waitingList.get(productId);
        int pos = 1;
        for (int id : queue) {
            if (id == userId) return "Position #" + pos + " in waiting list for " + productId;
            pos++;
        }
        return "Not in waiting list.";
    }

    public static void main(String[] args) {
        Problem2_InventoryManager manager = new Problem2_InventoryManager();

        System.out.println("=== Problem 2: E-commerce Flash Sale Inventory Manager ===\n");

        System.out.println("checkStock(\"IPHONE15_256GB\") -> " + manager.checkStock("IPHONE15_256GB"));

        // Simulate purchases
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 67890));

        // Drain the stock
        for (int i = 2; i < 100; i++) {
            manager.purchaseItem("IPHONE15_256GB", 10000 + i);
        }

        System.out.println("\nAfter 100 purchases:");
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 99999));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 88888));

        System.out.println("\nCheck waiting list:");
        System.out.println(manager.getWaitingListPosition("IPHONE15_256GB", 99999));

        System.out.println("\n--- Restock ---");
        manager.restock("IPHONE15_256GB", 2);
    }
}
