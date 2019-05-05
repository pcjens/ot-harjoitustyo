package otm.roguesque;

import java.util.ArrayList;
import javafx.application.Application;
import static otm.roguesque.Properties.getVersion;
import otm.roguesque.net.Leaderboard;
import otm.roguesque.net.LeaderboardClient;
import otm.roguesque.net.LeaderboardEntry;
import otm.roguesque.ui.RoguesqueApp;

/**
 * Peli käynnistyy tästä luokasta.
 *
 * @see otm.roguesque.ui.RoguesqueApp
 *
 * @author Jens Pitkänen
 */
public class Main {

    /**
     * Lataa metadataa, ja sitten käynnistää Roguesquen.
     *
     * @param args Komentoriviargumentit, joita ei oteta huomioon tällä hetkellä
     * mitenkään.
     */
    public static void main(String[] args) {
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
        LeaderboardClient client = new LeaderboardClient(Leaderboard.defaultHost, Leaderboard.defaultPort);
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
}
