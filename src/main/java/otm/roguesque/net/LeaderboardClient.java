package otm.roguesque.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Apuluokka jolla voi lähettää ja vastaanottaa tietoa LeaderboardServeriltä.
 *
 * @author Jens Pitkänen
 */
public class LeaderboardClient {

    private String host;
    private int port;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    /**
     * Tallentaa hostin ja portin, ja preparoi luokan kommunikoimaan palvelimen
     * kanssa.
     *
     * @param host Palvelimen hostname, eli käytännössä ip.
     * @param port Palvelimen portti.
     */
    public LeaderboardClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Lähettää uuden LeaderboardEntryn palvelimelle, palauttaen null mikäli
     * tämä onnistui. Mikäli ei onnistunut, palauttaa errorin sisältävän
     * Stringin.
     *
     * @param entry Lähetettävä entry.
     * @return Null, mikäli lähetys onnistui, muussa tapauksessa ongelmaa
     * kuvaileva merkkijono.
     */
    public String sendNewEntry(LeaderboardEntry entry) {
        if (!openConnection()) {
            return "ERROR: could not form connection to server";
        }
        writer.println("here comes a new entry");
        writer.println(entry.getName() + ";" + entry.getScore());
        try {
            String result = reader.readLine();
            if (result.equals("it has been done")) {
                return null;
            } else if (result.startsWith("ERROR")) {
                return result;
            }
        } catch (IOException ex) {
            return "ERROR: an exception occurred during transit";
        }
        closeConnection();
        return null;
    }

    /**
     * Palauttaa true mikäli palvelimeen saadaan yhteys, muuten false.
     *
     * @return Saadaanko palvelimeen yhteys?
     */
    public boolean serverResponds() {
        if (!openConnection()) {
            return false;
        }
        writer.println("rogueping");
        boolean ponged;
        try {
            ponged = reader.readLine().equals("roguepong");
        } catch (IOException ex) {
            return false;
        }
        closeConnection();
        return ponged;
    }

    /**
     * Palauttaa viimeisen päivän parhaat 10 pisteytystä. Palauttaa null mikäli
     * yhteyttä ei saada palvelimeen.
     *
     * @return Top 10 pisteet viimeisen 24h aikana.
     */
    public ArrayList<LeaderboardEntry> getTopDaily() {
        return getTopList("give me top 10 dailies");
    }

    /**
     * Palauttaa viimeisen viikon parhaat 10 pisteytystä. Palauttaa null mikäli
     * yhteyttä ei saada palvelimeen.
     *
     * @return Top 10 pisteet viimeisen viikon aikana.
     */
    public ArrayList<LeaderboardEntry> getTopWeekly() {
        return getTopList("give me top 10 weeklies");
    }

    /**
     * Palauttaa viimeisen parhaat 10 pisteytystä. Palauttaa null mikäli
     * yhteyttä ei saada palvelimeen.
     *
     * @return Top 10 pisteet koskaan.
     */
    public ArrayList<LeaderboardEntry> getTopOfAllTime() {
        return getTopList("give me top 10 of all time");
    }

    private ArrayList<LeaderboardEntry> getTopList(String request) {
        if (!openConnection()) {
            return null;
        }
        writer.println(request);
        ArrayList<String> response = readConnection();
        if (response == null) {
            return null;
        }
        closeConnection();
        return readEntries(response);
    }

    private ArrayList<LeaderboardEntry> readEntries(ArrayList<String> response) {
        ArrayList<LeaderboardEntry> entries = new ArrayList();
        for (String line : response) {
            String[] split = line.split(";");
            if (split.length == 2) {
                try {
                    entries.add(new LeaderboardEntry(split[0], Integer.parseInt(split[1])));
                } catch (NumberFormatException ex) {
                    System.err.println("[LeaderboardClient] Invalid score on line: " + line);
                }
            } else {
                System.err.println("[LeaderboardClient] Invalid amount of values on line: " + line);
            }
        }
        return entries;
    }

    private boolean openConnection() {
        try {
            socket = new Socket(host, port);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.println("rogueping");
            String response = reader.readLine();
            if (response == null || !response.equals("roguepong")) {
                System.err.println("[LeaderboardClient] " + (response == null ? "End of stream during connection. (Server probably disconnected.)" : "Invalid pong message."));
                return false;
            } else {
                return true;
            }
        } catch (IOException ex) {
            System.err.println("[LeaderboardClient] IOException when creating a connection: " + ex.getMessage());
            return false;
        }
    }

    private ArrayList<String> readConnection() {
        try {
            ArrayList<String> response = new ArrayList();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("that's all")) {
                    break;
                } else {
                    response.add(line);
                }
            }
            return response;
        } catch (IOException ex) {
            System.err.println("[LeaderboardClient] IOException when listening to the connection: " + ex.getMessage());
            return null;
        }
    }

    private void closeConnection() {
        try {
            writer.println("bye!");
            writer.close();
            reader.close();
            socket.close();
        } catch (IOException ex) {
            System.err.println("[LeaderboardClient] IOException when closing a connection: " + ex.getMessage());
        }
    }
}
