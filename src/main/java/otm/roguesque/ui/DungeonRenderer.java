package otm.roguesque.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import otm.roguesque.entities.Dungeon;
import otm.roguesque.entities.Entity;
import otm.roguesque.entities.Player;
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

    private int offsetX;
    private int offsetY;

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

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void loadDungeon(Dungeon dungeon) {
        this.width = dungeon.getWidth();
        this.height = dungeon.getHeight();
        this.tileImages = new Image[width * height];
        TileType[] tiles = dungeon.getTiles();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (tiles[x + y * width] == null) {
                    continue;
                }
                this.tileImages[x + y * width] = tileTypes[tiles[x + y * width].ordinal()];
            }
        }
    }

    public void draw(GraphicsContext ctx, Dungeon dungeon, double tileSize, int selectionX, int selectionY) {
        Canvas canvas = ctx.getCanvas();
        clearScreen(ctx, canvas);
        if (dungeon == null) {
            return;
        }

        int tilesX = (int) (canvas.getWidth() / tileSize);
        int tilesY = (int) (canvas.getHeight() / tileSize);
        updateOffsets(dungeon, tilesX, tilesY);
        drawMap(ctx, dungeon, tileSize, tilesX, tilesY);
        drawSelection(ctx, dungeon, tileSize, selectionX - offsetX, selectionY - offsetY);
    }

    private void clearScreen(GraphicsContext ctx, Canvas canvas) {
        ctx.setFill(Color.BLACK);
        ctx.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
    }

    private void updateOffsets(Dungeon dungeon, int tilesX, int tilesY) {
        Player player = dungeon.getPlayer();
        offsetX = Math.max(0, Math.min(width - tilesX, player.getX() - tilesX / 2));
        offsetY = Math.max(0, Math.min(height - tilesY, player.getY() - tilesY / 2));
    }

    private void drawMap(GraphicsContext ctx, Dungeon dungeon, double tileSize, int tilesX, int tilesY) {
        for (int y = offsetY; y < Math.min(offsetY + tilesY, height); y++) {
            for (int x = offsetX; x < Math.min(offsetX + tilesX, width); x++) {
                Entity entity = dungeon.getEntityAt(x, y);
                if (entity == null) {
                    if (tileImages[x + y * width] != null) {
                        ctx.drawImage(tileImages[x + y * width], (x - offsetX) * tileSize, (y - offsetY) * tileSize, tileSize, tileSize);
                    }
                } else {
                    drawEntity(ctx, tileSize, entity, (x - offsetX), (y - offsetY));
                }
            }
        }
    }

    private void drawSelection(GraphicsContext ctx, Dungeon dungeon, double tileSize, int selectionX, int selectionY) {
        Entity selectedEntity = dungeon.getPlayer().getLastEntityInteractedWith();
        if (selectedEntity != null) {
            ctx.drawImage(selectionImage,
                    selectedEntity.getX() * tileSize, selectedEntity.getY() * tileSize,
                    tileSize, tileSize);
        } else if (selectionX >= 0 && selectionY >= 0) {
            ctx.drawImage(selectionImage,
                    selectionX * tileSize, selectionY * tileSize,
                    tileSize, tileSize);
        }
    }

    public String getTileImageName(TileType tileType) {
        return tileTypeNames[tileType.ordinal()];
    }

    private void drawEntity(GraphicsContext ctx, double tileSize, Entity entity, int x, int y) {
        ctx.drawImage(entity.getImage(), x * tileSize, y * tileSize, tileSize, tileSize);

        int maxHp = entity.getMaxHealth();
        int hp = entity.getHealth();
        if (hp < maxHp) {
            double hpBarWidth = 24.0 * hp / maxHp;
            ctx.setFill(Color.WHITE);
            ctx.fillRect(x * tileSize + 2, y * tileSize - 7, 26, 6);
            ctx.setFill(Color.BLACK);
            ctx.fillRect(x * tileSize + 3 + hpBarWidth, y * tileSize - 6, 24 - hpBarWidth, 4);
        }
    }
}
