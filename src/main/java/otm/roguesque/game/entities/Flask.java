package otm.roguesque.game.entities;

import otm.roguesque.game.GlobalRandom;
import otm.roguesque.game.SpriteLoader;

/**
 * Juoma-olio. Antaa pelaajalle heti +5 elämäpistettä. Erittäin hyödyllinen
 * esimerkiksi jos pelaaja ei healaa Scythe of Tuonelan takia ollenkaan. Tippuu
 * satunnaisesti mihin vaan huoneeseen.
 *
 * @author Jens Pitkänen
 */
public class Flask extends Entity {

    private int healAmount;

    /**
     * Luo uuden juoman.
     */
    public Flask() {
        super(0, 100000, 0, 0, 0, "", "", "Flasks", SpriteLoader.loadImage("jar:/sprites/Flask.png"));
        setInvulnerability(true);
        healAmount = GlobalRandom.get().nextInt(5) + 2;
    }

    @Override
    public String getRichDescription() {
        return "Flask\n\nIt's a flask containing some liquid. Seems very lively!\n\nHEALTH: +" + healAmount;
    }

    /**
     * Jos hyökkäävä olio on pelaaja, se juo juoman.
     *
     * @param attackingEntity Hyökkäävä olio.
     */
    @Override
    protected void reactToAttack(Entity attackingEntity) {
        if (attackingEntity instanceof Player) {
            attackingEntity.setHealth(Math.min(attackingEntity.getMaxHealth(), attackingEntity.getHealth() + healAmount));
            setHealth(0);
        }
    }
}
