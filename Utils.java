import java.lang.Character;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Write a description of class Utils here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Utils {
    /**
     * Constructor for objects of class Utils
     */
    public Utils() {
    }

    /**
     * Converts a given string to title case, where the first letter of each word is capitalized.
     * Words are separated by spaces.
     * 
     * @param input The string to be converted to title case.
     * @return The input string converted to title case.
     */
    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String[] words = input.split(" ");
        StringBuilder titleCase = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                titleCase.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase()).append(" ");
            }
        }
        return titleCase.toString().trim();
    }

    public static String roomDirToSnake(Room room, String direction) {
        if (room == null || direction == null) {
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
     * 
     */
    public static boolean itemExistsInArray(String[] array, String item) { 
        return Arrays.asList(array).contains(item); 
    }

    public static void removeZeroQuantityItems(HashMap<Item, Integer> inventory) {
        inventory.entrySet().removeIf(entry -> entry.getValue() == 0);
    }

    public static void waitSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            System.out.println("An error occurred!");
            e.printStackTrace();
        }
    }
}
