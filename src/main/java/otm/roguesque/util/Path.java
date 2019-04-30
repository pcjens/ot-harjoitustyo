package otm.roguesque.util;

/**
 * Polku.
 *
 * @see otm.roguesque.util.Pathfinder#findPath(int, int, int, int, boolean[],
 * int, int)
 */
public class Path implements Comparable<Path> {

    private int x;
    private int y;
    private int length;
    private int nextDeltaX;
    private int nextDeltaY;
    private int targetX;
    private int targetY;

    Path(int x, int y, Path previousPath) {
        this.x = x;
        this.y = y;
        this.length = previousPath.length + 1;
        this.nextDeltaX = previousPath.nextDeltaX;
        this.nextDeltaY = previousPath.nextDeltaY;
        this.targetX = previousPath.targetX;
        this.targetY = previousPath.targetY;
    }

    Path(int x, int y, int length, int nextDeltaX, int nextDeltaY, int targetX, int targetY) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.nextDeltaX = nextDeltaX;
        this.nextDeltaY = nextDeltaY;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    /**
     * Palauttaa seuraavan polun "askeleen" x-komponentin.
     *
     * @return Kuinka monta x-koordinaattia seuraavassa askeleessa kuuluu edetä.
     */
    public int getNextDeltaX() {
        return nextDeltaX;
    }

    /**
     * Palauttaa seuraavan polun "askeleen" y-komponentin.
     *
     * @return Kuinka monta y-koordinaattia seuraavassa askeleessa kuuluu edetä.
     */
    public int getNextDeltaY() {
        return nextDeltaY;
    }

    /**
     * Palauttaa polun pituuden.
     *
     * @return Palauttaa polun jäljellä olevien askelien määrän.
     */
    public int getLength() {
        return length;
    }

    @Override
    public int compareTo(Path t) {
        Double thisLength = this.length + Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
        Double otherLength = t.length + Math.sqrt(Math.pow(t.x, 2) + Math.pow(t.y, 2));
        return thisLength.compareTo(otherLength);
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }
}
