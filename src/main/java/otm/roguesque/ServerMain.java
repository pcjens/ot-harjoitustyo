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
        boolean expectingPort = false;
        boolean expectingSaveFile = false;
        int port = Leaderboard.DEFAULT_PORT;
        String saveFile = "roguesque-server-data.csv";
        for (String arg : args) {
            if (expectingPort) {
                try {
                    port = Integer.parseInt(arg);
                } catch (NumberFormatException ex) {
                    System.out.println("Expected port number after the --port/-p flag!");
                    return;
                }
                expectingPort = false;
            } else if (expectingSaveFile) {
                saveFile = arg;
                expectingSaveFile = false;
            } else if (arg.equals("-h") || arg.equals("--help")) {
                showHelp();
                return;
            } else if (arg.equals("-i") || arg.equals("--interactive")) {
                interactive = true;
            } else if (arg.equals("-d") || arg.equals("--dry-run")) {
                saveToDisk = false;
            } else if (arg.equals("-id") || arg.equals("-di")) {
                interactive = true;
                saveToDisk = false;
            } else if (arg.equals("-p") || arg.equals("--port")) {
                expectingPort = true;
            } else if (arg.equals("-s") || arg.equals("--store")) {
                expectingSaveFile = true;
            }
        }
        LeaderboardServer server = new LeaderboardServer(port, saveToDisk, interactive, saveFile);
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
        System.out.println("  -p, --port         Sets the server port.");
        System.out.println("  -s, --store        Sets the file name where the leaderboards are saved (does not override -d).");
    }
}
