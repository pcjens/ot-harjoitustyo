package otm.roguesque;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lukee metadataa (version) ohjelman pom.properties tiedostosta.
 *
 * @author Jens Pitkänen
 */
public class Properties {

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

    static {
        InputStream stream = Main.class.getResourceAsStream("/META-INF/maven/otm/Roguesque/pom.properties");
        if (stream == null) {
            System.err.println("Failed to load pom.properties!");
        } else {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("=");
                    if (parts.length == 2 && parts[0].equals("version")) {
                        version = parts[1];
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Properties.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
