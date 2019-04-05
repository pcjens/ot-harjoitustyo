package otm.roguesque.entities;

public class Player extends Entity {

    private int gold = 0;

    public Player() {
        super(10, 2, 1, "Adventurer", "Delving dungeons.", "Adventurers", "/sprites/player.png");
    }

    public int getGold() {
        return gold;
    }
}
