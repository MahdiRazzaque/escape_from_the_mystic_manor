import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */

public class Game 
{
    private Parser parser;
    private Room currentRoom;
    private Room entranceHall, library, diningRoom, kitchen, greenhouse, study, masterBedroom, hiddenChamber;
    private Inventory inventory;
    private Item goldenKey, ancientBook, jewelledDagger, magicMirror, coin;
    private Character butler, maid, ghost, cat, securityGuard;
    public static HashMap<String, Item> itemMap;
    public static HashMap<String, Character> characterMap;
    private HashMap<String, String> oppositeDirections;
    private ArrayList<String> backCommandStack = new ArrayList<>();
    
    /**
     * Create the game and initialise its internal map.
     */
    public Game() {
        createRooms();
        parser = new Parser();
        initialiseInventory();
        
        initialiseItems();
        initialiseItemMap();

        initialiseCharacters();
        addCharactersToRooms();
        initialiseCharacterMap();

        initialiseOppositeDirections();

        inventory.addItem(coin, 10);
        inventory.addItem(jewelledDagger, 1);
        inventory.displayInventory();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()  {
        System.out.println("This is a test!");
        // Create the rooms
        entranceHall = new Room("Entrance Hall", "in the entrance hall of the Mystic Manor");
        library = new Room("Library", "in the library filled with ancient books");
        diningRoom = new Room("Dining Room", "in the grand dining room with a large table");
        kitchen = new Room("Kitchen", "in the kitchen with a locked pantry");
        greenhouse = new Room("Greenhouse", "in the indoor garden with exotic plants");
        study = new Room("Study", "in the quiet study with a locked drawer");
        masterBedroom = new Room("Master Bedroom", "in the luxurious master bedroom of the former owner");
        hiddenChamber = new Room("Hidden Chamber", "in the hidden chamber full of secrets");
        
        // Initialise room exits
        // Entrance Hall exits
        entranceHall.setExit("north", library);
        entranceHall.setExit("east", diningRoom);
        
        // Library exits
        library.setExit("south", entranceHall);
        library.setExit("north", study);
        
        // Dining Room exits
        diningRoom.setExit("west", entranceHall);
        diningRoom.setExit("north", kitchen);
        
        // Kitchen exits
        kitchen.setExit("south", diningRoom);
        kitchen.setExit("north", greenhouse);
        
        // Greenhouse exits
        greenhouse.setExit("south", kitchen);
        
        // Study exits
        study.setExit("south", library);
        study.setExit("west", masterBedroom);
        
        // Master Bedroom exits
        masterBedroom.setExit("east", study);
        masterBedroom.setExit("south", hiddenChamber);
        
        // Hidden chamber exits
        hiddenChamber.setExit("north", masterBedroom);
        
        // Set the starting room
        currentRoom = entranceHall;
        
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
                
        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome() {
        System.out.println();
        System.out.println("Welcome to the Mystic Manor!");
        System.out.println("Mystic Manor is an adventure game where you must find a way to escape.");
        System.out.println("Type 'help' if you need assistance.");
        System.out.println();
        currentRoom.displayRoomDetails(); 
    }
    

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) {
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        switch (commandWord) {
            case "help":
                printHelp();
                break;
            case "go":
                goRoom(command);
                break;
            case "quit":
                wantToQuit = quit(command);
                break;
            case "inventory":
                processInventoryCommand(command);
                break;
            case "back":
                processBackCommand();
                break;
            case "interact":
                processInteractCommand(command);
                break;
            case "room":
                processRoomCommand(command);
                break;
            default:
                System.out.println("Unknown command: " + commandWord);
                break;
        }

        // Triggers characters to randomly move to adjacent rooms with a change of 1/30
        // Only triggered when the player is inputting commands to prevent characters from randomly moving when the player is AFK
        randomCharacterMovement();

        // else command not recognised.
        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() {
        System.out.println("You are trapped in the Mystic Manor. You must find clues to escape.");
        System.out.println("Explore the rooms and gather the necessary items to unlock the front door.");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
    }


    /** 
     * Try to in to one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command) {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        }
        else {
            backCommandStack.add(oppositeDirections.get(direction));
            System.out.println(backCommandStack);
            
            currentRoom = nextRoom;
            currentRoom.displayRoomDetails();          
        }
    }
    
    /** 
     * Moves the player in the specified direction. If there is an exit in the given direction,
     * the player enters the new room and the room's description is displayed.
     * This method is used for the back command
     * @param direction The direction in which the player wants to move (e.g., "north", "east").
     */
    private void goRoom(String direction) {
        Room nextRoom = currentRoom.getExit(direction);
        currentRoom = nextRoom;
        currentRoom.displayRoomDetails(); 
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }
    
    /**
     * Processes inventory-related commands.
     */
    private void processInventoryCommand(Command command) {    
        if (!command.hasSecondWord()) {
            System.out.println("Available inventory commands: " + "\n" + 
                                "inventory display - Display current inventory" + "\n" +
                                "inventory drop [item] [quantity] - Drop an item from your inventory" + "\n" +
                                "inventory pickup [item] [quantity] - Pickup an item from the current room");
            return;
        }
        int quantity;
        switch (command.getSecondWord()) {
            case "display":
                inventory.displayInventory();
                break;
            case "drop":
                if (!command.hasThirdWord()) {
                    System.out.println("Drop what item?");
                    inventory.displayInventorySelection();
                    break;
                }

                try {
                    quantity = Integer.parseInt(command.getFourthWord());
                } catch (NumberFormatException e) {
                    System.out.println("Please specify a valid quantity.");
                    return;
                }

                processDropItem(command.getThirdWord(), quantity);
                break;
            case "pickup":
                if (!command.hasThirdWord()) {
                    System.out.println("Pickup what item?");
                    currentRoom.displayRoomInventory();
                    break;
                }

                try {
                    quantity = Integer.parseInt(command.getFourthWord());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid quantity. Please enter a number.");
                    return;
                }

                processPickupItem(command.getThirdWord(), quantity);
                break;
            default:
                System.out.println("Available inventory commands: " + "\n" + 
                                "inventory display - Display current inventory" + "\n" +
                                "inventory drop [item] [quantity] - Drop an item from your inventory" + "\n" +
                                "inventory pickup [item] [quantity] - Pickup an item from the current room");
                break;
        }
    }  
    
    /**
     * Processes the dropping of an item with a specified quantity, removing it from the player's inventory and adding it to the room's inventory.
     * @param itemName The name of the item to be dropped.
     * @param quantity The quantity of the item to be dropped.
     */
    private void processDropItem(String itemName, int quantity) {
        if (!itemMap.containsKey(itemName)) {
            System.out.println("Item not found");
            currentRoom.displayRoomInventory();
            return;
        }
    
        Item itemToBeDropped = itemMap.get(itemName);
        if (inventory.numberOfItem(itemToBeDropped) <= quantity) {
            System.out.println("You do not have enough " + itemName.replace("_", " ") + "s to drop");
            return;
        }
        inventory.dropItem(currentRoom, itemToBeDropped, quantity);
    }

    /**
     *
     */
    private void processPickupItem(String itemName, int quantity) {
        if (!itemMap.containsKey(itemName)) {
            System.out.println("Item not found");
            inventory.displayInventorySelection();
            return;
        }

        Item itemToBePickedUp = itemMap.get(itemName);
        // Check if there is enough of the item in the room
        if (currentRoom.numberOfItemInRoomInventory(itemToBePickedUp) < quantity) {
            System.out.println("Insufficient quantity in the room.");
            return;
        }

        // Perform the pickup
        inventory.pickupItem(currentRoom, itemToBePickedUp, quantity);
    }

    /**
     * Processes the back command, allowing the player to return to the previous room they just came from.
     * If the player hasn't moved yet, an error message is displayed.
     * The last move direction is popped from the backCommandStack and used to return to the previous room.
     */
    private void processBackCommand() {
        if (backCommandStack.size() <= 0) {
            System.out.println("There are no rooms for you to go back to.");
            return;
        }
        
        int lastIndex = backCommandStack.size() - 1;
        goRoom(backCommandStack.get(lastIndex));
        backCommandStack.remove(lastIndex);
    }

    /**
     *
     * @param command
     */
    private void processInteractCommand(Command command) {
        if(!command.hasSecondWord()) {
            System.out.println("State the character you want to interact with: ");
            currentRoom.displayCharacterSelection();
            return;
        }
        
        String characterSelected = command.getSecondWord();
        
        if (!characterMap.containsKey(characterSelected)) {
            System.out.println("State the character you want to interact with: ");
            currentRoom.displayCharacterSelection();
            return;
        }

        characterMap.get(characterSelected).interact();
    }

    /**
     *
     * @param command
     */
    private void processRoomCommand(Command command) {
        if(!command.hasSecondWord()) {
            System.out.println("Available room commands: " + "\n" +
                    "room info - Display information about the current room");
            return;
        }

        switch(command.getSecondWord()) {
            case "info":
                currentRoom.displayRoomDetails();
                break;
            default:
                System.out.println("Available room commands: " + "\n" +
                        "room info - Display information about the current room");
        }
    }

    /**
     *
     * @param item
     * @param quantity
     */
    public void givePlayerItem(Character character, Item item, Integer quantity) {
        if(character.numberOfItemInCharacterInventory(item) > quantity) {
            System.out.println("Character cannot give more items than they have");
            return;
        }

        if(!itemMap.containsKey(item.getName())) {
            System.out.println("Item not found");
        }

        character.removeItemFromCharacterInventory(item, quantity);
        inventory.addItem(item, quantity);
    }

    /**
     * Initialises the items in the game.
     */
    private void initialiseItems() {    
        goldenKey = new Item("golden key", 1);
        ancientBook = new Item("ancient book", 20);
        jewelledDagger = new Item("jewelled dagger", 15);
        magicMirror = new Item("magic mirror", 5);
        coin = new Item("coin", 1);
    }

    /**
     * Initialises the characters in the game.
     */
    private void initialiseCharacters() {
        butler = new Character("Butler", true, 100, entranceHall);
        maid = new Character("Maid", true, 80, kitchen);
        ghost = new Character("Ghost of the Former Owner", false, 150, masterBedroom);
        cat = new Character("Cat", false, 60, library);
        securityGuard = new Character("Security Guard", false, 120, entranceHall);
    }
    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param
     */
    private void initialiseInventory() {
        inventory = new Inventory(100);
    }

    /**
     * Initialises the item map with all the possible items in the game.
     */
    private void initialiseItemMap() {
        itemMap = new HashMap<>();
        itemMap.put("coin", coin);
        itemMap.put("golden_key", goldenKey);
        itemMap.put("ancient_book", ancientBook);
        itemMap.put("jewelled_dagger", jewelledDagger);
        itemMap.put("magic_mirror", magicMirror);
    }

    /**
     * Initialises the character map with all the possible characters in the game.
     */
    private void initialiseCharacterMap() {
        characterMap = new HashMap<>();
        characterMap.put("butler", butler);
        characterMap.put("maid", maid);
        characterMap.put("ghost_of_the_former_owner", ghost);
        characterMap.put("cat", cat);
        characterMap.put("security_guard", securityGuard);
    }


    /**
     * Initialises a HashMap that maps each direction (north, east, south, west) to its opposite direction.
     */
    private void initialiseOppositeDirections() {
        oppositeDirections = new HashMap<>();
        oppositeDirections.put("north", "south");
        oppositeDirections.put("east", "west");
        oppositeDirections.put("south", "north");
        oppositeDirections.put("west", "east");
    }
    
    /**
     * Adds all characters to their corresponding rooms.
     */
    private void addCharactersToRooms() {
        entranceHall.addCharacter(butler);
        entranceHall.addCharacter(securityGuard);
        library.addCharacter(cat);
        kitchen.addCharacter(maid);
        masterBedroom.addCharacter(ghost);       
    }

    private void randomCharacterMovement() {
        for(Character character : characterMap.values()) {
            character.randomRoomMovement();
        }
    }

}
