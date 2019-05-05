package otm.roguesque.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Luokka joka sisältää vakioita joita LeaderboardServer ja LeaderboardClient
 * käyttävät.
 *
 * @author Jens Pitkänen
 */
public class Leaderboard {

    public static String defaultHost = "rgsq.pitkanen.dev";
    public static int defaultPort = 5378;

    static {
        File configFile = new File("rgsq-server.config");
        if (configFile.exists()) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(configFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] split = line.split("=");
                    if (split.length == 2) {
                        if (split[0].toLowerCase().trim().equals("host")) {
                            defaultHost = split[1];
                        } else if (split[0].toLowerCase().trim().equals("port")) {
                            defaultPort = Integer.parseInt(split[1]);
                        }
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Leaderboard.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | NumberFormatException ex) {
                Logger.getLogger(Leaderboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
