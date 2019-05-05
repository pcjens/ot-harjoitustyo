package otm.roguesque.ui.states;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import otm.roguesque.game.entities.Player;
import otm.roguesque.net.Leaderboard;
import otm.roguesque.net.LeaderboardClient;
import otm.roguesque.net.LeaderboardEntry;
import otm.roguesque.ui.Button;
import otm.roguesque.ui.Input;
import otm.roguesque.ui.RoguesqueApp;

/**
 * Leaderboardille uusien pisteiden lähetys -tila. Pelaaja voi kirjoittaa
 * 3-kirjaimisen nimen, näkee pisteensä, ja voi joko palata game overiin tai
 * lähettää pisteensä leaderboardeille.
 *
 * @author Jens Pitkänen
 */
public class LeaderboardSubmissionState implements GameState {

    private static final Color SELECTED_UNDERLINE_COLOR = new Color(0.9, 0.9, 0.9, 1.0);
    private static final Color HOVERED_UNDERLINE_COLOR = new Color(0.9, 0.9, 0.9, 0.75);
    private static final Color DEFAULT_UNDERLINE_COLOR = new Color(0.9, 0.9, 0.9, 0.4);

    private static final KeyCode[] MOVE_LEFT = new KeyCode[]{KeyCode.LEFT};
    private static final KeyCode[] MOVE_RIGHT = new KeyCode[]{KeyCode.RIGHT};

    private Button submitButton = new Button(new KeyCode[]{KeyCode.S}, "Submit score", 170, 350, 160, 45, 0);
    private Button backButton = new Button(new KeyCode[]{KeyCode.B}, "Back", 350, 350, 80, 45, 0);

    private Button[] nameLetterButtons = new Button[]{
        new Button(new KeyCode[]{}, "-", 260, 220, 30, 45, 0),
        new Button(new KeyCode[]{}, "-", 300, 220, 30, 45, 0),
        new Button(new KeyCode[]{}, "-", 340, 220, 30, 45, 0)
    };

    private String name = "   ";
    private int selectedIndex = 0;
    private int hoveredIndex = -1;
    private String errorMessage = null;

    private LeaderboardClient leaderboard = new LeaderboardClient(Leaderboard.DEFAULT_HOST, Leaderboard.DEFAULT_PORT);

    @Override
    public void initialize() {
    }

    @Override
    public void draw(GraphicsContext ctx, float deltaSeconds, boolean showDebugInfo) {
        Canvas canvas = ctx.getCanvas();
        ctx.setFill(Color.BLACK);
        ctx.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
        double xOffset = (canvas.getWidth() - 640) / 2;
        double yOffset = (canvas.getHeight() - 480) / 2;

        drawTextInput(ctx, xOffset, yOffset);

        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_LOGO);
        submitButton.draw(ctx, xOffset, yOffset);
        backButton.draw(ctx, xOffset, yOffset);
    }

    private void drawTextInput(GraphicsContext ctx, double xOffset, double yOffset) {
        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_LOGO_SMALL);
        ctx.fillText("Write your name:", 160 + xOffset, 140 + yOffset);
        ctx.setFont(RoguesqueApp.FONT_UI);
        ctx.fillText("Symbols allowed from the Basic Latin block.", 80 + xOffset, 170 + yOffset);
        ctx.fillText("Only three symbols. Esc to deselect, tab to select.", 40 + xOffset, 195 + yOffset);
        for (int i = 0; i < 3; i++) {
            ctx.setFill(Color.WHITE);
            ctx.setFont(RoguesqueApp.FONT_LOGO);
            if (i < name.length()) {
                ctx.fillText(name.substring(i, i + 1), nameLetterButtons[i].getX() + xOffset, nameLetterButtons[i].getY() + 43 + yOffset);
            }
            ctx.setFill(selectedIndex == i ? SELECTED_UNDERLINE_COLOR : (hoveredIndex == i ? HOVERED_UNDERLINE_COLOR : DEFAULT_UNDERLINE_COLOR));
            ctx.fillRect(nameLetterButtons[i].getX() - 1 + xOffset, nameLetterButtons[i].getY() + 47 + yOffset, 30, 5);
        }
    }

    private boolean clickedAlready = false;

    @Override
    public int update(Input input, float deltaSeconds, boolean showDebugInfo) {
        clickedAlready = false;
        submitButton.setDisabled(!LeaderboardEntry.isValid(name));
        submitButton.update(input);
        backButton.update(input);
        if (submitButton.isClicked() && selectedIndex == -1 && submitScore()) {
            return GameState.STATE_GAMEOVER;
        }
        if (backButton.isClicked()) {
            return GameState.STATE_GAMEOVER;
        }
        updateLetterButtons(input);
        moveSelectionWithArrows(input);
        updateTextField(input);
        return -1;
    }

    private void updateLetterButtons(Input input) {
        hoveredIndex = -1;
        for (int i = 0; i < nameLetterButtons.length; i++) {
            nameLetterButtons[i].update(input);
            if (nameLetterButtons[i].isClicked()) {
                clickedAlready = true;
                selectedIndex = i;
            } else if (nameLetterButtons[i].isHovered()) {
                hoveredIndex = i;
            }
        }
        if (input.isPressed(Input.CONTROL_CLEAR_SELECTION) || input.clicked(MouseButton.PRIMARY) && !clickedAlready) {
            selectedIndex = -1;
        }
        if (input.isPressed(Input.CONTROL_SELECT_FIELD)) {
            selectedIndex = 0;
        }
    }

    private void moveSelectionWithArrows(Input input) {
        if (input.isPressed(MOVE_LEFT)) {
            if (selectedIndex == -1) {
                selectedIndex = 2;
            } else {
                selectedIndex = Math.max(0, selectedIndex - 1);
            }
        }
        if (input.isPressed(MOVE_RIGHT)) {
            if (selectedIndex == -1) {
                selectedIndex = 0;
            } else {
                selectedIndex = Math.min(2, selectedIndex + 1);
            }
        }
    }

    private void updateTextField(Input input) {
        for (String typed : input.getTypedChars()) {
            int codepoint = typed.codePointAt(0);
            if (codepoint == 8) {
                selectedIndex = selectedIndex == -1 ? 2 : Math.max(0, selectedIndex - 1);
                name = insertString(" ", name, selectedIndex);
            } else if (codepoint == 127) {
                name = insertString(" ", name, selectedIndex);
            }
            if (selectedIndex != -1 && LeaderboardEntry.codepointIsValid(codepoint)) {
                // Only allow the visible symbols of the Basic Latin block.
                name = insertString(typed, name, selectedIndex);
                selectedIndex++;
                if (selectedIndex >= 3) {
                    selectedIndex = -1;
                    break;
                }
            }
        }
    }

    private String insertString(String src, String dst, int index) {
        return dst.substring(0, index) + src + dst.substring(index + src.length());
    }

    private boolean submitScore() {
        if (!LeaderboardEntry.isValid(name)) {
            return false;
        }
        String response = leaderboard.sendNewEntry(new LeaderboardEntry(name, Player.getScore()));
        if (response != null) {
            errorMessage = response;
            return false;
        }
        return true;
    }
}
