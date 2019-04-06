
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import otm.roguesque.entities.Dungeon;
import otm.roguesque.entities.Player;

public class DungeonTest {

    private Player player;
    private Dungeon dungeon;

    @Before
    public void init() {
        player = new Player();
        dungeon = new Dungeon(10, 10, 12);
        dungeon.spawnEntity(player, 2, 2);
    }

    @Test
    public void playerDiesCorrectly() {
        for (int i = 0; i < 19; i++) {
            Assert.assertFalse(player.isDead());
            player.move(-1, 0);
            dungeon.processRound();
        }
        Assert.assertTrue(player.isDead());
    }

    @Test
    public void cleanupDeadEntitiesWorks() {
        dungeon.cleanupDeadEntities();
        Assert.assertTrue(dungeon.getEntities().contains(player));
        for (int i = 0; i < 19; i++) {
            player.move(-1, 0);
            dungeon.processRound();
        }
        Assert.assertTrue(dungeon.getEntities().contains(player));
        dungeon.cleanupDeadEntities();
        Assert.assertFalse(dungeon.getEntities().contains(player));
    }

    @Test
    public void playerGetsStatsFromItem() {
        for (int i = 0; i < 5; i++) {
            player.move(1, 0);
            dungeon.processRound();
            Assert.assertTrue(player.getAttack() == 2);
        }
        player.move(0, 1);
        Assert.assertTrue(player.getAttack() == 3);
    }
}
