package otm.roguesque;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import otm.roguesque.game.dungeon.Dungeon;
import otm.roguesque.game.dungeon.replay.PlayerActionType;
import otm.roguesque.game.entities.Player;

public class PlayerTest {

    private Player player;
    private Dungeon dungeon;

    @Before
    public void init() {
        dungeon = new Dungeon(12, 1);
        player = dungeon.getPlayer();
        player.recalculateLineOfSight(true);
    }

    @Test
    public void playerSeesWhatTheyShould() {
        dungeon.movePlayerNTimes(2, 0);
        dungeon.movePlayerNTimes(0, -1);
        dungeon.movePlayerNTimes(1, 0);
        Assert.assertTrue(player.inLineOfSight(11, 13));
    }

    @Test
    public void playerDoesNotSeeWhatTheyShouldNot() {
        dungeon.movePlayerNTimes(2, 0);
        dungeon.movePlayerNTimes(0, -1);
        dungeon.movePlayerNTimes(1, 0);
        Assert.assertFalse(player.inLineOfSight(11, 14));
    }

    @Test
    public void playerSeesWhatTheyShouldWhenIgnoringDistance() {
        dungeon.movePlayerNTimes(2, 0);
        dungeon.movePlayerNTimes(0, -1);
        dungeon.movePlayerNTimes(1, 0);
        dungeon.movePlayerNTimes(-4, 0);
        player.recalculateLineOfSight(true);
        Assert.assertTrue(player.inLineOfSight(11, 12));
    }

    @Test
    public void playerDoesNotSeeWhatTheyShouldNotWhenIgnoringDistance() {
        dungeon.movePlayerNTimes(2, 0);
        dungeon.movePlayerNTimes(0, -1);
        dungeon.movePlayerNTimes(1, 0);
        dungeon.movePlayerNTimes(-3, 0);
        player.recalculateLineOfSight(true);
        Assert.assertFalse(player.inLineOfSight(4, 16));
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
        dungeon.movePlayerNTimes(-1, 0);
        dungeon.movePlayerNTimes(0, 6);
        dungeon.movePlayerNTimes(1, 0);
        dungeon.movePlayerNTimes(0, 5);
        dungeon.movePlayerNTimes(15, 0);
        dungeon.movePlayerNTimes(0, 1);
        dungeon.movePlayerNTimes(23, 0);
        dungeon.movePlayerNTimes(0, -1);
        dungeon.movePlayerNTimes(8, 0);
        Assert.assertTrue(dungeon.canFinish());
        dungeon.runPlayerAction(PlayerActionType.NextLevel);
        Assert.assertTrue(!dungeon.canFinish());
    }

    private void moveToExamine() {
        dungeon.movePlayerNTimes(2, 0);
        dungeon.movePlayerNTimes(0, -1);
        dungeon.movePlayerNTimes(5, 0);
        dungeon.movePlayerNTimes(0, 2);
        dungeon.movePlayerNTimes(4, 0);
        dungeon.movePlayerNTimes(0, -1);
    }

    @Test
    public void collisionWorksWhenItHappens() {
        int originalY = player.getY();
        Assert.assertTrue(player.move(0, -1));
        Assert.assertFalse(player.move(0, -1));
        Assert.assertTrue(player.getY() == originalY - 1);
    }

    @Test
    public void collisionWorksWhenItDoesNotHappen() {
        int originalY = player.getY();
        Assert.assertTrue(player.move(0, -1));
        Assert.assertTrue(player.getY() == originalY - 1);
    }

    @Test
    public void playerDiesCorrectly() {
        Assert.assertFalse(player.isDead());
        movePlayerToDeath();
        Assert.assertTrue(player.isDead());
    }

    private void movePlayerToDeath() {
        dungeon.movePlayerNTimes(-1, 0);
        dungeon.movePlayerNTimes(0, 6);
        dungeon.movePlayerNTimes(1, 0);
        dungeon.movePlayerNTimes(0, 4);
        dungeon.movePlayerNTimes(-50, 0);
    }

    @Test
    public void playerGetsStatsFromItem() {
        Assert.assertTrue(player.getDefense() == 2);
        movePlayerToItem();
        Assert.assertTrue(player.getDefense() == 4);
    }

    private void movePlayerToItem() {
        dungeon.movePlayerNTimes(2, 0);
        dungeon.movePlayerNTimes(0, -1);
        dungeon.movePlayerNTimes(5, 0);
        dungeon.movePlayerNTimes(0, 2);
        dungeon.movePlayerNTimes(4, 0);
        dungeon.movePlayerNTimes(0, -2);
    }
}
