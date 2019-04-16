package otm.roguesque.ui;

import java.util.Random;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import otm.roguesque.entities.Dungeon;
import otm.roguesque.entities.Entity;
import otm.roguesque.entities.Player;
import otm.roguesque.entities.TileType;

public class InGameState implements GameState {

    private final Random rand;
    private final DungeonRenderer dungeonRenderer;
    private Dungeon dungeon;
    private Player player;

    private String statusLine;
    private String descriptionText;
    private int descriptionBoxLines;
    private float descriptionBoxFadeAway;
    private final float descriptionBoxFadeAwayDuration = 0.15f;

    private int selectionX = -1;
    private int selectionY = -1;
    private double tileSize = 32.0;

    private final Button nextLevelButton;

    public InGameState() {
        rand = new Random();
        dungeonRenderer = new DungeonRenderer();
        nextLevelButton = new Button(0, 0, 290, 45);
    }

    @Override
    public void initialize() {
        player = new Player();
        regenerateDungeon(1);
    }

    private void regenerateDungeon(int level) {
        dungeon = new Dungeon(level, rand.nextInt());
        dungeonRenderer.loadDungeon(dungeon);
        dungeon.spawnEntity(player, dungeon.getPlayerSpawnX(), dungeon.getPlayerSpawnY());
        statusLine = "Loading...";
        descriptionText = null;
    }

    @Override
    public void draw(GraphicsContext ctx, float deltaSeconds) {
        dungeonRenderer.draw(ctx, dungeon, tileSize, selectionX, selectionY);

        Canvas canvas = ctx.getCanvas();
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        drawBox(ctx, 20.0, height - 80.0, width - 40.0, 60.0, false);

        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_UI);
        ctx.fillText(statusLine, 40.0, height - 42.5);

        if (descriptionText != null || descriptionBoxFadeAway > 0) {
            drawDescriptionBox(ctx, deltaSeconds, width, height);
        }

        if (dungeon.canFinish()) {
            drawFinishButton(ctx, (int) width, (int) height);
        }
    }

    private void drawFinishButton(GraphicsContext ctx, int width, int height) {
        nextLevelButton.x = (width - nextLevelButton.width) / 2;
        nextLevelButton.y = (height - nextLevelButton.height) / 2;
        drawBox(ctx, nextLevelButton.x, nextLevelButton.y,
                nextLevelButton.width, nextLevelButton.height, nextLevelButton.hovered);
        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_UI);
        ctx.fillText("Move to the next floor?", nextLevelButton.x + 15, nextLevelButton.y + 30);
    }

    private void drawDescriptionBox(GraphicsContext ctx, float deltaSeconds, double width, double height) {
        double boxHeight = descriptionBoxLines * 28.0 + 14.0;
        if (descriptionBoxFadeAway == -1) {
            drawBox(ctx, width - 200.0, height - (100.0 + boxHeight), 180.0, boxHeight, false);
            ctx.fillText(descriptionText, width - 190.0, height - (70.0 + boxHeight));
        } else if (descriptionBoxFadeAway > 0) {
            descriptionBoxFadeAway -= deltaSeconds;
            float fadeOut = descriptionBoxFadeAway / descriptionBoxFadeAwayDuration;
            float fadeIn = 1.0f - fadeOut;
            drawBox(ctx, width - 200.0 + 100.0 * fadeIn,
                    height - (100.0 + boxHeight) + boxHeight / 2.0 * fadeIn,
                    180.0 * fadeOut,
                    boxHeight * fadeOut, false);
        }
    }

    private void drawBox(GraphicsContext ctx, double x, double y, double w, double h, boolean highlight) {
        ctx.setFill(Color.WHITE);
        ctx.fillRect(x, y, w, h);
        if (highlight) {
            ctx.setFill(Color.gray(0.3));
        } else {
            ctx.setFill(Color.BLACK);
        }
        ctx.fillRect(x + 4.0, y + 4.0, w - 8.0, h - 8.0);
        ctx.setFill(Color.WHITE);
    }

    @Override
    public int update(Input input, float deltaSeconds) {
        boolean progressRound = movePlayer(input);

        if (progressRound) {
            dungeon.processRound();
            if (dungeon.getPlayer().isDead()) {
                return GameState.STATE_GAMEOVER;
            }
            dungeon.cleanupDeadEntities();
        }

        if (dungeon.canFinish()) {
            nextLevelButton.update(input);
            if (nextLevelButton.clicked) {
                regenerateDungeon(dungeon.getLevel() + 1);
            }
        }

        selectTile(input);
        updateTexts();
        return -1;
    }

    private boolean movePlayer(Input input) {
        if (input.isPressed(Input.CONTROL_MOVE_UP)) {
            player.move(0, -1);
        } else if (input.isPressed(Input.CONTROL_MOVE_LEFT)) {
            player.move(-1, 0);
        } else if (input.isPressed(Input.CONTROL_MOVE_DOWN)) {
            player.move(0, 1);
        } else if (input.isPressed(Input.CONTROL_MOVE_RIGHT)) {
            player.move(1, 0);
        } else {
            return false;
        }
        return true;
    }

    private void selectTile(Input input) {
        if (player.getLastEntityInteractedWith() != null) {
            selectionX = -1;
            selectionY = -1;
        }

        if (input.clicked(MouseButton.SECONDARY)) {
            player.resetLastEntityInteractedWith();
            selectionX = -1;
            selectionY = -1;
        }

        if (input.clicked(MouseButton.PRIMARY)) {
            player.resetLastEntityInteractedWith();
            selectionX = (int) (input.getMouseX() / tileSize) + dungeonRenderer.getOffsetX();
            selectionY = (int) (input.getMouseY() / tileSize) + dungeonRenderer.getOffsetY();
        }
    }

    private void updateTexts() {
        statusLine = String.format("HP: %d/%d   ATK: %d   DEF: %d", player.getHealth(), player.getMaxHealth(), player.getAttack(), player.getDefense());
        descriptionText = player.getExaminationText();
        if (descriptionText == null && selectionX >= 0 && selectionY >= 0) {
            descriptionText = getDescriptionFromSelection();
        }
        if (descriptionText != null) {
            descriptionBoxFadeAway = -1.0f;
            descriptionBoxLines = descriptionText.split("\n").length;
        } else if (descriptionBoxFadeAway == -1) {
            descriptionBoxFadeAway = descriptionBoxFadeAwayDuration;
        }
    }

    private String getDescriptionFromSelection() {
        Entity e = dungeon.getEntityAt(selectionX, selectionY);
        if (e != null) {
            return e.getDescription();
        } else {
            TileType tile = dungeon.getTileAt(selectionX, selectionY);
            if (tile != null) {
                return tile.getDescription();
            }
        }
        return null;
    }
}
