import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class Room - a room in an adventure game.
 *
 * This class is part of the "Escape from the Mystic Manor" application. 
 * "Escape from the Mystic Manor" is a very simple, text based adventure game.  
 *
 * A "Room" represents one location in the scenery of the game.  It is 
 * connected to other rooms via exits.  For each existing exit, the room 
 * stores a reference to the neighboring room.
 * 
 * @author  Michael Kölling, David J. Barnes, Mahdi Razzaque
 * @version 24.11.2024
 */

public class Room 
{
    private String name, description; // The name and description of the room
    private HashMap<String, Room> exits; // Stores exits of this room
    private nonPlayerInventory roomInventory; // The inventory associated with the room
    private ArrayList<Character> characters = new ArrayList<>(); // List of characters in the room

    /**
     * Creates a room with the specified name and description. Initially, it has no exits.
     * The description is something like "a kitchen" or "an open courtyard".
     *
     * @param name The room's name.
     * @param description The room's description.
     */
    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        exits = new HashMap<>();
        roomInventory = new nonPlayerInventory(name, this);

        //Add all unlocked rooms to ArrayList for magic mirror
        if(!name.equalsIgnoreCase("Hidden Chamber") && !name.equalsIgnoreCase("Pantry")) {
            Game.allUnlockedRooms.add(this);
        }
    }
    
    /**
     * Define an exit from this room.
     * @param direction The direction of the exit.
     * @param neighbor  The room to which the exit leads.
     */
    public void setExit(String direction, Room neighbor) 
    {
        exits.put(direction, neighbor);
    }

    /**
     * Retrieves the name of the room.
     *
     * @return The name of the room.
     */
    public String getName() {
        return name;
    }

    /**
     * Displays the name of the room in a formatted way.
     * Converts the name to title case, formats it with a box
     * and then outputs it.
     */
    public void displayName() {
        String titleCase = Utils.toTitleCase(name); // Convert the room name to title case
        String boxedName = Utils.boxString(titleCase); // Format the title case name with a box
        System.out.println(boxedName); // Print the formatted name
    }

    /**
     * @return The short description of the room
     * (the one that was defined in the constructor).
     */
    public String getShortDescription()
    {
        return description;
    }

    /**
     * Return a description of the room in the form:
     *     You are in the kitchen.
     *     Exits: north west
     * @return A long description of this room
     */
    public String getLongDescription()
    {
        return "You are " + description + ".\n" + getExitString();
    }

    /**
     * Return a string describing the room's exits, for example
     * "Exits: north west".
     * @return Details of the room's exits.
     */
    private String getExitString()
    {
        String returnString = "Exits:";
        Set<String> keys = exits.keySet();
        for(String exit : keys) {
            returnString += " " + exit;
        }
        return returnString;
    }

    /**
     * Retrieves the exits of the room.
     * <p>
     * This method returns a HashMap containing the exits of the room,
     * where the keys are the directions and the values are the neighboring rooms.
     * <p>
     * @return A HashMap containing the exits of the room.
     */
    public HashMap<String, Room> getExits() {
        return exits;
    }

    /**
     * Return the room that is reached if we go from this room in direction
     * "direction". If there is no room in that direction, return null.
     * @param direction The exit's direction.
     * @return The room in the given direction.
     */
    public Room getExit(String direction) 
    {
        return exits.get(direction);
    }

    /**
     * Retrieves the list of characters currently in the room.
     * @return An ArrayList of characters currently in the room.
     */
    public ArrayList getCharacters() {
        return characters;
    }

    /**
     * Outputs a string showing which characters are currently in the given room.
     * <p>
     * This method checks if the room has any characters. If no characters are present,
     * it prints "Characters: None". Otherwise, it prints a list of the names of the characters
     * currently in the room, separated by commas.
     * <p>
     */
    public void displayCharacters() {
        if (characters.isEmpty()) { // Check if there are no characters in the room
            System.out.println("Characters: None"); // Print message indicating no characters are present
        } else {
            System.out.println("Characters: " + characters.stream().map(character -> character.getName()).collect(Collectors.joining(", "))); // Print names of characters in the room
        }
    }

    /**
     * Outputs a string showing which characters are currently in the given room.
     * <p>
     * This method checks if the room has any characters. If no characters are present,
     * it prints "Characters: None". Otherwise, it prints a list of the names of the characters
     * currently in the room, formatted for selection by converting the names to lowercase
     * and replacing spaces with underscores.
     * <p>
     */
    public void displayCharacterSelection() {
        if (characters.isEmpty()) { // Check if there are no characters in the room
            System.out.println("Characters: None"); // Output message indicating no characters are present
            return;
        }

        System.out.println("Characters: " + characters.stream()
                .map(character -> character.getName().toLowerCase().replaceAll(" ", "_")) // Format names for selection
                .collect(Collectors.joining(", "))); // Join formatted names with commas
    }

    /**
     * This method adds the specified character to the arrayList of characters present in the room.
     *
     * @param character The character to be added.
     */
    public void addCharacter(Character character) {
        characters.add(character);
    }


    /**
     * Removes the specified character from room
     * and transfers the character's inventory to the room's inventory.
     *
     * @param character The character to be removed.
     */
    public void removeCharacter(Character character) {
        character.transferInventoryToRoom(); // Transfer the character's inventory to the room's inventory
        characters.remove(character); // Remove the character from the list of characters in the room
    }

    /**
     * Returns the room's inventory.
     * @return The inventory associated with the room.
     */
    public nonPlayerInventory getRoomInventory() {
        return roomInventory;
    }
 
    /**
     * Adds an item to the room inventory.
     * @param item The item to be added.
     * @param quantity The quantity of the item to be added.
     */
    public void addItemToRoomInventory(Item item, Integer quantity) {
        roomInventory.addItem(item, quantity);
    }
    
    /**
     * Removes an item from the room inventory.
     * @param item The item to be removed.
     * @param quantity The quantity of the item to be removed.
     */
    public void removeItemFromRoomInventory(Item item, Integer quantity) {
        roomInventory.removeItem(item, quantity);
    }
    
    /**
     * Displays the current room inventory.
     */
    public void displayRoomInventory() {
        roomInventory.displayInventory("room");
    }

    /**
     * Displays the current room inventory as a selection.
     * All the room names are converted to lowercase
     * and spaces are replaced with underscores
     */
    public void displayRoomInventorySelection() {
        roomInventory.displayInventorySelection("room");
    }
    
    /**
     * Checks the number of a specific item in the room inventory.
     * @param item The item to check.
     * @return The number of the specified item in the room inventory.
     */
    public Integer numberOfItemInRoomInventory(Item item) {
        return roomInventory.numberOfItem(item);
    }
    
    /**
     * Clears all items from the room inventory.
     */
    public void clearRoomInventory() {
        roomInventory.clear();
    }
    
    /**
     * Adds all items from another inventory to the room inventory.
     * @param itemsToAdd The items to be added to the room inventory.
     */
    public void addAllItemsToRoomInventory(HashMap<Item, Integer> itemsToAdd) {
        roomInventory.addAll(itemsToAdd);
    }

    /**
     * Displays the details of the room.
     * <p>
     * This method prints the name of the room, the long description of the room,
     * the room's inventory, and the characters present in the room.
     * <p>
     */
    public void displayRoomDetails() {
        displayName(); // Print the name of the room
        System.out.println(getLongDescription()); // Print the long description of the room
        displayRoomInventory(); // Display the room's inventory
        displayCharacters(); // Display the characters present in the room
    }
}

