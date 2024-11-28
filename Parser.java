import java.util.Scanner;

/**
 * This parser reads user input and tries to interpret it as an "Adventure"
 * command. Every time it is called it reads a line from the terminal and
 * tries to interpret the line as a two word command. It returns the command
 * as an object of class Command.
 * <p>
 * The parser has a set of known command words. It checks user input against
 * the known commands, and if the input is not one of the known commands, it
 * returns a command object that is marked as an unknown command.
 * 
 * @author  Michael Kölling, David J. Barnes, Mahdi Razzaque
 * @version 28.11.2024
 */
public class Parser {
    private CommandWords commands;  // holds all valid command words
    private Scanner reader;         // source of command input

    /**
     * Create a parser to read from the terminal window.
     */
    public Parser() {
        commands = new CommandWords();
        reader = new Scanner(System.in);
    }

    /**
     * @return The next command from the user.
     */
    public Command getCommand() {
        String inputLine;   // will hold the full input line
        String word1 = null;
        String word2 = null;
        String word3 = null;
        String word4 = null;

        System.out.print("> ");     // print prompt

        inputLine = reader.nextLine().toLowerCase().trim();

        // Find up to two words on the line.
        Scanner tokenizer = new Scanner(inputLine);
        if (tokenizer.hasNext()) {
            word1 = tokenizer.next();      // get first word
            if (tokenizer.hasNext()) {
                word2 = tokenizer.next();      // get second word
                if (tokenizer.hasNext()) {
                    word3 = tokenizer.next();      // get third word
                    if (tokenizer.hasNext()) {
                        word4 = tokenizer.next();  // get fourth word
                    }
                }
            }
        }

        // Now check whether this word is known. If so, create a command
        // with it. If not, create a "null" command (for unknown command).
        if(commands.isCommand(word1)) {
            return new Command(word1, word2, word3, word4);
        }
        else {
            return new Command(null, word2, word3, word4); 
        }
    }

    /**
     * Prompts the user for a 'yes' or 'no' input and returns true for yes and false for no.
     * <p>
     * This method continually prompts the user to enter a valid response until
     * 'yes' or 'no' (or variations) are provided. It returns true for 'yes' and false for 'no'.
     *
     * @return true if the user inputs 'yes' or 'y'; false if the user inputs 'no' or 'n'.
     */
    public boolean getYesOrNo() {
        String inputLine; // will hold the full input line

        while (true) {
            System.out.print("> "); // print prompt

            inputLine = reader.nextLine().trim().toLowerCase();

            if (inputLine.equals("yes") || inputLine.equals("y")) {
                return true;
            } else if (inputLine.equals("no") || inputLine.equals("n")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'yes' or 'no'.");
            }
        }
    }

    /**
     * Prompts the user to select a difficulty level and returns the corresponding string value.
     * <p>
     * This method repeatedly prompts the user to enter a valid difficulty level until
     * 'easy', 'medium', or 'hard' is provided. It returns the selected difficulty level as a string.
     *
     * @return The selected difficulty level: 'easy', 'medium', or 'hard'.
     */
    public String getDifficulty() {
        String inputLine; // will hold the full input line

        while (true) {
            System.out.print("> "); // print prompt

            inputLine = reader.nextLine().trim().toLowerCase(); // read user input, trim whitespace, and convert to lowercase

            switch (inputLine) {
                case "easy" -> { return "easy"; }
                case "medium" -> { return "medium"; }
                case "hard" -> { return "hard"; }
                default -> System.out.println("Invalid input. Please enter 'easy', 'medium', or 'hard'."); // prompt user for valid input
            }
        }
    }

    /**
     * Print out a list of valid command words.
     */
    public void showCommands() {
        commands.showAll();
    }
}
