package otm.roguesque.ui;

import javafx.scene.canvas.Canvas;
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
    private final Image selectionImage;

    // Dungeon
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
        selectionImage = new Image(getClass().getResourceAsStream("/sprites/selection.png"), 32, 32, true, false);
    }

    public void loadDungeon(Dungeon dungeon) {
        this.width = dungeon.getWidth();
        this.height = dungeon.getHeight();
        this.tileImages = new Image[width * height];
        TileType[] tiles = dungeon.getTiles();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.tileImages[x + y * width] = tileTypes[tiles[x + y * width].ordinal()];
            }
        }
    }

    public void draw(GraphicsContext ctx, Dungeon dungeon, int selectionX, int selectionY) {
        Canvas canvas = ctx.getCanvas();
        ctx.setFill(Color.BLACK);
        ctx.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());

        if (dungeon == null) {
            return;
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Entity entity = dungeon.getEntityAt(x, y);
                if (entity == null) {
                    ctx.drawImage(tileImages[x + y * width], x * 32, y * 32);
                } else {
                    drawEntity(ctx, entity, x, y);
                }
            }
        }

        drawSelection(ctx, dungeon, selectionX, selectionY);
    }

    private void drawSelection(GraphicsContext ctx, Dungeon dungeon, int selectionX, int selectionY) {
        Entity selectedEntity = dungeon.getPlayer().getLastEntityInteractedWith();
        if (selectedEntity != null) {
            ctx.drawImage(selectionImage, selectedEntity.getX() * 32, selectedEntity.getY() * 32);
        } else if (selectionX >= 0 && selectionY >= 0) {
            ctx.drawImage(selectionImage, selectionX * 32, selectionY * 32);
        }
    }

    public String getTileImageName(TileType tileType) {
        return tileTypeNames[tileType.ordinal()];
    }

    private void drawEntity(GraphicsContext ctx, Entity entity, int x, int y) {
        ctx.drawImage(entity.getImage(), x * 32, y * 32);

        int maxHp = entity.getMaxHealth();
        int hp = entity.getHealth();
        if (hp < maxHp) {
            double hpBarWidth = 24.0 * hp / maxHp;
            ctx.setFill(Color.WHITE);
            ctx.fillRect(x * 32 + 2, y * 32 - 7, 26, 6);
            ctx.setFill(Color.BLACK);
            ctx.fillRect(x * 32 + 3 + hpBarWidth, y * 32 - 6, 24 - hpBarWidth, 4);
        }
    }
}
