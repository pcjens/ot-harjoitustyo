
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import otm.roguesque.game.DiceRoller;
import otm.roguesque.game.dungeon.Dungeon;
import otm.roguesque.game.entities.Player;

public class PlayerTest {

    private Player player;
    private Dungeon dungeon;

    @Before
    public void init() {
        DiceRoller.resetRandom(12);
        player = new Player();
        dungeon = new Dungeon(1);
        dungeon.spawnEntity(player, dungeon.getPlayerSpawnX(), dungeon.getPlayerSpawnY());
        player.recalculateLineOfSight(true);
    }

    @Test
    public void playerSeesWhatTheyShould() {
        dungeon.movePlayerNTimes(1, 0);
        dungeon.movePlayerNTimes(0, 3);
        Assert.assertTrue(player.inLineOfSight(23, 12));
    }

    @Test
    public void playerDoesNotSeeWhatTheyShouldNot() {
        dungeon.movePlayerNTimes(1, 0);
        dungeon.movePlayerNTimes(0, 3);
        Assert.assertFalse(player.inLineOfSight(22, 12));
    }

    @Test
    public void playerSeesWhatTheyShouldWhenIgnoringDistance() {
        dungeon.movePlayerNTimes(0, 2);
        dungeon.movePlayerNTimes(3, 0);
        player.recalculateLineOfSight(true);
        Assert.assertTrue(player.inLineOfSight(32, 8));
    }

    @Test
    public void playerDoesNotSeeWhatTheyShouldNotWhenIgnoringDistance() {
        dungeon.movePlayerNTimes(0, 2);
        dungeon.movePlayerNTimes(3, 0);
        player.recalculateLineOfSight(true);
        Assert.assertFalse(player.inLineOfSight(33, 8));
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
        dungeon.movePlayerNTimes(1, 0);
        dungeon.movePlayerNTimes(0, 7);
        dungeon.movePlayerNTimes(-4, 0);
        dungeon.movePlayerNTimes(0, 7);
        dungeon.movePlayerNTimes(-19, 0);
        dungeon.movePlayerNTimes(0, 7);
        dungeon.movePlayerNTimes(4, 0);
        dungeon.movePlayerNTimes(0, 8);
        dungeon.movePlayerNTimes(-3, 0);
        Assert.assertTrue(dungeon.canFinish());
    }

    private void moveToExamine() {
        dungeon.movePlayerNTimes(-6, 0);
        dungeon.movePlayerNTimes(0, 2);
        dungeon.movePlayerNTimes(-4, 0);
        dungeon.movePlayerNTimes(0, -2);
        dungeon.movePlayerNTimes(-4, 0);
        dungeon.movePlayerNTimes(-1, 0);
    }

    @Test
    public void collisionWorksWhenItHappens() {
        Assert.assertFalse(player.move(0, -1));
    }

    @Test
    public void collisionWorksWhenItDoesNotHappen() {
        Assert.assertTrue(player.move(0, 1));
    }

    @Test
    public void playerDiesCorrectly() {
        Assert.assertFalse(player.isDead());
        movePlayerToDeath();
        Assert.assertTrue(player.isDead());
    }

    private void movePlayerToDeath() {
        dungeon.movePlayerNTimes(-6, 0);
        dungeon.movePlayerNTimes(0, 2);
        dungeon.movePlayerNTimes(-4, 0);
        dungeon.movePlayerNTimes(0, -2);
        dungeon.movePlayerNTimes(-4, 0);
        dungeon.movePlayerNTimes(0, -16);
    }

    @Test
    public void playerGetsStatsFromItem() {
        Assert.assertTrue(player.getAttack() == 2);
        Assert.assertTrue(player.getDefense() == 1);
        movePlayerToItem();
        Assert.assertTrue(player.getAttack() == 4);
        Assert.assertTrue(player.getDefense() == 0);
    }

    private void movePlayerToItem() {
        dungeon.movePlayerNTimes(1, 0);
        dungeon.movePlayerNTimes(0, 7);
        dungeon.movePlayerNTimes(-4, 0);
        dungeon.movePlayerNTimes(0, 5);
        dungeon.movePlayerNTimes(4, 0);
    }
}
