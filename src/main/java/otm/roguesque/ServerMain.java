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

    private static boolean interactive = false;
    private static boolean saveToDisk = true;
    private static boolean expectingPort = false;
    private static boolean expectingSaveFile = false;
    private static int port = Leaderboard.defaultPort;
    private static String saveFile = "roguesque-server-data.csv";

    public static void main(String[] args) {
        for (String arg : args) {
            try {
                parseArg(arg);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                return;
            }
        }
        LeaderboardServer server = new LeaderboardServer(port, saveToDisk, interactive, saveFile);
        server.start();
    }

    private static void parseArg(String arg) throws Exception {
        if (handleExpecteds(arg)) {
            return;
        } else if (arg.equals("-h") || arg.equals("--help")) {
            showHelp();
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

    private static boolean handleExpecteds(String arg) throws Exception {
        if (expectingPort) {
            port = getPort(arg);
            expectingPort = false;
            return true;
        } else if (expectingSaveFile) {
            saveFile = arg;
            expectingSaveFile = false;
            return true;
        }
        return false;
    }

    private static int getPort(String s) throws Exception {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            throw new Exception("Expected port number after the --port/-p flag!");
        }
    }

    private static void showHelp() throws Exception {
        System.out.println("Roguesque Leaderboards Server (version " + getVersion() + ")");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java -jar Roguesque-" + getVersion() + ".jar [-id]  Launches the game's leaderboard server.");
        System.out.println("Flags:");
        System.out.println("  -i, --interactive  Launches the interactive CLI along with the server.");
        System.out.println("  -d, --dry-run      Disables saving the leaderboards to disk.");
        System.out.println("  -p, --port         Sets the server port.");
        System.out.println("  -s, --store        Sets the file name where the leaderboards are saved (does not override -d).");
        throw new Exception("");
    }
}
