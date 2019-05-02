package otm.roguesque.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Notifikaatioteksti joka voi ilmestyä olennon päälle informoimaan otetusta
 * vahingosta.
 *
 * @author Jens Pitkänen
 */
public class HitNotification {

    private static final Image SPLASH_IMAGE = new Image(HitNotification.class.getResourceAsStream("/sprites/Splash.png"));
    private static final Image NEUTRAL_SPLASH_IMAGE = new Image(HitNotification.class.getResourceAsStream("/sprites/SplashNeutral.png"));

    private int amount;
    private float length;
    private float time;
    private double x;
    private double y;

    public HitNotification(double x, double y, int amount, float length) {
        this.x = x;
        this.y = y;
        this.amount = amount;
        this.length = this.time = length;
    }

    public void update(float deltaTime) {
        time -= deltaTime;
    }

    public void draw(GraphicsContext ctx) {
        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_NOTIFICATION);
        if (amount == 0) {
            ctx.drawImage(NEUTRAL_SPLASH_IMAGE, x, y);
        } else {
            ctx.drawImage(SPLASH_IMAGE, x, y);
        }
        ctx.fillText(Integer.toString(amount), x + 4, y + 12);
    }

    public boolean hasDisappeared() {
        return time <= 0;
    }
}
