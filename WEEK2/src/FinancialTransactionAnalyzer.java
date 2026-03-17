
import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    long time;

    public Transaction(int id, int amount, String merchant, String account, long time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }
}

public class FinancialTransactionAnalyzer {

    List<Transaction> transactions = new ArrayList<>();


    // Add transaction
    public void addTransaction(Transaction t) {
        transactions.add(t);
    }


    // Classic Two-Sum
    public void findTwoSum(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction other = map.get(complement);

                System.out.println("Two-Sum Pair Found: "
                        + other.id + " + " + t.id);
            }

            map.put(t.amount, t);
        }
    }


    // Two-Sum with time window (1 hour)
    public void findTwoSumTimeWindow(int target) {

        long window = 3600000;

        HashMap<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction other = map.get(complement);

                if (Math.abs(t.time - other.time) <= window) {

                    System.out.println("Time Window Pair: "
                            + other.id + " + " + t.id);
                }
            }

            map.put(t.amount, t);
        }
    }


    // Duplicate detection
    public void detectDuplicates() {

        HashMap<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "_" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t);
        }

        for (String key : map.keySet()) {

            List<Transaction> list = map.get(key);

            if (list.size() > 1) {

                System.out.println("Duplicate transaction group:");

                for (Transaction t : list) {
                    System.out.println("ID: " + t.id + " Account: " + t.account);
                }
            }
        }
    }


    // K-Sum
    public void findKSum(int k, int target) {

        int[] amounts = new int[transactions.size()];

        for (int i = 0; i < transactions.size(); i++) {
            amounts[i] = transactions.get(i).amount;
        }

        Arrays.sort(amounts);

        List<List<Integer>> result = kSum(amounts, 0, k, target);

        for (List<Integer> r : result) {
            System.out.println("K-Sum Combination: " + r);
        }
    }


    private List<List<Integer>> kSum(int[] nums, int start, int k, int target) {

        List<List<Integer>> result = new ArrayList<>();

        if (k == 2) {

            HashSet<Integer> set = new HashSet<>();

            for (int i = start; i < nums.length; i++) {

                int complement = target - nums[i];

                if (set.contains(complement)) {

                    result.add(Arrays.asList(nums[i], complement));
                }

                set.add(nums[i]);
            }

            return result;
        }

        for (int i = start; i < nums.length; i++) {

            List<List<Integer>> sub = kSum(nums, i + 1, k - 1, target - nums[i]);

            for (List<Integer> arr : sub) {

                List<Integer> newList = new ArrayList<>();
                newList.add(nums[i]);
                newList.addAll(arr);

                result.add(newList);
            }
        }

        return result;
    }


    public static void main(String[] args) {

        FinancialTransactionAnalyzer analyzer = new FinancialTransactionAnalyzer();

        long now = System.currentTimeMillis();

        analyzer.addTransaction(new Transaction(1, 500, "StoreA", "acc1", now));
        analyzer.addTransaction(new Transaction(2, 300, "StoreB", "acc2", now));
        analyzer.addTransaction(new Transaction(3, 200, "StoreC", "acc3", now));
        analyzer.addTransaction(new Transaction(4, 500, "StoreA", "acc4", now));


        System.out.println("Two-Sum Result:");
        analyzer.findTwoSum(500);

        System.out.println("\nTwo-Sum Time Window:");
        analyzer.findTwoSumTimeWindow(500);

        System.out.println("\nDuplicate Detection:");
        analyzer.detectDuplicates();

        System.out.println("\nK-Sum Result:");
        analyzer.findKSum(3, 1000);
    }
}