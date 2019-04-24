package otm.roguesque;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
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
        Application.launch(RoguesqueApp.class);
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
