import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Dialog class manages the dialog interactions for various characters in the game.
 * <p>
 * This class holds the dialogs for different characters and the current dialog number for each character.
 * It provides a method to get the dialog for a given character and cycle through the dialog options.
 */
public class Dialog {

    public static HashMap<String, ArrayList<String>> dialog = new HashMap<>(); // Stores the dialog for each character
    public static HashMap<String, Integer> dialogNumber = new HashMap<>(); // Stores the current dialog number for each character

    // Static block to initialise the dialogs for the characters
    static {
        // Initialising dialog for Butler
        ArrayList<String> butlerDialog = new ArrayList<>();
        butlerDialog.add("Welcome to the Mystic Manor. Please, make yourself at home.");
        butlerDialog.add("The pantry key? It's well hidden, but perhaps you should seek the hidden chamber.");
        butlerDialog.add("Be careful, the ghost of the former owner is not easily defeated.");
        dialog.put("Butler", butlerDialog);
        dialogNumber.put("Butler", 0);

        // Initialising dialog for Maid
        ArrayList<String> maidDialog = new ArrayList<>();
        maidDialog.add("Good day! The kitchen is just down the hall. I hope you find what you are looking for.");
        maidDialog.add("The pantry is locked, but I have heard whispers about a key hidden in the hidden chamber.");
        maidDialog.add("If you're planning to face the ghost, you'll need something special. Have you spoken to the cat in the library?");
        dialog.put("Maid", maidDialog);
        dialogNumber.put("Maid", 0);

        // Initialising dialog for Ghost of the Former Owner
        ArrayList<String> ghostDialog = new ArrayList<>();
        ghostDialog.add("You dare enter my domain? You shall not pass without a fight!");
        ghostDialog.add("To defeat me, you will need more than courage. Only a certain power can banish me.");
        ghostDialog.add("The hidden chamber holds many secrets. You will need to defeat me to access it.");
        dialog.put("Ghost of the Former Owner", ghostDialog);
        dialogNumber.put("Ghost of the Former Owner", 0);

        // Initialising dialog for Cat
        ArrayList<String> catDialog = new ArrayList<>();
        catDialog.add("Meow. Welcome to the library. I have a riddle for you to solve.");
        catDialog.add("Solve my riddle and fetch me 5 coins, and I will give you the key weapon to defeat the ghost.");
        catDialog.add("I can remove dust and crumbs with ease, though I don’t use hands, water, or a breeze. What am I?");
        dialog.put("Cat", catDialog);
        dialogNumber.put("Cat", 0);

        // Initialising dialog for Security Guard
        ArrayList<String> securityGuardDialog = new ArrayList<>();
        securityGuardDialog.add("Hello there. I'm here to ensure the manor's safety.");
        securityGuardDialog.add("The ghost in the master bedroom is a formidable opponent. Make sure you're prepared.");
        securityGuardDialog.add("The cat in the library may have some valuable information for you.");
        dialog.put("Security Guard", securityGuardDialog);
        dialogNumber.put("Security Guard", 0);
    }

    /**
     * Retrieves and prints the next dialog for a given character.
     * <p>
     * This method retrieves the current dialog for the specified character and increments the dialog number.
     * If the end of the dialog list is reached, it resets the dialog number to 0.
     *
     * @param character The name of the character whose dialog is to be retrieved.
     */
    public static void getDialog(String character) {
        ArrayList<String> characterDialog = dialog.get(character);
        Integer currentDialogNumber = dialogNumber.get(character);
        if (currentDialogNumber == 2) {
            dialogNumber.put(character, 0);
        } else {
            dialogNumber.put(character, currentDialogNumber + 1);
        }
        System.out.println(String.format("[%d/3] ", currentDialogNumber + 1) + characterDialog.get(currentDialogNumber));
    }
}
