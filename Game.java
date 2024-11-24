import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

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
 * @author  Michael Kölling, David J. Barnes and Mahdi Razzaque
 * @version 24.11.2024
 */

public class Game {
    private Parser parser;
    private Room currentRoom;
    private Room entranceHall, library, diningRoom, kitchen, pantry, greenhouse, study, masterBedroom, hiddenChamber;
    private HashSet<Room> vistedRooms;
    private ArrayList<Room> allRooms;
    private lockedDoor kitchenPantry, bedroomChamber;
    private ArrayList<String> lockedDirections;
    private Inventory inventory;
    private Item ancientBook, jewelledDagger, magicMirror, coin, holyBread, vacuum;
    private Item pantryKey, chambersKey;
    private Character butler, maid, ghost, cat, securityGuard;
    private static HashMap<String, lockedDoor> lockedDoorsMap;
    public static HashMap<String, Item> itemMap;
    public static HashMap<String, Character> characterMap;
    private HashMap<String, String> oppositeDirections;
    private ArrayList<String> backCommandStack = new ArrayList<>();
    private boolean mapEnabled, randomCharacterMovement;

    /**
     * Create the game and initialise its internal map.
     */
    public Game() {
        createRooms();
        parser = new Parser();
        initialiseInventory();

        initialiseItems();
        addItemsToRooms();
        initialiseItemMap();

        initialiseCharacters();
        addCharactersToRooms();
        addItemsToCharacters();
        initialiseCharacterMap();

        initialiseLockedDoors();
        initialiseLockedDoorsMap();

        initialiseOppositeDirections();

        inventory.addItem(magicMirror, 1);
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

        // ArrayList of all rooms
        allRooms = new ArrayList<>();
        allRooms.add(entranceHall);
        allRooms.add(library);
        allRooms.add(diningRoom);
        allRooms.add(kitchen);
        allRooms.add(pantry);
        allRooms.add(greenhouse);
        allRooms.add(study);
        allRooms.add(masterBedroom);
        allRooms.add(hiddenChamber);



        // Track visted rooms
        vistedRooms = new HashSet<>();
        vistedRooms.add(currentRoom);
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

        // Triggers characters to randomly move to adjacent rooms with a chance chosen by the player
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
            return;
        }

        if(lockedDirections.contains(Utils.roomDirToSnake(currentRoom, direction))) {
            lockedDoor lockedDoor = lockedDoorsMap.get(Utils.roomDirToSnake(currentRoom, direction));

            if(inventory.numberOfItem(lockedDoor.getKey()) == 0) {
                System.out.println("Door is locked!");
                System.out.printf("To enter this room you must find the %s.\n", lockedDoor.getKey().getName());
                return;
            }
        }

        backCommandStack.add(oppositeDirections.get(direction));
        System.out.println(backCommandStack);

        currentRoom = nextRoom;
        currentRoom.displayRoomDetails();
        vistedRooms.add(currentRoom);

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
     */
    private void processInventoryCommand(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Available inventory commands: " + "\n" +
                                "inventory display - Display current inventory" + "\n" +
                                "inventory drop [item] [quantity] - Drop an item from your inventory" + "\n" +
                                "inventory pickup [item] [quantity] - Pickup an item from the current room");
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
                    System.out.println("Pickup what item?");
                    currentRoom.displayRoomInventorySelection();
                    break;
                }


                processPickupItem(command);
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
     * Processes the dropping of an item with a specified quantity
     * Removes the item from the player's inventory and adds it to the room's inventory.
     * @param command drop command to be processed
     */
    private void processDropItem(Command command) {
        String itemName = command.getThirdWord();
        int quantity;
        if (!itemMap.containsKey(itemName)) {
            System.out.println("Item not found");
            currentRoom.displayRoomInventory();
            return;
        }

        Item itemToBeDropped = itemMap.get(itemName);

        try {
            quantity = command.hasFourthWord() ? Integer.parseInt(command.getFourthWord()) : inventory.numberOfItem(itemToBeDropped);
        } catch (NumberFormatException e) {
            System.out.println("Please specify a valid quantity.");
            return;
        }

        if (inventory.numberOfItem(itemToBeDropped) < quantity) {
            System.out.println("You do not have enough " + itemName.replace("_", " ") + "s to drop");
            return;
        }
        inventory.dropItem(currentRoom, itemToBeDropped, quantity);
    }

    /**
     *
     */
    private void processPickupItem(Command command) {
        String itemName = command.getThirdWord();
        int quantity;
        if (!itemMap.containsKey(itemName)) {
            System.out.println("Item not found");
            currentRoom.displayRoomInventorySelection();
            return;
        }

        Item itemToBePickedUp = itemMap.get(itemName);

        try {
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

        // Perform the pickup
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
     * @param command
     */
    private void processUseCommand(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Use what item?");
            inventory.displayInventorySelection();
            return;
        }

        String itemName = command.getSecondWord();

        if (!itemMap.containsKey(itemName)) {
            System.out.println("Item not found");
            inventory.displayInventorySelection();
            return;
        }

        if(inventory.numberOfItem(itemMap.get(itemName)) == 0) {
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
                if (!command.hasThirdWord())
                {
                    System.out.println("Use on what?");
                    currentRoom.displayCharacterSelection();
                    return;
                }

                characterSelected = command.getThirdWord();

                if (!characterMap.containsKey(characterSelected)) {
                    System.out.println("State the character you want to attack: ");
                    currentRoom.displayCharacterSelection();
                    return;
                }

                character = characterMap.get(characterSelected);

                if(!currentRoom.getCharacters().contains(character)) {
                    System.out.println("You can't clean a character in a different room ");
                    return;
                }

                if (character.getPassive()) {
                    System.out.println("Now why would you want to attack a passive character...");
                    return;
                }

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
                Random random = new Random();
                int index = random.nextInt(allRooms.size());
                System.out.println("You gaze into the magic mirror, its surface shimmering with hidden truths.\n As your reflection wavers, a sudden flash of light surrounds you, and you feel a gentle pull. In an instant, you are transported to a random room, its unfamiliar surroundings both exciting and mysterious.");
                Utils.waitSeconds(3);
                goRoom(allRooms.get(index));
                break;

            case "holy_bread":
                System.out.println("As you consume the holy bread, a warmth spreads through your body.");
                Utils.waitSeconds(2);
                System.out.println("A radiant light fills the room, and you feel a deep sense of peace and fulfillment.");
                Utils.waitSeconds(2);
                System.out.println("Suddenly, you find yourself outside the manor, safe and free.");
                Utils.waitSeconds(2);
                System.out.println("Your quest is complete, brave traveler. The world is saved.");
                Utils.waitSeconds(2);
                System.out.println("Thank you for playing. Until next time, adventurer.");
                System.exit(0);
                break;

            case "pantry_key", "chambers_key":
                System.out.println("You do not need to use the key.");
                System.out.println("Use the `go` command with the key in your inventory to unlock the door.");
            break;

            case "vacuum":
                if (!command.hasThirdWord()) {
                    System.out.println("Use on what?");
                    currentRoom.displayCharacterSelection();
                    return;
                }

                characterSelected = command.getThirdWord();

                if (!characterMap.containsKey(characterSelected)) {
                    System.out.println("State the character you want to clean: ");
                    currentRoom.displayCharacterSelection();
                    return;
                }

                character = characterMap.get(characterSelected);

                if(!currentRoom.getCharacters().contains(character)) {
                    System.out.println("You can't attack a character in a different room ");
                    return;
                }

                if(character.getPassive()) {
                    currentRoom.removeCharacter(character);
                    System.out.printf(
                            "A wave of sorrow washes over the room as you realise that the %s is gone forever.\n",
                            character.getName());
                    System.out.println("They wouldn't have tried to hurt you. Why would you do such a thing?");
                }

                if(character.getName().equals("Ghost of the Former Owner")) {
                    System.out.println("So, you have discovered my weakness...");
                    Utils.waitSeconds(2);
                    System.out.println("I feel the strength draining from me...");
                    Utils.waitSeconds(2);
                    System.out.println("You have defeated me, your victory is assured.");
                    Utils.waitSeconds(2);
                    System.out.println("The path ahead is now clear. Farewell...");
                    System.out.println("With a final, sorrowful glance, the ghost drops the chamber's key, fading away into the ether.");
                    currentRoom.removeCharacter(character);
                }
            break;

            default:
                System.out.println("Item not found");
                inventory.displayInventorySelection();
                break;
        }
    }

    private void processAnswerCommand(Command command) {
        if(!cat.getInteractedWith()) {
            System.out.println("You must first uncover the riddle before attempting to answer.");
            return;
        }

        String catRoomName = cat.getCurrentRoom().getName();

        if(!currentRoom.getName().equals(catRoomName)) {
            System.out.println("The cat's riddle remains unsolved without its presence.");
            return;
        }

        if(inventory.numberOfItem(coin) < 5) {
            System.out.println("The path remains closed until you possess at least five coins.");
            return;
        }

        if(!command.hasSecondWord()) {
            System.out.println("The riddle stands unanswered. Provide your response to proceed.");
            return;
        }

        String answer = command.getSecondWord();

        if(!answer.equalsIgnoreCase("vacuum") && !answer.equalsIgnoreCase("hoover")) {
            System.out.println("The riddle remains unsolved. Try again.");
            return;
        }

        if(answer.equalsIgnoreCase("hoover")) {
            System.out.println("Ah, you're quite the clever one! The true answer is 'vacuum' but I'll let 'hoover' slide.");
        }

        System.out.println("Purrfect! You've cracked the riddle. " + "\n" +
                "As promised, I shall give you the key to your escape. Use it wisely, traveler.");
        inventory.removeItem(coin, 5);
        givePlayerItem(cat, vacuum, 1);
    }

    private void processMapCommand() {
        if(!mapEnabled) {
            System.out.println("Map is disabled.");
            return;
        }
//        if(vistedRooms.size() != 9) {
//            System.out.println("To unlock the secrets of the map, you must first journey through every chamber.");
//            return;
//        }
        displayMap();
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
            return;
        }

        character.removeItemFromCharacterInventory(item, quantity);
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
     * An example of a method - replace this comment with your own
     *
     * @param
     */
    private void initialiseInventory() {
        inventory = new Inventory(50);
    }


    private void initialiseLockedDoors() {
        String kitchenEast = Utils.roomDirToSnake(kitchen, "east");
        String bedroomSouth = Utils.roomDirToSnake(masterBedroom, "south");

        kitchenPantry = new lockedDoor(kitchenEast, pantryKey);
        bedroomChamber = new lockedDoor(bedroomSouth, chambersKey);

        lockedDirections = new ArrayList<>();
        lockedDirections.add(kitchenEast);
        lockedDirections.add(bedroomSouth);
    }

    private void initialiseLockedDoorsMap() {
        lockedDoorsMap = new HashMap<>();
        lockedDoorsMap.put(Utils.roomDirToSnake(kitchen, "east"), kitchenPantry);
        lockedDoorsMap.put(Utils.roomDirToSnake(masterBedroom, "south"), bedroomChamber);
    }

    /**
     * Initialises the item map with all the possible items in the game.
     */
    private void initialiseItemMap() {
        itemMap = new HashMap<>();
        itemMap.put("coin", coin);
        itemMap.put("ancient_book", ancientBook);
        itemMap.put("jewelled_dagger", jewelledDagger);
        itemMap.put("magic_mirror", magicMirror);
        itemMap.put("holy_bread", holyBread);
        itemMap.put("pantry_key", pantryKey);
        itemMap.put("chambers_key", chambersKey);
        itemMap.put("vacuum", vacuum);
    }

    /**
     * Initialises the character map with all the possible characters in the game.
     */
    private void initialiseCharacterMap() {
        characterMap = new HashMap<>();
        characterMap.put("butler", butler);
        characterMap.put("maid", maid);
        characterMap.put("ghost_of_the_former_owner", ghost);
        characterMap.put("ghost", ghost);
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

    private void addItemsToCharacters() {
        ghost.addItemToCharacterInventory(chambersKey, 1);
        cat.addItemToCharacterInventory(vacuum, 1);
    }

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

    private void randomCharacterMovement() {
        for(Character character : characterMap.values()) {
            character.randomRoomMovement();
        }
    }

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

        for (String line : mapLines) {
            System.out.println(line);
        }
    }

    private void setGameValues() {
        System.out.println("Would you like to enable the map? y/n");
        mapEnabled = parser.getYesOrNo();

        System.out.println("Would you like to enable random character movement?");
        System.out.println("(Characters will randomly move around the map once interacted with)");
        randomCharacterMovement = parser.getYesOrNo();

        if(!randomCharacterMovement) {
            for(Character character : characterMap.values()) {
                character.setRandomMovementValues(false, 100);
            }
            System.out.println("Game settings saved.");
            return;
        }

        System.out.println("Which difficulty of random character movement?");
        System.out.println("Options: easy (1/30) | medium (1/15) | hard (1/5)");
        Integer randomMovementChance = switch (parser.getDifficulty()) {
            case "easy" -> 30;
            case "medium" -> 15;
            case "hard" -> 5;
            default -> 100;
        };

        for(Character character : characterMap.values()) {
            character.setRandomMovementValues(true, randomMovementChance);
        }

        System.out.println("Game settings saved.\n");
    }
}
