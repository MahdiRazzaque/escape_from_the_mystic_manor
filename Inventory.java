import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Write a description of class Inventory here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Inventory
{
    // instance variables - replace the example below with your own
    private HashMap<Item, Integer> inventory = new HashMap<>();
    private Integer weight;
    private Integer maxWeight;
    
    /**
     * Constructor for objects of class Inventory
     */
    public Inventory(Integer maxWeight) {
        weight = 0;
        this.maxWeight = maxWeight;
    }

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public void addItem(Item item, Integer number) {
        if(((calculateInventoryWeight() + item.getWeight() * number) > maxWeight)) {
            System.out.println("\n**Inventory - Items not added**");
            System.out.println("You do not have enough inventory space for this");
            return;
        }
        
        if(!inventory.containsKey(item)) {
            inventory.put(item, number);
            System.out.println("\n**Inventory - Items added**");
            System.out.println(String.format("Added %d %s to your inventory", number, item.getName() + (number > 1 ? "s" : "")));
            displayInventoryWeight();
            return;
        }
        
        inventory.put(item, inventory.get(item) + number);
        System.out.println("\n**Inventory - Items added**");
        System.out.println(String.format("Added %d %s to your inventory", number, item.getName() + (number > 1 ? "s" : "")));
        displayInventoryWeight();
    }
    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public void removeItem(Item item, Integer number) {
        if(!inventory.containsKey(item)) {
            System.out.println("\n**Inventory - Items not removed **");
            System.out.println("You do not have any " + item.getName() + "(s).");
            return;
        }
        
        int numberInInventory = inventory.get(item);
        if(numberInInventory < number) {
            System.out.println("\n**Inventory - Items not removed**");
            System.out.println("You cannot remove more items than you have.");
        } else {
            System.out.println("\n**Inventory - Items removed***");
            inventory.put(item, inventory.get(item) - number);
            numberInInventory = inventory.get(item);
            System.out.println(String.format("You have %d %s left", numberInInventory, item.getName() + (numberInInventory > 1 ? "s" : "")));
            displayInventoryWeight();
        }
    }
    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public void displayInventory() {
        System.out.println("\n**Inventory**");
        if(inventory.size() <= 0) {
            System.out.println("Your inventory is empty.");
            return;
        }
            
        String inventoryList = inventory.keySet().stream()
            .map(item -> Utils.toTitleCase(item.getName()) + ": " + inventory.get(item) + " [Weight: " + (inventory.get(item) * item.getWeight()) + "]")
            .collect(Collectors.joining("\n"));
                
        System.out.println(inventoryList);
        displayInventoryWeight();
    }

    public void displayInventorySelection() {
        if(inventory.size() <= 0) {
            System.out.println("Items: None");
            return;
        }

        String inventoryList = inventory.keySet().stream()
                .map(item -> item.getName().toLowerCase().replaceAll(" ", "_"))
                .collect(Collectors.joining(", "));

        System.out.println("Items: " + inventoryList);
    }
    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public Integer calculateInventoryWeight() {
        if(inventory.size() <= 0) {
            weight = 0;
        } else {
            weight = 0;
            for(Item item: inventory.keySet()) {    
                weight += item.getWeight() * inventory.get(item);
            }
        }
        
        return weight;        
    }
    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public void displayInventoryWeight() {
        calculateInventoryWeight();
        System.out.println(String.format("Total weight: %d/%d",weight, maxWeight));       
    }
    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public Integer numberOfItem(Item item) {
        return inventory.getOrDefault(item, 0);      
    }

    /**
     *
     * @param currentRoom
     * @param item
     * @param quantity
     */
    public void dropItem(Room currentRoom, Item item, int quantity) {
        currentRoom.addItemToRoomInventory(item, quantity);
        removeItem(item, quantity);
        System.out.println("You have dropped " + quantity + " " + item.getName() + (quantity > 1 ? "s" : ""));
    }

    /**
     *
     * @param currentRoom
     * @param item
     * @param quantity
     */
    public void pickupItem(Room currentRoom, Item item, int quantity) {
        currentRoom.removeItemFromRoomInventory(item, quantity);
        addItem(item, quantity);
        System.out.println("You have picked up " + quantity + " " + item.getName() + (quantity > 1 ? "s" : ""));
    }

}
