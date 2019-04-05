package otm.roguesque.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import otm.roguesque.entities.Dungeon;
import otm.roguesque.entities.Player;

public class InGameState implements GameState {

    private static final KeyCode[] CONTROL_MOVE_UP = new KeyCode[]{
        KeyCode.W, KeyCode.UP, KeyCode.K
    };
    private static final KeyCode[] CONTROL_MOVE_LEFT = new KeyCode[]{
        KeyCode.A, KeyCode.LEFT, KeyCode.H
    };
    private static final KeyCode[] CONTROL_MOVE_DOWN = new KeyCode[]{
        KeyCode.S, KeyCode.DOWN, KeyCode.J
    };
    private static final KeyCode[] CONTROL_MOVE_RIGHT = new KeyCode[]{
        KeyCode.D, KeyCode.RIGHT, KeyCode.L
    };

    private final DungeonRenderer dungeonRenderer;
    private final Dungeon dungeon;
    private final Player player;

    public InGameState() {
        dungeonRenderer = new DungeonRenderer();
        dungeon = new Dungeon(10, 10);
        dungeonRenderer.loadDungeon(dungeon);
        player = new Player();
        dungeon.spawnEntity(player, 2, 2);
    }

    @Override
    public void draw(GraphicsContext ctx, float deltaSeconds) {
        dungeonRenderer.draw(ctx);
    }

    @Override
    public int update(Input input, float deltaSeconds) {
        boolean progressRound = movePlayer(input);

        if (progressRound) {
            dungeon.processRound();
            dungeon.cleanupDeadEntities();
        }

        return -1;
    }

    private boolean movePlayer(Input input) {
        if (input.isPressed(CONTROL_MOVE_UP)) {
            player.move(0, -1);
        } else if (input.isPressed(CONTROL_MOVE_LEFT)) {
            player.move(-1, 0);
        } else if (input.isPressed(CONTROL_MOVE_DOWN)) {
            player.move(0, 1);
        } else if (input.isPressed(CONTROL_MOVE_RIGHT)) {
            player.move(1, 0);
        } else {
            return false;
        }
        return true;
    }

}
