package otm.roguesque.game.entities;

public class Player extends Entity {

    private boolean[] tilesInLOS;
    private boolean[] uncoveredTiles;
    private int sightDistance = 5;

    public Player() {
        super(10, 2, 1, "Adventurer", "Seeking\n freedom.", "Adventurers", "/sprites/player.png");
    }

    public String getExaminationText() {
        if (lastEntityInteractedWith != null) {
            return lastEntityInteractedWith.getDescription();
        }
        return null;
    }

    public boolean inLineOfSight(int x, int y) {
        if (tilesInLOS != null) {
            return tilesInLOS[x + y * dungeon.getWidth()];
        } else {
            return false;
        }
    }

    public boolean isUncovered(int x, int y) {
        if (uncoveredTiles != null) {
            return uncoveredTiles[x + y * dungeon.getWidth()];
        } else {
            return false;
        }
    }

    public void resetUncovered() {
        if (uncoveredTiles == null) {
            return;
        }
        for (int i = 0; i < uncoveredTiles.length; i++) {
            uncoveredTiles[i] = false;
        }
    }

    public void recalculateLineOfSight(boolean ignoreDistance) {
        if (dungeon == null) {
            return;
        }
        int width = dungeon.getWidth();
        int height = dungeon.getHeight();
        boolean[] newLOS = new boolean[width * height];
        if (tilesInLOS == null || tilesInLOS.length != width * height) {
            tilesInLOS = new boolean[width * height];
            uncoveredTiles = new boolean[width * height];
        }
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
        int d = 2 * dy - dx;
        int scanY = y0;
        for (int scanX = x0; scanX <= x1; scanX++) {
            if (dungeon.solid(scanX, scanY)) {
                return true;
            }
            if (d > 0) {
                scanY += yStep;
                d -= 2 * dx;
            }
            d += 2 * dy;
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
        int d = 2 * dx - dy;
        int scanX = x0;
        for (int scanY = y0; scanY <= y1; scanY++) {
            if (dungeon.solid(scanX, scanY)) {
                return true;
            }
            if (d > 0) {
                scanX += xStep;
                d -= 2 * dy;
            }
            d += 2 * dx;
        }
        return false;
    }
}
