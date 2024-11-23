import java.util.ArrayList;

public class lockedDoor {
    private String roomPlusDirection;
    private Item key;

    public lockedDoor(String roomPlusDirection, Item key) {
        this.roomPlusDirection = roomPlusDirection;
        this.key = key;
    }

    public String getroomPlusDirection() {
        return roomPlusDirection;
    }

    public Item getKey() {
        return key;
    }
}
