package otm.roguesque.game.entities;

import otm.roguesque.game.GlobalRandom;
import otm.roguesque.util.Path;

/**
 * Luuranko. Seuraa pelaajaa maailman ääriin asti, mutta jaksaa askeltaa vain
 * joka toisella vuorolla.
 *
 * @author Jens Pitkänen
 */
public class Skeleton extends Entity implements AI {

    private int hunger = 10;
    private boolean offRound = false;

    /**
     * Luo uuden luurangon.
     */
    public Skeleton() {
        super(10, 3, 0, "Skeleton", "Spooky.\n And scary.", "Skeletons", "/sprites/Skeleton.png");
    }

    @Override
    public void processRound() {
        offRound = !offRound;
        if (offRound) {
            return;
        }

        Entity player = dungeon.getPlayer();
        Path path = dungeon.getPathTo(this, player);
        if (path != null) {
            move(path.getNextDeltaX(), path.getNextDeltaY());
        } else {
            int r = GlobalRandom.get().nextInt(4);
            int dx = (int) Math.cos(r * Math.PI / 2.0);
            int dy = (int) Math.sin(r * Math.PI / 2.0);
            move(dx, dy);
        }
    }
}
