package otm.roguesque.game.entities;

import otm.roguesque.game.GlobalRandom;

/**
 * Tavara-olio.
 *
 * @author Jens Pitkänen
 */
public class Item extends Entity {

    // TODO: Move the ItemData to a config file
    private static class ItemData {

        public String identifier;
        public String displayName;
        public String description;
        public int attackBoost;
        public int defenseBoost;

        public ItemData(String identifier, String displayName, String description, int attackBoost, int defenseBoost) {
            this.identifier = identifier;
            this.displayName = displayName;
            this.description = description;
            this.attackBoost = attackBoost;
            this.defenseBoost = defenseBoost;
        }
    }

    private static final ItemData[] ITEMS = new ItemData[]{
        new ItemData("FireWhip", "The Fiery Whip\n  of Balgor", "Pretty hot.\n\nSo hot you\nmight get\nburned.", 2, -1),
        new ItemData("IronSword", "    Sword\n   of Iron", "It's a sword.\n\nNothing\nspecial.", 1, 0),
        new ItemData("WoodenShield", "   Shield\n   of Wood", "It's a shield.\n\nNothing\nspecial.", 0, 1)
    };

    private int attackBoost = 0;
    private int defenseBoost = 0;

    /**
     * Luo uuden tavaran.
     *
     * @param level Kentän vaikeustaso mistä tämä tavara löytyy.
     */
    public Item(int level) {
        super(100000, 0, 0, "", "", "Items", "");

        ItemData data = ITEMS[GlobalRandom.get().nextInt(ITEMS.length)];
        loadImage("/sprites/Item" + data.identifier + ".png");
        this.name = data.displayName;
        this.attackBoost = data.attackBoost;
        this.defenseBoost = data.defenseBoost;
        this.description = data.description;
    }

    private String getStringFromBonus(int bonus) {
        if (bonus >= 0) {
            return "+" + bonus;
        } else {
            return "" + bonus;
        }
    }

    @Override
    public String getDescription() {
        return String.format("%s\n\n%s\n\nATK: %s\nDEF: %s", name, description, getStringFromBonus(attackBoost), getStringFromBonus(defenseBoost));
    }

    @Override
    protected void reactToAttack(Entity attackingEntity) {
        if (attackingEntity instanceof Player) {
            Player player = (Player) attackingEntity;
            player.attack = Math.max(1, player.attack + attackBoost);
            player.defense = Math.max(0, player.defense + defenseBoost);
            health = 0;
        }
    }
}
