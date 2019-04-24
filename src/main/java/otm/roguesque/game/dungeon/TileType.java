package otm.roguesque.game.dungeon;

/**
 * Laatan tyyppiä kuvaava enum.
 *
 * @see otm.roguesque.game.dungeon.Dungeon#getTileAt(int, int)
 *
 * @author Jens Pitkänen
 */
public enum TileType {
    Floor("Floor"), Wall("Wall"), Corridor("Corridor"), Ladder("Ladder\n\nIt's dark\ndown there.");

    private String description;

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
