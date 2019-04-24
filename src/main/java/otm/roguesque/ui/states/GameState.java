package otm.roguesque.ui.states;

import javafx.scene.canvas.GraphicsContext;
import otm.roguesque.ui.Input;

/**
 * Käyttöliittymän tilojen pohja-rajapinta.
 *
 * @author Jens Pitkänen
 */
public interface GameState {

    /**
     * Kuinka monta eri tilaa käyttöliittymällä on?
     */
    public static final int STATE_COUNT = 4;

    /**
     * "Sulje peli -tila".
     */
    public static final int STATE_QUIT = 4;
    /**
     * Intro-tila.
     */
    public static final int STATE_INTRO = 0;
    /**
     * Päävalikko-tila.
     */
    public static final int STATE_MAINMENU = 1;
    /**
     * "Itse peli" -tila.
     */
    public static final int STATE_INGAME = 2;
    /**
     * "Game over" -tila.
     */
    public static final int STATE_GAMEOVER = 3;

    /**
     * Piirrä tämä tila.
     *
     * @param ctx Käyttöliittymän piirtokonteksti.
     * @param deltaSeconds Kuinka monta sekuntia viime päivityksestä on?
     * @param showDebugInfo Ovatko debuggaus-tiedot esillä?
     */
    void draw(GraphicsContext ctx, float deltaSeconds, boolean showDebugInfo);

    /**
     * Päivitä tämä tila.
     *
     * @param input Käyttöliittymän ohjeislaitteiden tila.
     * @param deltaSeconds Kuinka monta sekuntia viime päivityksestä on?
     * @param showDebugInfo Ovatko debuggaus-tiedot esillä?
     * @return Seuraava tila, tai -1 mikäli tila ei muutu.
     */
    int update(Input input, float deltaSeconds, boolean showDebugInfo);

    /**
     * Alusta tila.
     */
    void initialize();
}
