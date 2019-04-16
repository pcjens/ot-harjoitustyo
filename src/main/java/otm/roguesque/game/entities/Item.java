package otm.roguesque.game.entities;

import java.util.Random;

public class Item extends Entity {

    private static final String[] GENERAL_PREFIXES = new String[]{
        "Shabby", "Decent", "Nice", "Pristine", "Exquisite", "Legendary"
    };
    private static final String[] OFFENSIVE_BASE_NAMES = new String[]{
        "Longsword", "Shortsword", "Bastard sword", "Pike", "Axe", "Rapier",
        "Whip", "Dao", "Katana", "Kukri"
    };
    private static final String[] OFFENSIVE_SUFFIXES = new String[]{
        "Death", "Destruction", "Fire", "Thunder"
    };
    private static final String[] DEFENSIVE_BASE_NAMES = new String[]{
        "Shield", "Armor", "Buckler", "Protective Glasses"
    };
    private static final String[] DEFENSIVE_SUFFIXES = new String[]{
        "Life", "Protection"
    };

    private int attackBoost = 0;
    private int defenseBoost = 0;

    public Item(int level, int seed) {
        super(1, 0, 1000000, "", "", "Items", "/sprites/item.png");

        Random rand = new Random(seed);

        if (rand.nextBoolean()) {
            attackBoost = rand.nextInt(1 + level) + 1;
            String quality = GENERAL_PREFIXES[Math.min(GENERAL_PREFIXES.length, attackBoost - 1)];
            String baseName = OFFENSIVE_BASE_NAMES[rand.nextInt(OFFENSIVE_BASE_NAMES.length)];
            String feature = OFFENSIVE_SUFFIXES[rand.nextInt(OFFENSIVE_SUFFIXES.length)];
            name = quality + " " + baseName;
            if (rand.nextInt(Math.max(3, 10 - level)) == 0) {
                name += " of\n" + feature;
                description = "\n" + baseName + "\nimbued with\n" + feature.toLowerCase() + ".\n";
            }
        } else {
            defenseBoost = rand.nextInt(1 + level) + 1;
            String quality = GENERAL_PREFIXES[Math.min(GENERAL_PREFIXES.length, defenseBoost - 1)];
            String baseName = DEFENSIVE_BASE_NAMES[rand.nextInt(DEFENSIVE_BASE_NAMES.length)];
            String feature = DEFENSIVE_SUFFIXES[rand.nextInt(DEFENSIVE_SUFFIXES.length)];
            name = quality + " " + baseName + " of " + feature;
            if (rand.nextInt(Math.max(3, 10 - level)) == 0) {
                name += " of\n" + feature;
                description = "\n" + baseName + "\nimbued with\n" + feature.toLowerCase() + ".\n";
            }
        }
    }

    @Override
    public String getDescription() {
        return String.format("%s\n%s\nATK: +%d\nDEF: +%d", name, description, attackBoost, defenseBoost);
    }

    @Override
    protected void reactToAttack(Entity attackingEntity) {
        if (attackingEntity instanceof Player) {
            Player player = (Player) attackingEntity;
            player.attack += attackBoost;
            player.defense += defenseBoost;
            health = 0;
        }
    }
}
