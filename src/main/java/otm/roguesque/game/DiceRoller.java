package otm.roguesque.game;

import java.util.Random;

public class DiceRoller {

    private static Random random = new Random(12);
    private static int seed = 12;

    public static Random getRandom() {
        return random;
    }

    public static int getSeed() {
        return seed;
    }

    public static void resetRandom(int seed) {
        DiceRoller.seed = seed;
        random = new Random(seed);
    }
}
