package otm.roguesque.game.dungeon.replay;

/**
 * Yhtä pelaajan tekemää asiaa kuvaava luokka, joka sisältää myös tiedon asian
 * toistomäärästä.
 *
 * @author Jens Pitkänen
 */
public class PlayerAction {

    private PlayerActionType type;
    private byte count;

    /**
     * Luo uuden PlayerActionin.
     *
     * @param type Pelaajan tekemän asian tyyppi.
     * @param count Pelaajan tekemän asian toistot.
     */
    public PlayerAction(PlayerActionType type, byte count) {
        this.type = type;
        this.count = count;
    }

    /**
     * Palauttaa pelaajan tekemän asian olemuksen.
     *
     * @return Pelaajan tekemän asian tyyppi.
     */
    public PlayerActionType getType() {
        return type;
    }

    /**
     * Palauttaa sen, kuinka monta kertaa pelaaja toisti tämän actionin.
     *
     * @return Toistojen määrä.
     */
    public byte getCount() {
        return count;
    }

    /**
     * Nostaa tehtyjen actionien määrää yhdellä.
     *
     * @see otm.roguesque.game.dungeon.replay.PlayerAction#getCount()
     */
    public void increment() {
        this.count++;
    }

    /**
     * Laskee tehtyjen actionien määrää yhdellä.
     *
     * @see otm.roguesque.game.dungeon.replay.PlayerAction#getCount()
     */
    public void decrement() {
        this.count--;
    }
}
