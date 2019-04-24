package otm.roguesque.game.dungeon;

public enum TileType {
    Floor("Floor"), Wall("Wall"), Corridor("Corridor"), Ladder("Ladder\n\nIt's dark\ndown there.");

    private String description;

    TileType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
