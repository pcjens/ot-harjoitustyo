package otm.roguesque.ui.states;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import otm.roguesque.ui.Input;
import otm.roguesque.ui.RoguesqueApp;

/**
 * Intro-tila. Ruudulle tulee näkyviin Roguesque-otsikko, se siirtyy ylös, ja
 * siirrytään päävalikkoon.
 *
 * @author Jens Pitkänen
 */
public class IntroState implements GameState {

    private final float revealDuration = 1.25f;
    private final float postRevealWait = 0.25f;
    private float progress;

    @Override
    public void initialize() {
        progress = 0.0f;
    }

    @Override
    public void draw(GraphicsContext ctx, float deltaSeconds, boolean showDebugInfo) {
        Canvas canvas = ctx.getCanvas();
        ctx.setFill(Color.BLACK);
        ctx.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
        double xOffset = (canvas.getWidth() - 640) / 2;
        double yOffset = (canvas.getHeight() - 480) / 2;

        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_LOGO);
        float titleOffset = 0.0f;
        if (progress > revealDuration) {
            titleOffset = 40.0f - 40.0f * (postRevealWait - (progress - revealDuration)) / postRevealWait;
        }
        ctx.fillText("Roguesque", 180.0 + xOffset, 240.0 - titleOffset + yOffset);
        ctx.setFill(Color.BLACK);
        ctx.fillRect(140.0 + xOffset, yOffset, 300.0 * Math.max(0.0, revealDuration - progress), 480.0);
    }

    @Override
    public int update(Input input, float deltaSeconds, boolean showDebugInfo) {
        progress += deltaSeconds;
        if (progress >= revealDuration + postRevealWait) {
            return GameState.STATE_MAINMENU;
        }

        if (input.isPressed(Input.CONTROL_SKIP_INTRO)) {
            return GameState.STATE_INGAME;
        }
        return -1;
    }
}
