/**
 * The lockedDoor class represents a door that requires a key to unlock.
 * <p>
 * Each lockedDoor object has a roomPlusDirection string which indicates the specific door that is locked.
 * For example, roomPlusDirection may be "kitchenEast", representing the door between the kitchen and the room to the east of it.
 * The key variable stores the item object needed to unlock the door.
 */
public class lockedDoor {
    private String roomPlusDirection; // Represents which door is locked
    private Item key; // Stores the item needed to unlock the door

    /**
     * Constructs a lockedDoor object with the specified room direction and key.
     *
     * @param roomPlusDirection The room and direction of the door, e.g., "kitchenEast".
     * @param key The item required to unlock the door.
     */
    public lockedDoor(String roomPlusDirection, Item key) {
        this.roomPlusDirection = roomPlusDirection;
        this.key = key;

        Game.lockedDoorsMap.put(roomPlusDirection, this);
    }

    /**
     * Retrieves the room and direction of the door.
     *
     * @return The room and direction of the door as a string.
     */
    public String getRoomPlusDirection() {
        return roomPlusDirection;
    }

    /**
     * Retrieves the key required to unlock the door.
     *
     * @return The item required to unlock the door.
     */
    public Item getKey() {
        return key;
    }
}
