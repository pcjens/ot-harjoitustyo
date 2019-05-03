package otm.roguesque.game.entities;

import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import otm.roguesque.game.GlobalRandom;
import otm.roguesque.game.dungeon.Dungeon;
import otm.roguesque.ui.HitNotification;
import otm.roguesque.util.Vector;

/**
 * Pohjaluokka kaikille pelin olioille.
 *
 * @author Jens Pitkänen
 */
public abstract class Entity {

    private Dungeon dungeon;
    private int x;
    private int y;
    private int maxHealth;
    private int health;
    private int damage;
    private int attack;
    private int defense;
    private String name;
    private String description;
    private String friendlyGroup;
    private Image image;
    private Entity lastEntityInteractedWith;
    private boolean invulnerable;

    private double offsetX = 0;
    private double offsetY = 0;

    private ArrayList<HitNotification> notifications = new ArrayList();

    Entity(int maxHealth, int damage, int attack, int defense, String name, String description, String friendlyGroup, Image image) {
        this.maxHealth = this.health = maxHealth;
        this.damage = damage;
        this.attack = attack;
        this.defense = defense;
        this.name = name;
        this.description = description;
        this.friendlyGroup = friendlyGroup;
        this.image = image;
        this.invulnerable = false;
    }

    /**
     * Piirtää notifikaatiot.
     *
     * @param ctx Piirtokonteksti.
     */
    public void drawNotifications(GraphicsContext ctx) {
        for (HitNotification n : notifications) {
            n.draw(ctx);
        }
    }

    /**
     * Päivittää notifikaatiot.
     *
     * @param deltaTime Delta-aika.
     */
    public void updateNotifications(float deltaTime) {
        for (HitNotification n : notifications) {
            n.update(deltaTime);
        }
        notifications.removeIf(n -> n.hasDisappeared());
    }

    private void hitNotify(int amount, float length) {
        for (HitNotification n : notifications) {
            n.bump();
        }
        HitNotification notif = new HitNotification(8, 8, amount, length);
        notifications.add(notif);
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
        int oldX = this.x;
        int oldY = this.y;
        this.x = x;
        this.y = y;
        dungeon.updateEntityCache(this, oldX, oldY);
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
     * @return Hyökkäysarvo.
     */
    public int getAttack() {
        return attack;
    }

    /**
     * Palauttaa olion vahinkoarvon.
     *
     * @return Vahinkoarvo.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Palauttaa olion puolustusarvon.
     *
     * @return Puolustusarvo.
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
    public String getRichDescription() {
        return String.format("%s\n\n%s\n\nHP: %d/%d\nDMG: %d\nATK: %d\nDEF: %d", name, description, health, maxHealth, damage, attack, defense);
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
     * Palauttaa animaatio-siirtymän x-akselilla tälle oliolle, ruuduissa.
     *
     * @return Animaatio-siirtymä x-akselilla tälle oliolle, ruuduissa.
     */
    public double getOffsetX() {
        return offsetX;
    }

    /**
     * Palauttaa animaatio-siirtymän y-akselilla tälle oliolle, ruuduissa.
     *
     * @return Animaatio-siirtymä y-akselilla tälle oliolle, ruuduissa.
     */
    public double getOffsetY() {
        return offsetY;
    }

    /**
     * Päivittää animaatiot deltaTimen verran eteenpäin.
     *
     * @param deltaSeconds Delta-aika.
     */
    public void updateAnimation(float deltaSeconds) {
        double sign = Math.signum(offsetX);
        this.offsetX -= sign * deltaSeconds * 10;
        if (sign != Math.signum(offsetX)) {
            offsetX = 0;
        }
        sign = Math.signum(offsetY);
        this.offsetY -= sign * deltaSeconds * 10;
        if (sign != Math.signum(offsetY)) {
            offsetY = 0;
        }
    }

    private void animateMovement(boolean moved, int deltaX, int deltaY) {
        if (moved) {
            offsetX = -deltaX;
            offsetY = -deltaY;
        } else {
            offsetX = 0.5 * deltaX;
            offsetY = 0.5 * deltaY;
        }
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

        boolean moved;
        if (hitAndCollide(newX, newY)) {
            moved = false;
        } else {
            moved = moveAndCollide(newX, newY);
        }

        animateMovement(moved, deltaX, deltaY);
        return moved;
    }

    private void takeDamage(Entity attacker) {
        if (attacker.damage == 0) {
            hitNotify(0, 0.5f);
        } else {
            int atkRoll = GlobalRandom.get().nextInt(attacker.attack + 2);
            int defRoll = GlobalRandom.get().nextInt(this.defense + 2);
            int finalDamage = atkRoll >= defRoll ? (GlobalRandom.get().nextInt(attacker.damage) + 1) : 0;
            this.health -= finalDamage;
            hitNotify(finalDamage, 0.5f);
        }
    }

    private boolean hitAndCollide(int newX, int newY) {
        boolean result = false;
        for (Entity hitEntity : dungeon.getEntitiesAt(newX, newY)) {
            lastEntityInteractedWith = hitEntity;
            hitEntity.reactToAttack(this);
            if (hitEntity.friendlyGroup.equals(this.friendlyGroup) || hitEntity.invulnerable) {
                result = true;
                continue;
            }
            hitEntity.takeDamage(this);
            if (!hitEntity.isDead()) {
                result = true;
            }
        }
        return result;
    }

    private boolean moveAndCollide(int newX, int newY) {
        if (!dungeon.solid(newX, newY)) {
            setPosition(newX, newY);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Reaktio, kun jokin hyökkää tähän olioon. Oletuksena ei siis mitään.
     *
     * @param attackingEntity Olio joka hyökkää.
     */
    protected void reactToAttack(Entity attackingEntity) {
    }

    /**
     * Palauttaa Dungeonin, jossa tämä olio elää.
     *
     * @return Dungeon, jossa tämä olio on.
     */
    protected final Dungeon getDungeon() {
        return dungeon;
    }

    /**
     * Palauttaa olion nimen.
     *
     * @return Olion nimi.
     */
    protected final String getName() {
        return name;
    }

    /**
     * Palauttaa olion kuvauksen.
     *
     * @return Olion kuvaus.
     */
    protected final String getDescription() {
        return description;
    }

    /**
     * Asettaa annetun kuvan olion kuvaksi.
     *
     * @param sprite Uusi kuva.
     */
    protected final void setImage(Image sprite) {
        this.image = sprite;
    }

    /**
     * Asettaa olion elämäpisteet.
     *
     * @param health Uudet elämäpisteet.
     */
    protected final void setHealth(int health) {
        this.health = health;
    }

    /**
     * Asettaa olion vahinkoarvon.
     *
     * @param damage Uusi vahinkoarvo.
     */
    protected final void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * Asettaa olion hyökkäysarvon.
     *
     * @param attack Uusi hyökkäysarvo.
     */
    protected final void setAttack(int attack) {
        this.attack = attack;
    }

    /**
     * Asettaa olion puolustusarvon.
     *
     * @param defense Uusi puolustusarvo.
     */
    protected final void setDefense(int defense) {
        this.defense = defense;
    }

    /**
     * Asettaa olion kuvauksen.
     *
     * @param description Uusi kuvaus.
     */
    protected final void setDescription(String description) {
        this.description = description;
    }

    /**
     * Asettaa olion nimen.
     *
     * @param name Uusi nimi.
     */
    protected final void setName(String name) {
        this.name = name;
    }

    protected final void setInvulnerability(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }
}
