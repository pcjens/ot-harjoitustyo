package otm.roguesque.entities;

public class Player extends Entity {

    public Player() {
        super(10, 2, 1, "Adventurer", "Seeking\n freedom.", "Adventurers", "/sprites/player.png");
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
}
