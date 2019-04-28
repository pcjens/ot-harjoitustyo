package otm.roguesque.util;

/**
 * Kaksiulotteinen vektori. Lyhentämään tiettyjä koodinpätkiä, erityisesti
 * koordinaattien välisiä etäisyyksiä tutkiessa.
 *
 * @author Jens Pitkänen
 */
public class Vector {

    private int x;
    private int y;
    private double distance;

    /**
     * Luo uuden vektorin.
     *
     * @param x Vektorin x-komponentti.
     * @param y Vektorin y-komponentti.
     */
    public Vector(int x, int y) {
        this.x = x;
        this.y = y;
        recalculateDistance();
    }

    /**
     * Palauttaa vektorin, jolla on sama suunta, mutta pituus on 1.
     *
     * @return Samansuuntainen vektori, jonka pituus on 1.
     */
    public Vector unit() {
        Vector result = new Vector(x, y);
        result.x /= distance;
        result.y /= distance;
        result.distance = 1;
        return result;
    }

    /**
     * Palauttaa vektorin, jossa lyhyempi komponentti on nollattu.
     *
     * @return Tämä vektori, mutta lyhyempi komponentti on 0.
     */
    public Vector lesserComponentZeroed() {
        Vector result = new Vector(x, y);
        if (Math.abs(x) > Math.abs(y)) {
            result.setY(0);
        } else {
            result.setX(0);
        }
        return result;
    }

    /**
     * Palauttaa vektorin x-komponentin.
     *
     * @return Vektorin x-komponentti.
     */
    public int getX() {
        return x;
    }

    /**
     * Asettaa vektorin x-komponentin.
     *
     * @param x Vektorin uusi x-komponentti.
     */
    public void setX(int x) {
        this.x = x;
        recalculateDistance();
    }

    /**
     * Palauttaa vektorin y-komponentin.
     *
     * @return Vektorin y-komponentti.
     */
    public int getY() {
        return y;
    }

    /**
     * Asettaa vektorin y-komponentin.
     *
     * @param y Vektorin uusi y-komponentti.
     */
    public void setY(int y) {
        this.y = y;
        recalculateDistance();
    }

    /**
     * Palauttaa vektorin pituuden.
     *
     * @return Vektorin pituus.
     */
    public double getDistance() {
        return distance;
    }

    private void recalculateDistance() {
        this.distance = Math.sqrt(x * x + y * y);
    }
}
