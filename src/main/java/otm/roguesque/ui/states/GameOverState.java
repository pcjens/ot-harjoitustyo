package otm.roguesque.ui.states;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javax.swing.JOptionPane;
import otm.roguesque.game.entities.Player;
import otm.roguesque.net.Leaderboard;
import otm.roguesque.net.LeaderboardClient;
import otm.roguesque.net.LeaderboardEntry;
import otm.roguesque.ui.Button;
import otm.roguesque.ui.Input;
import otm.roguesque.ui.RoguesqueApp;

/**
 * Peli loppui (kuolemaan) -pelitila. Ruudulla lukee "you are dead," ja
 * pelaajalle annetaan mahdollisuus pelata uudestaan tai sulkea peli.
 *
 * @author Jens Pitkänen
 */
public class GameOverState implements GameState {

    private static boolean animate = false;

    private Button saveReplayButton = new Button(new KeyCode[]{KeyCode.S}, "Save replay", 20, 20, 160, 45, 0);
    private Button mainmenuButton = new Button(new KeyCode[]{KeyCode.M}, "Main menu", 110, 420, 140, 45, 0);
    private Button submitScoreButton = new Button(new KeyCode[]{KeyCode.S}, "Submit score", 260, 420, 160, 45, 0);
    private Button quitButton = new Button(new KeyCode[]{KeyCode.Q}, "Quit", 430, 420, 80, 45, 0);

    private final LeaderboardClient leaderboards = new LeaderboardClient(Leaderboard.DEFAULT_HOST, Leaderboard.DEFAULT_PORT);
    private ArrayList<LeaderboardEntry> leaderboardDaily;
    private ArrayList<LeaderboardEntry> leaderboardWeekly;
    private ArrayList<LeaderboardEntry> leaderboardAllTime;

    private float initializeTime;
    private float refreshTimer;

    @Override
    public void initialize() {
        leaderboardDaily = leaderboards.getTopDaily();
        leaderboardWeekly = leaderboards.getTopWeekly();
        leaderboardAllTime = leaderboards.getTopOfAllTime();
        refreshTimer = 2.0f;
        if (animate) {
            animate = false;
            initializeTime = 2.0f;
        } else {
            initializeTime = 0.0f;
        }
    }

    @Override
    public void draw(GraphicsContext ctx, float deltaSeconds, boolean showDebugInfo) {
        Canvas canvas = ctx.getCanvas();
        ctx.setFill(Color.BLACK);
        ctx.fillRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
        double xOffset = (canvas.getWidth() - 640) / 2;
        double yOffset = (canvas.getHeight() - 480) / 2;

        drawTitleAndScore(ctx, xOffset, yOffset);
        if (initializeTime <= 0) {
            drawLeaderboards(ctx, xOffset, yOffset);
            drawButtons(ctx, showDebugInfo, xOffset, yOffset);
        } else {
            initializeTime = Math.max(0, initializeTime - deltaSeconds);
        }
    }

    private void drawTitleAndScore(GraphicsContext ctx, double xOffset, double yOffset) {
        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_LOGO_SMALL);
        ctx.fillText("You are dead. Your score: " + Player.getScore(), 30.0 + xOffset, 400.0 + yOffset - Math.min(1, initializeTime) * 140.0);
    }

    private void drawLeaderboards(GraphicsContext ctx, double xOffset, double yOffset) {
        ctx.setFill(Color.WHITE);
        ctx.setFont(RoguesqueApp.FONT_LOGO);
        ctx.fillText("Roguesque Leaderboards", 10 + xOffset, 60 + yOffset);
        ctx.setFont(RoguesqueApp.FONT_UI_LARGE);
        if (leaderboardDaily == null || leaderboardWeekly == null || leaderboardAllTime == null) {
            ctx.fillText("Couldn't connect to leaderboard server.", 20 + xOffset, 230 + yOffset);
            return;
        }
        ctx.fillText("Daily", 20 + xOffset, 100 + yOffset);
        ctx.fillText("Weekly", 220 + xOffset, 100 + yOffset);
        ctx.fillText("All Time", 420 + xOffset, 100 + yOffset);
        ctx.setFont(RoguesqueApp.FONT_UI);
        for (int i = 0; i < 10; i++) {
            drawLeaderboardLine(ctx, leaderboardDaily, i, 20 + xOffset, 130 + i * 25 + yOffset);
            drawLeaderboardLine(ctx, leaderboardWeekly, i, 220 + xOffset, 130 + i * 25 + yOffset);
            drawLeaderboardLine(ctx, leaderboardAllTime, i, 420 + xOffset, 130 + i * 25 + yOffset);
        }
    }

    private void drawLeaderboardLine(GraphicsContext ctx, ArrayList<LeaderboardEntry> entries, int index, double xOffset, double yOffset) {
        if (index >= entries.size()) {
            return;
        }
        LeaderboardEntry entry = entries.get(index);
        if (entry != null) {
            ctx.fillText((index + 1) + ". " + entry.getName() + " - " + entry.getScore(), xOffset, yOffset);
        }
    }

    private void drawButtons(GraphicsContext ctx, boolean showDebugInfo, double xOffset, double yOffset) {
        mainmenuButton.draw(ctx, xOffset, yOffset);
        submitScoreButton.draw(ctx, xOffset, yOffset);
        quitButton.draw(ctx, xOffset, yOffset);
        if (showDebugInfo) {
            saveReplayButton.draw(ctx, xOffset, yOffset);
        }
    }

    @Override
    public int update(Input input, float deltaSeconds, boolean showDebugInfo) {
        updateLeaderboards(deltaSeconds);
        int nextState = updateButtons(input);
        if (nextState != -1) {
            return nextState;
        }
        if (showDebugInfo) {
            saveReplayButton.update(input);
        }
        if (showDebugInfo && saveReplayButton.isClicked()) {
            saveReplay();
        }
        return -1;
    }

    private void updateLeaderboards(float deltaSeconds) {
        if (refreshTimer <= 0) {
            refreshTimer = 2.0f;
            ArrayList<LeaderboardEntry> dailies = leaderboards.getTopDaily();
            ArrayList<LeaderboardEntry> weeklies = leaderboards.getTopWeekly();
            ArrayList<LeaderboardEntry> allTime = leaderboards.getTopOfAllTime();
            if (dailies != null && weeklies != null && allTime != null) {
                leaderboardDaily = dailies;
                leaderboardWeekly = weeklies;
                leaderboardAllTime = allTime;
            }
        } else {
            refreshTimer -= deltaSeconds;
        }
    }

    private int updateButtons(Input input) {
        mainmenuButton.update(input);
        submitScoreButton.update(input);
        quitButton.update(input);

        if (mainmenuButton.isClicked()) {
            return GameState.STATE_MAINMENU;
        }
        if (submitScoreButton.isClicked()) {
            return GameState.STATE_LEADERBOARD_SUBMISSION;
        }
        if (quitButton.isClicked()) {
            return GameState.STATE_QUIT;
        }
        return -1;
    }

    private void saveReplay() {
        try {
            InGameState.latestReplay.saveTo(Paths.get(System.getProperty("user.dir"),
                    JOptionPane.showInputDialog("Save file name:", "roguesque-replay.rgsq")));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Couldn't save replay. Are you lacking permissions?", "Replay not saved", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Preparoi animaation seuraavalle kerralle kun GameOverState tulee
     * käyntiin. Kutsutaan InGameStatesta.
     */
    public static void prepareAnimation() {
        animate = true;
    }
}
