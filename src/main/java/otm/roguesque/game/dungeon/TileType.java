package otm.roguesque.game.dungeon;

public enum TileType {
    Floor("Floor"), HorizontalWall("Wall"), VerticalWall("Wall"), Corridor("Corridor"), Stairs("Stairs\n\nTo freedom!");

    private String description;

    TileType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
