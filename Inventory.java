import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The Inventory class manages the collection of items that a player can carry.
 * <p>
 * It handles adding and removing items, keeping track of the total weight,
 * and making sure that the maximum weight is not exceeded. It also
 * provides methods to display the inventory contents and their total weight.
 * <p>
 * The inventory is stored as a HashMap with items as keys and their quantities
 * as values.
 *
 * @author Mahdi Razzaque
 * @version 28.11.2024
 */

public class Inventory {
    private HashMap<Item, Integer> inventory = new HashMap<>(); // Stores items and their quantities
    private Integer weight; // The current total weight of items in the inventory
    private Integer maxWeight; // The maximum weight capacity of the inventory

    /**
     * Constructs an Inventory object with a specified maximum weight capacity.
     * <p>
     * Initialises the inventory with a given maximum weight and sets the current
     * weight to zero.
     *
     * @param maxWeight The maximum weight capacity of the inventory.
     */
    public Inventory(Integer maxWeight) {
        weight = 0;
        this.maxWeight = maxWeight;
    }

    /**
     * Returns the total weight of items currently in the player's inventory.
     *
     * @return The total weight of items in the inventory.
     */
    public Integer getWeight() {
        return weight;
    }

    /**
     * Returns the maximum weight that the player's inventory can hold.
     *
     * @return The maximum weight capacity of the inventory.
     */
    public Integer getMaxWeight() {
        return maxWeight;
    }

    /**
     * Adds a specified number of items to the inventory.
     * <p>
     * This method checks if adding the items will exceed the maximum weight capacity.
     * If not, it adds the items to the inventory. If the items are already in the inventory,
     * it increments the quantity; otherwise, it adds the new items. It also displays the
     * updated inventory weight.
     *
     * @param item The item to add to the inventory.
     * @param number The number of items to add.
     */
    public void addItem(Item item, Integer number) {
        if (((calculateInventoryWeight() + item.getWeight() * number) > maxWeight)) { // Checks to see if the total weight after the item addition exceeds the max weight
            System.out.println("\n**Inventory - Items not added**");
            System.out.println("You do not have enough inventory space for this");
            return;
        }

        if (!inventory.containsKey(item)) { //Checks to see if the inventory already includes an item. If not, it creates the key and sets the value.
            inventory.put(item, number);
            System.out.println("\n**Inventory - Items added**");
            System.out.println(String.format("Added %d %s to your inventory", number, item.getName() + (number > 1 ? "s" : "")));
            displayInventoryWeight();
            return;
        }

        inventory.put(item, inventory.get(item) + number); // Adds the quantity provided to the existing count
        System.out.println("\n**Inventory - Items added**");
        System.out.println(String.format("Added %d %s to your inventory", number, item.getName() + (number > 1 ? "s" : "")));
        displayInventoryWeight();
    }

    /**
     * Removes a specified number of items from the inventory.
     * <p>
     * This method checks if the items are in the inventory and if the quantity to be removed
     * does not exceed the quantity available. If the conditions are met, it removes the items
     * from the inventory and updates the inventory weight. It also removes items with zero quantity.
     *
     * @param item The item to remove from the inventory.
     * @param number The number of items to remove.
     */
    public void removeItem(Item item, Integer number) {
        if (!inventory.containsKey(item)) { // Check if the item is present in the inventory
            System.out.println("\n**Inventory - Items not removed **");
            System.out.println("You do not have any " + item.getName() + "(s).");
            return;
        }

        int numberInInventory = inventory.get(item); // Get the current number of the item in the inventory
        if (numberInInventory < number) { // Check if the inventory has enough items to remove
            System.out.println("\n**Inventory - Items not removed**");
            System.out.println("You cannot remove more items than you have.");
            return;
        }

        System.out.println("\n**Inventory - Items removed***");
        inventory.put(item, inventory.get(item) - number); // Update the quantity of the item in the inventory
        numberInInventory = inventory.get(item); // Update the number of the item in the inventory

        System.out.printf("You have %d %s left%n", numberInInventory, item.getName() + (numberInInventory > 1 ? "s" : "")); // Display the remaining number of the item
        displayInventoryWeight(); // Display the updated inventory weight
        Utils.removeZeroQuantityItems(inventory); // Remove items with zero quantity from the inventory
    }

    /**
     * Displays the current inventory.
     * <p>
     * This method prints the contents of the inventory, including the name,
     * quantity, and total weight of each item. It also displays the total weight.
     * If the inventory is empty, it prints a message indicating that.
     */
    public void displayInventory() {
        System.out.println("\n**Inventory**");
        if (inventory.isEmpty()) {
            System.out.println("Your inventory is empty.");
            return;
        }

        String inventoryList = inventory.keySet().stream()
                .map(item -> Utils.toTitleCase(item.getName()) + ": " + inventory.get(item) + " [Weight: " + (inventory.get(item) * item.getWeight()) + "]")
                .collect(Collectors.joining("\n"));

        System.out.println(inventoryList);
        displayInventoryWeight();
    }

    /**
     * Displays a selection of items in the inventory.
     * <p>
     * This method prints a list of item names formatted for selection (lowercase with spaces replaced with underscores).
     * If the inventory is empty, it prints a message indicating that.
     */
    public void displayInventorySelection() {
        if (inventory.isEmpty()) {
            System.out.println("Items: None");
            return;
        }

        String inventoryList = inventory.keySet().stream()
                .map(item -> item.getName().toLowerCase().replaceAll(" ", "_"))
                .collect(Collectors.joining(", "));

        System.out.println("Items: " + inventoryList);
    }

    /**
     * Calculates the total weight of the items in the inventory.
     * <p>
     * This method iterates through the items in the inventory and calculates the
     * total weight based on the quantity and weight of each item. If the inventory
     * is empty, the total weight is set to zero.
     *
     * @return The total weight of the items in the inventory.
     */
    public Integer calculateInventoryWeight() {
        if (inventory.isEmpty()) { // Check if the inventory is empty
            weight = 0;
        } else {
            weight = 0; // Reset weight to zero before calculating
            for (Item item : inventory.keySet()) { // Iterate through each item in the inventory
                weight += item.getWeight() * inventory.get(item); // Add the weight of the item multiplied by its quantity
            }
        }
        return weight;
    }

    /**
     * Displays the total weight of the items in the inventory.
     * <p>
     * This method calculates the total weight of the items in the inventory and
     * prints it along with the maximum weight capacity.
     */
    public void displayInventoryWeight() {
        calculateInventoryWeight(); // Calculate the total weight of the items in the inventory
        System.out.println(String.format("Total weight: %d/%d", weight, maxWeight)); // Print the total weight and the maximum weight
    }

    /**
     * Returns the number of items in the inventory. If the item is not found,
     * it defaults to returning 0.
     *
     * @param item The item whose quantity is to be retrieved.
     * @return The quantity of the item in the inventory.
     */
    public Integer numberOfItem(Item item) {
        return inventory.getOrDefault(item, 0);
    }

    /**
     * Drops a specified quantity of an item in the current room.
     * <p>
     * This method checks if the item is in the inventory and if dropping it in the current room would lock the player
     * out of the room, preventing the item from being dropped in that case. Otherwise, it adds the item to the
     * room's inventory, removes it from the player's inventory, and displays a confirmation message.
     *
     * @param currentRoom The current room where the item is to be dropped.
     * @param item The item to be dropped.
     * @param quantity The quantity of the item to drop.
     */
    public void dropItem(Room currentRoom, Item item, int quantity) {
        // Prevent dropping keys in specific rooms to avoid the player being locked out of rooms
        for(lockedDoor door : Game.lockedDoorObjects) {
            if (Game.allLockedRooms.contains(currentRoom) && door.getKey().equals(item)) {
                System.out.println("If you drop the key here, the door will lock behind you, sealing its secrets forever. I can't let you do that.");
                return;
            }
        }
        currentRoom.addItemToRoomInventory(item, quantity); // Add the item to the room's inventory
        removeItem(item, quantity); // Remove the item from the player's inventory
        System.out.println("You have dropped " + quantity + " " + item.getName() + (quantity > 1 ? "s" : "")); // Display confirmation message
    }

    /**
     * Picks up a specified quantity of an item from the current room and adds it to the inventory.
     * <p>
     * This method checks if adding the items will exceed the maximum weight capacity of the inventory.
     * If the weight limit will not be met, it removes the items from the room's inventory and adds them to the
     * player's inventory, then displays a confirmation message.
     *
     * @param currentRoom The current room where the item is being picked up from.
     * @param item The item to be picked up.
     * @param quantity The quantity of the item to pick up.
     */
    public void pickupItem(Room currentRoom, Item item, int quantity) {
        if (((calculateInventoryWeight() + item.getWeight() * quantity) > maxWeight)) { // Check if adding the item exceeds the max weight
            System.out.println("\n**Inventory - Items not added**");
            System.out.println("You do not have enough inventory space for this");
            return;
        }
        currentRoom.removeItemFromRoomInventory(item, quantity); // Remove the item from the room's inventory
        addItem(item, quantity); // Add the item to the player's inventory
        System.out.println("You have picked up " + quantity + " " + item.getName() + (quantity > 1 ? "s" : "")); // Display confirmation message
    }
}
