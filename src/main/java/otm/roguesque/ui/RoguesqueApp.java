package otm.roguesque.ui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import otm.roguesque.Properties;
import otm.roguesque.ui.states.GameOverState;
import otm.roguesque.ui.states.GameState;
import otm.roguesque.ui.states.InGameState;
import otm.roguesque.ui.states.IntroState;
import otm.roguesque.ui.states.LeaderboardSubmissionState;
import otm.roguesque.ui.states.MainMenuState;
import otm.roguesque.ui.states.ReplayGameState;

/**
 * Roguesque-pelin käyttöliittymä sekä ydin.
 *
 * @author Jens Pitkänen
 */
public class RoguesqueApp extends Application {

    /**
     * Logo-fontti.
     */
    public static final Font FONT_LOGO = Font.loadFont(RoguesqueApp.class.getResourceAsStream("/fonts/vt323/VT323-Regular.ttf"), 70.0);
    /**
     * Pienempi logo-fontti.
     */
    public static final Font FONT_LOGO_SMALL = Font.loadFont(RoguesqueApp.class.getResourceAsStream("/fonts/vt323/VT323-Regular.ttf"), 50.0);
    /**
     * Normaalitekstiä suurempi fontti.
     */
    public static final Font FONT_UI_LARGE = Font.loadFont(RoguesqueApp.class.getResourceAsStream("/fonts/vt323/VT323-Regular.ttf"), 36.0);
    /**
     * Normaalitekstin fontti.
     */
    public static final Font FONT_UI = Font.loadFont(RoguesqueApp.class.getResourceAsStream("/fonts/vt323/VT323-Regular.ttf"), 28.0);
    /**
     * Pienen notifikaatiotekstin fontti.
     */
    public static final Font FONT_NOTIFICATION = Font.loadFont(RoguesqueApp.class.getResourceAsStream("/fonts/vt323/VT323-Regular.ttf"), 14.0);

    // UI
    private final BorderPane mainPanel;
    private final Scene mainScene;
    private Input input;
    private GraphicsContext ctx;
    private Canvas canvas;

    // Performance statistics
    private boolean showDebugInfo = false;
    private final float[] deltaSecondsHistory = new float[100];
    private int deltaSecondsHistoryCounter = 0;

    // Game state stuff
    private final GameState[] gameStates;
    private int currentGameStateIndex;

    /**
     * Luo uuden käyttöliittymäinstanssin.
     */
    public RoguesqueApp() {
        mainPanel = new BorderPane();
        mainScene = new Scene(mainPanel, 640.0, 480.0);
        input = new Input();

        gameStates = new GameState[GameState.STATE_COUNT];
        gameStates[GameState.STATE_INTRO] = new IntroState();
        gameStates[GameState.STATE_MAINMENU] = new MainMenuState();
        gameStates[GameState.STATE_INGAME] = new InGameState();
        gameStates[GameState.STATE_REPLAY] = new ReplayGameState();
        gameStates[GameState.STATE_GAMEOVER] = new GameOverState();
        gameStates[GameState.STATE_LEADERBOARD_SUBMISSION] = new LeaderboardSubmissionState();

        currentGameStateIndex = GameState.STATE_INTRO;
        gameStates[currentGameStateIndex].initialize();
    }

    private void drawGame(float deltaSeconds) {
        ctx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gameStates[currentGameStateIndex].draw(ctx, deltaSeconds, showDebugInfo);

        float averageDeltaSeconds = updatePerformanceStats(deltaSeconds);
        if (showDebugInfo) {
            ctx.fillText("Game version: " + Properties.getVersion(), 10, 20);
            ctx.fillText("Average frame time: " + (int) (averageDeltaSeconds * 1000.0) + " ms", 10.0, 40.0);
        }
    }

    private float updatePerformanceStats(float deltaSeconds) {
        deltaSecondsHistory[deltaSecondsHistoryCounter++] = deltaSeconds;
        if (deltaSecondsHistoryCounter == deltaSecondsHistory.length) {
            deltaSecondsHistoryCounter = 0;
        }
        float averageDeltaSeconds = 0;
        for (float f : deltaSecondsHistory) {
            averageDeltaSeconds += f;
        }
        averageDeltaSeconds /= deltaSecondsHistory.length;
        return averageDeltaSeconds;
    }

    /* This is separated from drawGame just in case we want to switch to a
     * fixed timestemp sometime in the future */
    private void update(float deltaSeconds) {
        if (input.isPressed(Input.CONTROL_TOGGLE_DEBUG_INFO)) {
            showDebugInfo = !showDebugInfo;
        }

        int newState = gameStates[currentGameStateIndex].update(input, deltaSeconds, showDebugInfo);
        if (newState == GameState.STATE_QUIT) {
            Platform.exit();
        } else if (newState >= 0 && newState < GameState.STATE_COUNT) {
            currentGameStateIndex = newState;
            showDebugInfo = false;
            gameStates[currentGameStateIndex].initialize();
        }
    }

    private final AnimationTimer mainLoop = new AnimationTimer() {
        long lastTime = 0;

        // The currentTime argument doesn't start at 0, so lastTime is
        // wrong during the first frame, causing a big spike in delta time
        // at the very start. I'd rather just skip a frame to avoid that.
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
            input.clearInputs();
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

        setupEvents(mainScene);

        setupStage(stage);
        mainLoop.start();
        stage.show();
    }

    private void setupStage(Stage stage) {
        stage.setScene(mainScene);
        stage.setTitle("Roguesque");
        stage.setMinWidth(640);
        stage.setMinHeight(480);
    }

    private void setupEvents(Scene scene) {
        mainScene.setOnKeyTyped((event) -> {
            input.addTypedKey(event.getCharacter());
        });
        mainScene.setOnKeyPressed((event) -> {
            input.addPressedKey(event.getCode());
        });
        mainScene.setOnMouseMoved((event) -> {
            input.setMousePosition(event.getSceneX(), event.getSceneY());
        });
        mainScene.setOnMouseClicked((event) -> {
            input.fireClick(event.getButton());
        });
    }
}
