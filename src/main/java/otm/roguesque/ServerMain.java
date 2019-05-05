package otm.roguesque;

import otm.roguesque.net.Leaderboard;
import otm.roguesque.net.LeaderboardServer;

/**
 * Palvelimen pääluokka.
 *
 * @author Jens Pitkänen
 */
public class ServerMain {

    public static void main(String[] args) {
        LeaderboardServer server = new LeaderboardServer(Leaderboard.DEFAULT_PORT, true, true);
        server.start();
    }
}
