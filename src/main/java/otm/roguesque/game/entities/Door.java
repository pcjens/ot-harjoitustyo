package otm.roguesque.game.entities;

public class Door extends Entity {

    public Door() {
        super(1, 0, 1000000, "", "", "Doors", "/sprites/door.png");
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
