import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class Room - a room in an adventure game.
 *
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.  
 *
 * A "Room" represents one location in the scenery of the game.  It is 
 * connected to other rooms via exits.  For each existing exit, the room 
 * stores a reference to the neighboring room.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */

public class Room 
{
    private String name, description;
    private HashMap<String, Room> exits;        // stores exits of this room.
    private nonPlayerInventory roomInventory;
    private ArrayList<Character> characters = new ArrayList<>();

    /**
     * Create a room described "description". Initially, it has
     * no exits. "description" is something like "a kitchen" or
     * "an open court yard".
     * @param description The room's description.
     */
    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        exits = new HashMap<>();
        roomInventory = new nonPlayerInventory (name, this);
    }
    
    /**
     * Create a room described "description".
     * Adds characters to the room when provided.
     * If no characters are provided, the description only constructor is used.
     * @param name The room's name
     * @param description The room's description.
     * @param characters ArrayList of characters to be placed in the room
     */
    public Room(String name, String description, ArrayList characters) {
        this.name = name;
        this.description = description;
        exits = new HashMap<>();
        
        roomInventory = new nonPlayerInventory(name, this);
        this.characters = characters;
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
     *
     * @return
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return name The name of the room
     */
    public void displayName() {
        String titleCase = Utils.toTitleCase(name);
        String boxedName = Utils.boxString(titleCase);
        System.out.println(boxedName);
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
     * Returns ArrayList of characters currently in the given room
     * 
     * @return ArrayList of characters currently in the given room
    */
    public ArrayList getCharacters() {
        return characters;
    }
    
    /**
     * Outputs a string showing which characters are currently in the given room.
    */
    public void displayCharacters() {
        if(characters.size() <= 0) {
            System.out.println("Characters: None");
        } else {
            System.out.println("Characters: " + characters.stream().map(character -> character.getName()).collect(Collectors.joining(", ")));
        }
        
    }
    
    /**
     * Outputs a string showing which characters are currently in the given room.
    */
    public void displayCharacterSelection() {
        if(characters.size() <= 0) {
            System.out.println("Characters: None");
        } else {
            System.out.println("Characters: " + characters.stream().map(character -> character.getName().toLowerCase().replaceAll(" ", "_")).collect(Collectors.joining(", ")));
        }
        
    }
    
    /**
     * Adds the given character into the room
     * @param item The character to be added
    */
    public void addCharacter(Character character) {
        characters.add(character);
    }
    
    /**
     * Removes a specified character from a room
     * @param item The character to be removed
    */
    public void removeCharacter(Character character) {
        character.transferInventoryToRoom();
        characters.remove(character);
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
     *
     */
    public void displayRoomDetails() {
        displayName();
        System.out.println(getLongDescription());
        displayRoomInventory();
        displayCharacters();
    }
}

