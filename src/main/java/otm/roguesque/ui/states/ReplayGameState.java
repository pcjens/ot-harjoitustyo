package otm.roguesque.ui.states;

import java.io.File;
import java.io.IOException;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javax.swing.JOptionPane;
import otm.roguesque.game.dungeon.replay.PlayerAction;
import otm.roguesque.game.dungeon.replay.Replay;
import otm.roguesque.ui.Button;
import otm.roguesque.ui.Input;

/**
 * InGameState, paitsi että tämä perustuu Replay:n pelaajan oikeiden
 * napinpainalluksien sijaan.
 *
 * @see otm.roguesque.game.dungeon.replay.Replay
 * @author Jens Pitkänen
 */
public class ReplayGameState extends InGameState {

    private Replay replay;
    private boolean failedToLoad;
    private boolean finished;

    private final int actionCooldown = 10;
    private int actionTime = 0;

    private final Button returnButton = new Button(new KeyCode[]{KeyCode.R}, "Return to main menu", 200, 300, 250, 45, 0);

    @Override
    public void initialize() {
        failedToLoad = false;
        finished = false;
        replay = null;

        try {
            replay = new Replay(new File(JOptionPane.showInputDialog("Save file to load?", "roguesque-replay.rgsq")));
            short initialSeed = replay.getSeed();
            initializeDungeon(initialSeed);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "No such file.", "Error", JOptionPane.ERROR_MESSAGE);
            failedToLoad = true;
        }
    }

    @Override
    public void draw(GraphicsContext ctx, float deltaSeconds, boolean showDebugInfo) {
        if (!failedToLoad) {
            super.draw(ctx, deltaSeconds, showDebugInfo);
        }
        if (finished) {
            returnButton.draw(ctx);
        }
    }

    @Override
    public int update(Input input, float deltaSeconds, boolean showDebugInfo) {
        if (failedToLoad) {
            return GameState.STATE_MAINMENU;
        }

        int superReturnValue = super.update(input, deltaSeconds, showDebugInfo);
        if (superReturnValue == GameState.STATE_GAMEOVER) {
            finished = true;
        }

        if (finished) {
            returnButton.update(input);
            if (returnButton.isClicked()) {
                return GameState.STATE_MAINMENU;
            }
        }

        return -1;
    }

    @Override
    protected boolean updateGame(Input input) {
        if (failedToLoad) {
            return true;
        } else if (actionTime > 0) {
            actionTime--;
            return false;
        }
        actionTime = actionCooldown;
        PlayerAction action = replay.popAction();
        if (action == null) {
            finished = true;
            return false;
        }
        dungeon.runPlayerAction(action);
        if (action == PlayerAction.NextLevel) {
            reloadUI();
        }
        return action.proceedsRound() && processRound();
    }
}
