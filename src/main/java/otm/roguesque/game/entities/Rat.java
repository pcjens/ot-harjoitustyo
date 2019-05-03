package otm.roguesque.game.entities;

import otm.roguesque.game.GlobalRandom;
import otm.roguesque.game.SpriteLoader;
import otm.roguesque.util.Vector;

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
        super(5, 1, 1, 1, "Rat", "*squeek*", "Small Animals", SpriteLoader.loadImage("jar:/sprites/Rat.png"));
    }

    @Override
    public void processRound() {
        Vector delta = getVectorTo(getDungeon().getPlayer());
        if (delta.getDistance() > 8 || GlobalRandom.get().nextBoolean()) {
            int r = GlobalRandom.get().nextInt(4);
            int dx = (int) Math.cos(r * Math.PI / 2.0);
            int dy = (int) Math.sin(r * Math.PI / 2.0);
            move(dx, dy);
        } else {
            Vector moveAmount = delta.lesserComponentZeroed().unit();
            move(moveAmount.getX(), moveAmount.getY());
        }
    }
}
