package otm.roguesque.ui;

import java.util.ArrayList;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

/**
 * Käyttöliittymän kontrolli-laitteita kuvaava luokka. Sisältää tiedot hiiren ja
 * näppäimistön tapahtumista.
 *
 * @author Jens Pitkänen
 */
public class Input {

    /**
     * Debug-informaation näkyvyyttä vaihtava nappi.
     */
    public static final KeyCode[] CONTROL_TOGGLE_DEBUG_INFO = new KeyCode[]{
        KeyCode.F3
    };

    /**
     * Intron skippaamisnappi.
     */
    public static final KeyCode[] CONTROL_SKIP_INTRO = new KeyCode[]{
        KeyCode.ESCAPE, KeyCode.SPACE
    };

    /**
     * Napit joilla liikutaan ylös.
     */
    public static final KeyCode[] CONTROL_MOVE_UP = new KeyCode[]{
        KeyCode.W, KeyCode.UP, KeyCode.K
    };
    /**
     * Napit joilla liikutaan vasemmalle.
     */
    public static final KeyCode[] CONTROL_MOVE_LEFT = new KeyCode[]{
        KeyCode.A, KeyCode.LEFT, KeyCode.H
    };
    /**
     * Napit joilla liikutaan alas.
     */
    public static final KeyCode[] CONTROL_MOVE_DOWN = new KeyCode[]{
        KeyCode.S, KeyCode.DOWN, KeyCode.J
    };
    /**
     * Napit joilla liikutaan oikealle.
     */
    public static final KeyCode[] CONTROL_MOVE_RIGHT = new KeyCode[]{
        KeyCode.D, KeyCode.RIGHT, KeyCode.L
    };

    private final ArrayList<KeyCode> keysPressed = new ArrayList();
    private final ArrayList<MouseButton> mouseButtonsPressed = new ArrayList();
    private double mouseX;
    private double mouseY;

    /**
     * Rekisteröi uuden napinpainalluksen. Kutsutaan RoguesqueAppista.
     *
     * @param kc Painettu nappi.
     */
    public void addPressedKey(KeyCode kc) {
        keysPressed.add(kc);
    }

    /**
     * Resetoi painetut napit, tapahtuu jokaisen ruutupäivityksen jälkeen.
     */
    public void clearInputs() {
        keysPressed.clear();
        mouseButtonsPressed.clear();
    }

    /**
     * Onko yksikään annetuista napeista painettuna?
     *
     * @param codes Napit.
     * @return Onko yksikään napeista painettuna?
     */
    public boolean isPressed(KeyCode[] codes) {
        for (KeyCode kc : codes) {
            if (keysPressed.contains(kc)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Asettaa hiiren uuden paikan.
     *
     * @param x Hiiren x-koordinaatti (pikseleissä).
     * @param y Hiiren y-koordinaatti (pikseleissä).
     */
    public void setMousePosition(double x, double y) {
        this.mouseX = x;
        this.mouseY = y;
    }

    /**
     * Rekisteröi hiiren napinpainalluksen.
     *
     * @param button Hiiren nappi jota painettiin.
     */
    public void fireClick(MouseButton button) {
        mouseButtonsPressed.add(button);
    }

    /**
     * Onko tämä hiiren painike painettuna?
     *
     * @param button Kyseinen painike.
     * @return Onko tämä hiiren painike painettuna?
     */
    public boolean clicked(MouseButton button) {
        return mouseButtonsPressed.contains(button);
    }

    /**
     * Palauttaa hiiren x-koordinaatin.
     *
     * @return Hiiren x-koordinaatti.
     */
    public double getMouseX() {
        return mouseX;
    }

    /**
     * Palauttaa hiiren y-koordinaatin.
     *
     * @return Hiiren y-koordinaatti.
     */
    public double getMouseY() {
        return mouseY;
    }

    /**
     * Palauttaa true, mikäli hiiri on annetun neliön sisällä.
     *
     * @param x Neliön ylävasemman kulman x-koordinaatti.
     * @param y Neliön ylävasemman kulman y-koordinaatti.
     * @param width Neliön leveys.
     * @param height Neliön korkeus.
     * @return Onko hiiri neliön sisällä?
     */
    public boolean containsMouse(double x, double y, double width, double height) {
        return !(mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height);
    }
}
