package otm.roguesque.ui;

import javafx.scene.canvas.GraphicsContext;
import otm.roguesque.entities.Dungeon;
import otm.roguesque.entities.Player;

public class InGameState implements GameState {

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
            if (dungeon.getPlayer().isDead()) {
                return GameState.STATE_GAMEOVER;
            }
            dungeon.cleanupDeadEntities();
        }

        return -1;
    }

    private boolean movePlayer(Input input) {
        if (input.isPressed(Input.CONTROL_MOVE_UP)) {
            player.move(0, -1);
        } else if (input.isPressed(Input.CONTROL_MOVE_LEFT)) {
            player.move(-1, 0);
        } else if (input.isPressed(Input.CONTROL_MOVE_DOWN)) {
            player.move(0, 1);
        } else if (input.isPressed(Input.CONTROL_MOVE_RIGHT)) {
            player.move(1, 0);
        } else {
            return false;
        }
        return true;
    }

}
