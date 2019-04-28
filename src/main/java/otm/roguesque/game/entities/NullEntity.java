package otm.roguesque.game.entities;

import otm.roguesque.game.GlobalRandom;

/**
 * Null-olio.
 *
 * @author Jens Pitkänen
 */
public class NullEntity extends Entity implements AI {

    /**
     * Luo uuden Null-olion. Tämä on lähinnä debuggausta helpottava olio jonka
     * ei pitäisi ilmestyä pelissä. Luolageneraattori luo tällaisen olion vain
     * silloin, kun uudelle vastustajatyypille ei ole kirjoitettu haaraa
     * vastustajaolioiden luomisen hoitavassa switch-käskyssä.
     */
    public NullEntity() {
        super(100, 0, 0, "Null", "???", "???", "/sprites/Null.png");
    }

    @Override
    public void processRound() {
        int r = GlobalRandom.get().nextInt(4);
        int dx = (int) Math.cos(r * Math.PI / 2.0);
        int dy = (int) Math.sin(r * Math.PI / 2.0);
        move(dx, dy);
    }
}
