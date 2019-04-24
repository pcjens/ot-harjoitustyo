package otm.roguesque.game.entities;

/**
 * Tekoälyjä kuvaava rajapinta.
 *
 * @author Jens Pitkänen
 */
public interface AI {

    /**
     * Pyörittää tekoälyä yhden vuoron verran ja toteuttaa sen päätökset.
     */
    void processRound();
}
