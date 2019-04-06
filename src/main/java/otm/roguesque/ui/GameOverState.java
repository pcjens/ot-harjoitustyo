package otm.roguesque.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameOverState implements GameState {

    @Override
    public void initialize() {
    }

    @Override
    public void draw(GraphicsContext ctx, float deltaSeconds) {
        Canvas canvas = ctx.getCanvas();
        ctx.setFill(Color.BLACK);
        ctx.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());

        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_LOGO_SMALL);
        ctx.fillText("You are dead.", 180.0, 220.0);

        ctx.setFont(RoguesqueApp.FONT_UI);
        ctx.fillText("Re[p]lay", 180.0, 300.0);
        ctx.fillText("[Q]uit", 300.0, 300.0);
    }

    @Override
    public int update(Input input, float deltaSeconds) {
        if (input.isPressed(Input.CONTROL_PLAY)) {
            return GameState.STATE_INGAME;
        }
        if (input.isPressed(Input.CONTROL_QUIT)) {
            return GameState.STATE_QUIT;
        }
        return -1;
    }
}
