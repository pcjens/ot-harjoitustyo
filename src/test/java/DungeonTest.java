
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import otm.roguesque.game.dungeon.Dungeon;
import otm.roguesque.game.entities.Player;

public class DungeonTest {

    private Player player;
    private Dungeon dungeon;

    @Before
    public void init() {
        player = new Player();
        dungeon = new Dungeon(1, 12);
        dungeon.spawnEntity(player, dungeon.getPlayerSpawnX(), dungeon.getPlayerSpawnY());
    }

    @Test
    public void playerDiesCorrectly() {
        Assert.assertFalse(player.isDead());
        movePlayerToDeath();
        Assert.assertTrue(player.isDead());
    }

    @Test
    public void cleanupDeadEntitiesWorks() {
        dungeon.cleanupDeadEntities();
        Assert.assertTrue(dungeon.getEntities().contains(player));
        movePlayerToDeath();
        Assert.assertTrue(dungeon.getEntities().contains(player));
        dungeon.cleanupDeadEntities();
        Assert.assertFalse(dungeon.getEntities().contains(player));
    }

    private void movePlayerToDeath() {
        dungeon.movePlayerNTimes(-8, 0);
        dungeon.movePlayerNTimes(0, -4);
        dungeon.movePlayerNTimes(-3, 0);
        dungeon.movePlayerNTimes(0, 1);
        dungeon.movePlayerNTimes(-4, 0);
        dungeon.movePlayerNTimes(0, -14);
    }

    @Test
    public void playerGetsStatsFromItem() {
        Assert.assertTrue(player.getDefense() == 1);
        movePlayerToItem();
        Assert.assertTrue(player.getDefense() == 3);
    }

    private void movePlayerToItem() {
        dungeon.movePlayerNTimes(0, -2);
        dungeon.movePlayerNTimes(3, 0);
        dungeon.movePlayerNTimes(0, 1);
        dungeon.movePlayerNTimes(4, 0);
        dungeon.movePlayerNTimes(0, -1);
    }
}
