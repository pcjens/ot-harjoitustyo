package otm.roguesque.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class MainMenuState implements GameState {

    private static final KeyCode[] CONTROL_PLAY = new KeyCode[]{KeyCode.P};
    private static final KeyCode[] CONTROL_QUIT = new KeyCode[]{KeyCode.Q};

    @Override
    public void draw(GraphicsContext ctx, float deltaSeconds) {
        Canvas canvas = ctx.getCanvas();
        ctx.setFill(Color.BLACK);
        ctx.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_LOGO);
        ctx.fillText("Roguesque", 180.0, 200.0);

        ctx.setFont(RoguesqueApp.FONT_UI);
        ctx.fillText("[P]lay", 180.0, 300.0);
        ctx.fillText("[Q]uit", 280.0, 300.0);
    }

    @Override
    public int update(Input input, float deltaSeconds) {
        if (input.isPressed(CONTROL_PLAY)) {
            return GameState.STATE_INGAME;
        }
        if (input.isPressed(CONTROL_QUIT)) {
            return GameState.STATE_QUIT;
        }
        return -1;
    }
}
