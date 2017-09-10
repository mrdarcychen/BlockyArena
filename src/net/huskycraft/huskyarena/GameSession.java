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

    public boolean status;

    public GameSession(HuskyArena plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
        players = new ArrayList<>();
        teamBlue = new ArrayList<>();
        teamRed = new ArrayList<>();
    }

    private void sessionAboutToStart() {
        plugin.getLogger().info("Session is about to start!");
        Task task = Task.builder().execute(() -> sessionStarting()).delay(10, TimeUnit.SECONDS).submit(plugin);
    }

    private void sessionStarting() {
        Collections.shuffle(players);
        teamBlue.addAll(players.subList(0, players.size() / 2 + players.size() % 2));
        teamRed.addAll(players.subList(players.size() / 2 + players.size() % 2, players.size()));
        for (Player player : teamBlue) {
            player.setLocation(arena.getBlueSpawn());
        }
        for (Player player : teamRed) {
            player.setLocation(arena.getRedSpawn());
        }
        sessionInProgress();
    }

    private void sessionInProgress() {
        for (Player player : players) {
            player.sendMessage(Text.of("Go Go Go!"));
        }
        plugin.getLogger().info("RED" + teamRed.toString());
        plugin.getLogger().info("BLUE" + teamBlue.toString());
        plugin.getLogger().info("Yeah! Session in progress!");
        Task gameCountdown = Task.builder().execute(() -> sessionStopping()).delay(10, TimeUnit.SECONDS).submit(plugin);
    }

    /**
     * Calculating player stats and announce winner.
     */
    private void sessionStopping() {
    }

    /**
     * Teleport all players to spawn and refresh world.
     */
    private void sessionStopped() {

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

        if (players.size() == 2) {
            sessionAboutToStart();
        }
    }
}
