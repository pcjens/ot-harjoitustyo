package otm.roguesque.ui;

import javafx.scene.canvas.GraphicsContext;

public interface GameState {

    void draw(GraphicsContext ctx, float deltaSeconds);

    void update(Input input, float deltaSeconds);
}
