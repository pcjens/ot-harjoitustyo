package otm.roguesque;

import static otm.roguesque.Properties.getVersion;
import otm.roguesque.net.Leaderboard;
import otm.roguesque.net.LeaderboardServer;

/**
 * Palvelimen pääluokka.
 *
 * @author Jens Pitkänen
 */
public class ServerMain {

    public static void main(String[] args) {
        boolean interactive = false;
        boolean saveToDisk = true;
        for (String arg : args) {
            if (arg.equals("-h") || arg.equals("--help")) {
                showHelp();
                return;
            } else if (arg.equals("-i") || arg.equals("--interactive")) {
                interactive = true;
            } else if (arg.equals("-d") || arg.equals("--dry-run")) {
                saveToDisk = false;
            } else if (arg.equals("-id") || arg.equals("-di")) {
                interactive = true;
                saveToDisk = false;
            }
        }
        LeaderboardServer server = new LeaderboardServer(Leaderboard.DEFAULT_PORT, saveToDisk, interactive);
        server.start();
    }

    private static void showHelp() {
        System.out.println("Roguesque Leaderboards Server (version " + getVersion() + ")");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java -jar Roguesque-" + getVersion() + ".jar [-id]  Launches the game's leaderboard server.");
        System.out.println("Flags:");
        System.out.println("  -i, --interactive  Launches the interactive CLI along with the server.");
        System.out.println("  -d, --dry-run      Disables saving the leaderboards to disk.");
    }
}
