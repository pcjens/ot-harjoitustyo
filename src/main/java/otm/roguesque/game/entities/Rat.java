package otm.roguesque.game.entities;

import otm.roguesque.game.GlobalRandom;

/**
 * Rotta-olio.
 *
 * @author Jens Pitkänen
 */
public class Rat extends Entity implements AI {

    /**
     * Luo uuden rotan.
     */
    public Rat() {
        super(5, 1, 0, "Rat", "*squeek*", "Small Animals", "/sprites/Rat.png");
    }

    @Override
    public void processRound() {
        Player player = dungeon.getPlayer();
        int dx = player.getX() - x;
        int dy = player.getY() - y;
        if (dx > 6 || dy > 6 || GlobalRandom.get().nextBoolean()) {
            int r = GlobalRandom.get().nextInt(4);
            dx = (int) Math.cos(r * Math.PI / 2.0);
            dy = (int) Math.sin(r * Math.PI / 2.0);
            move(dx, dy);
        } else {
            if (Math.abs(dx) > Math.abs(dy)) {
                this.move((int) Math.signum(dx), 0);
            } else {
                this.move(0, (int) Math.signum(dy));
            }
        }
    }
}
