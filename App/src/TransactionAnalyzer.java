import java.util.*;

public class TransactionAnalyzer {

    static class Transaction {
        int id;
        int amount;
        String merchant;
        String account;
        int time; // minutes since start of day

        Transaction(int id, int amount, String merchant, String account, int time) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.account = account;
            this.time = time;
        }

        public String toString() {
            return "id:" + id + " amount:" + amount;
        }
    }

    static List<Transaction> transactions = new ArrayList<>();

    // ---------------- Two Sum ----------------
    static void findTwoSum(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                Transaction t2 = map.get(complement);
                result.add("(" + t2.id + "," + t.id + ")");
            }

            map.put(t.amount, t);
        }

        System.out.println("Two Sum Pairs: " + result);
    }

    // ------------ Two Sum with Time Window (1 hour) ------------
    static void findTwoSumTimeWindow(int target) {

        HashMap<Integer, List<Transaction>> map = new HashMap<>();

        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                for (Transaction prev : map.get(complement)) {

                    if (Math.abs(t.time - prev.time) <= 60) {
                        result.add("(" + prev.id + "," + t.id + ")");
                    }
                }
            }

            map.computeIfAbsent(t.amount, k -> new ArrayList<>()).add(t);
        }

        System.out.println("Two Sum within 1 hour: " + result);
    }

    // ---------------- Duplicate Detection ----------------
    static void detectDuplicates() {

        HashMap<String, Set<String>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "_" + t.merchant;

            map.computeIfAbsent(key, k -> new HashSet<>()).add(t.account);
        }

        System.out.println("Duplicate transactions:");

        for (String key : map.keySet()) {

            Set<String> accounts = map.get(key);

            if (accounts.size() > 1) {

                String[] parts = key.split("_");

                System.out.println(
                        "{amount:" + parts[0] +
                                ", merchant:" + parts[1] +
                                ", accounts:" + accounts + "}"
                );
            }
        }
    }

    // ---------------- K Sum ----------------
    static void findKSum(int k, int target) {

        List<List<Transaction>> result = new ArrayList<>();

        kSumHelper(0, k, target, new ArrayList<>(), result);

        System.out.println("K-Sum Results:");

        for (List<Transaction> list : result) {

            System.out.print("(");
            for (Transaction t : list) {
                System.out.print("id:" + t.id + " ");
            }
            System.out.println(")");
        }
    }

    static void kSumHelper(int start, int k, int target,
                           List<Transaction> current,
                           List<List<Transaction>> result) {

        if (k == 0 && target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (k <= 0) return;

        for (int i = start; i < transactions.size(); i++) {

            Transaction t = transactions.get(i);

            current.add(t);

            kSumHelper(i + 1, k - 1, target - t.amount, current, result);

            current.remove(current.size() - 1);
        }
    }

    // ---------------- Main ----------------
    public static void main(String[] args) {

        transactions.add(new Transaction(1, 500, "StoreA", "acc1", 600));
        transactions.add(new Transaction(2, 300, "StoreB", "acc2", 615));
        transactions.add(new Transaction(3, 200, "StoreC", "acc3", 630));
        transactions.add(new Transaction(4, 500, "StoreA", "acc4", 640));

        findTwoSum(500);

        findTwoSumTimeWindow(500);

        detectDuplicates();

        findKSum(3, 1000);
    }
}