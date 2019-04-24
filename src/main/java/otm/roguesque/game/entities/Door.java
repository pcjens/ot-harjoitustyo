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
        super(1, 0, 1000000, "", "", "Doors", "/sprites/Door.png");
    }

    @Override
    public String getDescription() {
        return "Door\n\nIt's a door.\nWhere does\nit lead?";
    }

    @Override
    protected void reactToAttack(Entity attackingEntity) {
        if (attackingEntity instanceof Player) {
            health = 0;
        }
    }
}
