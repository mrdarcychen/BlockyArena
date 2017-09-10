package net.huskycraft.huskyarena;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class GameSession {

    private HuskyArena plugin;

    private Arena arena;

    private ArrayList<Player> players;
    private ArrayList<Player> teamBlue;
    private ArrayList<Player> teamRed;

    Task lobbyTimer, gameTimer;

    private int minPlayer = 2;
    public boolean status;

    public GameSession(HuskyArena plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
        players = new ArrayList<>();
        teamBlue = new ArrayList<>();
        teamRed = new ArrayList<>();
    }

    private void sessionAboutToStart() {
        // TODO
        lobbyTimer = Task.builder().execute(() -> sessionStarting()).delay(arena.getLobbyCountdown(), TimeUnit.SECONDS).submit(plugin);
    }

    private void sessionStarting() {
        Collections.shuffle(players);
        for (Player player : players) {
            if (players.indexOf(player) % 2 == 0) {
                teamBlue.add(player);
                player.setLocation(arena.getBlueSpawn());
            } else {
                teamRed.add(player);
                player.setLocation(arena.getRedSpawn());
            }
        }

        sessionInProgress();
    }

    private void sessionInProgress() {
        // TODO
        gameTimer = Task.builder().execute(() -> sessionStopping()).delay(arena.getGameCountdown(), TimeUnit.SECONDS).submit(plugin);
    }

    /**
     * Calculating player stats and announce winner.
     */
    private void sessionStopping() {
        // TODO
    }

    /**
     * Teleport all players to spawn and refresh world.
     */
    private void sessionStopped() {
        // TODO
    }

    public Arena getArena() {
        return arena;
    }

    public void add(Player player) {
        players.add(player);
        player.getInventory().clear();
        player.gameMode().set(GameModes.ADVENTURE);
        player.setLocation(arena.getLobbySpawn());
        checkSessionPreReq();
    }

    private void checkSessionPreReq() {
        if (players.size() < minPlayer) {
            lobbyTimer.cancel();
        }
        if (players.size() == minPlayer) {
            sessionAboutToStart();
        }
    }
}
