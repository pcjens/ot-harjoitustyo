package otm.roguesque.entities;

public class Player extends Entity {

    private int gold = 0;

    public Player() {
        super(10, 2, 1, "Adventurer", "Delving dungeons.", "Adventurers", "/sprites/player.png");
    }

    public void resetLastEntityInteractedWith() {
        lastEntityInteractedWith = null;
    }

    public String getExaminationText() {
        if (lastEntityInteractedWith != null) {
            return lastEntityInteractedWith.getDescription();
        }
        return null;
    }

    public int getGold() {
        return gold;
    }
}
