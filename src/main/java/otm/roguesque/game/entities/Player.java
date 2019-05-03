package otm.roguesque.game.entities;

import otm.roguesque.game.dungeon.Dungeon;

/**
 * Pelaaja-olio.
 *
 * @author Jens Pitkänen
 */
public class Player extends Entity implements AI {

    private boolean[] tilesInLOS;
    private boolean[] uncoveredTiles;
    private int sightDistance = 7;
    private int autoHealCooldown = 10;
    private int currentAutoHealCooldown = autoHealCooldown;

    /**
     * Luo uuden pelaaja-olion.
     */
    public Player() {
        super(10, 1, 1, 2, "Adventurer", "Seeking\n freedom.", "Adventurers", "/sprites/Player.png");
    }

    @Override
    public void processRound() {
        if (getHealth() < getMaxHealth() && --currentAutoHealCooldown <= 0) {
            setHealth(Math.min(getMaxHealth(), getHealth() + getMaxHealth() / 10));
            currentAutoHealCooldown = autoHealCooldown;
        }
    }

    /**
     * Palauttaa vuoromäärän joka pelaajalla menee saada passiivisesti 10%
     * elämäpisteistään takaisin.
     *
     * @return "Autoheal" vuoromäärä.
     */
    public int getAutoHealCooldown() {
        return autoHealCooldown;
    }

    /**
     * Asettaa vuoromäärän joka pelaajalla menee saada passiivisesti 10%
     * elämäpisteistään takaisin.
     *
     * @param autoHealCooldown Uusi "autoheal" vuoromäärä.
     */
    public void setAutoHealCooldown(int autoHealCooldown) {
        this.autoHealCooldown = autoHealCooldown;
    }

    /**
     * Palauttaa etäisyyden, kuinka pitkälle pelaaja näkee vastustajat.
     *
     * @return Näköetäisyys.
     */
    public int getSightDistance() {
        return sightDistance;
    }

    /**
     * Palauttaa olion kuvaustekstin johon pelaaja on viimeksi törmännyt.
     * Käytetään vihollisen tilan esittämisessä.
     *
     * @return Törmätyn olion kuvausteksti.
     */
    public String getExaminationText() {
        if (getLastEntityInteractedWith() != null) {
            return getLastEntityInteractedWith().getRichDescription();
        }
        return null;
    }

    /**
     * Palauttaa, onko annetut koordinaatit pelaajan näkökentässä.
     *
     * @param x Tutkittava x-koordinaatti.
     * @param y Tutkittava y-koordinaatti.
     * @return Onko koordinaatti pelaajan näkökentässä?
     */
    public boolean inLineOfSight(int x, int y) {
        if (tilesInLOS != null) {
            return tilesInLOS[x + y * getDungeon().getWidth()];
        } else {
            return false;
        }
    }

    /**
     * Palauttaa, onko annetut koordinaatit olleet joskus pelaajan näkökentässä.
     *
     * @param x Tutkittava x-koordinaatti.
     * @param y Tutkittava y-koordinaatti.
     * @return Onko koordinaatti joskus ollut pelaajan näkökentässä?
     */
    public boolean isUncovered(int x, int y) {
        if (uncoveredTiles != null) {
            return uncoveredTiles[x + y * getDungeon().getWidth()];
        } else {
            return false;
        }
    }

    /**
     * Nollaa tiedon siitä, mitkä koordinaatit on nähty aikaisemmin.
     */
    public void resetUncovered() {
        if (uncoveredTiles == null) {
            return;
        }
        for (int i = 0; i < uncoveredTiles.length; i++) {
            uncoveredTiles[i] = false;
        }
    }

    /**
     * Laskee uudelleen koordinaattien näkyvyyden pelaajalle. Tätä kutsutaan
     * joka kerta, kun pelaaja liikkuu.
     *
     * @param ignoreDistance Jätetäänkö pelaajan näköetäisyys huomiotta?
     * (Tarkoittaa käytännössä ääretöntä näköetäisyyttä.)
     */
    public void recalculateLineOfSight(boolean ignoreDistance) {
        Dungeon dungeon = getDungeon();
        if (dungeon != null) {
            int width = dungeon.getWidth();
            int height = dungeon.getHeight();
            boolean[] newLOS = new boolean[width * height];
            initializeLOSIfNeeded(width, height);
            for (int scanY = 0; scanY < height; scanY++) {
                for (int scanX = 0; scanX < width; scanX++) {
                    updateLOS(newLOS, ignoreDistance ? Integer.MAX_VALUE : sightDistance, scanX, scanY, width, height);
                    tilesInLOS[scanX + scanY * width] = false;
                }
            }
            for (int scanY = 0; scanY < height; scanY++) {
                for (int scanX = 0; scanX < width; scanX++) {
                    applyLOS(newLOS, scanX, scanY, width, height);
                }
            }
        }
    }

    private void initializeLOSIfNeeded(int width, int height) {
        if (tilesInLOS == null || tilesInLOS.length != width * height) {
            tilesInLOS = new boolean[width * height];
            uncoveredTiles = new boolean[width * height];
        }
    }

    private void updateLOS(boolean[] newLOS, int maxDist, int x, int y, int width, int height) {
        int index = x + y * width;
        int dx = x - getX();
        int dy = y - getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < maxDist && !intersecting(getX(), getY(), x, y)) {
            newLOS[index] = true;
        }
    }

    private void applyLOS(boolean[] newLOS, int x, int y, int width, int height) {
        int index = x + y * width;
        for (int yOffset = -1; yOffset <= 1; yOffset++) {
            for (int xOffset = -1; xOffset <= 1; xOffset++) {
                int xx = x + xOffset;
                int yy = y + yOffset;
                if (xx >= 0 && xx < width && yy >= 0 && yy < height && newLOS[xx + yy * width]) {
                    tilesInLOS[index] = true;
                    uncoveredTiles[index] = true;
                }
            }
        }
    }

    private boolean intersecting(int x0, int y0, int x1, int y1) {
        if (Math.abs(y1 - y0) < Math.abs(x1 - x0)) {
            if (x0 > x1) {
                return intersectingLow(x1, y1, x0, y0);
            } else {
                return intersectingLow(x0, y0, x1, y1);
            }
        } else {
            if (y0 > y1) {
                return intersectingHigh(x1, y1, x0, y0);
            } else {
                return intersectingHigh(x0, y0, x1, y1);
            }
        }
    }

    private boolean intersectingLow(int x0, int y0, int x1, int y1) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int yStep = 1;
        if (dy < 0) {
            yStep = -1;
            dy = -dy;
        }
        int[] vals = new int[]{2 * dy - dx, y0};
        for (int scanX = x0; scanX <= x1; scanX++) {
            if (getDungeon().solid(scanX, vals[1])) {
                return true;
            }
            step(yStep, dx, dy, vals);
        }
        return false;
    }

    private boolean intersectingHigh(int x0, int y0, int x1, int y1) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int xStep = 1;
        if (dx < 0) {
            xStep = -1;
            dx = -dx;
        }
        int[] vals = new int[]{2 * dx - dy, x0};
        for (int scanY = y0; scanY <= y1; scanY++) {
            if (getDungeon().solid(vals[1], scanY)) {
                return true;
            }
            step(xStep, dy, dx, vals);
        }
        return false;
    }

    private void step(int step, int dx, int dy, int[] vals) {
        if (vals[0] > 0) {
            vals[1] += step;
            vals[0] -= 2 * dx;
        }
        vals[0] += 2 * dy;
    }
}
