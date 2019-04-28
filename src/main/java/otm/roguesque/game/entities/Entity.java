package otm.roguesque.game.entities;

import javafx.scene.image.Image;
import otm.roguesque.game.dungeon.Dungeon;
import otm.roguesque.util.Vector;

/**
 * Pohjaluokka kaikille pelin olioille.
 *
 * @author Jens Pitkänen
 */
public abstract class Entity {

    protected Dungeon dungeon;
    protected int x;
    protected int y;
    protected int maxHealth;
    protected int health;
    protected int attack;
    protected int defense;
    protected String name;
    protected String description;
    protected String friendlyGroup;
    protected Image image;
    protected Entity lastEntityInteractedWith;

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

    /**
     * Palauttaa vektorin toiseen olioon. Eli: jos palautetun vektorin
     * x-komponentin lisää tämän olion x-koordinaattiin, ja y-komponentin
     * y-koordinaattiin, saa otherEntityn koordinaatit.
     *
     * @param otherEntity Toinen olio.
     * @return Vektori tästä oliosta toiseen.
     */
    public Vector getVectorTo(Entity otherEntity) {
        return new Vector(otherEntity.getX() - getX(), otherEntity.getY() - getY());
    }

    /**
     * Palauttaa vektorin siihen suuntaan, mihin olion pitäisi liikkua, jotta se
     * siirtyisi kohti otherEntityä. Vektorin suunta on liikkumasuunta, vektorin
     * pituus on löydetyn reitin pituus.
     *
     * @param otherEntity Toinen olio.
     * @return Vektori suuntaan, mihin olion pitäisi siirtyä päätyäkseen toisen
     * luokse.
     */
    public Vector pathfindTo(Entity otherEntity) {
        return new Vector(otherEntity.getX() - getX(), otherEntity.getY() - getY());
    }

    /**
     * Siirtää olion annettuihin koordinaatteihin.
     *
     * @param x Olion uusi x-koordinaatti.
     * @param y Olion uusi y-koordinaatti.
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Päivittää olion viittauksen kenttään, jossa se on. Tarkoitettu
     * kutsuttavaksi lähinnä Dungeonin spawnEntitystä.
     *
     * @see
     * otm.roguesque.game.dungeon.Dungeon#spawnEntity(otm.roguesque.game.entities.Entity,
     * int, int)
     *
     * @param dungeon Kenttä johon olio luodaan.
     */
    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    /**
     * Palauttaa olion maksimimäärän elämäpisteitä.
     *
     * @return Suurin mahdollinen arvo johon elämäpisteet voivat nousta.
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Palauttaa olion tämänhetkiset elämäpisteet.
     *
     * @return Elämäpisteet.
     */
    public int getHealth() {
        return health;
    }

    /**
     * Palauttaa olion hyökkäysarvon.
     *
     * @see otm.roguesque.game.entities.Entity#getDefense()
     *
     * @return Hyökkäysarvo. Kun olio lyö vastustajaa, vastustaja menettää
     * maksimissaan atk - def elämäpisteitä, missä atk on tämä, ja def on
     * vastustajan puolustusarvo jonka saa funktiolla getDefense.
     */
    public int getAttack() {
        return attack;
    }

    /**
     * Palauttaa olion hyökkäysarvon.
     *
     * @see otm.roguesque.game.entities.Entity#getAttack()
     *
     * @return Puolustusarvo. Kun vastustaja lyö tätä oliota, olio menettää
     * maksimissaan atk - def elämäpisteitä, missä atk on vastustajan
     * hyökkäysarvo, jonka saa funktiolla getAttack, ja def on tämä.
     */
    public int getDefense() {
        return defense;
    }

    /**
     * Olion x-koordinaatti.
     *
     * @return Olion x-koordinaatti.
     */
    public int getX() {
        return x;
    }

    /**
     * Olion y-koordinaatti.
     *
     * @return Olion y-koordinaatti.
     */
    public int getY() {
        return y;
    }

    /**
     * Palauttaa viimeisimmän olion, jonka kanssa tämä olio törmäsi. Eli
     * käytännössä se minkä kanssa käydään taistelua.
     *
     * @return Viimeisin olio, jonka kanssa tämä olio törmäsi.
     */
    public Entity getLastEntityInteractedWith() {
        return lastEntityInteractedWith;
    }

    /**
     * Palauttaa kuvauksen oliosta. Sisältää olion nimen, vapaan kuvauksen, sekä
     * elämä/hyökkäys/puolustusarvot.
     *
     * @return Kuvaus oliosta.
     */
    public String getDescription() {
        return String.format("%s\n\n%s\n\nHP: %d/%d\nATK: %d\nDEF: %d", name, description, health, maxHealth, attack, defense);
    }

    /**
     * Palauttaa olion hengissäolotilan.
     *
     * @return Onko olio kuollut, eli onko sen elämäpisteet pienemmät tai
     * yhtäsuuri kuin 0?
     */
    public boolean isDead() {
        return this.health <= 0;
    }

    /**
     * Nollaa tiedon viimeisimmästä oliosta johon tämä olio törmäsi.
     *
     * @see otm.roguesque.game.entities.Entity#getLastEntityInteractedWith()
     */
    public void resetLastEntityInteractedWith() {
        lastEntityInteractedWith = null;
    }

    /**
     * Palauttaa kuvan joka piirretään kentälle kuvaamaan tätä oliota.
     *
     * @return Olion kuva.
     */
    public Image getImage() {
        return image;
    }

    /**
     * Liikuta olio uuteen paikkaan.
     *
     * @param deltaX Kuinka paljon nykyiseen x-koordinaattiin lisätään.
     * @param deltaY Kuinka paljon nykyiseen y-koordinaattiin lisätään.
     * @return Pystyikö olio liikkumaan uuteen paikkaan?
     */
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

    private void takeDamage(int attack) {
        this.health -= Math.max(0, attack - this.defense);
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

    protected void reactToAttack(Entity attackingEntity) {
    }
}
