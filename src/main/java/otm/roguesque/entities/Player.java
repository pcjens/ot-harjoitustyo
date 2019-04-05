package otm.roguesque.entities;

import javafx.scene.image.Image;

public class Player extends Entity {

    private Image playerImage;
    private int gold = 0;

    public Player() {
        maxHealth = 10;
        health = 10;
        attack = 2;
        defense = 1;
        friendlyGroup = "Adventurer";
        playerImage = new Image(getClass().getResourceAsStream("/sprites/player.png"), 32, 32, true, false);
    }

    public int getGold() {
        return gold;
    }

    @Override
    public Image getImage() {
        return playerImage;
    }
}
