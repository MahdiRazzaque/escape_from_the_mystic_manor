/**
 * This class is used to create item objects.
 * Items are objects the characters can find and use throughout the game.
 * Each object will have a name and a weight.
 *
 * @author Mahdi Razzaque
 * @version 21.11.24
 */
public class Item  {
    private String itemName; // Holds the name of the item
    private Integer weight;  // Holds the weight of the given item.

    /**
     * Constructor for item objects.
     * <p>
     * Initialises an item with a specified name and weight.
     *
     * @param name Name of the item.
     * @param weight Weight of the item.
     */
    public Item(String name, Integer weight) {
        itemName = name;
        this.weight = weight;
        Game.itemMap.put(Utils.toSnakeCase(name), this);
    }

    /**
     * Returns the name of the item.
     *
     * @return The name of the item.
     */
    public String getName() {
        return itemName;
    }

    /**
     * Returns the weight of the item.
     *
     * @return The weight of the item.
     */
    public Integer getWeight() {
        return weight;
    }
}
