/**
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.  
 * <p>
 * This class holds information about a command that was issued by the user.
 * A command currently consists of two strings: a command word and a second
 * word (for example, if the command was "take map", then the two strings
 * obviously are "take" and "map").
 * <p>
 * The way this is used is: Commands are already checked for being valid
 * command words. If the user entered an invalid command (a word that is not
 * known) then the command word is <null>.
 * <p>
 * If the command had only one word, then the second word, third and fourth word is <null>.
 * 
 * @author  Michael Kölling, David J. Barnes, Mahdi Razzaque
 * @version 24.11.2024
 */

public class Command
{
    private String commandWord;
    private String secondWord;
    private String thirdWord;
    private String fourthWord;

    /**
     * Create a command object. First, second, and third words must be supplied, but
     * any of them can be null.
     * @param firstWord The first word of the command. Null if the command was not recognised.
     * @param secondWord The second word of the command.
     * @param thirdWord The third word of the command.
     * @param fourthWord The fourth word of the command, typically used for quantities.
     */
    public Command(String firstWord, String secondWord, String thirdWord, String fourthWord) {
        commandWord = firstWord;
        this.secondWord = secondWord;
        this.thirdWord = thirdWord;
        this.fourthWord = fourthWord;
    }

    /**
     * Return the command word (the first word) of this command. If the
     * command was not understood, the result is null.
     * @return The command word.
     */
    public String getCommandWord()
    {
        return commandWord;
    }

    /**
     * @return The second word of this command. Returns null if there was no
     * second word.
     */
    public String getSecondWord()
    {
        return secondWord;
    }
    
    /**
     * @return The third word of this command. Returns null if there was no
     * third word.
     */
    public String getThirdWord() {
        return thirdWord;
    }
    
    /**
     * @return The fourth word of this command. Returns null if there was no
     * fourth word.
     */
    public String getFourthWord() {
        return fourthWord;
    }

    /**
     * @return true if this command was not understood.
     */
    public boolean isUnknown()
    {
        return (commandWord == null);
    }

    /**
     * @return true if the command has a second word.
     */
    public boolean hasSecondWord()
    {
        return (secondWord != null);
    }
    
    /**
     * @return true if the command has a second word.
     */
    public boolean hasThirdWord(){
        return (thirdWord != null);
    }
    
    /**
     * @return true if the command has a fourth word.
     */
    public boolean hasFourthWord() {
        return (fourthWord != null);
    }

}

