package otm.roguesque.game.entities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javafx.scene.image.Image;
import otm.roguesque.game.GlobalRandom;
import otm.roguesque.game.SpriteLoader;

/**
 * Tavara-olio.
 *
 * @author Jens Pitkänen
 */
public class Item extends Entity {

    private static class ItemData {

        public final String identifier;
        public final String displayName;
        public final String description;
        public final String effect;
        public final int damageBoost;
        public final int attackBoost;
        public final int defenseBoost;

        public ItemData(String identifier, String displayName, String description, String effect, int damageBoost, int attackBoost, int defenseBoost) {
            this.identifier = identifier;
            this.displayName = displayName;
            this.description = description;
            this.effect = effect;
            this.damageBoost = damageBoost;
            this.attackBoost = attackBoost;
            this.defenseBoost = defenseBoost;
        }
    }

    private static ItemData[] ITEMS = new ItemData[]{
        new ItemData("jar:/sprites/ItemIronSword.png", "Sword of Iron", "It's a sword. Nothing special.", "", 0, 1, 0),
        new ItemData("jar:/sprites/ItemWoodenShield.png", "Shield of Wood", "It's a shield. Nothing special.", "", 0, 0, 1)
    };

    static {
        int i = 0;
        String latestLine;
        try {
            List<String> lines = Files.readAllLines(Paths.get(System.getProperty("user.dir"), "items.csv"));
            ItemData[] loadedItems = new ItemData[lines.size()];
            for (String line : lines) {
                latestLine = line;
                String[] parts = line.split(";");
                if (parts.length == 7) {
                    loadedItems[i] = new ItemData(parts[0], parts[1],
                            parts[2].replace("\\n", "\n"), parts[3],
                            Integer.parseInt(parts[4]),
                            Integer.parseInt(parts[5]),
                            Integer.parseInt(parts[6]));
                } else {
                    System.err.println("Wrong amount of parts on line " + i + ": " + latestLine);
                    throw new Exception();
                }
                i++;
            }
            ITEMS = loadedItems;
        } catch (IOException ex) {
            System.err.println("Item configuration file 'items.config' is missing, using default item set.");
        } catch (NumberFormatException ex) {
            System.err.println("Couldn't parse number on line " + i + ".");
        } catch (Exception ex) {
        }
    }

    private final String effect;
    private final int damageBoost;
    private final int attackBoost;
    private final int defenseBoost;
    private final Image itemImage;
    private final String itemName;
    private final String itemDescription;

    private boolean boxed = true;

    /**
     * Luo uuden tavaran.
     *
     * @param level Kentän vaikeustaso mistä tämä tavara löytyy.
     */
    public Item(int level) {
        super(100000, 0, 0, 0, "", "", "Items", SpriteLoader.loadImage("jar:/sprites/ItemChest.png"));

        ItemData data = ITEMS[GlobalRandom.get().nextInt(ITEMS.length)];

        this.setName("Chest");
        this.setDescription("What could be inside?");
        this.setInvulnerability(true);

        this.effect = data.effect;
        this.itemName = data.displayName;
        this.itemDescription = data.description;
        this.itemImage = SpriteLoader.loadImage(data.identifier);
        this.damageBoost = data.damageBoost;
        this.attackBoost = data.attackBoost;
        this.defenseBoost = data.defenseBoost;
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
        if (boxed) {
            return String.format("%s\n\n%s", getName(), getDescription());
        } else {
            String description = String.format("%s\n\n%s\n", getName(), getDescription());
            description += damageBoost == 0 ? "" : String.format("\nDMG: %s", getStringFromBonus(damageBoost));
            description += attackBoost == 0 ? "" : String.format("\nATK: %s", getStringFromBonus(attackBoost));
            description += defenseBoost == 0 ? "" : String.format("\nDEF: %s", getStringFromBonus(defenseBoost));
            return description;
        }
    }

    @Override
    protected void reactToAttack(Entity attackingEntity) {
        if (attackingEntity instanceof Player) {
            if (boxed) {
                boxed = false;
                setImage(itemImage);
                setName(itemName);
                setDescription(itemDescription);
            } else {
                Player player = (Player) attackingEntity;
                player.setDamage(Math.max(1, player.getDamage() + damageBoost));
                player.setAttack(Math.max(1, player.getAttack() + attackBoost));
                player.setDefense(Math.max(0, player.getDefense() + defenseBoost));
                applyEffect(player);
                setHealth(0);
            }
        }
    }

    private void applyEffect(Player player) {
        switch (effect) {
            case "binoculars":
                player.setSightDistance(player.getSightDistance() + 2);
                break;
            case "medication":
                player.setAutoHealCooldown(Math.max(1, player.getAutoHealCooldown() - 2));
                break;
            default: break;
        }
    }
}
