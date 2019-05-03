package otm.roguesque.game.entities;

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
        super(100000, 0, 0, 0, "", "", "Doors", "/sprites/Door.png");
        setInvulnerability(true);
    }

    @Override
    public String getRichDescription() {
        return "Door\n\nIt's a door.\nWhere does\nit lead?";
    }

    /**
     * Jos hyökkäävä olio on pelaaja, ovi "aukeaa" (eli kuolee).
     *
     * @param attackingEntity Hyökkäävä olio.
     */
    @Override
    protected void reactToAttack(Entity attackingEntity) {
        if (attackingEntity instanceof Player) {
            setHealth(0);
        }
    }
}
