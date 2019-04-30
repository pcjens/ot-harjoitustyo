package otm.roguesque.game.dungeon;

/**
 * Laatan tyyppiä kuvaava enum.
 *
 * @see otm.roguesque.game.dungeon.Dungeon#getTileAt(int, int)
 *
 * @author Jens Pitkänen
 */
public enum TileType {
    /**
     * Lattia, näkyy pelissä puisena alueena huoneiden sisällä.
     */
    Floor("Floor"),
    /**
     * Seinä, näkyy pelissä kivisenä muurina huoneiden ympärillä.
     */
    Wall("Wall"),
    /**
     * Käytävä, näkyy pelissä samanlaisena kuin lattia, huoneiden välillä.
     */
    Corridor("Corridor"),
    /**
     * Tikkaat seuraavaan kenttään, näkyy pelissä tikkaina alas puulattian läpi.
     */
    Ladder("Ladder\n\nIt's dark\ndown there.");

    private final String description;

    TileType(String description) {
        this.description = description;
    }

    /**
     * Palauttaa kuvauksen laatasta.
     *
     * @return Kuvaus tästä laatassta.
     */
    public String getDescription() {
        return description;
    }
}
