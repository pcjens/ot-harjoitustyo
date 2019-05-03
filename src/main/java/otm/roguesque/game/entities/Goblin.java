package otm.roguesque.game.entities;

import otm.roguesque.game.GlobalRandom;
import otm.roguesque.game.SpriteLoader;
import otm.roguesque.util.Path;
import otm.roguesque.util.Vector;

/**
 * Menninkäinen. Syö rottia kun tulee nälkä, muulloin lähinnä käy pelaajan
 * kimppuun.
 *
 * @author Jens Pitkänen
 */
public class Goblin extends Entity implements AI {

    private int hunger = 10;
    private int playerChaseRoundCount = 0;

    /**
     * Luo uuden menninkäisen.
     */
    public Goblin() {
        super(8, 3, 3, 1, "Goblin", "Looks hungry. And evil.", "Goblins", SpriteLoader.loadImage("jar:/sprites/Goblin.png"));
    }

    @Override
    public void processRound() {
        Entity target = getDungeon().getPlayer();
        Vector delta = getVectorTo(target);
        Path pathToPlayer;
        if (delta.getDistance() < 10 && (pathToPlayer = getDungeon().getPathTo(this, target)) != null && playerChaseRoundCount < 4) {
            delta = new Vector(pathToPlayer.getNextDeltaX(), pathToPlayer.getNextDeltaY());
            playerChaseRoundCount++;
        } else if (playerChaseRoundCount == 4) {
            playerChaseRoundCount = 0;
            delta = new Vector(0, 0);
        } else if (hunger == 0) {
            target = getDungeon().getClosestEntityOfType(Rat.class, getX(), getY());
            if (target != null) {
                delta = getVectorTo(target).lesserComponentZeroed().unit();
            }
        }
        if (delta.getDistance() > 1) {
            int r = GlobalRandom.get().nextInt(4);
            delta = new Vector((int) Math.cos(r * Math.PI / 2.0), (int) Math.sin(r * Math.PI / 2.0));
            hunger -= hunger > 0 ? 1 : 0;
        }
        move(delta.getX(), delta.getY());
        if (target != null && target.isDead()) {
            hunger = 10;
        }
    }
}
