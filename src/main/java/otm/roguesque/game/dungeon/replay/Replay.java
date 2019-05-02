package otm.roguesque.game.dungeon.replay;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import otm.roguesque.game.dungeon.Dungeon;

/**
 * Tämä luokka kuvaa yhtä peliä, pelaajan tekemien asioiden näkökulmasta. Pelin
 * aikana Dungeon rakentaa tällaista, ja tämän voi sitten tallentaa tiedostoon.
 * Jälkeenpäin tämän voisi ladata tiedostosta, ja tähän perustuen voidaan
 * pyörittää kokonainen peli uudestaan.
 *
 * @see otm.roguesque.game.dungeon.replay.Replay#popAction()
 * @see otm.roguesque.game.dungeon.replay.Replay#getSeed()
 *
 * @author Jens Pitkänen
 */
public class Replay {

    private static final byte[] FILE_HEADER = new byte[]{'R', 'G', 'S', 'Q'};
    private static final byte FILE_HEADER_VERSION = 0;
    private final long seed;
    private final ArrayDeque<PlayerAction> actions;

    /**
     * Luo uuden Replayn, tarkoitettu kutsuttavaksi Dungeonista.
     *
     * @see otm.roguesque.game.dungeon.Dungeon#Dungeon(long, int)
     *
     * @param seed Seed-luku johon ylempänä mainittu Dungeon pohjautuu.
     */
    public Replay(long seed) {
        this.seed = seed;
        this.actions = new ArrayDeque();
    }

    /**
     * Lataa Replayn tiedostosta.
     *
     * @see otm.roguesque.game.dungeon.replay.Replay#saveTo(java.nio.file.Path)
     *
     * @param path Polku tiedostoon johon Replay on tallennettu.
     * @throws FileNotFoundException Jos tiedostoa ei löydy, siitä ei voida
     * luoda Replayta.
     * @throws IOException Jos tiedoston lukemisessa tulee ongelma, siitä ei
     * void aluoda Replayta.
     */
    public Replay(Path path) throws IOException {
        byte[] contents = Files.readAllBytes(path);
        for (int i = 0; i < FILE_HEADER.length; i++) {
            if (contents[i] != FILE_HEADER[i]) {
                throw new IOException("Invalid file header.");
            }
        }

        actions = new ArrayDeque();
        // Switch based on the file version
        switch (contents[FILE_HEADER.length]) {
            case 0:
                seed = loadReplay(contents, FILE_HEADER.length + 1);
                break;
            default:
                throw new IOException("Invalid file version. (This could be caused by trying to load a newer save on an old version.)");
        }
    }

    private long loadReplay(byte[] bytes, int offset) {
        long loadedSeed = 0;
        for (int shift = 0; shift < 8; shift++) {
            loadedSeed <<= 8;
            loadedSeed |= bytes[offset + shift] & 0xFF;
        }
        for (int i = offset + 8; i < bytes.length; i += 2) {
            byte count = bytes[i];
            byte action = bytes[i + 1];
            actions.add(new PlayerAction(PlayerActionType.values()[action], count));
        }
        System.out.println(loadedSeed);
        return loadedSeed;
    }

    /**
     * Tallentaa Replayn tiedostoon.
     *
     * @see otm.roguesque.game.dungeon.replay.Replay#Replay(java.nio.file.Path)
     *
     * @param path Tiedosto johon Replay tallennetaan.
     * @throws IOException Tiedoston kirjoittamisessa voi tulla vastaan
     * ongelmia.
     */
    public void saveTo(Path path) throws IOException {
        byte[] bytes = new byte[FILE_HEADER.length + 9 + actions.size() * 2];
        System.arraycopy(FILE_HEADER, 0, bytes, 0, FILE_HEADER.length);
        int index = FILE_HEADER.length;
        bytes[index++] = FILE_HEADER_VERSION;
        long savedSeed = seed;
        for (int shift = 7; shift >= 0; shift--) {
            bytes[index + shift] = (byte) (savedSeed & 0xFF);
            savedSeed >>= 8;
        }
        index += 8;
        System.out.println(seed);
        for (PlayerAction action : actions) {
            bytes[index++] = action.getCount();
            bytes[index++] = (byte) action.getType().ordinal();
        }
        Files.write(path, bytes, StandardOpenOption.CREATE);
    }

    /**
     * Lisää uusi pelaaja-action listaan. Tätä kutsutaan Dungeonista pelin
     * edetessä.
     *
     * @param action Pelaajan tekemä asia.
     */
    public void addAction(PlayerActionType action) {
        if (!actions.isEmpty()) {
            PlayerAction latestAction = actions.getLast();
            if (latestAction.getType() == action && latestAction.getCount() < 127) {
                latestAction.increment();
                return;
            }
        }
        actions.addLast(new PlayerAction(action, (byte) 1));
    }

    /**
     * Lukee yhden pelaaja-actionin listasta, lähtien ensimmäisestä lisätystä.
     *
     * @see
     * otm.roguesque.game.dungeon.replay.Replay#addAction(otm.roguesque.game.dungeon.replay.PlayerActionType)
     *
     * @return Pelaajan tekemä asia.
     */
    public PlayerActionType popAction() {
        if (actions.isEmpty()) {
            return null;
        }
        PlayerAction action = actions.peekFirst();
        action.decrement();
        if (action.getCount() <= 0) {
            actions.removeFirst();
        }
        return action.getType();
    }

    /**
     * Palauttaa sen Dungeonin seed-luvun, jossa tämä Replay-instanssi alunperin
     * luotiin.
     *
     * @return Tämän Replayn Dungeonin seed-luku.
     */
    public long getSeed() {
        return seed;
    }

    /**
     * Pyörittää jokaisen pelaajan tekemän asian järjestyksessä läpi annetun
     * Dungeonin sisällä.
     *
     * @see otm.roguesque.game.dungeon.replay.Replay#getSeed()
     *
     * @param dungeon Dungeon, jossa pelaajan tekemiset simuloidaan. Tämän
     * Dungeonin seed-luku tulee olla sama kuin tämän Replayn.
     */
    public void play(Dungeon dungeon) {
        PlayerActionType action;
        while ((action = popAction()) != null) {
            dungeon.runPlayerAction(action);
            if (action.proceedsRound()) {
                dungeon.processRound();
            }
        }
    }
}
