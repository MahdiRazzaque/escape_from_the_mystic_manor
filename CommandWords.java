import java.util.HashMap;

/**
 * This class is part of the "Escape from the Mystic Manor" application.
 * "Escape from the Mystic Manor" is a very simple, text based adventure game.
 * <p>
 * This class holds an enumeration of all command words known to the game.
 * It is used to recognise commands as they are typed in.
 * <p>
 * @author  Michael Kölling, David J. Barnes, Mahdi Razzaque
 * @version 28.11.2024
 */

public class CommandWords {
    // a constant array that holds all valid command words
    private static final String[] validCommands = {
        "go", "back", "help", "inventory", "interact", "use", "answer", "room", "map", "configure", "quit"
    };
    private HashMap<String, String> commandDescriptions;

    /**
     * Constructor - initialise the command words.
     */
    public CommandWords() {
        commandDescriptions = new HashMap<>();
        commandDescriptions.put("go", "go [north/east/south/west] - Choose a room to move into");
        commandDescriptions.put("back", "back - Go back to the previous room");
        commandDescriptions.put("help", "help - Show this help message");
        commandDescriptions.put("inventory", "inventory [display/drop/pickup] - Inventory commands");
        commandDescriptions.put("interact", "interact [character_name] - Interact with a character");
        commandDescriptions.put("use", "use [item_name] - Use an item");
        commandDescriptions.put("answer", "answer [your answer] - Answer a character's riddle");
        commandDescriptions.put("room", "room [info] - Room commands");
        commandDescriptions.put("map", "map - Display the map");
        commandDescriptions.put("configure", "configure - Configure game settings");
        commandDescriptions.put("quit", "quit - Quit the game");
    }

    /**
     * Check whether a given String is a valid command word. 
     * @return true if it is, false if it isn't.
     */
    public boolean isCommand(String aString)
    {
        for (String validCommand : validCommands) {
            if (validCommand.equals(aString))
                return true;
        }
        // if we get here, the string was not found in the commands
        return false;
    }

    /**
     * Print all valid commands and their descriptions
     */
    public void showAll() {
        for (String command : commandDescriptions.keySet()) {
            System.out.println(commandDescriptions.get(command));
        }
    }
}
