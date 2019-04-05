package otm.roguesque.entities;

import java.util.Random;

public class Rat extends Entity implements AI {

    private static Random rand = new Random(123);

    public Rat() {
        super(5, 2, 0, "Rat", "*squeek*", "Small Animals", "/sprites/rat.png");
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
