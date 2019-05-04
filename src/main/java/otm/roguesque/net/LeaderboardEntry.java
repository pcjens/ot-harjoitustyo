package otm.roguesque.net;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Yhtä leaderboardilla olevaa ennätystä kuvaava luokka.
 *
 * @author Jens Pitkänen
 */
public class LeaderboardEntry implements Comparable<LeaderboardEntry> {

    private final String name;
    private final int score;
    private final LocalDateTime time;

    /**
     * Luo uuden ennätystä kuvaavan olion.
     *
     * @param name Ennätyksen tekijän nimi.
     * @param score Ennätyspisteet.
     */
    public LeaderboardEntry(String name, int score) {
        this.name = name;
        this.score = score;
        this.time = null;
    }

    /**
     * Luo uuden ennätystä kuvaavan olion.
     *
     * @param name Ennätyksen tekijän nimi.
     * @param score Ennätyspisteet.
     * @param date Hetki jolloin ennätys luotiin.
     */
    public LeaderboardEntry(String name, int score, LocalDateTime date) {
        this.name = name;
        this.score = score;
        this.time = date;
    }

    /**
     * Palauttaa ennätyksen tekijän nimen.
     *
     * @return Nimi.
     */
    public String getName() {
        return name;
    }

    /**
     * Palauttaa ennätyksen pisteet.
     *
     * @return Pisteet.
     */
    public int getScore() {
        return score;
    }

    /**
     * Palauttaa hetken jolloin tämä ennätys tuli palvelimelle.
     *
     * @return Ennätyksen luomishetki.
     */
    public LocalDateTime getDateTime() {
        return time;
    }

    @Override
    public int compareTo(LeaderboardEntry t) {
        return Integer.compare(score, t.score);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.name);
        hash = 59 * hash + this.score;
        hash = 59 * hash + Objects.hashCode(this.time);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LeaderboardEntry)) {
            return false;
        } else {
            LeaderboardEntry other = (LeaderboardEntry) obj;
            return other.name.equals(name) && other.score == score && other.time.equals(time);
        }
    }
}
