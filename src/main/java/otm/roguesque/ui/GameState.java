package otm.roguesque.ui;

import javafx.scene.canvas.GraphicsContext;

public interface GameState {

    public static final int STATE_COUNT = 4;

    public static final int STATE_QUIT = 4;
    public static final int STATE_INTRO = 0;
    public static final int STATE_MAINMENU = 1;
    public static final int STATE_INGAME = 2;
    public static final int STATE_GAMEOVER = 3;

    void draw(GraphicsContext ctx, float deltaSeconds);

    int update(Input input, float deltaSeconds);

    void initialize();
}
