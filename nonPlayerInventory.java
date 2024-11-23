import jdk.jshell.execution.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Write a description of class Inventory here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class nonPlayerInventory
{
    // instance variables - replace the example below with your own
    private HashMap<Item, Integer> inventory = new HashMap<>();
    private Room room;
    private String name;
    
    private String[] roomNames = { "Entrance Hall", "Library", "Dining Room", "Kitchen", "Pantry", "Greenhouse", "Study", "Master Bedroom","Hidden Chamber"};
    private String[] characterNames = {"Butler", "Maid", "Ghost of the Former Owner", "Cat", "Security Guard"};


    /**
     * Constructor for objects of class nonPlayerInventory for rooms/characters
     */
    public nonPlayerInventory(String name, Room room) {
        this.room = room;
        this.name = name;
    }

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public void addItem(Item item, Integer number) {        
        if(!inventory.containsKey(item)) {
            inventory.put(item, number);
            return;
        }
        
        inventory.put(item, inventory.get(item) + number);
    }
    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public void removeItem(Item item, Integer number) {
        if(!inventory.containsKey(item)) {
            return;
        }
        
        int numberInInventory = inventory.get(item);
        if(numberInInventory > number) {
            inventory.put(item, inventory.get(item) - number);
        } else {
            inventory.put(item, 0);
        }
        Utils.removeZeroQuantityItems(inventory);
    }
    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public void displayInventory() {
        if(Utils.itemExistsInArray(characterNames, name)) {
            System.out.println("\n**Inventory of " + name + " **");
            
            if(inventory.size() <= 0) {
                System.out.println("Inventory is empty.");
                return;
            }
            
            String inventoryList = inventory.keySet().stream()
                .map(item -> Utils.toTitleCase(item.getName()) + ": " + inventory.get(item))
                .collect(Collectors.joining(", "));
                
            System.out.println(inventoryList);
        }
        
        if(Utils.itemExistsInArray(roomNames, name)) {
           
            if(inventory.size() <= 0) {
                System.out.println("Items: None");
                return;
            }
            
            String inventoryList = "Items: " + inventory.keySet().stream()
                .map(item -> Utils.toTitleCase(item.getName()) + ": " + inventory.get(item))
                .collect(Collectors.joining(", "));
                
            System.out.println(inventoryList);
        }
    }

    public void displayInventorySelection() {
        if(Utils.itemExistsInArray(characterNames, name)) {
            if(inventory.size() <= 0) {
                System.out.printf("Inventory of %s: None\n", name);
                return;
            }

            String inventoryList = inventory.keySet().stream()
                    .map(item -> item.getName().toLowerCase().replaceAll(" ", "_"))
                    .collect(Collectors.joining(", "));

            System.out.printf("Items of %s: %s\n", name, inventoryList);
        }

        if(Utils.itemExistsInArray(roomNames, name)) {
            if(inventory.size() <= 0) {
                System.out.println("Items: None");
                return;
            }

            String inventoryList = inventory.keySet().stream()
                    .map(item -> item.getName().toLowerCase().replaceAll(" ", "_"))
                    .collect(Collectors.joining(", "));

            System.out.println("Items: " + inventoryList);
        }
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
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */    
    public ArrayList<Item> getItems() {
        return new ArrayList<>(inventory.keySet());
    }
    
    public HashMap<Item, Integer> getInventory() {
        return inventory;
    }
    
    public void addAll(HashMap<Item, Integer> itemsToAdd) {
        for (Map.Entry<Item, Integer> entry : itemsToAdd.entrySet()) {
            addItem(entry.getKey(), entry.getValue());
        }
    }
    
    public void clear() {
        inventory.clear();
    } 
}
