package otm.roguesque.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import javafx.scene.image.Image;

/**
 * Pelissä olevien kuvien latausluokka. Tällä luokalla voi ladata .jarin
 * sisäisiä resursseja sekä ulkopuolisia tiedostoja. Peruspeli sisältää
 * tietenkin vain viittauksia .jarin sisäisiin tiedostoihin, mutta peliin on
 * helppo tehdä modeja/lisäyksiä näin.
 *
 * Tämä luokka myös ylläpitää omaa cacheaan kuville, joten yksittäisiä kuvia ei
 * ladata montaa kertaa turhaan.
 *
 * @author Jens Pitkänen
 */
public class SpriteLoader {

    private static final Image NULL_IMAGE = new Image(SpriteLoader.class.getResourceAsStream("/sprites/Null.png"));
    private static HashMap<String, Image> cache = new HashMap();

    /**
     * Lataa kuvan annetusta urlista. Url on joko muotoa
     * "jar:/sprites/SomeSprite.png" tai "file:jokin/polku/Kuvaan.png". Jar
     * etsii .jar tiedostosta, eli käytännössä paketista
     * otm.roguesque.resources, sillä sen sisällöt ovat .jarin juuressa. File
     * etsii käyttäjän tiedostojärjestelmästä, ja "file:" jälkeen voi laittaa
     * minkä vain tiedosto-urlin, absoluuttisen tai relatiivisen. Jos
     * Roguesque-X.X.jar-tiedoston kanssa samassa kansiossa on kansio
     * "customSprites", jonka sisällä on tiedosto "CoolSword.png", sen voi
     * ladata urlilla "file:customSprites/CoolSword.png".
     *
     * @param url Url kuvaan.
     * @return Urlista ladattu kuva.
     */
    public static Image loadImage(String url) {
        Image cached = cache.get(url);
        if (cached == null) {
            cached = loadImageFromUrl(url);
            cache.put(url, cached);
        }
        return cached;
    }

    private static Image loadImageFromUrl(String url) {
        if (!url.contains(":")) {
            System.err.println("No scheme provided in url: '" + url + "'.");
            return NULL_IMAGE;
        }
        String[] split = url.split(":");
        String scheme = split[0];
        String location = split[1];
        switch (scheme) {
            case "jar":
                InputStream stream = SpriteLoader.class.getResourceAsStream(location);
                return stream != null ? new Image(stream) : NULL_IMAGE;
            case "file":
                return loadImageFromFile(location);
            default:
                System.err.println("Invalid scheme in url: '" + url + "'.");
                return NULL_IMAGE;
        }
    }

    private static Image loadImageFromFile(String location) {
        try {
            return new Image(new FileInputStream(new File(location)));
        } catch (FileNotFoundException ex) {
            System.err.println("No file found at: '" + location + "'");
            return NULL_IMAGE;
        }
    }
}
