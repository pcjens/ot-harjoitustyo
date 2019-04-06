package otm.roguesque.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

public class MainMenuState implements GameState {

    private static final double PLAY_X = 180.0;
    private static final double PLAY_Y = 280.0;
    private static final double PLAY_WIDTH = 75.0;
    private static final double PLAY_HEIGHT = 30.0;
    private static final double QUIT_X = 280.0;
    private static final double QUIT_Y = 280.0;
    private static final double QUIT_WIDTH = 75.0;
    private static final double QUIT_HEIGHT = 30.0;

    private boolean hoveringPlay = false;
    private boolean hoveringQuit = false;

    @Override
    public void initialize() {
    }

    @Override
    public void draw(GraphicsContext ctx, float deltaSeconds) {
        Canvas canvas = ctx.getCanvas();
        ctx.setFill(Color.BLACK);
        ctx.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_LOGO);
        ctx.fillText("Roguesque", 180.0, 200.0);

        ctx.setFont(RoguesqueApp.FONT_UI);
        highlightIf(ctx, hoveringPlay);
        ctx.fillText("[P]lay", PLAY_X, PLAY_Y + 20.0);
        highlightIf(ctx, hoveringQuit);
        ctx.fillText("[Q]uit", QUIT_X, QUIT_Y + 20.0);
    }

    private void highlightIf(GraphicsContext ctx, boolean b) {
        if (b) {
            ctx.setFill(Color.YELLOW);
        } else {
            ctx.setFill(Color.WHITE);
        }
    }

    @Override
    public int update(Input input, float deltaSeconds) {
        hoveringPlay = input.containsMouse(PLAY_X, PLAY_Y, PLAY_WIDTH, PLAY_HEIGHT);
        hoveringQuit = input.containsMouse(QUIT_X, QUIT_Y, QUIT_WIDTH, QUIT_HEIGHT);

        if (input.isPressed(Input.CONTROL_PLAY) || (hoveringPlay && input.clicked(MouseButton.PRIMARY))) {
            return GameState.STATE_INGAME;
        }
        if (input.isPressed(Input.CONTROL_QUIT) || (hoveringQuit && input.clicked(MouseButton.PRIMARY))) {
            return GameState.STATE_QUIT;
        }

        return -1;
    }
}
