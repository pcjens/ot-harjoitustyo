package otm.roguesque.ui;

import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import otm.roguesque.entities.Dungeon;
import otm.roguesque.entities.Player;

public class RoguesqueApp extends Application {

    // Controls
    private static final KeyCode[] CONTROL_MOVE_UP = new KeyCode[]{
        KeyCode.W, KeyCode.UP, KeyCode.K
    };
    private static final KeyCode[] CONTROL_MOVE_LEFT = new KeyCode[]{
        KeyCode.A, KeyCode.LEFT, KeyCode.H
    };
    private static final KeyCode[] CONTROL_MOVE_DOWN = new KeyCode[]{
        KeyCode.S, KeyCode.DOWN, KeyCode.J
    };
    private static final KeyCode[] CONTROL_MOVE_RIGHT = new KeyCode[]{
        KeyCode.D, KeyCode.RIGHT, KeyCode.L
    };

    // UI
    private final BorderPane mainPanel;
    private final Scene mainScene;
    private final ArrayList<KeyCode> keysPressed = new ArrayList();
    private GraphicsContext ctx;
    private Canvas canvas;

    // Performance statistics
    private boolean showPerformanceDetails = false;
    private final float[] deltaSecondsHistory = new float[100];
    private int deltaSecondsHistoryCounter = 0;

    // Game specific stuff
    private final DungeonRenderer dungeonRenderer;
    private final Dungeon dungeon;
    private final Player player;

    public RoguesqueApp() {
        mainPanel = new BorderPane();
        mainScene = new Scene(mainPanel, 640.0, 480.0);
        dungeonRenderer = new DungeonRenderer();
        dungeon = new Dungeon(10, 10);
        dungeonRenderer.loadDungeon(dungeon);
        player = new Player();
        dungeon.spawnEntity(player, 2, 2);
    }

    private void drawGame(float deltaSeconds) {
        // Clear screen
        ctx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Update performance stats
        deltaSecondsHistory[deltaSecondsHistoryCounter++] = deltaSeconds;
        if (deltaSecondsHistoryCounter == deltaSecondsHistory.length) {
            deltaSecondsHistoryCounter = 0;
        }
        float averageDeltaSeconds = 0;
        for (float f : deltaSecondsHistory) {
            averageDeltaSeconds += f;
        }
        averageDeltaSeconds /= deltaSecondsHistory.length;

        // Show performance stats
        if (showPerformanceDetails) {
            ctx.fillText("Average frame time: " + (int) (averageDeltaSeconds * 1000.0) + " ms", 10.0, 20.0);
        }

        // Render dungeon
        dungeonRenderer.draw(ctx);
    }

    /* This is separated from drawGame just in case we want to switch to a
     * fixed timestemp sometime in the future */
    private void update(float deltaSeconds) {
        if (keysPressed.contains(KeyCode.F3)) {
            showPerformanceDetails = !showPerformanceDetails;
        }

        boolean progressRound = movePlayer();

        if (progressRound) {
            dungeon.processRound();
            dungeon.cleanupDeadEntities();
        }
    }

    private boolean movePlayer() {
        if (isPressed(CONTROL_MOVE_UP)) {
            player.move(0, -1);
        } else if (isPressed(CONTROL_MOVE_LEFT)) {
            player.move(-1, 0);
        } else if (isPressed(CONTROL_MOVE_DOWN)) {
            player.move(0, 1);
        } else if (isPressed(CONTROL_MOVE_RIGHT)) {
            player.move(1, 0);
        } else {
            return false;
        }
        return true;
    }

    private boolean isPressed(KeyCode[] codes) {
        for (KeyCode kc : codes) {
            if (keysPressed.contains(kc)) {
                return true;
            }
        }
        return false;
    }

    private final AnimationTimer mainLoop = new AnimationTimer() {
        long lastTime = 0;

        /* The currentTime argument doesn't start at 0, so lastTime is
             * wrong during the first frame, causing a big spike in delta time
             * at the very start. I'd rather just skip a frame to avoid that.
         */
        boolean firstRun = true;

        @Override
        public void handle(long currentTime /* note: timestamp in nanoseconds */) {
            float deltaSeconds = (float) ((currentTime - lastTime) / 1_000_000_000.0);
            lastTime = currentTime;
            if (firstRun) {
                firstRun = false;
                return;
            }

            update(deltaSeconds);
            drawGame(deltaSeconds);
            keysPressed.clear();
        }
    };

    @Override
    public void start(Stage stage) {
        canvas = new Canvas();
        ctx = canvas.getGraphicsContext2D();
        mainPanel.setCenter(canvas);
        canvas.widthProperty().bind(mainScene.widthProperty());
        canvas.widthProperty().addListener((observable, oldValue, newValue) -> {
            drawGame(0.0001f);
        });
        canvas.heightProperty().bind(mainScene.heightProperty());
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> {
            drawGame(0.0001f);
        });

        mainScene.setOnKeyPressed((event) -> {
            keysPressed.add(event.getCode());
        });

        mainLoop.start();

        stage.setScene(mainScene);
        stage.setTitle("Roguesque");
        stage.show();
    }
}
