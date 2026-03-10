import java.util.*;

public class EcommerceFlashSaleInventoryManager{

    // productId -> stock count
    HashMap<String, Integer> inventory = new HashMap<>();

    // productId -> waiting list of users
    HashMap<String, LinkedHashMap<Integer, Integer>> waitingList = new HashMap<>();

    // Constructor
    public EcommerceFlashSaleInventoryManager() {
        inventory.put("IPHONE15_256GB", 100);
        waitingList.put("IPHONE15_256GB", new LinkedHashMap<>());
    }

    // Check stock
    public int checkStock(String productId) {
        return inventory.getOrDefault(productId, 0);
    }

    // Purchase item (thread-safe)
    public synchronized String purchaseItem(String productId, int userId) {

        int stock = inventory.getOrDefault(productId, 0);

        if (stock > 0) {
            inventory.put(productId, stock - 1);
            return "Success, " + (stock - 1) + " units remaining";
        } else {

            LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

            int position = queue.size() + 1;
            queue.put(userId, position);

            return "Added to waiting list, position #" + position;
        }
    }

    public static void main(String[] args) {

        EcommerceFlashSaleInventoryManager manager = new EcommerceFlashSaleInventoryManager();

        System.out.println("Stock: " + manager.checkStock("IPHONE15_256GB") + " units available");

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 67890));

        // simulate many purchases
        for (int i = 1; i <= 100; i++) {
            manager.purchaseItem("IPHONE15_256GB", 10000 + i);
        }

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 99999));
    }
}
