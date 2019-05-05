package otm.roguesque;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import otm.roguesque.net.Leaderboard;
import otm.roguesque.net.LeaderboardClient;
import otm.roguesque.net.LeaderboardEntry;
import otm.roguesque.ui.RoguesqueApp;

/**
 * Peli käynnistyy tästä luokasta. Toimii lähinnä RoguesqueAppin käynnistäjänä,
 * mutta lukee myös hieman metadataa (version) ohjelman pom.properties
 * tiedostosta.
 *
 * @see otm.roguesque.ui.RoguesqueApp
 *
 * @author Jens Pitkänen
 */
public class Main {

    private static String version = "unknown";

    /**
     * Palauttaa ohjelman version perustuen pom.propertiesiin. Huom.
     * Netbeansista käynnistetyssä versiossa tämä palauttaa "unknown", sillä
     * pom.properties ei löydy sen käynnistämästä .jarista jostain syystä.
     * Mavenin package-komennolla luodut .jarit kuitenkin toimivat oikein.
     *
     * @return Versio. (Yleensä numero, mahdollisesti päättyy "-SNAPSHOT".)
     */
    public static String getVersion() {
        return version;
    }

    /**
     * Lataa metadataa, ja sitten käynnistää Roguesquen.
     *
     * @param args Komentoriviargumentit, joita ei oteta huomioon tällä hetkellä
     * mitenkään.
     */
    public static void main(String[] args) {
        try {
            loadProperties();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (args.length == 0) {
            Application.launch(RoguesqueApp.class);
        } else if (args.length == 1 && (args[0].equals("--server") || args[0].equals("-s"))) {
            ServerMain.main(args);
        } else if (args.length == 1 && (args[0].equals("--leaderboards") || args[0].equals("-l"))) {
            printLeaderboards();
        } else {
            showHelp();
        }
    }

    private static void showHelp() {
        System.out.println("Roguesque (version " + getVersion() + ")");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java -jar Roguesque-" + getVersion() + ".jar                 Launches the game.");
        System.out.println("  java -jar Roguesque-" + getVersion() + ".jar --server        Launches the game's leaderboard server.");
        System.out.println("  java -jar Roguesque-" + getVersion() + ".jar --leaderboards  Fetches the leaderboards from the default leaderboard server.");
    }

    private static void printLeaderboards() {
        LeaderboardClient client = new LeaderboardClient(Leaderboard.DEFAULT_HOST, Leaderboard.DEFAULT_PORT);
        printLeaderboard("Top 10 entries today:", client.getTopDaily());
        printLeaderboard("Top 10 entries this week:", client.getTopWeekly());
        printLeaderboard("Top 10 entries of all time:", client.getTopOfAllTime());
    }

    private static void printLeaderboard(String title, ArrayList<LeaderboardEntry> entries) {
        System.out.println(title);
        int i = 1;
        for (LeaderboardEntry entry : entries) {
            System.out.println("  " + (i++) + ". " + entry.getName() + " - " + entry.getScore());
        }
    }

    private static void loadProperties() throws IOException {
        InputStream stream = Main.class.getResourceAsStream("/META-INF/maven/otm/Roguesque/pom.properties");
        if (stream == null) {
            System.err.println("Failed to load pom.properties!");
            return;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("=");
            if (parts.length == 2 && parts[0].equals("version")) {
                version = parts[1];
            }
        }
    }
}
