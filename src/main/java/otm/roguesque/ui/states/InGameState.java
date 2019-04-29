package otm.roguesque.ui.states;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javax.swing.JOptionPane;
import otm.roguesque.game.GlobalRandom;
import otm.roguesque.game.dungeon.Dungeon;
import otm.roguesque.game.dungeon.TileType;
import otm.roguesque.game.dungeon.replay.PlayerAction;
import otm.roguesque.game.dungeon.replay.Replay;
import otm.roguesque.game.entities.Entity;
import otm.roguesque.game.entities.Player;
import otm.roguesque.ui.Button;
import otm.roguesque.ui.DungeonRenderer;
import otm.roguesque.ui.Input;
import otm.roguesque.ui.RenderingUtil;
import otm.roguesque.ui.RoguesqueApp;

/**
 * "Itse peli" -tila. Ruudulle piirtyy kenttä ja pelitilanne, ja peliä voi
 * pelata tässä tilassa.
 *
 * @author Jens Pitkänen
 */
public class InGameState implements GameState {

    protected static Replay latestReplay;

    protected Dungeon dungeon;

    private final DungeonRenderer dungeonRenderer;
    private Player player;

    private String statusLine;
    private String descriptionText;
    private int descriptionBoxLines;
    private float descriptionBoxFadeAway;
    private final float descriptionBoxFadeAwayDuration = 0.15f;

    private final double tileSize = 32.0;
    private int selectionX;
    private int selectionY;

    private final Button nextLevelButton = new Button(new KeyCode[]{KeyCode.M}, "Move to the next floor?", 0, 0, 290, 45, 0);
    private final Button seedCopyButton = new Button(new KeyCode[]{KeyCode.C}, "Copy seed", 220, 60, 120, 35, 0, 9);
    private final Button dungeonRegenerateButton = new Button(new KeyCode[]{KeyCode.R}, "Regenerate level", 360, 60, 195, 35, 0, 9);
    private final Button saveReplayButton = new Button(new KeyCode[]{KeyCode.V}, "Save replay", 360, 110, 195, 35, 40, 9);

    /**
     * Luo uuden instanssin tästä tilasta. Luodaan kerran peliä käynnistäessä.
     */
    public InGameState() {
        dungeonRenderer = new DungeonRenderer();
    }

    @Override
    public void initialize() {
        short initialSeed = (short) GlobalRandom.get().nextInt(0xFFFF);
        initializeDungeon(initialSeed);
    }

    protected void initializeDungeon(short seed) {
        dungeon = new Dungeon(seed, 1);
        player = dungeon.getPlayer();
        reloadUI();
    }

    protected void reloadUI() {
        dungeonRenderer.loadDungeon(dungeon);
        player.resetUncovered();
        player.recalculateLineOfSight(true);
        statusLine = "Loading...";
        descriptionText = null;
        descriptionBoxFadeAway = 0;
        selectionX = -1;
        selectionY = -1;
    }

    @Override
    public void draw(GraphicsContext ctx, float deltaSeconds, boolean showDebugInfo) {
        dungeonRenderer.draw(ctx, tileSize, selectionX, selectionY);
        for (Entity e : dungeon.getEntities()) {
            e.drawNotifications(ctx, dungeonRenderer.getOffsetX(), dungeonRenderer.getOffsetY(), tileSize, deltaSeconds);
        }

        Canvas canvas = ctx.getCanvas();
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        RenderingUtil.drawBox(ctx, 20.0, height - 80.0, width - 40.0, 60.0, false);

        drawStatusLine(ctx, height);
        if (descriptionText != null || descriptionBoxFadeAway > 0) {
            drawDescriptionBox(ctx, deltaSeconds, width, height);
        }
        if (dungeon.canFinish()) {
            drawFinishButton(ctx, (int) width, (int) height);
        }
        if (showDebugInfo) {
            drawDebugInformation(ctx);
        }
    }

    private void drawStatusLine(GraphicsContext ctx, double height) {
        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_UI);
        ctx.fillText(statusLine, 40.0, height - 42.5);
    }

    private void drawDescriptionBox(GraphicsContext ctx, float deltaSeconds, double width, double height) {
        double boxHeight = descriptionBoxLines * 28.0 + 14.0;
        if (descriptionBoxFadeAway == -1) {
            RenderingUtil.drawBox(ctx, width - 200.0, height - (100.0 + boxHeight), 180.0, boxHeight, false);
            ctx.fillText(descriptionText, width - 190.0, height - (70.0 + boxHeight));
        } else if (descriptionBoxFadeAway > 0) {
            descriptionBoxFadeAway -= deltaSeconds;
            float fadeOut = descriptionBoxFadeAway / descriptionBoxFadeAwayDuration;
            float fadeIn = 1.0f - fadeOut;
            RenderingUtil.drawBox(ctx, width - 200.0 + 100.0 * fadeIn,
                    height - (100.0 + boxHeight) + boxHeight / 2.0 * fadeIn,
                    180.0 * fadeOut,
                    boxHeight * fadeOut, false);
        }
    }

    private void drawFinishButton(GraphicsContext ctx, int width, int height) {
        nextLevelButton.setX((width - nextLevelButton.getWidth()) / 2);
        nextLevelButton.setY((height - nextLevelButton.getHeight()) / 2);
        nextLevelButton.draw(ctx);
    }

    private void drawDebugInformation(GraphicsContext ctx) {
        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_UI);
        int y = 80;
        seedCopyButton.draw(ctx);
        dungeonRegenerateButton.draw(ctx);
        saveReplayButton.draw(ctx);
        ctx.fillText("Seed: " + GlobalRandom.getSeed(), 10, y += 20);
        ctx.fillText("Player coordinates: " + player.getX() + ", " + player.getY(), 10, y += 20);
        ctx.fillText("Room dimensions: " + dungeon.getWidth() + ", " + dungeon.getHeight(), 10, y += 20);
        ctx.fillText("Entities: " + dungeon.getEntities().size(), 10, y += 20);
    }

    @Override
    public int update(Input input, float deltaSeconds, boolean showDebugInfo) {
        if (updateGame(input)) {
            latestReplay = dungeon.getReplay();
            return GameState.STATE_GAMEOVER;
        }

        selectTile(input);
        updateTexts();

        if (showDebugInfo) {
            updateDebugButtons(input);
        }

        return -1;
    }

    /**
     * Päivittää pelin tilan perustuen näppäimiin ja klikkauksiin joita tällä
     * päivityksellä tapahtui. ReplayGameState overridee tämän.
     *
     * @see
     * otm.roguesque.ui.states.ReplayGameState#updateGame(otm.roguesque.ui.Input)
     *
     * @param input Input-olio.
     * @return Loppuiko peli gameoveriin?
     */
    protected boolean updateGame(Input input) {
        boolean shouldProcessRound = movePlayer(input);

        if (shouldProcessRound && processRound()) {
            return true;
        }

        if (dungeon.canFinish()) {
            nextLevelButton.update(input);
            if (nextLevelButton.isClicked()) {
                dungeon.runPlayerAction(PlayerAction.NextLevel);
                reloadUI();
            }
        }
        return false;
    }

    private void saveReplay(String fileName) {
        try {
            dungeon.getReplay().saveTo(new File(fileName));
        } catch (IOException ex) {
        }
    }

    private boolean movePlayer(Input input) {
        if (input.isPressed(Input.CONTROL_MOVE_UP)) {
            dungeon.runPlayerAction(PlayerAction.MoveUp);
        } else if (input.isPressed(Input.CONTROL_MOVE_LEFT)) {
            dungeon.runPlayerAction(PlayerAction.MoveLeft);
        } else if (input.isPressed(Input.CONTROL_MOVE_DOWN)) {
            dungeon.runPlayerAction(PlayerAction.MoveDown);
        } else if (input.isPressed(Input.CONTROL_MOVE_RIGHT)) {
            dungeon.runPlayerAction(PlayerAction.MoveRight);
        } else {
            return false;
        }
        return true;
    }

    protected boolean processRound() {
        dungeon.processRound();
        if (player.isDead()) {
            return true;
        }
        dungeon.cleanupDeadEntities();
        player.recalculateLineOfSight(false);
        // Player moved -> reset selection, this feels more "right"
        // according to playtesters.
        selectionX = -1;
        selectionY = -1;
        return false;
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

    private void updateDebugButtons(Input input) {
        seedCopyButton.update(input);
        dungeonRegenerateButton.update(input);
        saveReplayButton.update(input);

        String currentSeed = Long.toString(GlobalRandom.getSeed());
        if (seedCopyButton.isClicked()) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(currentSeed), null);
        }
        if (dungeonRegenerateButton.isClicked()) {
            // TODO: Replace with something that's not Swing?
            // Not a high priority though, this is a debugging feature
            String result = JOptionPane.showInputDialog(null, "Please enter a new seed:", currentSeed);
            try {
                initializeDungeon(Short.parseShort(result));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "The seed was not a number.", "Dungeon not regenerated", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (saveReplayButton.isClicked()) {
            saveReplay(JOptionPane.showInputDialog("Save file name:", "roguesque-replay.rgsq"));
        }
    }
}
