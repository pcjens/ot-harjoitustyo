package otm.roguesque.game.entities;

import javafx.scene.image.Image;
import otm.roguesque.game.dungeon.Dungeon;

public abstract class Entity {

    protected Dungeon dungeon;
    protected int x;
    protected int y;
    protected int maxHealth = 10;
    protected int health = 10;
    protected int attack = 2;
    protected int defense = 1;
    protected String name = "Unnamed";
    protected String description = "Mysterious.";
    protected String friendlyGroup = "Default";
    protected Image image;
    protected Entity lastEntityInteractedWith = null;

    protected Entity(int maxHealth, int attack, int defense, String name, String description, String friendlyGroup, String spritePath) {
        this.maxHealth = this.health = maxHealth;
        this.attack = attack;
        this.defense = defense;
        this.name = name;
        this.description = description;
        this.friendlyGroup = friendlyGroup;
        if (!spritePath.isEmpty()) {
            this.image = new Image(getClass().getResourceAsStream(spritePath), 32, 32, true, false);
        }
    }

    protected final void loadImage(String spritePath) {
        this.image = new Image(getClass().getResourceAsStream(spritePath), 32, 32, true, false);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Entity getLastEntityInteractedWith() {
        return lastEntityInteractedWith;
    }

    public String getDescription() {
        return String.format("%s\n\n%s\n\nHP: %d/%d\nATK: %d\nDEF: %d", name, description, health, maxHealth, attack, defense);
    }

    public void takeDamage(int attack) {
        this.health -= Math.max(0, attack - this.defense);
    }

    public boolean isDead() {
        return this.health <= 0;
    }

    public void resetLastEntityInteractedWith() {
        lastEntityInteractedWith = null;
    }

    public boolean move(int deltaX, int deltaY) {
        if (dungeon == null) {
            return false;
        }

        int newX = x + deltaX;
        int newY = y + deltaY;

        if (hitAndCollide(newX, newY)) {
            return false;
        }

        return moveAndCollide(newX, newY);
    }

    private boolean hitAndCollide(int newX, int newY) {
        Entity hitEntity = dungeon.getEntityAt(newX, newY);
        if (hitEntity != null) {
            lastEntityInteractedWith = hitEntity;
            hitEntity.reactToAttack(this);
            if (hitEntity.friendlyGroup.equals(this.friendlyGroup)) {
                return true;
            }
            hitEntity.takeDamage(attack);
            if (!hitEntity.isDead()) {
                return true;
            }
        }

        return false;
    }

    private boolean moveAndCollide(int newX, int newY) {
        if (!dungeon.solid(newX, newY)) {
            this.x = newX;
            this.y = newY;
            return true;
        } else {
            return false;
        }
    }

    public Image getImage() {
        return image;
    }

    protected void reactToAttack(Entity attackingEntity) {
    }
}
