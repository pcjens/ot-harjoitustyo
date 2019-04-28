package otm.roguesque.game.dungeon;

import java.util.ArrayList;
import otm.roguesque.game.entities.AI;
import otm.roguesque.game.entities.Door;
import otm.roguesque.game.entities.Entity;
import otm.roguesque.game.entities.Player;

/**
 * Tämä luokka sisältää kaiken, mistä yksi kenttä pelissä koostuu. Käytännössä
 * tämä viittaa huoneisiin ja vihollisiin / tavaroihin joita niissä huoneissa
 * on.
 *
 * Tämä luokka sisältää myös kenttägeneraation, sillä sen siirtäminen toiseen
 * luokkaan loisi kovin paljon "peilattua" koodia (samoja muuttujia kahdessa
 * paikassa, jotka pitää synkronoida).
 *
 * @author Jens Pitkänen
 */
public class Dungeon {

    protected static final int MAX_ROOM_WIDTH = 14;
    protected static final int MAX_ROOM_HEIGHT = 12;
    protected static final int MIN_ROOM_WIDTH = 6;
    protected static final int MIN_ROOM_HEIGHT = 5;
    protected static final int MIN_ROOM_MARGIN = 2;
    protected static final int MAX_ROOMS = 10;

    private final boolean[] solid;
    private final TileType[] tiles;
    private final int width;
    private final int height;
    private final int level;

    private ArrayList<Entity> entities;
    private Player player;
    private int playerSpawnX;
    private int playerSpawnY;

    /**
     * Luo uuden pelikentän.
     *
     * @param level Kuinka mones taso kyseessä. Pienimmillään 1, tämä kuvaa
     * kentän vaikeustasoa. Peräkkäisten kenttien levelin kuuluisi nousta 1:llä
     * joka tasossa.
     */
    public Dungeon(int level) {
        this.width = (int) (MAX_ROOM_WIDTH * (Math.sqrt(MAX_ROOMS) + 1));
        this.height = (int) (MAX_ROOM_HEIGHT * (Math.sqrt(MAX_ROOMS) + 1));
        this.level = level;
        this.entities = new ArrayList();
        this.tiles = new TileType[width * height];
        this.solid = new boolean[width * height];
    }

    /**
     * Lisää uuden olion kenttään.
     *
     * @param entity Olio joka halutaan lisätä.
     * @param x Olion uuden paikan x-koordinaatti.
     * @param y Olion uuden paikan y-koordinaatti.
     */
    public final void spawnEntity(Entity entity, int x, int y) {
        this.entities.add(entity);
        entity.setDungeon(this);
        entity.setPosition(x, y);
        if (entity instanceof Player) {
            this.player = (Player) entity;
        }
    }

    /**
     * Palauttaa x-koordinaatin kohdasta, johon pelaaja pitäisi spawnata.
     *
     * @see
     * otm.roguesque.game.dungeon.Dungeon#spawnEntity(otm.roguesque.game.entities.Entity,
     * int, int)
     *
     * @return Pelaajan alkukohdan x-koordinaatti.
     */
    public int getPlayerSpawnX() {
        return playerSpawnX;
    }

    /**
     * Palauttaa y-koordinaatin kohdasta, johon pelaaja pitäisi spawnata.
     *
     * @see
     * otm.roguesque.game.dungeon.Dungeon#spawnEntity(otm.roguesque.game.entities.Entity,
     * int, int)
     *
     * @return Pelaajan alkukohdan y-koordinaatti.
     */
    public int getPlayerSpawnY() {
        return playerSpawnY;
    }

    /**
     * Palauttaa pelaajan.
     *
     * @return Kenttään lisätty pelaaja, jolla tällä hetkellä pelataan.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Palauttaa koko kentän leveyden. Käytännössä mikään olio ei koskaan
     * saavuta kentän rajoja, sillä huoneet ovat rajojen sisällä.
     *
     * @return Kentän leveys.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Palauttaa koko kentän korkeus. Käytännössä mikään olio ei koskaan saavuta
     * kentän rajoja, sillä huoneet ovat rajojen sisällä.
     *
     * @return Kentän korkeus (pituus y-akselilla).
     */
    public int getHeight() {
        return height;
    }

    /**
     * Palauttaa taulukon kaikista kentän laatoista. Huom. tämä palauttaa raa'an
     * taulukon jota luola käyttää, eli älä muokkaa tätä taulukkoa ellet halua
     * pysyviä muutoksia.
     *
     * @return Taulukko kaikista laatoista.
     */
    public TileType[] getTiles() {
        return tiles;
    }

    /**
     * Palauttaa listan kaikista kentällä olevista olioista.
     *
     * @return Kentällä olevat oliot.
     */
    public ArrayList<Entity> getEntities() {
        return entities;
    }

    /**
     * Palauttaa kentän vaikeustason.
     *
     * @see otm.roguesque.game.dungeon.Dungeon#Dungeon(int)
     *
     * @return Kentän vaikeustaso.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Palauttaa true silloin kun pelaaja voisi siirtyä seuraavaan kenttään, eli
     * ruudulla pitäisi olla näkyvissä "Seuraava taso"-nappi.
     *
     * @return Voiko pelaaja siirtyä seuraavaan kenttään tästä ruudussa?
     */
    public boolean canFinish() {
        return getTileAt(player.getX(), player.getY()) == TileType.Ladder;
    }

    /**
     * Palauttaa true mikäli annetuissa koordinaateissa on seinä, tai jokin muu
     * asia jonka läpi ei voi kävellä.
     *
     * @param x Tutkitun ruudun x-koordinaatti.
     * @param y Tutkitun ruudun y-koordinaatti.
     * @return Onko tutkittu ruutu kiinteä? (Jos on, sen läpi ei voi kävellä.)
     */
    public boolean solid(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height || tiles[x + y * width] == null) {
            return true;
        }
        return solid[x + y * width];
    }

    /**
     * Palauttaa olion annetuissa koordinaateissa.
     *
     * @param x Tutkitun ruudun x-koordinaatti.
     * @param y Tutkitun ruudun y-koordinaatti.
     * @return Olio annetussa ruudussa, voi olla null.
     */
    public Entity getEntityAt(int x, int y) {
        for (Entity e : entities) {
            if (e.getX() == x && e.getY() == y && !e.isDead()) {
                return e;
            }
        }
        return null;
    }

    /**
     * Palauttaa laatan annetuissa koordinaateissa.
     *
     * @param x Tutkitun ruudun x-koordinaatti.
     * @param y Tutkitun ruudun y-koordinaatti.
     * @return Laatan tyyppi annetussa ruudussa, voi olla null.
     */
    public TileType getTileAt(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return null;
        }
        return tiles[x + y * width];
    }

    /**
     * Pyörittää olioita (joilla on tekoäly) yhden vuoron eteenpäin.
     *
     * @see otm.roguesque.game.entities.AI
     */
    public void processRound() {
        for (Entity e : entities) {
            if (e instanceof AI) {
                ((AI) e).processRound();
            }
        }
    }

    /**
     * Siivoaa kuolleet oliot kentältä (eli poistaa kokonaan oliolistalta).
     */
    public void cleanupDeadEntities() {
        entities.forEach((entity) -> {
            Entity lastEntityInteractedWith = entity.getLastEntityInteractedWith();
            if (lastEntityInteractedWith != null && lastEntityInteractedWith.isDead()) {
                entity.resetLastEntityInteractedWith();
            }
        });
        entities.removeIf((entity) -> {
            boolean dead = entity.isDead();
            if (dead && entity instanceof Door) {
                solid[entity.getX() + entity.getY() * width] = false;
            }
            return dead;
        });
    }

    /**
     * Vain testauksessa käytetty funktio, joka liikuttaa pelaajaa tietyn määrän
     * vasemmalle/oikealle, ja sen jälkeen ylös/alas.
     *
     * @param x Kuinka monta ruutua pelaaja liikkuu oikealle? (-x liikuu x
     * kertaa vasemmalle)
     * @param y Kuinka monta ruutua pelaaja liikkuu alas? (-y liikuu y kertaa
     * ylös)
     */
    public void movePlayerNTimes(int x, int y) {
        for (int i = 0; i < Math.abs(x); i++) {
            player.move((int) Math.signum(x), 0);
            processRound();
            cleanupDeadEntities();
            player.recalculateLineOfSight(false);
        }
        for (int i = 0; i < Math.abs(y); i++) {
            player.move(0, (int) Math.signum(y));
            processRound();
            cleanupDeadEntities();
            player.recalculateLineOfSight(false);
        }
    }

    protected void updateSolidity() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (tiles[x + y * width] == TileType.Wall) {
                    this.solid[x + y * width] = true;
                }
            }
        }
        for (Entity e : entities) {
            if (e instanceof Door) {
                this.solid[e.getX() + e.getY() * width] = true;
            }
        }
    }

    protected void setPlayerSpawn(int x, int y) {
        playerSpawnX = x;
        playerSpawnY = y;
    }
}
