package otm.roguesque.ui;

import java.util.ArrayList;
import javafx.scene.input.KeyCode;

public class Input {

    public static final KeyCode[] CONTROL_TOGGLE_PERF_STATS = new KeyCode[]{
        KeyCode.F3
    };

    public static final KeyCode[] CONTROL_PLAY = new KeyCode[]{KeyCode.P};
    public static final KeyCode[] CONTROL_QUIT = new KeyCode[]{KeyCode.Q};

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

    private final ArrayList<KeyCode> keysPressed = new ArrayList();

    public void addPressedKey(KeyCode kc) {
        keysPressed.add(kc);
    }

    public void clearPressedKeys() {
        keysPressed.clear();
    }

    public boolean isPressed(KeyCode[] codes) {
        for (KeyCode kc : codes) {
            if (keysPressed.contains(kc)) {
                return true;
            }
        }
        return false;
    }
}
