package otm.roguesque.game.dungeon;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import otm.roguesque.game.GlobalRandom;
import otm.roguesque.game.dungeon.replay.PlayerAction;
import otm.roguesque.game.dungeon.replay.Replay;
import otm.roguesque.game.entities.AI;
import otm.roguesque.game.entities.Door;
import otm.roguesque.game.entities.Entity;
import otm.roguesque.game.entities.Player;
import otm.roguesque.util.Path;
import otm.roguesque.util.Pathfinder;

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

    protected static final int MAX_ROOM_WIDTH = 11;
    protected static final int MAX_ROOM_HEIGHT = 9;
    protected static final int MIN_ROOM_WIDTH = 6;
    protected static final int MIN_ROOM_HEIGHT = 5;
    protected static final int MIN_ROOM_MARGIN = 2;
    protected static final int MAX_ROOMS = 10;
    protected static final int MIN_ROOMS = 5;

    private final boolean[] solid;
    private final boolean[] doored;
    private final TileType[] tiles;
    private final ArrayList<Entity> entities;
    private final Player player;
    private final int width;
    private final int height;
    private final Replay replay;
    private int level;

    private int playerSpawnX;
    private int playerSpawnY;

    /**
     * Luo uuden pelikentän.
     *
     * @param seed Alkuperäinen seed-luku kentän generointiin.
     * @param level Ensimmäisen kentän vaikeustaso.
     */
    public Dungeon(long seed, int level) {
        this.width = (int) (MAX_ROOM_WIDTH * (Math.sqrt(MAX_ROOMS) + 1));
        this.height = (int) (MAX_ROOM_HEIGHT * (Math.sqrt(MAX_ROOMS) + 1));
        this.tiles = new TileType[width * height];
        this.entities = new ArrayList();
        this.player = new Player();
        this.solid = new boolean[width * height];
        this.doored = new boolean[width * height];
        this.replay = new Replay(seed);
        this.level = level;
        regenerateDungeon(seed);
    }

    /**
     * Tekee jonkin pelaajan määrittämän actionin.
     *
     * @param action Mitä pelaaja haluaa tehdä?
     */
    public void runPlayerAction(PlayerAction action) {
        switch (action) {
            case NextLevel:
                if (canFinish()) {
                    level++;
                    regenerateDungeon(GlobalRandom.get().nextLong());
                }
                break;
            case MoveUp:
                player.move(0, -1);
                break;
            case MoveLeft:
                player.move(-1, 0);
                break;
            case MoveDown:
                player.move(0, 1);
                break;
            case MoveRight:
                player.move(1, 0);
                break;
            default:
                break;
        }
        replay.addAction(action);
    }

    /**
     * Palauttaa Replay-olion. Tämän voi tallentaa tiedostoon myöhempää katselua
     * varten.
     *
     * @return
     */
    public Replay getReplay() {
        return replay;
    }

    private void regenerateDungeon(long seed) {
        entities.clear();
        clearTiles();

        GlobalRandom.reset(seed);
        DungeonGenerator.generateNewDungeon(this);
        spawnEntity(player, playerSpawnX, playerSpawnY);
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
        return solid[x + y * width] || doored[x + y * width];
    }

    /**
     * Palauttaa olion annetuissa koordinaateissa.
     *
     * @param x Tutkitun ruudun x-koordinaatti.
     * @param y Tutkitun ruudun y-koordinaatti.
     * @return Olio annetussa ruudussa, voi olla null.
     */
    // TODO: Optimize this further, this is called a lot
    // Also could just remove the `doored` array if this was better.
    public Entity getEntityAt(int x, int y) {
        for (Entity e : entities) {
            if (e.getX() == x && e.getY() == y && !e.isDead()) {
                return e;
            }
        }
        return null;
    }

    /**
     * Palauttaa lähimmän olion joka on kyseistä tyyppiä.
     *
     * @param type Haetun olion tyyppi.
     * @param x Haun aloituksen x-koordinaatti.
     * @param y Haun aloituksen y-koordinaatti.
     * @return Lähin olio annettuihin koordinaatteihin.
     */
    public Entity getClosestEntityOfType(Class<?> type, int x, int y) {
        HashSet<Integer> visited = new HashSet();
        ArrayDeque<Integer> queue = new ArrayDeque();
        queue.add(x + y * width);
        while (!queue.isEmpty()) {
            int current = queue.pollFirst();
            if (visited.contains(current) || solid[current]) {
                continue;
            }
            visited.add(current);
            Entity entity = getEntityAt(current % width, current / width);
            if (entity != null && entity.getClass() == type) {
                return entity;
            }
            addNeighboringTiles(queue, current);
        }
        return null;
    }

    private void addNeighboringTiles(ArrayDeque<Integer> queue, int index) {
        int x = index % width;
        int y = index / width;
        if (x > 0) {
            queue.add(index - 1);
        }
        if (x < width - 1) {
            queue.add(index + 1);
        }
        if (y > 0) {
            queue.add(index - height);
        }
        if (y < height - 1) {
            queue.add(index + width);
        }
    }

    /**
     * Palauttaa vektorin suuntaan, johon koordinaateista pitäisi siirtyä, jotta
     * seuraisi lyhintä reittiä sen luokse, ja vektorin pituus on reitin pituus.
     *
     * @param from Polkua seuraava olio.
     * @param to Olio jota seurataan.
     * @return Polku olioon.
     */
    public Path getPathTo(Entity from, Entity to) {
        return Pathfinder.findPath(from.getX(), from.getY(), to.getX(), to.getY(), solid, width, height);
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
     * Pyörittää olioita (joilla on tekoäly ja ovat hengissä) yhden vuoron
     * eteenpäin.
     *
     * @see otm.roguesque.game.entities.AI
     */
    public void processRound() {
        for (Entity e : entities) {
            if (e instanceof AI && !e.isDead()) {
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
                doored[entity.getX() + entity.getY() * width] = false;
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
            runPlayerAction(x < 0 ? PlayerAction.MoveLeft : PlayerAction.MoveRight);
            processRound();
            cleanupDeadEntities();
            player.recalculateLineOfSight(false);
        }
        for (int i = 0; i < Math.abs(y); i++) {
            runPlayerAction(y < 0 ? PlayerAction.MoveUp : PlayerAction.MoveDown);
            processRound();
            cleanupDeadEntities();
            player.recalculateLineOfSight(false);
        }
    }

    protected void updateSolidity() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (tiles[x + y * width] == TileType.Wall || tiles[x + y * width] == null) {
                    this.solid[x + y * width] = true;
                }
            }
        }
        for (Entity e : entities) {
            if (e instanceof Door) {
                this.doored[e.getX() + e.getY() * width] = true;
            }
        }
    }

    protected void setPlayerSpawn(int x, int y) {
        playerSpawnX = x;
        playerSpawnY = y;
    }

    private void clearTiles() {
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = null;
            solid[i] = false;
            doored[i] = false;
        }
    }
}
