package otm.roguesque.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import otm.roguesque.entities.Dungeon;
import otm.roguesque.entities.Entity;
import otm.roguesque.entities.Player;
import otm.roguesque.entities.TileType;

public class DungeonRenderer {

    // Sprites
    private final Image playerImage;
    private final Image[] tileTypes;

    // Dungeon
    private Dungeon dungeon;
    private int width;
    private int height;
    private Image[] tileImages;

    public DungeonRenderer() {
        playerImage = new Image(getClass().getResourceAsStream("/sprites/player.png"), 32, 32, true, false);

        // Check the tiles in TileType.java, these should be in the same order
        tileTypes = new Image[]{
            new Image(getClass().getResourceAsStream("/sprites/floor.png"), 32, 32, true, false),
            new Image(getClass().getResourceAsStream("/sprites/wallHorizontal.png"), 32, 32, true, false),
            new Image(getClass().getResourceAsStream("/sprites/wallVertical.png"), 32, 32, true, false),
            new Image(getClass().getResourceAsStream("/sprites/door.png"), 32, 32, true, false),
            new Image(getClass().getResourceAsStream("/sprites/corridor.png"), 32, 32, true, false),
            new Image(getClass().getResourceAsStream("/sprites/stairs.png"), 32, 32, true, false)
        };
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
        this.width = this.dungeon.getWidth();
        this.height = this.dungeon.getHeight();
        this.tileImages = new Image[width * height];
        TileType[] tiles = this.dungeon.getTiles();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.tileImages[x + y * width] = tileTypes[tiles[x + y * width].ordinal()];
            }
        }
    }

    public void draw(GraphicsContext ctx) {
        if (this.dungeon == null) {
            return;
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Entity entity = dungeon.getEntityAt(x, y);
                if (entity instanceof Player) {
                    ctx.drawImage(playerImage, x * 32, y * 32);
                } else if (entity != null) {
                    // TODO: Entity rendering
                } else {
                    ctx.drawImage(tileImages[x + y * width], x * 32, y * 32);
                }
            }
        }
    }
}
