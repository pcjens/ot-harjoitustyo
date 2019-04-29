package otm.roguesque.ui.states;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import otm.roguesque.game.dungeon.PlayerAction;
import otm.roguesque.game.dungeon.Replay;
import otm.roguesque.ui.Input;

/**
 * InGameState, paitsi että tämä perustuu Replay:n pelaajan oikeiden
 * napinpainalluksien sijaan.
 *
 * @see otm.roguesque.game.dungeon.Replay
 * @author Jens Pitkänen
 */
public class ReplayGameState extends InGameState {

    private Replay replay;
    private boolean failedToLoad = false;

    private final int actionCooldown = 10;
    private int actionTime = 0;

    @Override
    public void initialize() {
        try {
            replay = new Replay(new File("replay-for-debugging.rgsq"));
            long initialSeed = replay.getSeed();
            initializeDungeon(initialSeed);
        } catch (IOException ex) {
            Logger.getLogger(ReplayGameState.class.getName()).log(Level.SEVERE, null, ex);
            failedToLoad = true;
        }
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
        dungeon.runPlayerAction(action);
        if (action == PlayerAction.NextLevel) {
            reloadUI();
        }
        return action.proceedsRound() && processRound();

    }
}
