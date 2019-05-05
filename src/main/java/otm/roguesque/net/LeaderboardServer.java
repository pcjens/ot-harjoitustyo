package otm.roguesque.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pieni palvelin joka ylläpitää listaa pisteistä joita pelaajat ovat saaneet.
 *
 * @author Jens Pitkänen
 */
public class LeaderboardServer {

    private ArrayList<LeaderboardEntry> dailies = new ArrayList();
    private ArrayList<LeaderboardEntry> weeklies = new ArrayList();
    private ArrayList<LeaderboardEntry> allTime = new ArrayList();

    private String saveFile;
    private boolean saveToDisk;
    private boolean interactive;
    private ServerSocket serverSocket;
    private int openListeners = 0;

    private final Runnable responder = new Runnable() {
        @Override
        public void run() {
            openListeners++;
            try {
                Socket socket = serverSocket.accept();
                // The accept() can throw, hence why a new thread is started in the catch.
                if (!serverSocket.isClosed()) {
                    new Thread(responder).start();
                }
                handleSocket(socket);
            } catch (IOException ex) {
                if (!serverSocket.isClosed()) {
                    new Thread(responder).start();
                }
            }
            openListeners--;
        }
    };

    private final Runnable monitor = new Runnable() {
        @Override
        public void run() {
            Scanner in = new Scanner(System.in);
            System.out.println("Type 'status' for information about the server's current status.\nType 'stop' to stop the server gracefully.");
            String line;
            while ((line = in.nextLine()) != null && !serverSocket.isClosed()) {
                if (line.equals("status")) {
                    System.out.println("Open listeners: " + openListeners);
                } else if (line.equals("stop")) {
                    saveToDisk();
                    stop();
                    System.exit(0);
                }
            }
        }
    };

    private final Runnable timeCuller = new Runnable() {
        @Override
        public void run() {
            while (!serverSocket.isClosed()) {
                LocalDateTime current = LocalDateTime.now();
                boolean changed = cullOldEntries(current.minusDays(1), dailies);
                changed |= cullOldEntries(current.minusWeeks(1), weeklies);
                if (changed) {
                    saveToDisk();
                }

                try {
                    Thread.sleep(60_000L);
                } catch (InterruptedException ex) {
                    Logger.getLogger(LeaderboardServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    };

    /**
     * Luo uuden leaderboard-palvelin-luokan, muttei vielä ala kuuntelemaan
     * pyyntöjä.
     *
     * @param port Portti jolle palvelin avataan.
     * @param saveToDisk Tallennetaanko leaderboardien tilanne levylle?
     * @param interactive Käynnistetäänkö interaktiivinen käyttöliittymä?
     * @param saveFile Tiedoston nimi, johon leaderboardien tilanne
     * tallennetaan, mikäli saveToDisk on true.
     */
    public LeaderboardServer(int port, boolean saveToDisk, boolean interactive, String saveFile) {
        this.saveToDisk = saveToDisk;
        this.interactive = interactive;
        this.saveFile = saveFile;
        loadFromDisk();

        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            System.err.println("[LeaderboardServer] IOException when starting server: " + ex.getMessage());
        }

        final LeaderboardServer server = this;
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (!server.serverSocket.isClosed()) {
                    server.saveToDisk();
                    server.stop();
                }
            }
        });
    }

    /**
     * Käynnistää palvelimen.
     */
    public void start() {
        new Thread(timeCuller).start();
        if (interactive) {
            new Thread(monitor).start();
        }
        new Thread(responder).start();
    }

    /**
     * Sulkee palvelimen.
     */
    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException ex) {
            System.err.println("[LeaderboardServer] IOException when closing server socket: " + ex.getMessage());
        }
    }

    private void handleSocket(Socket socket) throws IOException {
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals("rogueping")) {
                writer.println("roguepong");
            } else if (line.equals("bye!")) {
                break;
            } else if (line.equals("give me top 10 dailies")) {
                sendList(writer, dailies);
            } else if (line.equals("give me top 10 weeklies")) {
                sendList(writer, weeklies);
            } else if (line.equals("give me top 10 of all time")) {
                sendList(writer, allTime);
            } else if (line.equals("here comes a new entry")) {
                readNewEntry(reader, writer);
            }
        }
    }

    private void readNewEntry(BufferedReader reader, PrintWriter writer) throws IOException {
        String[] parts = reader.readLine().split(";");
        boolean tooManyParts = parts.length != 2, notThreeLetters = parts[0].length() != 3, invalidName = !LeaderboardEntry.isValid(parts[0]);
        if (tooManyParts || notThreeLetters || invalidName) {
            writer.println(tooManyParts ? "ERROR: Too many parts." : (notThreeLetters ? "ERROR: Name not 3 letters." : (invalidName ? "ERROR: Invalid name." : "ERROR: Unknown error.")));
            return;
        }
        try {
            addLeaderboardEntry(new LeaderboardEntry(parts[0], Integer.parseInt(parts[1]), LocalDateTime.now()));
            saveToDisk();
            writer.println("it has been done");
        } catch (NumberFormatException ex) {
            writer.println("ERROR: Could not parse score.");
        }
    }

    private void sendList(PrintWriter writer, ArrayList<LeaderboardEntry> entries) {
        for (LeaderboardEntry entry : entries) {
            writer.println(entry.getName() + ";" + entry.getScore());
        }
        writer.println("that's all");
    }

    private boolean cullOldEntries(LocalDateTime oldest, ArrayList<LeaderboardEntry> entries) {
        int count = entries.size();
        entries.removeIf((entry) -> entry.getDateTime().isBefore(oldest));
        return entries.size() < count;
    }

    private void addLeaderboardEntry(LeaderboardEntry entry) {
        addEntryToList(entry, dailies);
        addEntryToList(entry, weeklies);
        addEntryToList(entry, allTime);
    }

    private void addEntryToList(LeaderboardEntry entry, ArrayList<LeaderboardEntry> list) {
        list.removeIf((e) -> e.getName().equals(entry.getName()) && e.getScore() == entry.getScore());
        list.add(entry);
        list.sort((a, b) -> b.compareTo(a));
        while (list.size() > 10) {
            list.remove(list.size() - 1);
        }
    }

    private void saveToDisk() {
        if (!saveToDisk) {
            return;
        }

        try {
            saveTo(new FileWriter(new File(saveFile)));
            System.out.println("[LeaderboardServer] Saved data to disk successfully.");
        } catch (IOException ex) {
            System.err.println("[LeaderboardServer] Saving data to disk failed: " + ex.getMessage());
        }
    }

    /**
     * Kirjoittaa leaderboardin sisällöt writeriin. Tämä on yleisimmässä
     * tilanteessa FileWriter joka kirjoittaa tiedostoon, joka on myöhemmin
     * luetaan loadFromilla.
     *
     * @see otm.roguesque.net.LeaderboardServer#loadFrom(java.io.Reader)
     *
     * @param raw Datan destinaatio.
     */
    public void saveTo(Writer raw) {
        try (PrintWriter writer = new PrintWriter(raw)) {
            for (LeaderboardEntry entry : allTime) {
                writer.println(entry.getName() + ";" + entry.getScore() + ";" + entry.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            writer.flush();
        }
    }

    private void loadFromDisk() {
        if (!saveToDisk) {
            return;
        }

        File file = new File(saveFile);
        if (file.exists()) {
            try {
                loadFrom(new FileReader(file));
                System.out.println("[LeaderboardServer] Loaded data from disk successfully.");
            } catch (FileNotFoundException ex) {
                System.err.println("[LeaderboardServer] Can't load data from disk: " + file.getName() + " does not exist.");
            }
        }
    }

    /**
     * Lataa leaderboardin sisällöt readerista. Tämä on yleisimmässä tilanteessa
     * FileReader joka lukee tiedostosta joka on tallennettu saveFromlla.
     *
     * @see otm.roguesque.net.LeaderboardServer#saveTo(java.io.Writer)
     *
     * @param raw Datan lähde.
     */
    public void loadFrom(Reader raw) {
        try {
            BufferedReader reader = new BufferedReader(raw);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                addLeaderboardEntry(new LeaderboardEntry(parts[0], Integer.parseInt(parts[1]), LocalDateTime.parse(parts[2], DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
            }
            reader.close();
        } catch (IOException | NumberFormatException | IndexOutOfBoundsException ex) {
        }
    }
}
