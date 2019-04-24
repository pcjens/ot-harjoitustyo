package otm.roguesque.ui;

import java.util.ArrayList;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

public class Input {

    public static final KeyCode[] CONTROL_TOGGLE_DEBUG_INFO = new KeyCode[]{
        KeyCode.F3
    };

    public static final KeyCode[] CONTROL_PLAY = new KeyCode[]{KeyCode.P};
    public static final KeyCode[] CONTROL_QUIT = new KeyCode[]{KeyCode.Q};
    public static final KeyCode[] CONTROL_SKIP_INTRO = new KeyCode[]{
        KeyCode.ESCAPE, KeyCode.SPACE
    };

    public static final KeyCode[] CONTROL_MOVE_UP = new KeyCode[]{
        KeyCode.W, KeyCode.UP, KeyCode.K
    };
    public static final KeyCode[] CONTROL_MOVE_LEFT = new KeyCode[]{
        KeyCode.A, KeyCode.LEFT, KeyCode.H
    };
    public static final KeyCode[] CONTROL_MOVE_DOWN = new KeyCode[]{
        KeyCode.S, KeyCode.DOWN, KeyCode.J
    };
    public static final KeyCode[] CONTROL_MOVE_RIGHT = new KeyCode[]{
        KeyCode.D, KeyCode.RIGHT, KeyCode.L
    };
    public static final KeyCode[] CONTROL_NEXT_LEVEL = new KeyCode[]{KeyCode.M};

    private final ArrayList<KeyCode> keysPressed = new ArrayList();
    private final ArrayList<MouseButton> mouseButtonsPressed = new ArrayList();
    private double mouseX;
    private double mouseY;

    public void addPressedKey(KeyCode kc) {
        keysPressed.add(kc);
    }

    public void clearInputs() {
        keysPressed.clear();
        mouseButtonsPressed.clear();
    }

    public boolean isPressed(KeyCode[] codes) {
        for (KeyCode kc : codes) {
            if (keysPressed.contains(kc)) {
                return true;
            }
        }
        return false;
    }

    public void setMousePosition(double x, double y) {
        this.mouseX = x;
        this.mouseY = y;
    }

    public void fireClick(MouseButton button) {
        mouseButtonsPressed.add(button);
    }

    public boolean clicked(MouseButton button) {
        return mouseButtonsPressed.contains(button);
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public boolean containsMouse(double x, double y, double width, double height) {
        return !(mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height);
    }
}
