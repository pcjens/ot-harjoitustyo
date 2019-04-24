package otm.roguesque.game;

import java.util.Random;

/**
 * Tämä luokka sisältää satunnaislukugeneraattorin, jota peli voi käyttää
 * pelimekaniikkojen laskuissa. Tämä on parempi kuin moni Random-luokka
 * ripoteltuna ympäri koodia, sillä tällä saa tehtyä pelistä melko
 * deterministisen.
 *
 * @author Jens Pitkänen
 */
public class GlobalRandom {

    private static Random random = new Random(12);
    private static int seed = 12;

    /**
     * Palauttaa instanssin Random-luokasta. Tätä instanssia on tarkoitus
     * käyttää kaikkiin pelimekaniikkoihin vaikuttaviin asioihin, eikä mihinkään
     * muuhun, jotta peli pysyy deterministisenä.
     *
     * @see otm.roguesque.game.GlobalRandom#reset(int)
     *
     * @return Random joka luodaan resetissä.
     */
    public static Random get() {
        return random;
    }

    /**
     * Palauttaa seed-luvun joka määriteltiin viimeisimmässä resetissä.
     *
     * @see otm.roguesque.game.GlobalRandom#reset(int)
     *
     * @return Seed-luku joka annetaan resetissä parametrinä.
     */
    public static int getSeed() {
        return seed;
    }

    /**
     * Alustaa satunnaislukugeneraattorin. Tämän generaattorin saa käyttöön
     * get-funktiolla.
     *
     * @see otm.roguesque.game.GlobalRandom#get()
     * @see java.util.Random#Random(long)
     *
     * @param seed Seed-luku, johon perustuen satunnaislukugeneraattori luodaan.
     */
    public static void reset(int seed) {
        GlobalRandom.seed = seed;
        random = new Random(seed);
    }
}
