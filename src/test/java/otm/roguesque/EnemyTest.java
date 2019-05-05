package otm.roguesque;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import otm.roguesque.game.dungeon.Dungeon;
import otm.roguesque.game.entities.Dragon;
import otm.roguesque.game.entities.Goblin;
import otm.roguesque.game.entities.Player;
import otm.roguesque.game.entities.Rat;
import otm.roguesque.game.entities.Skeleton;

public class EnemyTest {

    private Player player;
    private Dungeon dungeon;

    @Before
    public void init() {
        dungeon = new Dungeon(12, 1);
        player = dungeon.getPlayer();
        player.recalculateLineOfSight(true);
    }

    @Test
    public void skeletonMovesOnlyEveryOtherTurn() {
        dungeon.movePlayerNTimes(-2, 0);
        dungeon.movePlayerNTimes(0, -1);
        Skeleton skeleton = new Skeleton();
        dungeon.spawnEntity(skeleton, player.getX() + 4, player.getY() + 1);
        int skeletonX = skeleton.getX();
        int skeletonY = skeleton.getY();
        dungeon.movePlayerNTimes(-1, 0);
        boolean skeletonMoved = skeletonX != skeleton.getX() || skeletonY != skeleton.getY();
        skeletonX = skeleton.getX();
        skeletonY = skeleton.getY();
        dungeon.movePlayerNTimes(-1, 0);
        Assert.assertTrue(skeletonMoved != (skeletonX != skeleton.getX() || skeletonY != skeleton.getY()));
    }

    @Test
    public void goblinHuntsForRatsOverPlayer() {
        dungeon.movePlayerNTimes(-2, 0);
        dungeon.movePlayerNTimes(0, -1);
        Goblin goblin = new Goblin();
        goblin.setHunger(0);
        dungeon.spawnEntity(goblin, player.getX() + 4, player.getY() + 1);
        Rat rat = new Rat();
        rat.setAIControlled(false);
        dungeon.spawnEntity(rat, player.getX(), player.getY() + 2);
        dungeon.movePlayerNTimes(-8, 0);
        Assert.assertTrue(!player.isDead());
        Assert.assertTrue(!rat.isDead());
        dungeon.movePlayerNTimes(-1, 0);
        Assert.assertTrue(!player.isDead());
        Assert.assertTrue(rat.isDead());
    }

    @Test
    public void dragonHuntsForPlayerOverRats() {
        dungeon.movePlayerNTimes(-2, 0);
        dungeon.movePlayerNTimes(0, -1);
        Dragon dragon = new Dragon();
        dragon.setHunger(0);
        dungeon.spawnEntity(dragon, player.getX() + 4, player.getY() + 1);
        Rat rat = new Rat();
        rat.setAIControlled(false);
        dungeon.spawnEntity(rat, player.getX(), player.getY() + 2);
        dungeon.movePlayerNTimes(-9, 0);
        Assert.assertTrue(!player.isDead());
        Assert.assertTrue(!rat.isDead());
        dungeon.movePlayerNTimes(-1, 0);
        Assert.assertTrue(player.isDead());
        Assert.assertTrue(!rat.isDead());
    }

    @Test
    public void dragonSleepsAfterEating() {
        dungeon.movePlayerNTimes(-2, 0);
        dungeon.movePlayerNTimes(0, -1);
        Dragon dragon = new Dragon();
        dragon.setHunger(0);
        dungeon.spawnEntity(dragon, player.getX() + 4, player.getY() + 1);
        Goblin goblin = new Goblin();
        goblin.setAIControlled(false);
        dungeon.spawnEntity(goblin, player.getX(), player.getY() + 2);
        Assert.assertTrue(!player.isDead());
        Assert.assertTrue(!goblin.isDead());
        dungeon.movePlayerNTimes(-15, 0);
        Assert.assertTrue(player.isDead());
        Assert.assertTrue(goblin.isDead());
        int dragonX = dragon.getX();
        int dragonY = dragon.getY();
        dungeon.processRound();
        Assert.assertTrue(dragonX == dragon.getX());
        Assert.assertTrue(dragonY == dragon.getY());
    }

    @Test
    public void dragonHuntsForGoblinsOverPlayer() {
        dungeon.movePlayerNTimes(-2, 0);
        dungeon.movePlayerNTimes(0, -1);
        Dragon dragon = new Dragon();
        dragon.setHunger(0);
        dungeon.spawnEntity(dragon, player.getX() + 4, player.getY() + 1);
        Goblin goblin = new Goblin();
        goblin.setAIControlled(false);
        dungeon.spawnEntity(goblin, player.getX(), player.getY() + 2);
        dungeon.movePlayerNTimes(-8, 0);
        Assert.assertTrue(!player.isDead());
        Assert.assertTrue(!goblin.isDead());
        dungeon.movePlayerNTimes(-1, 0);
        Assert.assertTrue(!player.isDead());
        Assert.assertTrue(goblin.isDead());
    }
}
