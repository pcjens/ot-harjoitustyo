package otm.roguesque.ui.states;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import otm.roguesque.ui.Button;
import otm.roguesque.ui.Input;
import otm.roguesque.ui.RoguesqueApp;

public class MainMenuState implements GameState {

    private final Button playButton = new Button("Play", 180, 280, 80, 45, 0);
    private final Button quitButton = new Button("Quit", 280, 280, 80, 45, 0);

    @Override
    public void initialize() {
    }

    @Override
    public void draw(GraphicsContext ctx, float deltaSeconds, boolean showDebugInfo) {
        Canvas canvas = ctx.getCanvas();
        ctx.setFill(Color.BLACK);
        ctx.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_LOGO);
        ctx.fillText("Roguesque", 180.0, 200.0);

        playButton.draw(ctx);
        quitButton.draw(ctx);
    }

    @Override
    public int update(Input input, float deltaSeconds, boolean showDebugInfo) {
        playButton.update(input);
        quitButton.update(input);

        if (input.isPressed(Input.CONTROL_PLAY) || playButton.isClicked()) {
            return GameState.STATE_INGAME;
        }
        if (input.isPressed(Input.CONTROL_QUIT) || quitButton.isClicked()) {
            return GameState.STATE_QUIT;
        }

        return -1;
    }
}
