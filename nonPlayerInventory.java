import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * The nonPlayerInventory class manages the collection of items for non-player entities such as rooms and characters.
 * <p>
 * It provides methods for adding, removing, displaying items, and checking the number of items.
 * The class is initialised with the name of the entity (room or character) and its associated room.
 * <p>
 * The inventory is stored as a HashMap with items as keys and their quantities as values.
 *
 * @author Mahdi Razzaque
 * @version 28.11.2024
 */

public class nonPlayerInventory {
    private HashMap<Item, Integer> inventory = new HashMap<>();
    private Room room;
    private String name;

    /**
     * Constructor for objects of class nonPlayerInventory for rooms/characters.
     * <p>
     * Initialises the inventory with the specified name and room.
     *
     * @param name The name of the room or character associated with this inventory.
     * @param room The room where the inventory is located.
     */
    public nonPlayerInventory(String name, Room room) {
        this.room = room;
        this.name = name;
    }

    /**
     * Adds an item to the inventory.
     * <p>
     * This method adds the specified item to the inventory with the given quantity.
     * If the item already exists in the inventory, it updates the quantity accordingly.
     *
     * @param item The item to be added.
     * @param number The quantity of the item to be added.
     */
    public void addItem(Item item, Integer number) {
        if (!inventory.containsKey(item)) {
            inventory.put(item, number); // Add the item with the specified quantity
            return;
        }
        inventory.put(item, inventory.get(item) + number); // Update the quantity if the item already exists
    }

    /**
     * Removes an item from the inventory.
     * <p>
     * This method removes the specified quantity of an item from the inventory.
     * If the item does not exist in the inventory, it does nothing.
     * If the quantity to be removed is greater than the quantity in the inventory, it sets the quantity to zero.
     *
     * @param item The item to be removed.
     * @param number The quantity of the item to be removed.
     */
    public void removeItem(Item item, Integer number) {
        if (!inventory.containsKey(item)) {
            return; // Do nothing if the item does not exist in the inventory
        }

        int numberInInventory = inventory.get(item);
        if (numberInInventory > number) {
            inventory.put(item, inventory.get(item) - number); // Update the quantity if the current amount is greater than the number to be removed
        } else {
            inventory.put(item, 0); // Set the quantity to zero if the number to be removed is greater
        }
        Utils.removeZeroQuantityItems(inventory); // Remove items with zero quantity from the inventory
    }


    /**
     * Displays the inventory of a room or character.
     * <p>
     * This method prints the inventory of the specified room or character. If the inventory is empty,
     * it prints a message indicating that the inventory is empty.
     *
     * @param type Indicates whether to display the inventory for a room or a character.
     */
    public void displayInventory(String type) {
        String inventoryList;
        switch (type) {
            case "character":
                System.out.println("\n**Inventory of " + name + " **");

                if (inventory.isEmpty()) {
                    System.out.println("Inventory of " + name + " is empty.");
                    return;
                }

                inventoryList = inventory.keySet().stream()
                        .map(item -> Utils.toTitleCase(item.getName()) + ": " + inventory.get(item))
                        .collect(Collectors.joining(", "));

                System.out.println(inventoryList);
                break;

            case "room":
                if (inventory.isEmpty()) {
                    System.out.println("Items: None");
                    return;
                }

                inventoryList = "Items: " + inventory.keySet().stream()
                        .map(item -> Utils.toTitleCase(item.getName()) + ": " + inventory.get(item))
                        .collect(Collectors.joining(", "));

                System.out.println(inventoryList);
                break;
        }
    }

    /**
     * Displays a selection of items in the inventory.
     * <p>
     * This method prints a list of item names formatted for selection (lowercase with spaces replaced with underscores).
     * If the inventory is empty, it prints a message indicating that.
     *
     * @param type Indicates whether to display the inventory for a room or a character.
     */
    public void displayInventorySelection(String type) {
        String inventoryList;
        switch (type) {
            case "character":
                if (inventory.isEmpty()) {
                    System.out.printf("Inventory of %s: None\n", name);
                    return;
                }

                inventoryList = inventory.keySet().stream()
                        .map(item -> item.getName().toLowerCase().replaceAll(" ", "_"))
                        .collect(Collectors.joining(", "));

                System.out.printf("Items of %s: %s\n", name, inventoryList);
                break;

            case "room":
                if (inventory.isEmpty()) {
                    System.out.println("Items: None");
                    return;
                }

                inventoryList = inventory.keySet().stream()
                        .map(item -> item.getName().toLowerCase().replaceAll(" ", "_"))
                        .collect(Collectors.joining(", "));

                System.out.println("Items: " + inventoryList);
                break;
        }
    }

    /**
     * This method retrieves the quantity of the given item in the inventory. If the item does not exist
     * in the inventory, it returns zero.
     *
     * @param item The item whose quantity is to be checked.
     * @return The quantity of the specified item in the inventory.
     */
    public Integer numberOfItem(Item item) {
        return inventory.getOrDefault(item, 0);
    }

    /**
     * This method retrieves all the items in the inventory and returns them as an ArrayList.
     *
     * @return An ArrayList containing all the items in the inventory.
     */
    public ArrayList<Item> getItems() {
        return new ArrayList<>(inventory.keySet());
    }

    /**
     * This method retrieves the inventory of items and their quantities.
     * <p>
     * @return A HashMap containing all the items and their quantities in the inventory.
     */
    public HashMap<Item, Integer> getInventory() {
        return inventory;
    }

    /**
     * Adds all items from the provided map to the inventory.
     * <p>
     * This method iterates through the provided map of items and their quantities,
     * adding each item to the inventory.
     * <p>
     * This is used to move all the items from a character inventory
     * into the room inventory upon the character's death
     *
     * @param itemsToAdd A HashMap containing items and their quantities to be added to the inventory.
     */
    public void addAll(HashMap<Item, Integer> itemsToAdd) {
        for (Map.Entry<Item, Integer> entry : itemsToAdd.entrySet()) {
            addItem(entry.getKey(), entry.getValue());
        }
    }

    /**
     * This method removes all items from the inventory.
     */
    public void clear() {
        inventory.clear();
    }
}
