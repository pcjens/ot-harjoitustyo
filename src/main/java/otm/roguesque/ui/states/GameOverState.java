package otm.roguesque.ui.states;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import otm.roguesque.ui.Button;
import otm.roguesque.ui.Input;
import otm.roguesque.ui.RoguesqueApp;

public class GameOverState implements GameState {

    private Button replayButton = new Button("Replay", 180, 280, 100, 45, 20);
    private Button quitButton = new Button("Quit", 300, 280, 80, 45, 0);

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

        replayButton.draw(ctx);
        quitButton.draw(ctx);
    }

    @Override
    public int update(Input input, float deltaSeconds) {
        replayButton.update(input);
        quitButton.update(input);

        if (input.isPressed(Input.CONTROL_PLAY) || replayButton.isClicked()) {
            return GameState.STATE_INGAME;
        }
        if (input.isPressed(Input.CONTROL_QUIT) || quitButton.isClicked()) {
            return GameState.STATE_QUIT;
        }
        return -1;
    }
}
