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
    private double yDrift;

    /**
     * Luo uuden osuma-notifikaation.
     *
     * @param x Osuma-notifikaation x-koordinaatti suhteessa olioon jonka päälle
     * notifikaatio ilmestyy.
     * @param y Osuma-notifikaation y-koordinaatti suhteessa olioon jonka päälle
     * notifikaatio ilmestyy.
     * @param amount Kuinka paljon vahinkoa osumanotifikaatio esittää?
     * @param length Kuinka pitkään notifikaation tulisi olla näkyvissä?
     */
    public HitNotification(double x, double y, int amount, float length) {
        this.x = x;
        this.y = y;
        this.amount = amount;
        this.length = this.time = length;
    }

    /**
     * Päivittää notifikaation ajastimen.
     *
     * @param deltaTime Delta-aika.
     */
    public void update(float deltaTime) {
        time -= deltaTime;
        yDrift += deltaTime * Math.max(0, 32 - yDrift);
    }

    /**
     * Siirtää notifikaatiota yhden askeleen ylös, tehdäkseen tilaa uudelle
     * notifikaatiolle.
     */
    public void bump() {
        yDrift = 0;
        y -= 18;
    }

    /**
     * Piirtää notifikaation.
     *
     * @param ctx Piirto-konteksti.
     */
    public void draw(GraphicsContext ctx) {
        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_NOTIFICATION);
        if (amount == 0) {
            ctx.drawImage(NEUTRAL_SPLASH_IMAGE, x, y - yDrift);
        } else {
            ctx.drawImage(SPLASH_IMAGE, x, y - yDrift);
        }
        ctx.fillText(Integer.toString(amount), x + 4, y + 12 - yDrift);
    }

    /**
     * Onko notifikaatio ollut olemassa konstruktorissa annetun length:n ajan?
     *
     * @see otm.roguesque.ui.HitNotification#HitNotification(double, double,
     * int, float)
     *
     * @return Onko notifikaatio ollut olemassa tarpeeksi pitkään, eli pitäisikö
     * sen kadota?
     */
    public boolean hasDisappeared() {
        return time <= 0;
    }
}
