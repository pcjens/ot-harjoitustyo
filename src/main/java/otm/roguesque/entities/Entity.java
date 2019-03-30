package otm.roguesque.entities;

public class Entity {

    protected Dungeon dungeon;
    protected int x;
    protected int y;

    protected void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    protected void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean move(int deltaX, int deltaY) {
        if (dungeon == null) {
            return false;
        }
        int newX = x + deltaX;
        int newY = y + deltaY;
        if (!dungeon.solid(newX, newY)) {
            this.x = newX;
            this.y = newY;
            return true;
        } else {
            return false;
        }
    }
}
