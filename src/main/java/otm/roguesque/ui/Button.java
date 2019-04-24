package otm.roguesque.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

public class Button {

    private String text;
    private int x;
    private int y;
    private int width;
    private int height;
    private int hotkeyUnderlineOffset;
    private int padding;
    private boolean hovered;
    private boolean clicked;

    public Button(String text, int x, int y, int width, int height, int hotkeyUnderlineOffset) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hotkeyUnderlineOffset = hotkeyUnderlineOffset;
        this.padding = 15;
    }

    public Button(String text, int x, int y, int width, int height, int hotkeyUnderlineOffset, int padding) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hotkeyUnderlineOffset = hotkeyUnderlineOffset;
        this.padding = padding;
    }

    public void update(Input input) {
        hovered = input.containsMouse(x, y, width, height);
        clicked = hovered && input.clicked(MouseButton.PRIMARY);
    }

    public void draw(GraphicsContext ctx) {
        RenderingUtil.drawBox(ctx, x, y, width, height, hovered);
        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_UI);
        ctx.fillText(text, x + padding, y + 15 + padding);
        ctx.fillRect(x + padding + hotkeyUnderlineOffset, y + 19 + padding, 10, 2);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isHovered() {
        return hovered;
    }

    public boolean isClicked() {
        return clicked;
    }
}
