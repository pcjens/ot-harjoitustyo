package otm.roguesque.game.dungeon;

/**
 * Pelaajan tekemiä asioita kuvaava enum.
 *
 * @author Jens Pitkänen
 */
public enum PlayerAction {
    NextLevel(false), MoveUp(true), MoveLeft(true), MoveDown(true), MoveRight(true);

    private final boolean proceedsRound;

    PlayerAction(boolean proceedsRound) {
        this.proceedsRound = proceedsRound;
    }

    public boolean proceedsRound() {
        return proceedsRound;
    }
}
