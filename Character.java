    import java.awt.*;
    import java.lang.reflect.Array;
    import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Random;

/**
 * Class Character - represents a character in an adventure game.
 *
 * A "Character" represents a player or non-player character within the game, 
 * possessing attributes such as health, inventory, and location within a room.
 * 
 * @author Mahdi
 * @version 17.11.2024
 */
public class Character
{
    private String name;
    private Boolean passive;
    private Integer currentHealth;
    private Integer maxHealth;
    private Room currentRoom;
    private nonPlayerInventory characterInventory;
    private boolean interactedWith;

    /**
     * Constructs a Character object with the specified name, passivity status, maximum health, and current room.
     * @param name The name of the character.
     * @param passive Indicates whether the character is passive.
     * @param maxHealth The maximum health value of the character.
     * @param currentRoom The room where the character is currently located.
     */
    public Character(String name, Boolean passive, Integer maxHealth, Room currentRoom) {
        this.name = name;
        this.passive = passive;
        currentHealth = this.maxHealth = maxHealth;
        this.currentRoom = currentRoom;
        characterInventory = new nonPlayerInventory(name, currentRoom);
        interactedWith = false;
    }

    /**
     * Returns the name of the character.
     * @return The name of the character.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns whether the character is passive or not.
     * @return true if the character is passive; false otherwise.
     */
    public Boolean getPassive() {
        return passive;
    }
    
    /**
     * Returns the current health of the character.
     * @return The current health of the character.
     */
    public Integer getHealth() {
        return currentHealth;
    }
    
    /**
     * Adds the provided health value to the current health of the character.
     * @param health The amount of health to add.
     */
    public void addHealth(Integer health) {
        if ((currentHealth + health) >= maxHealth) {
            currentHealth = maxHealth;
            return;
        }
        currentHealth += health;
    }
    
    /**
     * Removes the provided health value from the current health of the character.
     * If the health drops to zero or below, the character's inventory is transferred to the room's inventory.
     * @param health The amount of health to remove.
     */
    public void removeHealth(Integer health) {
        if ((currentHealth - health) <= 0) {
            currentHealth = 0;
            transferInventoryToRoom();
        } else {
            currentHealth -= health;
        }
    }
  
    /**
     * Transfers the character's inventory to the room's inventory
     * when the character's health reaches zero.
     */
    private void transferInventoryToRoom() {
        if (currentRoom != null) {
            currentRoom.getRoomInventory().addAll(characterInventory.getInventory());
            characterInventory.clear();
        }
    }
    
    /**
     * Adds an item to the character's inventory.
     * @param item The item to be added.
     * @param quantity The quantity of the item to be added.
     */
    public void addItemToCharacterInventory(Item item, Integer quantity) {
        characterInventory.addItem(item, quantity);
    }
    
    /**
     * Removes an item from the character's inventory.
     * @param item The item to be removed.
     * @param quantity The quantity of the item to be removed.
     */
    public void removeItemFromCharacterInventory(Item item, Integer quantity) {
        characterInventory.removeItem(item, quantity);
    }
    
    /**
     * Displays the character's inventory.
     */
    public void displayCharacterInventory() {
        characterInventory.displayInventory();
    }
    
    /**
     * Checks the number of a specific item in the character's inventory.
     * @param item The item to check.
     * @return The number of the specified item in the character's inventory.
     */
    public Integer numberOfItemInCharacterInventory(Item item) {
        return characterInventory.numberOfItem(item);
    }
    
    /**
     * Clears the character's inventory.
     */
    public void clearCharacterInventory() {
        characterInventory.clear();
    }
    
    /**
     * Adds all items from another inventory to the character's inventory.
     * @param itemsToAdd The items to be added to the character's inventory.
     */
    public void addAllItemsToCharacterInventory(HashMap<Item, Integer> itemsToAdd) {
        characterInventory.addAll(itemsToAdd);
    }
    
    /**
     *
     */
    public void interact() {
        Dialog.getDialog(name);
        interactedWith = true;
    }

    public void goRoom(String direction) {
        Room nextRoom = currentRoom.getExit(direction);
        currentRoom = nextRoom;
    }

    public HashMap<String, Room> getRoomExits() {
        return currentRoom.getExits();
    }

    public void randomRoomMovement() {
        ArrayList<String> exits = new ArrayList<>(getRoomExits().keySet());
        Random random = new Random();
        int randomIndex = random.nextInt(exits.size());
        int randomChance = random.nextInt(30);
        if(randomChance == 15 && interactedWith) {
            goRoom(exits.get(randomIndex));
            //System.out.println(name + " has moved to " + currentRoom.getName());
        }

    }
}
