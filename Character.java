import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Class Character - represents a character in an adventure game.
 * <p>
 * A "Character" represents a player or non-player character within the game, 
 * with attributes such as health, passiveness, inventory, and location within the map.
 *
 * @author Mahdi Razzaque
 * @version 28.11.2024
 */
public class Character {
    private String name;
    private Boolean passive;
    private Integer currentHealth;
    private Integer maxHealth;
    private Room currentRoom;
    private nonPlayerInventory characterInventory;
    private boolean interactedWith;
    private boolean randomCharacterMovement;
    private Integer randomMovementChance;

    /**
     * Constructs a Character object with the specified name, passivity, maximum health, and current room.
     *
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
        characterInventory = new nonPlayerInventory(name, currentRoom); // Creates an inventory for the character
        interactedWith = false;

        //Game.characterMap.put(Utils.toSnakeCase(name), this); //Add the character to the character map
        Game.characterMap.put(Utils.toSnakeCase(name), this); //Add the character to the character map
    }

    /**
     * Returns the name of the character.
     *
     * @return The name of the character.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns whether the character is passive or not.
     *
     * @return true if the character is passive; false otherwise.
     */
    public Boolean getPassive() {
        return passive;
    }
    
    /**
     * Returns the current health of the character.
     *
     * @return The current health of the character.
     */
    public Integer getHealth() {
        return currentHealth;
    }

    /**
     * Returns if the character has been interacted with
     *
     * @return true if the character has been interacted with; false otherwise.
     */
    public boolean getInteractedWith() {
        return interactedWith;
    }

    /**
     * This method returns the room where the character is currently located.
     *
     * @return The room where the character is currently located.
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Adds the provided health value to the current health of the character.
     *
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
     *
     * @param health The amount of health to remove.
     */
    public void removeHealth(Integer health) {
        if ((currentHealth - health) == 0) {
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
    public void transferInventoryToRoom() {
        if (currentRoom != null) {
            currentRoom.getRoomInventory().addAll(characterInventory.getInventory());
            characterInventory.clear();
        }
    }
    
    /**
     * Adds a specified quantity of an item to the character's inventory.
     *
     * @param item The item to be added.
     * @param quantity The quantity of the item to be added.
     */
    public void addItemToCharacterInventory(Item item, Integer quantity) {
        characterInventory.addItem(item, quantity);
    }
    
    /**
     * Removes a specified quantity of an item from the character's inventory.
     *
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
        characterInventory.displayInventory("character");
    }
    
    /**
     * Returns the number of a specific item in the character's inventory.
     *
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
     *
     * @param itemsToAdd The items to be added to the character's inventory.
     */
    public void addAllItemsToCharacterInventory(HashMap<Item, Integer> itemsToAdd) {
        characterInventory.addAll(itemsToAdd);
    }

    /**
     * Initiates interaction with the character.
     * <p>
     * This method triggers a dialog with the character and sets the interaction status to true.
     * The dialog is managed by the `Dialog` class, which displays the character's dialog, once interacted with.
     */
    public void interact() {
        Dialog.getDialog(name); // Trigger a dialog with the character
        interactedWith = true; // Set the interaction status to true
    }

    /**
     * This method retrieves the room in the given direction and updates the character's
     * current room to this new room.
     *
     * @param direction The direction in which to move the character.
     */
    public void goRoom(String direction) {
        currentRoom.removeCharacter(this);
        Room nextRoom = currentRoom.getExit(direction); // Retrieve the room in the specified direction
        currentRoom = nextRoom; // Update the character's current room to the new room
        currentRoom.addCharacter(this);
    }

    /**
     * This method returns a HashMap containing the exits of the room where the
     * character is currently located.
     *
     * @return A HashMap containing the exits of the current room.
     */
    public HashMap<String, Room> getRoomExits() {
        return currentRoom.getExits();
    }

    /**
     * This method sets whether the character will move randomly and the
     * chance of random movement.
     *
     * @param randomCharacterMovement Whether the character should move randomly.
     * @param randomMovementChance The chance of the character moving randomly.
     */
    public void setRandomMovementValues(Boolean randomCharacterMovement, Integer randomMovementChance) {
        this.randomCharacterMovement = randomCharacterMovement; // Set the random movement flag
        this.randomMovementChance = randomMovementChance; // Set the random movement chance
    }

    /**
     * Moves the character to a random room based on the specified random movement chance.
     * <p>
     * This method selects a random exit from the current room's exits and moves the character
     * to the corresponding room if the random movement conditions are met.
     * The conditions include if random character movement is enabled, the random chance
     * being met, and the character having been interacted with.
     */
    public void randomRoomMovement() {
        if (!randomCharacterMovement) return; // Check if random movement is enabled
        if (!interactedWith) return; // Check if the character has been interacted with
        ArrayList<String> exits = new ArrayList<>(getRoomExits().keySet()); // Get the list of room exits
        Random random = new Random();
        int randomIndex = random.nextInt(exits.size()); // Select a random index for the exits
        int randomChance = random.nextInt(randomMovementChance); // Generate a random chance
        if (randomChance == randomMovementChance / 2) { // Check if movement conditions are met
            goRoom(exits.get(randomIndex)); // Move the character to the random exit room
            //System.out.println(name + " has moved to " + currentRoom.getName()); // Print movement confirmation
        }
    }
}
