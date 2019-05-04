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
    private Entity hunted = null;

    /**
     * Luo uuden menninkäisen.
     */
    public Goblin() {
        super(2, 8, 2, 2, 1, "Goblin", "Looks hungry. And evil.", "Goblins", SpriteLoader.loadImage("jar:/sprites/Goblin.png"));
    }

    @Override
    public void processRound() {
        hunger -= hunger > 0 ? 1 : 0;
        Vector delta = hunger == 0 ? huntForFood() : null;
        delta = delta == null ? huntPlayer(getDungeon().getPlayer()) : delta;
        if (delta == null) {
            delta = wander();
        }
        move(delta.getX(), delta.getY());
        if (hunted != null && hunted.isDead()) {
            hunger = 10;
        }
    }

    private Vector huntPlayer(Player player) {
        Path pathToPlayer = getDungeon().getPathTo(this, player);
        if (pathToPlayer != null && pathToPlayer.getLength() < 15 && playerChaseRoundCount < 2) {
            playerChaseRoundCount++;
            return new Vector(pathToPlayer.getNextDeltaX(), pathToPlayer.getNextDeltaY());
        } else {
            playerChaseRoundCount = 0;
            return null;
        }
    }

    private Vector huntForFood() {
        Entity targetRat = getDungeon().getClosestEntityOfType(Rat.class, getX(), getY());
        if (targetRat == null) {
            return null;
        }
        Path pathToRat = getDungeon().getPathTo(this, targetRat);
        if (pathToRat != null) {
            return new Vector(pathToRat.getNextDeltaX(), pathToRat.getNextDeltaY());
        } else {
            return null;
        }
    }

    private Vector wander() {
        int r = GlobalRandom.get().nextInt(4);
        return new Vector((int) Math.cos(r * Math.PI / 2.0), (int) Math.sin(r * Math.PI / 2.0));
    }
}
