import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.Random;

/**
 *  This class is the main class of the "Escape from the Mystic Manor" application.
 *  "Escape from the Mystic Manor" is a very simple, text based adventure game.  Users
 *  can walk around some scenery. That's all. It should really be extended
 *  to make it more interesting!
 *  <p>
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 *  <p>
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 *
 * @author  Michael Kölling, David J. Barnes and Mahdi Razzaque
 * @version 24.11.2024
 */

public class Game {
    private Parser parser; // The parser to handle user inputs
    private Room currentRoom; // The current room the player is in
    private Room entranceHall, library, diningRoom, kitchen, pantry, greenhouse, study, masterBedroom, hiddenChamber; // Rooms in the game
    private HashSet<Room> visitedRooms; // Set of visited rooms
    public static ArrayList<Room> allUnlockedRooms; // List of all unlocked rooms for the use of magic mirror
    public static ArrayList<Room> allLockedRooms; // List of all locked rooms for the use of the magic mirror
    private lockedDoor kitchenPantry, bedroomChamber; // lockedDoor objects
    private ArrayList<String> lockedDirections; // ArrayList to store locked room/directions for goRoom command
    private Inventory inventory; // The player's inventory
    private Item ancientBook, jewelledDagger, magicMirror, coin, holyBread, vacuum; // Items in the game
    private Item pantryKey, chambersKey; // Keys for locked doors
    private Character butler, maid, ghost, cat, securityGuard; // Non-player characters in the game
    public static HashMap<String, lockedDoor> lockedDoorsMap; // Maps room + direction to a lockedDoor object
    public static HashMap<String, Item> itemMap; // Map of items
    public static HashMap<String, Character> characterMap; // Map of characters
    private HashMap<String, String> oppositeDirections; // Map of opposite directions for back command
    private Stack<String> backCommandStack = new Stack<>();
    private boolean mapEnabled, randomCharacterMovement; // Flags for if map is enabled and random character movement


    /**
     * Constructor for the Game class.
     * <p>
     * This constructor initialises the game by setting up rooms, items, characters, locked doors,
     * and other necessary components. It calls various methods to create and initialise these components.
     */
    public Game() {
        // ArrayList of all unlocked rooms (all rooms excluding hidden chambers and pantry)
        allUnlockedRooms = new ArrayList<>();
        allLockedRooms = new ArrayList<>();

        createRooms();
        parser = new Parser();
        initialiseInventory();

        //Initialise maps
        itemMap = new HashMap<>();
        characterMap = new HashMap<>();
        lockedDoorsMap = new HashMap<>();
        initialiseOppositeDirections();

        initialiseItems();
        addItemsToRooms();

        initialiseCharacters();
        addCharactersToRooms();
        addItemsToCharacters();

        initialiseLockedDoors();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()  {
        // Create the rooms
        entranceHall = new Room("Entrance Hall", "in the entrance hall of the Mystic Manor");
        library = new Room("Library", "in the library filled with ancient books");
        diningRoom = new Room("Dining Room", "in the grand dining room with a large table");
        kitchen = new Room("Kitchen", "in the kitchen with a locked pantry");
        pantry = new Room("Pantry", "in the pantry, where a vital item for your escape awaits");
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
        kitchen.setExit("east", pantry);

        // Pantry exits
        pantry.setExit("west", kitchen);

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

        // Track visited rooms
        visitedRooms = new HashSet<>();
        visitedRooms.add(currentRoom);
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() {
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
     * Prompts the player to change game settings such as if map is enabled and random character movement.
     */
    private void printWelcome() {
        System.out.println();
        System.out.println("Welcome to the Mystic Manor!");
        System.out.println("Mystic Manor is an adventure game where you must find a way to escape.");
        System.out.println("Type 'help' if you need assistance.");
        System.out.println();

        System.out.println("Before you venture forth, let us adjust a few settings to shape your journey.");
        setGameValues();

        currentRoom.displayRoomDetails();
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) {
        boolean wantToQuit = false;

        // If the command word is not found, a message is outputted and the command is ignored.
        if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        // Triggers characters to randomly move to adjacent rooms with a chance chosen by the player
        // Only triggered when the player is inputting commands to prevent characters from randomly moving when the player is AFK
        randomCharacterMovement();

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
            case "use":
                processUseCommand(command);
                break;
            case "answer":
                processAnswerCommand(command);
                break;
            case "map":
                processMapCommand();
                break;
            case "configure":
                setGameValues();
                break;
            default:
                System.out.println("Unknown command: " + commandWord);
                break;
        }

        // else command not recognised.
        return wantToQuit;
    }

    // Implementations of user commands:

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
            return;
        }

        // Check if the direction is locked
        if (lockedDirections.contains(Utils.roomDirToSnake(currentRoom, direction))) {
            lockedDoor lockedDoor = lockedDoorsMap.get(Utils.roomDirToSnake(currentRoom, direction));

            // If the player doesn't have the key, prevent entry and display a message
            if (inventory.numberOfItem(lockedDoor.getKey()) == 0) {
                System.out.println("Door is locked!");
                System.out.printf("To enter this room you must find the %s.\n", lockedDoor.getKey().getName());
                return;
            }
        }

        // Add the opposite direction to the back command stack for backtracking
        backCommandStack.push(oppositeDirections.get(direction));
        //System.out.println(backCommandStack);

        // Move the player to the next room and display its details
        currentRoom = nextRoom;
        currentRoom.displayRoomDetails();
        visitedRooms.add(currentRoom); // Mark the room as visited
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
     * Moves the player to the specified room and displays the details of the new room.
     *
     * @param room The room to which the player will move.
     */
    private void goRoom(Room room) {
        currentRoom = room;
        currentRoom.displayRoomDetails();
    }

    /**
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) {
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
     * <p>
     * This method handles various commands related to the inventory. It provides options to display the inventory,
     * drop an item from the inventory, or pick up an item from the current room. If no second word is provided in
     * the command, it displays the available inventory commands.
     * <p>
     * @param command The command object containing the user's input.
     */
    private void processInventoryCommand(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Available inventory commands: " + "\n" +
                    "inventory display - Display current inventory" + "\n" +
                    "inventory drop [item] [quantity] - Drop an item from your inventory" + "\n" +
                    "inventory pickup [item] [quantity] - Pick up an item from the current room");
            return;
        }

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

                processDropItem(command);
                break;
            case "pickup":
                if (!command.hasThirdWord()) {
                    System.out.println("Pick up what item?");
                    currentRoom.displayRoomInventorySelection();
                    break;
                }

                processPickupItem(command);
                break;
            default:
                System.out.println("Available inventory commands: " + "\n" +
                        "inventory display - Display current inventory" + "\n" +
                        "inventory drop [item] [quantity] - Drop an item from your inventory" + "\n" +
                        "inventory pickup [item] [quantity] - Pick up an item from the current room");
                break;
        }
    }

    /**
     * Removes the item from the player's inventory and adds it to the room's inventory.
     * If the item is not found or the specified quantity is invalid, an appropriate message is displayed.
     * <p>
     * @param command The drop command to be processed.
     */
    private void processDropItem(Command command) {
        String itemName = command.getThirdWord(); // Get the item name from the command
        int quantity;

        // Check if the item exists in the item map
        if (!itemMap.containsKey(itemName)) {
            System.out.println("Item not found");
            currentRoom.displayRoomInventory();
            return;
        }

        Item itemToBeDropped = itemMap.get(itemName); // Retrieve the item object from the item map

        try {
            // Determine the quantity to drop, either from the command or default to the total quantity in inventory
            quantity = command.hasFourthWord() ? Integer.parseInt(command.getFourthWord()) : inventory.numberOfItem(itemToBeDropped);
        } catch (NumberFormatException e) {
            System.out.println("Please specify a valid quantity.");
            return;
        }

        // Check if the player has enough of the item to drop the specified quantity
        if (inventory.numberOfItem(itemToBeDropped) < quantity) {
            System.out.println("You do not have enough " + itemName.replace("_", " ") + "s to drop");
            return;
        }

        // Remove the item from the player's inventory and add it to the room's inventory
        inventory.dropItem(currentRoom, itemToBeDropped, quantity);
    }

    /**
     * Processes the picking up of an item with a specified quantity.
     * <p>
     * Removes the item from the room's inventory and adds it to the player's inventory.
     * If the item is not found or the specified quantity is invalid, an appropriate message is displayed.
     * <p>
     * @param command The pickup command to be processed.
     */
    private void processPickupItem(Command command) {
        String itemName = command.getThirdWord(); // Get the item name from the command
        int quantity;

        // Check if the item exists in the item map
        if (!itemMap.containsKey(itemName)) {
            System.out.println("Item not found");
            currentRoom.displayRoomInventorySelection();
            return;
        }

        Item itemToBePickedUp = itemMap.get(itemName); // Retrieve the item object from the item map

        try {
            // Determine the quantity to pick up, either from the command or default to the total quantity in the room
            quantity = command.hasFourthWord() ? Integer.parseInt(command.getFourthWord())
                    : currentRoom.numberOfItemInRoomInventory(itemToBePickedUp);
        } catch (NumberFormatException e) {
            System.out.println("Please specify a valid quantity.");
            return;
        }

        // Check if there is enough of the item in the room
        if (currentRoom.numberOfItemInRoomInventory(itemToBePickedUp) < quantity) {
            System.out.println("Insufficient quantity in the room.");
            return;
        }

        // Remove the item from the room inventory and add it to the player inventory
        inventory.pickupItem(currentRoom, itemToBePickedUp, quantity);
    }

    /**
     * Processes the back command, allowing the player to return to the previous room they just came from.
     * If the player hasn't moved yet, an error message is displayed.
     * The last move direction is popped from the backCommandStack and used to return to the previous room.
     */
    private void processBackCommand() {
        if (backCommandStack.isEmpty()) {
            System.out.println("There are no rooms for you to go back to.");
            return;
        }

        goRoom(backCommandStack.pop());
    }

    /**
     * This method handles the command to interact with a specified character in the current room.
     * If no second word is provided in the command, it prompts the player to specify the character.
     * If the character is not found, it prompts the player again to specify the character.
     * <p>
     * @param command The interact command to be processed.
     */
    private void processInteractCommand(Command command) {
        if (!command.hasSecondWord()) {
            // Prompt the player to state the character to interact with if not provided
            System.out.println("State the character you want to interact with: ");
            System.out.println("interact [character_name]");
            currentRoom.displayCharacterSelection();
            return;
        }

        String characterSelected = command.getSecondWord(); // Get the character name from the command

        // Check if the character exists in the character map
        if (!characterMap.containsKey(characterSelected)) {
            // Prompt the player again to state the character if not found
            System.out.println("State the character you want to interact with: ");
            currentRoom.displayCharacterSelection();
            return;
        }

        // Interact with the specified character
        characterMap.get(characterSelected).interact();
    }

    /**
     * This method handles various commands related to the current room. It provides options to display information about the current room.
     * If no second word is provided in the command, it displays the available room commands.
     *
     * @param command The room command to be processed.
     */
    private void processRoomCommand(Command command) {
        if (!command.hasSecondWord()) {
            // Display available room commands if no second word is provided
            System.out.println("Available room commands: " + "\n" +
                    "room info - Display information about the current room");
            return;
        }

        switch (command.getSecondWord()) {
            case "info":
                // Display details of the current room
                currentRoom.displayRoomDetails();
                break;
            default:
                // Display available room commands if the command is not recognised
                System.out.println("Available room commands: " + "\n" +
                        "room info - Display information about the current room");
        }
    }


    /**
     * Processes the use command for an item.
     * <p>
     * This method handles the command to use a specified item from the inventory. If no second word is provided,
     * it prompts the player to specify the item. If the item is not found or the player does not have the item,
     * an appropriate message is displayed.
     * <p>
     * @param command The use command to be processed.
     */
    private void processUseCommand(Command command) {
        if (!command.hasSecondWord()) {
            // Prompt the player to state the item to use if not provided
            System.out.println("Use what item?");
            inventory.displayInventorySelection();
            return;
        }

        String itemName = command.getSecondWord(); // Get the item name from the command

        // Check if the item exists in the item map
        if (!itemMap.containsKey(itemName)) {
            System.out.println("Item not found");
            inventory.displayInventorySelection();
            return;
        }

        // Check if the player has the item in their inventory
        if (inventory.numberOfItem(itemMap.get(itemName)) == 0) {
            System.out.println("You cannot use an item that you do not have.");
            return;
        }

        String characterSelected;
        Character character;

        switch (itemName) {
            case "coin":
                System.out.println("You can only trade coins not use them.");
                break;

            case "ancient_book":
                // Display cryptic messages related to the ancient book
                System.out.println(
                        "In shadows deep where secrets lie, A path to freedom draws you night. " + "\n" +
                                "Five coins to feline, sleek and sly, Unlock the truth, let whispers fly."
                );
                Utils.waitSeconds(3);
                System.out.println(
                        "A dagger’s gleam, though jewels may blaze, A futile weapon in ghostly haze. " + "\n" +
                                "Seek the feline's riddles, full of grace, To start anew in hall’s embrace."
                );
                Utils.waitSeconds(3);
                System.out.println(
                        "The bread of sacred light will show, The door to realms where you must go. " + "\n" +
                                "Follow clues, and wisdom gleam, To wake the dawn from twilight’s dream."
                );
                break;

            case "jewelled_dagger":
                // Prompt the player to state what to use the item on
                if (!command.hasThirdWord()) {
                    System.out.println("Use on what?");
                    currentRoom.displayCharacterSelection();
                    return;
                }

                characterSelected = command.getThirdWord(); // Get the character name to use the dagger on

                // Check if the character exists in the character map
                if (!characterMap.containsKey(characterSelected)) {
                    System.out.println("State the character you want to attack: ");
                    currentRoom.displayCharacterSelection();
                    return;
                }

                character = characterMap.get(characterSelected);

                // Check if the character is in the current room
                if (!currentRoom.getCharacters().contains(character)) {
                    System.out.println("You can't attack a character in a different room");
                    return;
                }

                // Check if the character is passive
                if (character.getPassive()) {
                    System.out.println("Now why would you want to attack a passive character...");
                    return;
                }

                // Special case for the Ghost of the Former Owner
                if (character.getName().equals("Ghost of the Former Owner")) {
                    System.out.println("As you brandish the jewelled dagger, the ghost of the former owner gazes at you with a mix of pity and amusement.");
                    Utils.waitSeconds(3);  // Pause for 3 seconds
                    System.out.println("'Mortal weaponry holds no power over the ethereal,' the ghost whispers, the dagger's jewels flickering dimly.");
                    Utils.waitSeconds(3);  // Pause for 3 seconds
                    System.out.println("'Seek a different path to banish me from this realm.'");
                    return;
                }
                break;

            case "magic_mirror":
                // Use the magic mirror to transport to a random room
                Random random = new Random();
                int index = random.nextInt(allUnlockedRooms.size());
                System.out.println(
                        "You gaze into the magic mirror, its surface shimmering with hidden truths." + "\n" +
                        "As your reflection wavers, a sudden flash of light surrounds you, and you feel a gentle pull. " + "\n" +
                        "In an instant, you are transported to a random room, its unfamiliar surroundings both exciting and mysterious."
                );
                //Clear the backCommandStack to prevent errors
                backCommandStack.clear();
                System.out.println("The magic of the mirror wipes away your recent steps, leaving only the path ahead to explore.");
                Utils.waitSeconds(3);
                goRoom(allUnlockedRooms.get(index));
                break;

            case "holy_bread":
                // Use the holy bread for a special effect
                System.out.println("As you consume the holy bread, a warmth spreads through your body.");
                Utils.waitSeconds(2);
                System.out.println("A radiant light fills the room, and you feel a deep sense of peace and fulfilment.");
                Utils.waitSeconds(2);
                System.out.println("Suddenly, you find yourself outside the manor, safe and free.");
                Utils.waitSeconds(2);
                System.out.println("Your quest is complete, brave traveller. The world is saved.");
                Utils.waitSeconds(2);
                System.out.println("Thank you for playing. Until next time, adventurer.");
                System.exit(0);
                break;

            case "pantry_key", "chambers_key":
                System.out.println("You do not need to use the key.");
                System.out.println("Use the `go` command with the key in your inventory to unlock the door.");
                break;

            case "vacuum":
                // Prompt the player to state what to use the item on
                if (!command.hasThirdWord()) {
                    System.out.println("Use on what?");
                    currentRoom.displayCharacterSelection();
                    return;
                }

                characterSelected = command.getThirdWord(); // Get the character name to use the vacuum on

                // Check if the character exists in the character map
                if (!characterMap.containsKey(characterSelected)) {
                    System.out.println("State the character you want to clean: ");
                    currentRoom.displayCharacterSelection();
                    return;
                }

                character = characterMap.get(characterSelected);

                // Check if the character is in the current room
                if (!currentRoom.getCharacters().contains(character)) {
                    System.out.println("You can't clean a character in a different room");
                    return;
                }

                // Special case for passive characters
                if (character.getPassive()) {
                    currentRoom.removeCharacter(character);
                    System.out.printf(
                            "A wave of sorrow washes over the room as you realise that the %s is gone forever.\n",
                            character.getName());
                    System.out.println("They wouldn't have tried to hurt you. Why would you do such a thing?");
                    return;
                }

                // Special case for the Ghost of the Former Owner
                if (character.getName().equals("Ghost of the Former Owner")) {
                    System.out.println("So, you have discovered my weakness...");
                    Utils.waitSeconds(2);
                    System.out.println("I feel the strength draining from me...");
                    Utils.waitSeconds(2);
                    System.out.println("You have defeated me, your victory is assured.");
                    Utils.waitSeconds(2);
                    System.out.println("The path ahead is now clear. Farewell...");
                    System.out.println("With a final, sorrowful glance, the ghost drops the chamber's key, fading away into the ether.");
                    currentRoom.removeCharacter(character);
                    return;
                }
                break;

            default:
                System.out.println("Item not found");
                inventory.displayInventorySelection();
                break;
        }
    }

    /**
     * Processes the answer command for the cat's riddle.
     * <p>
     * This method handles the command to answer the cat's riddle. If the player has not interacted with the cat,
     * or if the player is not in the same room as the cat, or if the player does not have enough coins,
     * an appropriate message is displayed. If the answer is correct, the player is rewarded.
     * <p>
     * @param command The answer command to be processed.
     */
    private void processAnswerCommand(Command command) {
        // Check if the player has interacted with the cat
        if (!cat.getInteractedWith()) {
            System.out.println("You must first uncover the riddle before attempting to answer.");
            return;
        }

        String catRoomName = cat.getCurrentRoom().getName(); // Get the name of the room where the cat is located

        // Check if the player is in the same room as the cat
        if (!currentRoom.getName().equals(catRoomName)) {
            System.out.println("The cat's riddle remains unsolved without its presence.");
            return;
        }

        // Check if the player has at least five coins
        if (inventory.numberOfItem(coin) < 5) {
            System.out.println("The path remains closed until you possess at least five coins.");
            return;
        }

        // Check if the command has a second word (the answer to the riddle)
        if (!command.hasSecondWord()) {
            System.out.println("The riddle stands unanswered. Provide your response to proceed.");
            return;
        }

        String answer = command.getSecondWord(); // Get the player's answer

        // Check if the answer is correct
        if (!answer.equalsIgnoreCase("vacuum") && !answer.equalsIgnoreCase("hoover")) {
            System.out.println("The riddle remains unsolved. Try again.");
            return;
        }

        // Special message if the answer is "hoover"
        if (answer.equalsIgnoreCase("hoover")) {
            System.out.println("Ah, you're quite the clever one! The true answer is 'vacuum' but I'll let 'hoover' slide.");
        }

        // Correct answer response
        System.out.println("Purrfect! You've cracked the riddle. " + "\n" +
                "As promised, I shall give you the key to your escape. Use it wisely, traveller.");


        // Checks if adding the vacuum to the player's inventory would exceed the maximum weight limit.
        // If it does, a message prompts the player to free up space in the inventory and try again.
        if (inventory.getWeight() + vacuum.getWeight() > inventory.getMaxWeight()) {
            // Inform the player that their inventory is too heavy to claim the reward
            System.out.println("Your burden is too great to claim the reward. Free up some space in your inventory, then try again.");
            return;
        }


        inventory.removeItem(coin, 5); // Remove five coins from the player's inventory
        givePlayerItem(cat, vacuum, 1); // Give the vacuum item to the player
    }

    /**
     * Processes the map command.
     * <p>
     * This method handles the command to display the map. If the map is disabled, it informs the player.
     * Originally, the player had to explore all rooms to access the map, but now they have the option to enable the map.
     */
    private void processMapCommand() {
        if(!mapEnabled) {
            System.out.println("Map is disabled.");
            return;
        }
        /*
        if(visitedRooms.size() != 9) {
            System.out.println("To unlock the secrets of the map, you must first journey through every chamber.");
            return;
        }
        */
        displayMap();
    }

    /**
     * Transfers an item from a character to the player's inventory.
     * <p>
     * This method checks if the character has enough of the specified item to give to the player.
     * If the character has fewer items than the specified quantity, an appropriate message is displayed.
     * The item is then removed from the character's inventory and added to the player's inventory.
     * <p>
     * @param character The character giving the item.
     * @param item The item to be given to the player.
     * @param quantity The quantity of the item to be transferred.
     */
    public void givePlayerItem(Character character, Item item, Integer quantity) {
        // Check if the character has more items than the specified quantity
        if (character.numberOfItemInCharacterInventory(item) < quantity) {
            System.out.println("Character cannot give more items than they have");
            return;
        }

        // Check if the item exists in the item map
        if (!itemMap.containsKey(item.getName())) {
            System.out.println("Item not found");
            return;
        }

        // Checks if adding the item(s) to the player's inventory would exceed the maximum weight limit.
        // If it does, the item is not added to the players inventory and a message is displayed
        if (inventory.getWeight() + (item.getWeight() * quantity) > inventory.getMaxWeight()) {
            System.out.println("The player does not have enough inventory space to receive this item");
            return;
        }

        // Remove the item from the character's inventory
        character.removeItemFromCharacterInventory(item, quantity);

        // Add the item to the player's inventory
        inventory.addItem(item, quantity);
    }

    /**
     * Initialises the items in the game.
     */
    private void initialiseItems() {
        ancientBook = new Item("ancient book", 20);
        jewelledDagger = new Item("jewelled dagger", 15);
        magicMirror = new Item("magic mirror", 5);
        coin = new Item("coin", 1);
        holyBread = new Item("holy bread", 10);
        vacuum = new Item("vacuum", 10);

        pantryKey = new Item("pantry key", 5);
        chambersKey = new Item("chambers key", 5);
    }

    /**
     * Initialises the characters in the game.
     */
    private void initialiseCharacters() {
        butler = new Character("Butler", true, 100, entranceHall);
        maid = new Character("Maid", true, 80, kitchen);
        ghost = new Character("Ghost of the Former Owner", false, 150, masterBedroom);
        cat = new Character("Cat", true, 60, library);
        securityGuard = new Character("Security Guard", true, 120, entranceHall);
    }

    /**
     * Initialises the inventory
     */
    private void initialiseInventory() {
        inventory = new Inventory(50);
    }

    /**
     * Initialises the lockedDoors objects in the game.
     * <p>
     * This method creates lockedDoors within the game. Each lockedDoor takes a room and a direction to lock.
     * The room name and direction is concatenated to store the position of the locked door.
     * When moving rooms, the game checks to see if the current room the player is in,
     * and the direction they're trying to move in is locked. They m
     */
    private void initialiseLockedDoors() {
        // Convert room and direction to a string format used for identifying locked doors
        String kitchenEast = Utils.roomDirToSnake(kitchen, "east");
        String bedroomSouth = Utils.roomDirToSnake(masterBedroom, "south");

        // Create locked doors with corresponding keys
        kitchenPantry = new lockedDoor(kitchen, "east", pantryKey);
        bedroomChamber = new lockedDoor(masterBedroom, "south", chambersKey);

        // Initialise the list of locked directions
        lockedDirections = new ArrayList<>();
        lockedDirections.add(kitchenEast);
        lockedDirections.add(bedroomSouth);
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

    /**
     * Adds specific items to the inventories of characters in the game.
     */
    private void addItemsToCharacters() {
        ghost.addItemToCharacterInventory(chambersKey, 1);
        cat.addItemToCharacterInventory(vacuum, 1);
    }

    /**
     * Adds items around the map
     */
    private void addItemsToRooms() {
        // Add coins to various rooms
        entranceHall.addItemToRoomInventory(coin, 1);
        library.addItemToRoomInventory(coin, 1);
        diningRoom.addItemToRoomInventory(coin, 1);
        kitchen.addItemToRoomInventory(coin, 1);
        study.addItemToRoomInventory(coin, 1);

        // Add holy bread to the pantry
        pantry.addItemToRoomInventory(holyBread, 1);

        // Add ancient book to the library
        library.addItemToRoomInventory(ancientBook, 1);

        // Add jewelled dagger to greenhouse
        greenhouse.addItemToRoomInventory(jewelledDagger, 1);

        // Add magic mirror to the study
        study.addItemToRoomInventory(magicMirror, 1);

        //Add pantry key to hidden chambers
        hiddenChamber.addItemToRoomInventory(pantryKey, 1);
    }

    /**
     * Handles random movement of characters within the game.
     * <p>
     * This method iterates over all characters in the character map and triggers random room movement for each character.
     */
    private void randomCharacterMovement() {
        // Iterate over all characters in the character map
        for (Character character : characterMap.values()) {
            // Trigger random room movement for the character
            character.randomRoomMovement();
        }
    }

    /**
     * Displays the map of the game world.
     * <p>
     * This method prints a visual representation of the game's map to the console.
     * The map consists of various rooms connected by directions, representing the layout of the game world.
     */
    private void displayMap() {
        String[] mapLines = {
              "================       =================       ================",
              "||            ||_______||             ||       ||            ||",
              "||   Master   ||_______||    Study    ||       || Greenhouse ||",
              "||  Bedroom   ||       ||             ||       ||            ||",
              "================       =================       ================",
              "       ||                      ||                     ||",
              "       ||                      ||                     ||",
              "       ||                      ||                     ||",
              "       ||                      ||                     ||",
              "=======||=======       ========||=======       =======||=======        ================",
              "||            ||       ||             ||       ||            ||________||            ||",
              "||   Hidden   ||       ||   Library   ||       ||  Kitchen   ||________||   Pantry   ||",
              "||  Chamber   ||       ||             ||       ||            ||        ||            ||",
              "================       =================       ================        ================",
              "                               ||                     ||",
              "                               ||                     ||",
              "                               ||                     ||",
              "                               ||                     ||",
              "                       =================       ================",
              "                       ||             ||_______||            ||",
              "                       ||  Entrance   ||_______||   Dining   ||",
              "                       ||    Hall     ||       ||    Room    ||",
              "                       =================       ================"
        };

        // Print each line of the map to the console
        for (String line : mapLines) {
            System.out.println(line);
        }
    }

    /**
     * Configures the game settings at game start and user input
     * <p>
     * This method prompts the player to enable or disable the map and random character movement.
     * It also allows the player to set the difficulty level for random character movement.
     * <p>
     * The settings are then applied to the game.
     */
    private void setGameValues() {
        // Prompt the player to enable or disable the map
        System.out.println("Would you like to enable the map? y/n");
        mapEnabled = parser.getYesOrNo();

        // Prompt the player to enable or disable random character movement
        System.out.println("Would you like to enable random character movement?");
        System.out.println("(Characters will randomly move around the map once interacted with)");
        randomCharacterMovement = parser.getYesOrNo();

        // If random character movement is disabled, set the movement values and save settings
        if (!randomCharacterMovement) {
            for (Character character : characterMap.values()) {
                character.setRandomMovementValues(false, 100);
            }
            System.out.println("Game settings saved.");
            return;
        }

        // Prompt the player to select the difficulty level for random character movement
        System.out.println("Which difficulty of random character movement?");
        System.out.println("Options [Mode/Chance]: easy (1/30) | medium (1/15) | hard (1/5)");
        Integer randomMovementChance = switch (parser.getDifficulty()) {
            case "easy" -> 30;
            case "medium" -> 15;
            case "hard" -> 5;
            default -> 100;
        };

        // Set the random movement values for each character based on the selected difficulty
        for (Character character : characterMap.values()) {
            character.setRandomMovementValues(true, randomMovementChance);
        }

        System.out.println("Game settings saved.\n");
    }
}
