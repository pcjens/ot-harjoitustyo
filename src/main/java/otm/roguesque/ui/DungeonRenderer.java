package otm.roguesque.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import otm.roguesque.game.dungeon.Dungeon;
import otm.roguesque.game.dungeon.TileType;
import otm.roguesque.game.entities.Door;
import otm.roguesque.game.entities.Entity;
import otm.roguesque.game.entities.Player;

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
            "/sprites/wall.png",
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
        updateOffsets(dungeon, tilesX, tilesY, tilesX - (int) (220 / tileSize), tilesY - (int) (100 / tileSize));
        drawMap(ctx, dungeon, tileSize, tilesX, tilesY);
        drawSelection(ctx, dungeon, tileSize, selectionX - offsetX, selectionY - offsetY);
    }

    private void clearScreen(GraphicsContext ctx, Canvas canvas) {
        ctx.setFill(Color.BLACK);
        ctx.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
    }

    private void updateOffsets(Dungeon dungeon, int tilesX, int tilesY, int maxTilesX, int maxTilesY) {
        Player player = dungeon.getPlayer();
        offsetX = Math.max(0, Math.min(width - maxTilesX, player.getX() - tilesX / 2));
        offsetY = Math.max(0, Math.min(height - maxTilesY, player.getY() - tilesY / 2));
    }

    private void drawMap(GraphicsContext ctx, Dungeon dungeon, double tileSize, int tilesX, int tilesY) {
        Player player = dungeon.getPlayer();
        for (int y = offsetY; y < Math.min(offsetY + tilesY, height); y++) {
            for (int x = offsetX; x < Math.min(offsetX + tilesX, width); x++) {
                if (!player.hasBeenInLineOfSight(x, y)) {
                    continue;
                }
                double drawX = (x - offsetX) * tileSize;
                double drawY = (y - offsetY) * tileSize;
                if (tileImages[x + y * width] != null) {
                    ctx.drawImage(tileImages[x + y * width], drawX, drawY, tileSize, tileSize);
                }
                Entity entity = dungeon.getEntityAt(x, y);
                if (entity != null && (player.inLineOfSight(x, y) || entity instanceof Door)) {
                    drawEntity(ctx, tileSize, entity, drawX, drawY);
                }
                if (!player.inLineOfSight(x, y)) {
                    ctx.setFill(new Color(0.0, 0.0, 0.0, 0.5));
                    ctx.fillRect(drawX, drawY, tileSize, tileSize);
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

    private void drawEntity(GraphicsContext ctx, double tileSize, Entity entity, double x, double y) {
        ctx.drawImage(entity.getImage(), x, y, tileSize, tileSize);

        int maxHp = entity.getMaxHealth();
        int hp = entity.getHealth();
        if (hp < maxHp) {
            double hpBarWidth = 24.0 * hp / maxHp;
            ctx.setFill(Color.WHITE);
            ctx.fillRect(x + 2, y - 7, 26, 6);
            ctx.setFill(Color.BLACK);
            ctx.fillRect(x + 3 + hpBarWidth, y - 6, 24 - hpBarWidth, 4);
        }
    }
}
