package otm.roguesque.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

/**
 * Tämä luokka sisältää nappien välistä toistuvaa koodia.
 *
 * @author Jens Pitkänen
 */
public class Button {

    private static final Color DISABLED_OVERLAY_COLOR = new Color(0, 0, 0, 0.4);

    private KeyCode[] keys;
    private String text;
    private int x;
    private int y;
    private int width;
    private int height;
    private int hotkeyUnderlineOffset;
    private int padding;
    private boolean hovered;
    private boolean clicked;

    private double lastOffsetX = 0;
    private double lastOffsetY = 0;

    private boolean disabled = false;

    /**
     * Luo uuden napin.
     *
     * @param keys Näppäimet jotka toimivat napin aktivointinappeina.
     * @param text Napin teksti.
     * @param x Napin x-koordinaatti.
     * @param y Napin y-koordinaatti.
     * @param width Napin leveys.
     * @param height Napin korkeus.
     * @param hotkeyUnderlineOffset Kuinka monta pikseliä alaviivaa siirretään
     * oikealle?
     */
    public Button(KeyCode[] keys, String text, int x, int y, int width, int height, int hotkeyUnderlineOffset) {
        this.keys = keys;
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hotkeyUnderlineOffset = hotkeyUnderlineOffset;
        this.padding = 15;
    }

    /**
     * Luo uuden napin.
     *
     * @param keys Näppäimet jotka toimivat napin aktivointinappeina.
     * @param text Napin teksti.
     * @param x Napin x-koordinaatti.
     * @param y Napin y-koordinaatti.
     * @param width Napin leveys.
     * @param height Napin korkeus.
     * @param hotkeyUnderlineOffset Kuinka monta pikseliä alaviivaa siirretään
     * oikealle?
     * @param padding Kuinka monta pikseliä tekstin ja napin reunojen välillä
     * on?
     */
    public Button(KeyCode[] keys, String text, int x, int y, int width, int height, int hotkeyUnderlineOffset, int padding) {
        this.keys = keys;
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hotkeyUnderlineOffset = hotkeyUnderlineOffset;
        this.padding = padding;
    }

    /**
     * Päivittää napin tilanteen riippuen hiiren paikasta.
     *
     * @param input Käyttöliittymän Input-olio.
     */
    public void update(Input input) {
        hovered = input.containsMouse(x + lastOffsetX, y + lastOffsetY, width, height);
        clicked = input.isPressed(keys) || (hovered && input.clicked(MouseButton.PRIMARY));
    }

    /**
     * Asettaa napin päälle / pois päältä.
     *
     * @param disabled True, niin nappia ei voi painaa ja se tummentuu, false,
     * niin nappia voi painaa ja se näkyy normaalisti.
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Piirtää napin.
     *
     * @param ctx Käyttöliittymän piirtokonteksti.
     */
    public void draw(GraphicsContext ctx) {
        draw(ctx, 0, 0);
    }

    /**
     * Piirtää napin, ja lisää annetut arvot napin x- ja y-koordinaatteihin.
     *
     * @param ctx Käyttöliittymän piirtokonteksti.
     * @param xOffset Siirtymä x-akselilla.
     * @param yOffset Siirtymä y-akselilla.
     */
    public void draw(GraphicsContext ctx, double xOffset, double yOffset) {
        this.lastOffsetX = xOffset;
        this.lastOffsetY = yOffset;
        RenderingUtil.drawBox(ctx, x + xOffset, y + yOffset, width, height, isHovered());
        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_UI);
        ctx.fillText(text, x + xOffset + padding, y + 15 + yOffset + padding);
        ctx.fillRect(x + xOffset + padding + hotkeyUnderlineOffset, y + 19 + yOffset + padding, 10, 2);
        if (disabled) {
            ctx.setFill(DISABLED_OVERLAY_COLOR);
            ctx.fillRect(x + xOffset, y + yOffset, width, height);
        }
    }

    /**
     * Asettaa napin x-koordinaatin.
     *
     * @param x Napin x-koordinaatti.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Asettaa napin y-koordinaatin.
     *
     * @param y Napin y-koordinaatti.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Palauttaa napin x-koordinaatin.
     *
     * @return Napin x-koordinaatti.
     */
    public int getX() {
        return x;
    }

    /**
     * Palauttaa napin y-koordinaatin.
     *
     * @return Napin y-koordinaatti.
     */
    public int getY() {
        return y;
    }

    /**
     * Palauttaa napin leveyden.
     *
     * @return Napin leveys.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Palauttaa napin korkeuden.
     *
     * @return Napin korkeus.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Palauttaa true, mikäli hiiri on napin päällä.
     *
     * @return Onko hiiri napin päällä?
     */
    public boolean isHovered() {
        return !disabled && hovered;
    }

    /**
     * Palauttaa true, mikäli hiiri on napin päällä ja sitä klikattiin juuri.
     *
     * @return Onko hiiri napin päällä ja painettu pohjaan?
     */
    public boolean isClicked() {
        return !disabled && clicked;
    }
}
