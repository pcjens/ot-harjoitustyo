package otm.roguesque.util;

import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * Polkuetsintäalgortmi. (Ja roolipeli, heh.)
 *
 * @author Jens Pitkänen
 */
public class Pathfinder {

    private Pathfinder() {
    }

    /**
     * Etsii polun paikasta A paikkaan B.
     *
     * @param startX Paikan A x-koordinaatti.
     * @param startY Paikan A y-koordinaatti.
     * @param endX Paikan B x-koordinaatti.
     * @param endY Paikan B y-koordinaatti.
     * @param solid Kentän läpikulkevuus. (True = ei voi kävellä läpi, false =
     * voi kävellä läpi.)
     * @param width Kentän leveys.
     * @param height Kentän korkeus.
     * @return Polku paikasta A paikkaan B.
     */
    public static Path findPath(int startX, int startY, int endX, int endY, boolean[] solid, int width, int height) {
        if (startX == endX && startY == endY) {
            return new Path(startX, startY, 0, 0, 0, endX, endY);
        }

        PriorityQueue<Path> queue = new PriorityQueue();
        HashSet<Integer> visited = new HashSet();
        visited.add(startX + startY * width);
        addInitialNeighbors(queue, startX, startY, endX, endY, solid, width, height);
        while (!queue.isEmpty()) {
            Path current = queue.poll();
            int i = current.getX() + current.getY() * width;
            if (visited.contains(i)) {
                continue;
            }
            visited.add(i);
            if (current.getX() == endX && current.getY() == endY) {
                return current;
            }
            addNeighbors(queue, visited, current, solid, width, height);
        }
        System.out.println("Couldn't find path from " + startX + ", " + startY + " to " + endX + ", " + endY);
        return null;
    }

    private static void addInitialNeighbors(PriorityQueue<Path> queue, int x, int y, int targetX, int targetY, boolean[] solid, int width, int height) {
        if (x > 0 && !solid[x - 1 + y * width]) {
            queue.add(new Path(x - 1, y, 1, -1, 0, targetX, targetY));
        }
        if (x < width - 1 && !solid[x + 1 + y * width]) {
            queue.add(new Path(x + 1, y, 1, 1, 0, targetX, targetY));
        }
        if (y > 0 && !solid[x + (y - 1) * width]) {
            queue.add(new Path(x, y - 1, 1, 0, -1, targetX, targetY));
        }
        if (y < height - 1 && !solid[x + (y + 1) * width]) {
            queue.add(new Path(x, y + 1, 1, 0, 1, targetX, targetY));
        }
    }

    private static void addNeighbors(PriorityQueue<Path> queue, HashSet<Integer> visited, Path previousPath, boolean[] solid, int width, int height) {
        int x = previousPath.getX();
        int y = previousPath.getY();
        if (x > 0 && !solid[x - 1 + y * width] && !visited.contains(x - 1 + y * width)) {
            queue.add(new Path(x - 1, y, previousPath));
        }
        if (x < width - 1 && !solid[x + 1 + y * width] && !visited.contains(x + 1 + y * width)) {
            queue.add(new Path(x + 1, y, previousPath));
        }
        if (y > 0 && !solid[x + (y - 1) * width] && !visited.contains(x + (y - 1) * width)) {
            queue.add(new Path(x, y - 1, previousPath));
        }
        if (y < height - 1 && !solid[x + (y + 1) * width] && !visited.contains(x + (y + 1) * width)) {
            queue.add(new Path(x, y + 1, previousPath));
        }
    }
}
