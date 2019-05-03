package otm.roguesque.game.entities;

import otm.roguesque.game.GlobalRandom;
import otm.roguesque.game.SpriteLoader;
import otm.roguesque.game.dungeon.Dungeon;
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
        super(10, 5, 1, 5, "Skeleton", "Spooky, scary. Skeleton.", "Skeletons", SpriteLoader.loadImage("jar:/sprites/Skeleton.png"));
    }

    @Override
    public void processRound() {
        offRound = !offRound;
        if (offRound) {
            return;
        }

        Dungeon dungeon = getDungeon();
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
