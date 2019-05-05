package otm.roguesque.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import otm.roguesque.game.SpriteLoader;
import otm.roguesque.game.dungeon.Dungeon;
import otm.roguesque.game.dungeon.TileType;
import otm.roguesque.game.entities.Door;
import otm.roguesque.game.entities.Entity;
import otm.roguesque.game.entities.Player;

/**
 * Kenttärendereri. Piirtää kentän.
 *
 * @author Jens Pitkänen
 */
public class DungeonRenderer {

    // Sprites
    private final String[] tileTypeNames;
    private final Image[] tileTypes;
    private final Image selectionImage;

    // Dungeon
    private int width;
    private int height;
    private Image[] tileImages;
    private Dungeon dungeon;

    private double offsetX;
    private double offsetY;

    /**
     * Luo uuden kenttärendererin.
     */
    public DungeonRenderer() {
        // Check the tiles in TileType.java, these should be in the same order
        // (This is also tested in TileGraphicTest.java)
        tileTypeNames = new String[]{
            "jar:/sprites/Floor.png",
            "jar:/sprites/Wall.png",
            "jar:/sprites/Corridor.png",
            "jar:/sprites/Ladder.png"
        };

        tileTypes = new Image[tileTypeNames.length];
        for (int i = 0; i < tileTypeNames.length; i++) {
            tileTypes[i] = SpriteLoader.loadImage(tileTypeNames[i]);
        }
        selectionImage = SpriteLoader.loadImage("jar:/sprites/Selection.png");
    }

    /**
     * Palauttaa "kameran x-koordinaatin," eli kuinka paljon näkyvä tilanne on
     * siirtynyt suhteessa kentän origoon vaakatasossa.
     *
     * @return Kameran x-siirtymä.
     */
    public double getOffsetX() {
        return offsetX;
    }

    /**
     * Palauttaa "kameran y-koordinaatin," eli kuinka paljon näkyvä tilanne on
     * siirtynyt suhteessa kentän origoon pystysuunnassa.
     *
     * @return Kameran y-siirtymä.
     */
    public double getOffsetY() {
        return offsetY;
    }

    /**
     * Lataa uuden kentän renderöijään.
     *
     * @param dungeon Uusi kenttä.
     */
    public void loadDungeon(Dungeon dungeon) {
        this.width = dungeon.getWidth();
        this.height = dungeon.getHeight();
        this.tileImages = new Image[width * height];
        this.dungeon = dungeon;
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

    /**
     * Piirtää kentän.
     *
     * @param ctx Käyttöliittymän piirtokonteksti.
     * @param tileSize Yhden ruudun leveys ja korkeus. Käytännössä aina 32,
     * mutta kaiken varalta tässä parametrina.
     * @param selectionX Valitun ruudun x-koordinaatti, tai -1 jos valintaa ei
     * ole.
     * @param selectionY Valitun ruudun y-koordinaatti, tai -1 jos valintaa ei
     * ole.
     */
    public void draw(GraphicsContext ctx, double tileSize, int selectionX, int selectionY) {
        Canvas canvas = ctx.getCanvas();
        clearScreen(ctx, canvas);
        if (dungeon == null) {
            return;
        }

        int tilesX = (int) (canvas.getWidth() / tileSize);
        int tilesY = (int) (canvas.getHeight() / tileSize);
        updateOffsets(tilesX, tilesY, tilesX - (int) (220 / tileSize), tilesY - (int) (100 / tileSize));
        drawMap(ctx, tileSize, tilesX, tilesY);
        drawEntities(ctx, tileSize);
        drawMapFog(ctx, tileSize, tilesX, tilesY);
        drawSelection(ctx, tileSize, selectionX - (int) offsetX, selectionY - (int) offsetY);
    }

    private void clearScreen(GraphicsContext ctx, Canvas canvas) {
        ctx.setFill(Color.BLACK);
        ctx.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
    }

    private void updateOffsets(int tilesX, int tilesY, int maxTilesX, int maxTilesY) {
        Player player = dungeon.getPlayer();
        offsetX = Math.max(0, Math.min(width - maxTilesX, player.getX() - tilesX / 2 + player.getOffsetX()));
        offsetY = Math.max(0, Math.min(height - maxTilesY, player.getY() - tilesY / 2 + player.getOffsetY()));
    }

    private void drawMap(GraphicsContext ctx, double tileSize, int tilesX, int tilesY) {
        Player player = dungeon.getPlayer();
        for (int y = (int) offsetY; y < Math.min((int) Math.ceil(offsetY + tilesY), height); y++) {
            for (int x = (int) offsetX; x < Math.min((int) Math.ceil(offsetX + tilesX), width); x++) {
                if (!player.isUncovered(x, y)) {
                    continue;
                }
                double drawX = (x - offsetX) * tileSize;
                double drawY = (y - offsetY) * tileSize;
                drawTile(ctx, x, y, drawX, drawY, tileSize);
            }
        }
    }

    private void drawMapFog(GraphicsContext ctx, double tileSize, int tilesX, int tilesY) {
        Player player = dungeon.getPlayer();
        for (int y = (int) offsetY; y < Math.min((int) Math.ceil(offsetY + tilesY), height); y++) {
            for (int x = (int) offsetX; x < Math.min((int) Math.ceil(offsetX + tilesX), width); x++) {
                if (!player.isUncovered(x, y)) {
                    continue;
                }
                double drawX = (x - offsetX) * tileSize;
                double drawY = (y - offsetY) * tileSize;
                double distanceFromPlayer = Math.min(player.getSightDistance(), Math.sqrt(Math.pow(player.getX() - x, 2) + Math.pow(player.getY() - y, 2)));
                double visibility = Math.pow(Math.min(1, 1 - (distanceFromPlayer - 2.4) / (player.getSightDistance() - 2.4)), 2);
                drawFog(ctx, !player.inLineOfSight(x, y) ? 0 : visibility, drawX, drawY, tileSize);
            }
        }
    }

    private void drawEntities(GraphicsContext ctx, double tileSize) {
        Player player = dungeon.getPlayer();
        for (Entity entity : dungeon.getEntities()) {
            int x = entity.getX();
            int y = entity.getY();
            if (player.isUncovered(x, y) && (player.inLineOfSight(x, y) || entity instanceof Door)) {
                double drawX = (x - offsetX) * tileSize;
                double drawY = (y - offsetY) * tileSize;
                drawEntity(ctx, tileSize, entity, drawX, drawY);
            }
        }
    }

    private void drawTile(GraphicsContext ctx, int x, int y, double drawX, double drawY, double tileSize) {
        if (tileImages[x + y * width] != null) {
            ctx.drawImage(tileImages[x + y * width], drawX, drawY, tileSize, tileSize);
        }
    }

    private void drawFog(GraphicsContext ctx, double visibility, double drawX, double drawY, double tileSize) {
        if (visibility == 1) {
            return;
        }
        ctx.setFill(new Color(0.0, 0.0, 0.0, Math.max(0, Math.min(0.5, 0.5 - visibility * 0.5))));
        ctx.fillRect(drawX, drawY, tileSize, tileSize);
    }

    private void drawSelection(GraphicsContext ctx, double tileSize, int selectionX, int selectionY) {
        Entity selectedEntity = dungeon.getPlayer().getLastEntityInteractedWith();
        if (selectedEntity != null) {
            ctx.drawImage(selectionImage,
                    (selectedEntity.getX() - offsetX + selectedEntity.getOffsetX()) * tileSize,
                    (selectedEntity.getY() - offsetY + selectedEntity.getOffsetY()) * tileSize,
                    tileSize, tileSize);
        } else if (selectionX >= 0 && selectionY >= 0) {
            ctx.drawImage(selectionImage,
                    selectionX * tileSize, selectionY * tileSize,
                    tileSize, tileSize);
        }
    }

    /**
     * Palauttaa urlin laatan kuvaan, tämä on tarkoitettu käytettäväksi
     * testeille.
     *
     * @param tileType Laatan tyyppi/
     * @return Url.
     */
    public String getTileImageName(TileType tileType) {
        return tileTypeNames[tileType.ordinal()];
    }

    private void drawEntity(GraphicsContext ctx, double tileSize, Entity entity, double x, double y) {
        x += entity.getOffsetX() * tileSize;
        y += entity.getOffsetY() * tileSize;
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
