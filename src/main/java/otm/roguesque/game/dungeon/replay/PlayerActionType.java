package otm.roguesque.game.dungeon.replay;

/**
 * Pelaajan tekemiä asioita kuvaava enum.
 *
 * @author Jens Pitkänen
 */
public enum PlayerActionType {
    /**
     * Seuraavaan tasoon siirtyminen, kun pelaaja on tikkaiden päällä.
     */
    NextLevel(false),
    /**
     * Yksi askel ylös.
     */
    MoveUp(true),
    /**
     * Yksi askel vasemmalle.
     */
    MoveLeft(true),
    /**
     * Yksi askel alas.
     */
    MoveDown(true),
    /**
     * Yksi askel oikealle.
     */
    MoveRight(true);

    private final boolean proceedsRound;

    PlayerActionType(boolean proceedsRound) {
        this.proceedsRound = proceedsRound;
    }

    /**
     * Palauttaa true, mikäli tämän PlayerActionin pitäisi edistää peliä
     * vuorolla. Käytännössä siis kaikki paitsi NextLevel.
     *
     * @return Tuleeko peliä edistää vuorolla tämän jälkeen?
     */
    public boolean proceedsRound() {
        return proceedsRound;
    }
}
