package otm.roguesque.game.entities;

import otm.roguesque.game.GlobalRandom;
import otm.roguesque.game.SpriteLoader;
import otm.roguesque.util.Path;
import otm.roguesque.util.Vector;

/**
 * Lohikäärmé. Syö menninkäisisä kun tulee nälkä (usein), muulloin käy pelaajan
 * kimppuun (eikä päästä pakoon).
 *
 * @author Jens Pitkänen
 */
public class Dragon extends Entity implements AI {

    private int hunger = 6;
    private Entity hunted;

    /**
     * Luo uuden lohikäärmeen.
     */
    public Dragon() {
        super(25, 6, 6, 6, "Dragon", "Breathes fire. A day's work for a knight.", "Dragons", SpriteLoader.loadImage("jar:/sprites/Dragon.png"));
    }

    @Override
    public void processRound() {
        hunger -= hunger > 0 ? 1 : 0;
        Vector delta = hunger == 0 ? huntForFood() : null;
        delta = delta == null ? huntPlayer(getDungeon().getPlayer()) : delta;
        if (delta == null) {
            if (hunger > 3) {
                return; // Siesta
            } else {
                delta = wander();
            }
        }
        move(delta.getX(), delta.getY());
        if (hunted != null && hunted.isDead()) {
            hunger = 6;
        }
    }

    private Vector huntPlayer(Player player) {
        Path pathToPlayer = getDungeon().getPathTo(this, player);
        if (pathToPlayer != null && pathToPlayer.getLength() < 15) {
            return new Vector(pathToPlayer.getNextDeltaX(), pathToPlayer.getNextDeltaY());
        } else {
            return null;
        }
    }

    private Vector huntForFood() {
        Entity targetGoblin = getDungeon().getClosestEntityOfType(Goblin.class, getX(), getY());
        if (targetGoblin == null) {
            return null;
        }
        Path pathToGoblin = getDungeon().getPathTo(this, targetGoblin);
        if (pathToGoblin != null) {
            return new Vector(pathToGoblin.getNextDeltaX(), pathToGoblin.getNextDeltaY());
        } else {
            return null;
        }
    }

    private Vector wander() {
        int r = GlobalRandom.get().nextInt(4);
        return new Vector((int) Math.cos(r * Math.PI / 2.0), (int) Math.sin(r * Math.PI / 2.0));
    }
}
