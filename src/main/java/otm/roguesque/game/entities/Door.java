package otm.roguesque.game.entities;

import otm.roguesque.game.SpriteLoader;

/**
 * Ovi-olio. Ovet "aukeavat" (kuolevat) kun pelaaja "avaa sen" (eli lyö sitä).
 *
 * @author Jens Pitkänen
 */
public class Door extends Entity {

    /**
     * Luo uuden oven.
     */
    public Door() {
        super(0, 100000, 0, 0, 0, "", "", "Doors", SpriteLoader.loadImage("jar:/sprites/Door.png"));
        setInvulnerability(true);
    }

    @Override
    public String getRichDescription() {
        return "Door\n\nIt's a door.\nWhere does it lead?";
    }

    /**
     * Jos hyökkäävä olio on pelaaja, ovi "aukeaa" (eli kuolee).
     *
     * @param attackingEntity Hyökkäävä olio.
     */
    @Override
    protected void reactToAttack(Entity attackingEntity) {
        if (attackingEntity instanceof Player || attackingEntity instanceof Dragon) {
            setHealth(0);
        }
    }
}
