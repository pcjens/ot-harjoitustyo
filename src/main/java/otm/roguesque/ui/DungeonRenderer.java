package otm.roguesque.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import otm.roguesque.entities.Dungeon;
import otm.roguesque.entities.Entity;
import otm.roguesque.entities.TileType;

public class DungeonRenderer {

    // Sprites
    private final String[] tileTypeNames;
    private final Image[] tileTypes;

    // Dungeon
    private Dungeon dungeon;
    private int width;
    private int height;
    private Image[] tileImages;

    public DungeonRenderer() {
        // Check the tiles in TileType.java, these should be in the same order
        // (This is also tested in TileGraphicTest.java)
        tileTypeNames = new String[]{
            "/sprites/floor.png",
            "/sprites/wallHorizontal.png",
            "/sprites/wallVertical.png",
            "/sprites/door.png",
            "/sprites/corridor.png",
            "/sprites/stairs.png"
        };

        tileTypes = new Image[tileTypeNames.length];
        for (int i = 0; i < tileTypeNames.length; i++) {
            tileTypes[i] = new Image(getClass().getResourceAsStream(tileTypeNames[i]),
                    32, 32, true, false);
        }
    }

    public void loadDungeon(Dungeon dungeon) {
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
                if (entity == null) {
                    ctx.drawImage(tileImages[x + y * width], x * 32, y * 32);
                } else {
                    ctx.drawImage(entity.getImage(), x * 32, y * 32);

                    int maxHp = entity.getMaxHealth();
                    int hp = entity.getHealth();
                    if (hp < maxHp) {
                        ctx.setFill(Color.BLACK);
                        ctx.fillRect(x * 32 + 2, y * 32 - 5, 26, 6);
                        ctx.setFill(Color.RED);
                        ctx.fillRect(x * 32 + 3, y * 32 - 4, 24, 4);
                        ctx.setFill(Color.GREEN);
                        ctx.fillRect(x * 32 + 3, y * 32 - 4, 24.0 * hp / maxHp, 4);
                        // Set back to default, just in case
                        ctx.setFill(Color.BLACK);
                    }
                }
            }
        }
    }

    public String getTileImageName(TileType tileType) {
        return tileTypeNames[tileType.ordinal()];
    }
}
