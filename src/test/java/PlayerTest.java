
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import otm.roguesque.game.dungeon.Dungeon;
import otm.roguesque.game.entities.Player;

public class PlayerTest {

    private Player player;
    private Dungeon dungeon;

    @Before
    public void init() {
        player = new Player();
        dungeon = new Dungeon(1, 12);
        dungeon.spawnEntity(player, dungeon.getPlayerSpawnX(), dungeon.getPlayerSpawnY());
    }

    // These should work, as the UI relies on that to display stats.
    @Test
    public void examinationTextIsNullWithoutLastEntity() {
        moveToExamine();
        player.resetLastEntityInteractedWith();
        Assert.assertNull(player.getExaminationText());
    }

    @Test
    public void examinationTextIsNotNullWithLastEntity() {
        moveToExamine();
        Assert.assertNotNull(player.getExaminationText());
    }

    @Test
    public void levelCanBeFinished() {
        dungeon.movePlayerNTimes(-6, 0);
        dungeon.movePlayerNTimes(0, 4);
        dungeon.movePlayerNTimes(-1, 0);
        dungeon.movePlayerNTimes(0, 7);
        dungeon.movePlayerNTimes(-5, 0);
        dungeon.movePlayerNTimes(0, -1);
        dungeon.movePlayerNTimes(-12, 0);
        dungeon.movePlayerNTimes(0, 8);
        dungeon.movePlayerNTimes(1, 0);
        dungeon.movePlayerNTimes(0, 7);
        dungeon.movePlayerNTimes(-2, 0);
        Assert.assertTrue(dungeon.canFinish());
    }

    private void moveToExamine() {
        dungeon.movePlayerNTimes(-8, 0);
        dungeon.movePlayerNTimes(0, -4);
        dungeon.movePlayerNTimes(-4, 0);
        dungeon.movePlayerNTimes(0, 1);
        dungeon.movePlayerNTimes(-5, 0);
        dungeon.movePlayerNTimes(0, 1);
        dungeon.movePlayerNTimes(-1, 0);
        dungeon.movePlayerNTimes(0, 1);
    }

    @Test
    public void collisionWorksWhenItHappens() {
        Assert.assertFalse(player.move(1, 0));
    }

    @Test
    public void collisionWorksWhenItDoesNotHappen() {
        Assert.assertTrue(player.move(-1, 0));
    }
}
