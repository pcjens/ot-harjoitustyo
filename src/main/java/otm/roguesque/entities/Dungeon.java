package otm.roguesque.entities;

import java.util.ArrayList;
import java.util.Random;

public class Dungeon {

    private final boolean[] solid;
    private final TileType[] tiles;
    private final int width;
    private final int height;

    private ArrayList<Entity> entities;
    private Player player;

    public Dungeon(int width, int height, int seed) {
        this.width = width;
        this.height = height;
        this.entities = new ArrayList();
        this.tiles = new TileType[width * height];

        Random rand = new Random(seed);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y == 0 || y == height - 1) {
                    tiles[x + y * width] = TileType.HorizontalWall;
                } else if (x == 0 || x == width - 1) {
                    tiles[x + y * width] = TileType.VerticalWall;
                } else {
                    tiles[x + y * width] = TileType.Floor;

                    if (rand.nextFloat() < 0.02) {
                        spawnEntity(new Rat(), x, y);
                    }
                    if (rand.nextFloat() < 0.02) {
                        spawnEntity(new Item(1, rand.nextInt()), x, y);
                    }
                }
            }
        }
        tiles[(rand.nextInt(width - 2) + 1) + (rand.nextInt(height - 2) + 1) * width] = TileType.Stairs;

        this.solid = new boolean[]{
            false, true, true, false, false, false
        };
    }

    public final void spawnEntity(Entity e, int x, int y) {
        this.entities.add(e);
        e.setDungeon(this);
        e.setPosition(x, y);
        if (e instanceof Player) {
            this.player = (Player) e;
        }
    }

    public Player getPlayer() {
        return player;
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
            if (e.getX() == x && e.getY() == y && !e.isDead()) {
                return e;
            }
        }
        return null;
    }

    public TileType getTileAt(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return null;
        }
        return tiles[x + y * width];
    }

    public void processRound() {
        for (Entity e : entities) {
            if (e instanceof AI) {
                ((AI) e).processRound(this);
            }
        }
    }

    public void cleanupDeadEntities() {
        entities.forEach((entity) -> {
            if (entity.lastEntityInteractedWith != null
                    && entity.lastEntityInteractedWith.isDead()) {
                entity.lastEntityInteractedWith = null;
            }
        });
        entities.removeIf((entity) -> entity.isDead());
    }
}
