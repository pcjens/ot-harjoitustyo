package otm.roguesque.ui.states;

import java.io.IOException;
import java.nio.file.Paths;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javax.swing.JOptionPane;
import otm.roguesque.ui.Button;
import otm.roguesque.ui.Input;
import otm.roguesque.ui.RoguesqueApp;

/**
 * Peli loppui (kuolemaan) -pelitila. Ruudulla lukee "you are dead," ja
 * pelaajalle annetaan mahdollisuus pelata uudestaan tai sulkea peli.
 *
 * @author Jens Pitkänen
 */
public class GameOverState implements GameState {

    private Button mainmenuButton = new Button(new KeyCode[]{KeyCode.M}, "Main menu", 180, 280, 140, 45, 0);
    private Button saveReplayButton = new Button(new KeyCode[]{KeyCode.S}, "Save replay", 180, 350, 160, 45, 0);
    private Button quitButton = new Button(new KeyCode[]{KeyCode.Q}, "Quit", 340, 280, 80, 45, 0);

    @Override
    public void initialize() {
    }

    @Override
    public void draw(GraphicsContext ctx, float deltaSeconds, boolean showDebugInfo) {
        Canvas canvas = ctx.getCanvas();
        ctx.setFill(Color.BLACK);
        ctx.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());

        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_LOGO_SMALL);
        ctx.fillText("You are dead.", 180.0, 220.0);

        mainmenuButton.draw(ctx);
        quitButton.draw(ctx);
        if (showDebugInfo) {
            saveReplayButton.draw(ctx);
        }
    }

    @Override
    public int update(Input input, float deltaSeconds, boolean showDebugInfo) {
        mainmenuButton.update(input);
        quitButton.update(input);
        if (showDebugInfo) {
            saveReplayButton.update(input);
        }

        if (mainmenuButton.isClicked()) {
            return GameState.STATE_MAINMENU;
        }
        if (quitButton.isClicked()) {
            return GameState.STATE_QUIT;
        }
        if (showDebugInfo && saveReplayButton.isClicked()) {
            saveReplay();
        }
        return -1;
    }

    private void saveReplay() {
        try {
            InGameState.latestReplay.saveTo(Paths.get(System.getProperty("user.dir"),
                    JOptionPane.showInputDialog("Save file name:", "roguesque-replay.rgsq")));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Couldn't save replay. Are you lacking permissions?", "Replay not saved", JOptionPane.WARNING_MESSAGE);
        }
    }
}
