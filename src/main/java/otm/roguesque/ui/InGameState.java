package otm.roguesque.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import otm.roguesque.entities.Dungeon;
import otm.roguesque.entities.Player;

public class InGameState implements GameState {

    private final DungeonRenderer dungeonRenderer;
    private final Dungeon dungeon;
    private final Player player;

    private String statusLine;
    private String descriptionBox;
    private int descriptionBoxLines;

    public InGameState() {
        dungeonRenderer = new DungeonRenderer();
        dungeon = new Dungeon(10, 10);
        dungeonRenderer.loadDungeon(dungeon);
        player = new Player();
        dungeon.spawnEntity(player, 2, 2);
        statusLine = "Loading...";
        descriptionBox = null;
    }

    @Override
    public void draw(GraphicsContext ctx, float deltaSeconds) {
        dungeonRenderer.draw(ctx);

        Canvas canvas = ctx.getCanvas();
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        ctx.setFill(Color.WHITE);
        ctx.fillRect(20.0, height - 80.0, width - 40.0, 60.0);
        ctx.setFill(Color.BLACK);
        ctx.fillRect(24.0, height - 76.0, width - 48.0, 52.0);

        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_UI);
        ctx.fillText(statusLine, 40.0, height - 42.5);

        if (descriptionBox != null) {
            double boxHeight = descriptionBoxLines * 30.0;
            ctx.setFill(Color.WHITE);
            ctx.fillRect(width - 200.0, height - (100.0 + boxHeight), 180.0, boxHeight);
            ctx.setFill(Color.BLACK);
            ctx.fillRect(width - 196.0, height - (100.0 + boxHeight - 4.0), 172.0, boxHeight - 8.0);
            ctx.setFill(Color.WHITE);
            ctx.fillText(descriptionBox, width - 190.0, height - (70.0 + boxHeight));
        }
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

        statusLine = String.format("HP: %d/%d   ATK: %d   DEF: %d   GOLD: %d", player.getHealth(), player.getMaxHealth(), player.getAttack(), player.getDefense(), player.getGold());
        descriptionBox = player.getDescriptionText();
        if (descriptionBox != null) {
            descriptionBoxLines = descriptionBox.split("\n").length;
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
