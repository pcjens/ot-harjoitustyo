package otm.roguesque.game.dungeon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;

/**
 * Tämä luokka kuvaa yhtä peliä, pelaajan tekemien asioiden näkökulmasta. Pelin
 * aikana Dungeon rakentaa tällaista, ja tämän voi sitten tallentaa tiedostoon.
 * Tämän jälkeen tämän voisi ladata tiedostosta, ja tähän perustuen voidaan
 * pyörittää kokonainen peli uudestaan.
 *
 * @author jens
 */
public class Replay {

    private final long seed;
    private final ArrayDeque<PlayerAction> actions;

    public Replay(long seed) {
        this.seed = seed;
        this.actions = new ArrayDeque();
    }

    public Replay(File file) throws FileNotFoundException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            this.actions = new ArrayDeque();
            this.seed = reader.read();
            int action;
            while ((action = reader.read()) != -1) {
                actions.add(PlayerAction.values()[action]);
            }
        }
    }

    public void addAction(PlayerAction action) {
        actions.addLast(action);
    }

    public PlayerAction popAction() {
        return actions.pollFirst();
    }

    public long getSeed() {
        return seed;
    }

    public void saveTo(File file) throws IOException {
        if (file.exists()) {
            file.delete();
            file.createNewFile();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write((int) (seed & 0xFFFF));
            for (PlayerAction action : actions) {
                writer.write(action.ordinal());
            }
        }
    }
}
