package otm.roguesque.ui;

import java.util.ArrayList;
import javafx.scene.input.KeyCode;

public class Input {

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
