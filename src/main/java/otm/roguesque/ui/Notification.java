package otm.roguesque.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Notifikaatioteksti joka voi ilmestyä olennon päälle informoimaan
 * jonkinlaisesta tapahtumasta.
 *
 * @author Jens Pitkänen
 */
public class Notification {

    private Color color;
    private String text;
    private float length;
    private float time;
    private double x;
    private double y;

    public Notification(double x, double y, String text, Color color, float length) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.color = color;
        this.length = this.time = length;
    }

    public void draw(GraphicsContext ctx, int offsetX, int offsetY, double tileSize, float deltaTime) {
        time -= deltaTime;
        y -= deltaTime * 32 * (time / length);
        ctx.setFill(color.interpolate(Color.TRANSPARENT, time / length));
        ctx.setFont(RoguesqueApp.FONT_NOTIFICATION);
        ctx.fillText(text, x - offsetX * tileSize + 12, y - offsetY * tileSize);
    }

    public boolean hasDisappeared() {
        return time <= 0;
    }
}
