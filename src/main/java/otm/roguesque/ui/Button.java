package otm.roguesque.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

/**
 * Tämä luokka sisältää nappien välistä toistuvaa koodia.
 *
 * @author Jens Pitkänen
 */
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

    /**
     * Luo uuden napin.
     *
     * @param text Napin teksti.
     * @param x Napin x-koordinaatti.
     * @param y Napin y-koordinaatti.
     * @param width Napin leveys.
     * @param height Napin korkeus.
     * @param hotkeyUnderlineOffset Kuinka monta pikseliä alaviivaa siirretään
     * oikealle?
     */
    public Button(String text, int x, int y, int width, int height, int hotkeyUnderlineOffset) {
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
    public Button(String text, int x, int y, int width, int height, int hotkeyUnderlineOffset, int padding) {
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
        hovered = input.containsMouse(x, y, width, height);
        clicked = hovered && input.clicked(MouseButton.PRIMARY);
    }

    /**
     * Piirtää napin.
     *
     * @param ctx Käyttöliittymän piirtokonteksti.
     */
    public void draw(GraphicsContext ctx) {
        RenderingUtil.drawBox(ctx, x, y, width, height, hovered);
        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_UI);
        ctx.fillText(text, x + padding, y + 15 + padding);
        ctx.fillRect(x + padding + hotkeyUnderlineOffset, y + 19 + padding, 10, 2);
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
        return hovered;
    }

    /**
     * Palauttaa true, mikäli hiiri on napin päällä ja sitä klikattiin juuri.
     *
     * @return Onko hiiri napin päällä ja painettu pohjaan?
     */
    public boolean isClicked() {
        return clicked;
    }
}
