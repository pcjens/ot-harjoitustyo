package otm.roguesque.entities;

import java.util.Random;
import javafx.scene.image.Image;

public class Rat extends Entity implements AI {

    private static Random rand = new Random(123);

    private Image ratImage;

    public Rat() {
        maxHealth = 5;
        health = 5;
        attack = 2;
        defense = 0;
        friendlyGroup = "Small Animals";
        ratImage = new Image(getClass().getResourceAsStream("/sprites/rat.png"), 32, 32, true, false);
    }

    @Override
    public Image getImage() {
        return ratImage;
    }

    @Override
    public void processRound(Dungeon dungeon) {
        Player player = dungeon.getPlayer();
        int dx = player.getX() - x;
        int dy = player.getY() - y;
        if (dx > 6 || dy > 6 || rand.nextBoolean()) {
            int r = rand.nextInt(4);
            int x = (int) Math.cos(r * Math.PI / 2.0);
            int y = (int) Math.sin(r * Math.PI / 2.0);
            move(x, y);
        } else {
            if (Math.abs(dx) > Math.abs(dy)) {
                this.move((int) Math.signum(dx), 0);
            } else {
                this.move(0, (int) Math.signum(dy));
            }
        }
    }
}
