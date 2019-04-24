package otm.roguesque.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Apufunktioita yleisien piirrettävien asioiden piirtämiseen.
 *
 * @author Jens Pitkänen
 */
public class RenderingUtil {

    /**
     * Piirtää neliön.
     *
     * @param ctx Käyttöliittymän piirtokonteksti.
     * @param x Neliön ylävasemman kulman x-koordinaatti.
     * @param y Neliön ylävasemman kulman y-koordinaatti.
     * @param width Neliön leveys.
     * @param height Neliön korkeus.
     * @param highlight Onko laatikko "huomiota vaativa"?
     */
    public static void drawBox(GraphicsContext ctx, double x, double y, double width, double height, boolean highlight) {
        ctx.setFill(Color.WHITE);
        ctx.fillRect(x, y, width, height);
        if (highlight) {
            ctx.setFill(Color.gray(0.3));
        } else {
            ctx.setFill(Color.BLACK);
        }
        ctx.fillRect(x + 4.0, y + 4.0, width - 8.0, height - 8.0);
        ctx.setFill(Color.WHITE);
    }
}
