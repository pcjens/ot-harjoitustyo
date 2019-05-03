package otm.roguesque.game.entities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import otm.roguesque.game.GlobalRandom;

/**
 * Tavara-olio.
 *
 * @author Jens Pitkänen
 */
public class Item extends Entity {

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

    private static ItemData[] ITEMS = new ItemData[]{
        new ItemData("IronSword", "    Sword\n   of Iron", "It's a sword.\n\nNothing\nspecial.", 1, 0),
        new ItemData("WoodenShield", "   Shield\n   of Wood", "It's a shield.\n\nNothing\nspecial.", 0, 1)
    };

    static {
        int i = 0;
        try {
            List<String> lines = Files.readAllLines(Paths.get(System.getProperty("user.dir"), "items.csv"));
            ITEMS = new ItemData[lines.size()];
            for (String line : lines) {
                String[] parts = line.split(";");
                if (parts.length == 5) {
                    ITEMS[i++] = new ItemData(parts[0].replace("\\n", "\n"),
                            parts[1].replace("\\n", "\n"),
                            parts[2].replace("\\n", "\n"),
                            Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
                } else {
                    System.err.println("Wrong amount of parts on line " + i + ".");
                }
            }
        } catch (IOException ex) {
            System.err.println("Item configuration file 'items.config' is missing, using default item set.");
        } catch (NumberFormatException ex) {
            System.err.println("Couldn't parse number on line " + i + ".");
        }
    }

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
        this.setName(data.displayName);
        this.attackBoost = data.attackBoost;
        this.defenseBoost = data.defenseBoost;
        this.setDescription(data.description);
    }

    private String getStringFromBonus(int bonus) {
        if (bonus >= 0) {
            return "+" + bonus;
        } else {
            return "" + bonus;
        }
    }

    @Override
    public String getRichDescription() {
        return String.format("%s\n\n%s\n\nATK: %s\nDEF: %s", getName(), getDescription(), getStringFromBonus(attackBoost), getStringFromBonus(defenseBoost));
    }

    @Override
    protected void reactToAttack(Entity attackingEntity) {
        if (attackingEntity instanceof Player) {
            Player player = (Player) attackingEntity;
            player.setAttack(Math.max(1, player.getAttack() + attackBoost));
            player.setDefense(Math.max(0, player.getDefense() + defenseBoost));
            setHealth(0);
        }
    }
}
