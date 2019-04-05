
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import otm.roguesque.entities.Dungeon;
import otm.roguesque.entities.Player;

public class PlayerTest {

    private Player player;
    private Dungeon dungeon;

    @Before
    public void init() {
        player = new Player();
        dungeon = new Dungeon(10, 10, 12);
        dungeon.spawnEntity(player, 2, 2);
    }

    // These should work, as the UI relies on that to display stats.
    @Test
    public void examinationTextIsNullWithoutLastEntity() {
        player.move(0, 1);
        player.move(0, 1);
        player.move(0, 1);
        player.move(1, 0);
        player.resetLastEntityInteractedWith();
        Assert.assertNull(player.getExaminationText());
    }

    @Test
    public void examinationTextIsNotNullWithLastEntity() {
        player.move(0, 1);
        player.move(0, 1);
        player.move(0, 1);
        player.move(1, 0);
        Assert.assertNotNull(player.getExaminationText());
    }

    @Test
    public void collisionWorks() {
        player.move(-1, 0);
        Assert.assertFalse(player.move(-1, 0));
    }
}
