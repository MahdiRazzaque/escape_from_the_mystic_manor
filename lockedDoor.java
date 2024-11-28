import java.util.HashMap;

/**
 * The lockedDoor class represents a door that requires a key to unlock.
 * <p>
 * Each lockedDoor object has a roomPlusDirection string which indicates the specific door that is locked.
 * For example, roomPlusDirection may be "kitchenEast", representing the door between the kitchen and the room to the east of it.
 * The key variable stores the item object needed to unlock the door.
 *
 * @author Mahdi Razzaque
 * @version 28.11.24
 */
public class lockedDoor {
    private String roomPlusDirection; // Represents which door is locked
    private Item key; // Stores the item needed to unlock the door

    /**
     * Constructs a lockedDoor object with the specified room and direction, and the key required to unlock it.
     * <p>
     * This method converts the room and direction into a concatenated snake case string, assigns the key,
     * and updates the game's locked doors map, list of all locked rooms and a list of all lockedDoor objects.
     *
     * @param room The room where the door is located.
     * @param direction The direction in which the door is situated from the room, e.g., "east".
     * @param key The item required to unlock the door.
     */
    public lockedDoor(Room room, String direction, Item key) {
        // Convert room and direction to a snake_case string for identifying the locked door
        this.roomPlusDirection = Utils.roomDirToSnake(room, direction);
        this.key = key;

        Game.lockedDoorsMap.put(roomPlusDirection, this); // Add the locked door to the game's locked doors map

        Game.lockedDoorObjects.add(this); // Add the locked door to the list of locked doors

        Game.allUnlockedRooms.remove(room.getExit(direction));  // Remove the locked room from the allUnlockedRooms list
        Game.allLockedRooms.add(room.getExit(direction));   // Add the locked room to the allLockedRooms list
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
