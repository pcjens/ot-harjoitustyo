package otm.roguesque.game.dungeon.replay;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

    private final short seed;
    private final ArrayDeque<PlayerAction> actions;

    /**
     * Luo uuden Replayn, tarkoitettu kutsuttavaksi Dungeonista.
     *
     * @see otm.roguesque.game.dungeon.Dungeon#Dungeon(short, int)
     *
     * @param seed Seed-luku johon ylempänä mainittu Dungeon pohjautuu.
     */
    public Replay(short seed) {
        this.seed = seed;
        this.actions = new ArrayDeque();
    }

    /**
     * Lataa Replayn tiedostosta.
     *
     * @see otm.roguesque.game.dungeon.replay.Replay#saveTo(java.io.File)
     *
     * @param file Tiedosto johon Replay on tallennettu.
     * @throws FileNotFoundException Jos tiedostoa ei löydy, siitä ei voida
     * luoda Replayta.
     * @throws IOException Jos tiedoston lukemisessa tulee ongelma, siitä ei
     * void aluoda Replayta.
     */
    public Replay(File file) throws FileNotFoundException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            this.actions = new ArrayDeque();
            this.seed = (short) reader.read();
            int count;
            int action;
            while ((count = reader.read()) != -1 && (action = reader.read()) != -1) {
                actions.add(new PlayerAction(PlayerActionType.values()[action], count));
            }
        }
    }

    /**
     * Tallentaa Replayn tiedostoon.
     *
     * @see otm.roguesque.game.dungeon.replay.Replay#Replay(java.io.File)
     *
     * @param file Tiedosto johon Replay tallennetaan.
     * @throws IOException Tiedoston kirjoittamisessa voi tulla vastaan
     * ongelmia.
     */
    public void saveTo(File file) throws IOException {
        if (file.exists()) {
            file.delete();
            file.createNewFile();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write((int) (seed & 0xFFFF));
            for (PlayerAction action : actions) {
                writer.write(action.getCount());
                writer.write(action.getType().ordinal());
            }
        }
    }

    /**
     * Lisää uusi pelaaja-action listaan. Tätä kutsutaan Dungeonista pelin
     * edetessä.
     *
     * @param action Pelaajan tekemä asia.
     */
    public void addAction(PlayerActionType action) {
        if (!actions.isEmpty() && actions.getLast().getType() == action) {
            actions.getLast().increment();
        } else {
            actions.addLast(new PlayerAction(action, 1));
        }
    }

    /**
     * Lukee yhden pelaaja-actionin listasta, lähtien ensimmäisestä lisätystä.
     *
     * @see
     * otm.roguesque.game.dungeon.replay.Replay#addAction(otm.roguesque.game.dungeon.replay.PlayerAction)
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
    public short getSeed() {
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
