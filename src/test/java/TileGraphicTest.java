
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import otm.roguesque.game.dungeon.TileType;
import otm.roguesque.ui.DungeonRenderer;

// These tests should make sure that the loading path for each tile sprite is
// correct, as this depends on the path array being in the same order as the
// enum, and so is prone to break if changed without care.
public class TileGraphicTest {

    private static DungeonRenderer renderer;

    @BeforeClass
    public static void initialize() {
        renderer = new DungeonRenderer();
    }

    // Enum order / file name equality tests
    @Test
    public void floorImageIsCorrect() {
        Assert.assertEquals("/sprites/floor.png", renderer.getTileImageName(TileType.Floor));
    }

    @Test
    public void wallImageIsCorrect() {
        Assert.assertEquals("/sprites/wall.png", renderer.getTileImageName(TileType.Wall));
    }

    @Test
    public void corridorImageIsCorrect() {
        Assert.assertEquals("/sprites/corridor.png", renderer.getTileImageName(TileType.Corridor));
    }

    @Test
    public void stairsImageIsCorrect() {
        Assert.assertEquals("/sprites/stairs.png", renderer.getTileImageName(TileType.Stairs));
    }

    // File existence tests
    @Test
    public void floorImageExists() {
        Assert.assertNotNull(getClass().getResourceAsStream(renderer.getTileImageName(TileType.Floor)));
    }

    @Test
    public void wallImageExists() {
        Assert.assertNotNull(getClass().getResourceAsStream(renderer.getTileImageName(TileType.Wall)));
    }

    @Test
    public void corridorImageExists() {
        Assert.assertNotNull(getClass().getResourceAsStream(renderer.getTileImageName(TileType.Corridor)));
    }

    @Test
    public void stairsImageExists() {
        Assert.assertNotNull(getClass().getResourceAsStream(renderer.getTileImageName(TileType.Stairs)));
    }
}
