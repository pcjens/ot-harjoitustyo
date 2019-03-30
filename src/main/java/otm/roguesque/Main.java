package otm.roguesque;

import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    // UI
    private final BorderPane mainPanel;
    private final Scene mainScene;
    private final ArrayList<KeyCode> keysTyped = new ArrayList();

    // Performance statistics
    private boolean showPerformanceDetails = true;
    private final float[] deltaSecondsHistory = new float[100];
    private int deltaSecondsHistoryCounter = 0;

    public Main() {
        mainPanel = new BorderPane();
        mainScene = new Scene(mainPanel, 640.0, 480.0);
    }

    private void drawGame(GraphicsContext ctx, float deltaSeconds) {
        // Clear screen
        Canvas canvas = ctx.getCanvas();
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
    }

    /* This is separated from drawGame just in case we want to switch to a 
     * fixed timestemp sometime in the future */
    private void update(float deltaSeconds) {
        if (keysTyped.contains(KeyCode.F3)) {
            showPerformanceDetails = !showPerformanceDetails;
        }
    }

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas();
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        mainPanel.setCenter(canvas);
        canvas.widthProperty().bind(mainScene.widthProperty());
        canvas.widthProperty().addListener((observable, oldValue, newValue) -> {
            drawGame(ctx, 0.0001f);
        });
        canvas.heightProperty().bind(mainScene.heightProperty());
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> {
            drawGame(ctx, 0.0001f);
        });

        mainScene.setOnKeyPressed((event) -> {
            keysTyped.add(event.getCode());
        });

        new AnimationTimer() {
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
                drawGame(ctx, deltaSeconds);
                keysTyped.clear();
            }
        }.start();

        stage.setScene(mainScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(Main.class);
    }
}
