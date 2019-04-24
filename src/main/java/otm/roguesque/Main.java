package otm.roguesque;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import otm.roguesque.ui.RoguesqueApp;

public class Main {

    private static String version = "unknown";

    public static String getVersion() {
        return version;
    }

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
