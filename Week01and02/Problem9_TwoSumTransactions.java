import java.util.*;

public class Problem9_TwoSumTransactions {

    // Inner class to represent a transaction
    static class Transaction {
        int id;
        double amount;
        String merchant;
        String account;
        String time;

        Transaction(int id, double amount, String merchant, String account, String time) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.account = account;
            this.time = time;
        }

        @Override
        public String toString() {
            return String.format("{id:%d, amount:%.0f, merchant:\"%s\", account:\"%s\", time:\"%s\"}",
                                 id, amount, merchant, account, time);
        }
    }

    private List<Transaction> transactions = new ArrayList<>();

    // Add a transaction
    public void addTransaction(int id, double amount, String merchant, String account, String time) {
        transactions.add(new Transaction(id, amount, merchant, account, time));
    }

    // Classic Two-Sum: find pairs summing to target - O(n)
    public List<int[]> findTwoSum(double target) {
        List<int[]> result = new ArrayList<>();
        // HashMap<complement, index>
        HashMap<Double, Integer> complementMap = new HashMap<>();

        for (int i = 0; i < transactions.size(); i++) {
            double amt = transactions.get(i).amount;
            double complement = target - amt;

            if (complementMap.containsKey(amt)) {
                int j = complementMap.get(amt);
                result.add(new int[]{transactions.get(j).id, transactions.get(i).id});
            }
            complementMap.put(complement, i);
        }
        return result;
    }

    // Two-Sum within a time window (simplified: filter by consecutive times)
    public List<int[]> findTwoSumWithTimeWindow(double target, int maxTimeDiffMinutes) {
        List<int[]> result = new ArrayList<>();
        for (int i = 0; i < transactions.size(); i++) {
            for (int j = i + 1; j < transactions.size(); j++) {
                Transaction t1 = transactions.get(i);
                Transaction t2 = transactions.get(j);
                int timeDiff = Math.abs(parseMinutes(t1.time) - parseMinutes(t2.time));
                if (timeDiff <= maxTimeDiffMinutes &&
                    Math.abs((t1.amount + t2.amount) - target) < 0.01) {
                    result.add(new int[]{t1.id, t2.id});
                }
            }
        }
        return result;
    }

    // Helper: parse "HH:MM" to total minutes
    private int parseMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    // K-Sum: find K transactions summing to target
    public List<List<Integer>> findKSum(int k, double target) {
        List<List<Integer>> result = new ArrayList<>();
        double[] amounts = new double[transactions.size()];
        int[] ids = new int[transactions.size()];
        for (int i = 0; i < transactions.size(); i++) {
            amounts[i] = transactions.get(i).amount;
            ids[i] = transactions.get(i).id;
        }
        kSumHelper(amounts, ids, 0, k, target, new ArrayList<>(), result);
        return result;
    }

    private void kSumHelper(double[] amounts, int[] ids, int start, int k, double remaining,
                             List<Integer> current, List<List<Integer>> result) {
        if (k == 0 && Math.abs(remaining) < 0.01) {
            result.add(new ArrayList<>(current));
            return;
        }
        if (k == 0 || start >= amounts.length) return;

        for (int i = start; i < amounts.length; i++) {
            current.add(ids[i]);
            kSumHelper(amounts, ids, i + 1, k - 1, remaining - amounts[i], current, result);
            current.remove(current.size() - 1);
        }
    }

    // Detect duplicate transactions (same amount + merchant, different accounts)
    public void detectDuplicates() {
        // HashMap<"amount_merchant", List<Transaction>>
        HashMap<String, List<Transaction>> grouped = new HashMap<>();

        for (Transaction t : transactions) {
            String key = (int)t.amount + "_" + t.merchant;
            grouped.putIfAbsent(key, new ArrayList<>());
            grouped.get(key).add(t);
        }

        System.out.println("\nDuplicate Detection Results:");
        boolean found = false;
        for (Map.Entry<String, List<Transaction>> entry : grouped.entrySet()) {
            List<Transaction> group = entry.getValue();
            Set<String> accounts = new HashSet<>();
            for (Transaction t : group) accounts.add(t.account);
            if (accounts.size() > 1) {
                found = true;
                System.out.println("  ⚠ DUPLICATE: " + entry.getKey() + " -> Accounts: " + accounts);
            }
        }
        if (!found) System.out.println("  No duplicates found.");
    }

    public static void main(String[] args) {
        Problem9_TwoSumTransactions system = new Problem9_TwoSumTransactions();

        System.out.println("=== Problem 9: Two-Sum Problem for Financial Transactions ===\n");

        system.addTransaction(1, 500, "Store A", "acc1", "10:00");
        system.addTransaction(2, 300, "Store B", "acc2", "10:15");
        system.addTransaction(3, 200, "Store C", "acc3", "10:30");
        system.addTransaction(4, 700, "Store D", "acc4", "11:00");
        system.addTransaction(5, 500, "Store A", "acc5", "10:05"); // duplicate merchant+amount

        System.out.println("Transactions loaded: " + 5);

        System.out.println("\n--- findTwoSum(target=500) ---");
        List<int[]> pairs = system.findTwoSum(500);
        for (int[] pair : pairs) {
            System.out.println("  Pair: id=" + pair[0] + " + id=" + pair[1]);
        }

        System.out.println("\n--- findTwoSum(target=1000) ---");
        pairs = system.findTwoSum(1000);
        for (int[] pair : pairs) {
            System.out.println("  Pair: id=" + pair[0] + " + id=" + pair[1]);
        }

        System.out.println("\n--- findKSum(k=3, target=1000) ---");
        List<List<Integer>> kResults = system.findKSum(3, 1000);
        for (List<Integer> combo : kResults) {
            System.out.println("  Combination: " + combo);
        }

        System.out.println("\n--- findTwoSumWithTimeWindow(target=500, within 30 mins) ---");
        List<int[]> windowPairs = system.findTwoSumWithTimeWindow(500, 30);
        for (int[] pair : windowPairs) {
            System.out.println("  Pair: id=" + pair[0] + " + id=" + pair[1]);
        }

        system.detectDuplicates();
    }
}
