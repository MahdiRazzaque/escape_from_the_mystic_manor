import java.lang.Character;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Collection of static functions which are used through the program
 *
 * @author Mahdi Razzaque
 * @version 24.11.24
 */
public class Utils {
     /**
     * Converts a given string to title case, where the first letter of each word is capitalised.
     * Words are separated by spaces.
     *
     * @param input The string to be converted to title case.
     * @return The input string converted to title case.
     */
    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) { // Checks to see if the input is empty or null. Returns the same input is it is
            return input;
        }
        String[] words = input.split(" "); // Splits the input into individual words and places them into an array
        StringBuilder titleCase = new StringBuilder();
        for (String word : words) { // For each word it capitalises the first letter, and converts the rest of the word to lowercase and then adds it to the return value
            if (!word.isEmpty()) {
                titleCase.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase()).append(" ");
            }
        }
        return titleCase.toString().trim();
    }

    /**
     * Concatenates the name of the room (in snake case and lowercase) and the direction string
     * @param room The room object to fetch the name of
     * @param direction The direction string
     * @return The concatenated string of the name of the room (in snake case and lowercase) and the direction string
     */
    public static String roomDirToSnake(Room room, String direction) {
        if (room == null || direction == null) {    //Returns null if either of the parameters are null
            return null;
        }

        return room.getName().replaceAll(" ", "_").toLowerCase() + direction;
    }


    /**
     * Takes a string and outputs it in a box design with equals signs.
     * @param input The string to be boxed.
     * @return A string representing the input in a box design with equals signs.
     */
    public static String boxString(String input) {
        int length = input.length();
        String horizontalBorder = "=".repeat(length + 4);
        String boxedString = "| " + input + " |";
    
        return horizontalBorder + "\n" + boxedString + "\n" + horizontalBorder;
    }

    /**
     * Checks if a specified item exists within a given array.
     *
     * This method converts the array to a list and then checks if the specified item
     * is contained within that list.
     *
     * @param array The array to search through.
     * @param item The item to look for in the array.
     * @return true if the item exists in the array; false otherwise.
     */
    public static boolean itemExistsInArray(String[] array, String item) {
        return Arrays.asList(array).contains(item);
    }


    /**
     * Removes all items with a quantity of zero from the inventory.
     *
     * This method iterates through the inventory and removes any entries
     * where the item's quantity is zero.
     *
     * @param inventory The inventory from which items with zero quantity will be removed.
     */
    public static void removeZeroQuantityItems(HashMap<Item, Integer> inventory) {
        inventory.entrySet().removeIf(entry -> entry.getValue() == 0);
    }


    /**
     * Creates a delay for a specified number of seconds.
     *
     * This method delays the program's execution for the given number of seconds.
     *
     * @param seconds The number of seconds to pause.
     */
    public static void waitSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            System.out.println("An error occurred!");
            e.printStackTrace();
        }
    }
}
