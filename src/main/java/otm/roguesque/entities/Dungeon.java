package otm.roguesque.entities;

import java.util.ArrayList;

public class Dungeon {

    private final boolean[] solid;
    private final TileType[] tiles;
    private ArrayList<Entity> entities;
    private final int width;
    private final int height;

    public Dungeon(int width, int height) {
        this.width = width;
        this.height = height;
        this.entities = new ArrayList();
        this.tiles = new TileType[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y == 0 || y == height - 1) {
                    tiles[x + y * width] = TileType.HorizontalWall;
                } else if (x == 0 || x == width - 1) {
                    tiles[x + y * width] = TileType.VerticalWall;
                } else {
                    tiles[x + y * width] = TileType.Floor;
                }
            }
        }

        this.solid = new boolean[]{
            false, true, true, false, false, false
        };
    }

    public void spawnEntity(Entity e, int x, int y) {
        this.entities.add(e);
        e.setDungeon(this);
        e.setPosition(x, y);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public TileType[] getTiles() {
        return tiles;
    }

    public boolean solid(int x, int y) {
        return solid[tiles[x + y * width].ordinal()];
    }

    public Entity getEntityAt(int x, int y) {
        for (Entity e : entities) {
            if (e.getX() == x && e.getY() == y) {
                return e;
            }
        }
        return null;
    }
}
