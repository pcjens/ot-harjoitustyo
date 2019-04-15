package otm.roguesque.ui;

import javafx.scene.input.MouseButton;

public class Button {
    public int x;
    public int y;
    public int width;
    public int height;
    public boolean hovered;
    public boolean clicked;
    
    public Button(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public void update(Input input) {
        hovered = input.containsMouse(x, y, width, height);
        clicked = hovered && input.clicked(MouseButton.PRIMARY);
    }
}
