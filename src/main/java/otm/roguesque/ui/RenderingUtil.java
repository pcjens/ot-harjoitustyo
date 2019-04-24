package otm.roguesque.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RenderingUtil {

    public static void drawBox(GraphicsContext ctx, double x, double y, double w, double h, boolean highlight) {
        ctx.setFill(Color.WHITE);
        ctx.fillRect(x, y, w, h);
        if (highlight) {
            ctx.setFill(Color.gray(0.3));
        } else {
            ctx.setFill(Color.BLACK);
        }
        ctx.fillRect(x + 4.0, y + 4.0, w - 8.0, h - 8.0);
        ctx.setFill(Color.WHITE);
    }
}
