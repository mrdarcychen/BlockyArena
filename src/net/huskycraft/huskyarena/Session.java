package net.huskycraft.huskyarena;

import com.typesafe.config.ConfigException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Session {

    private HuskyArena plugin;

    private Arena arena;

    private ArrayList<Player> players;
    private ArrayList<Player> teamBlue;
    private ArrayList<Player> teamRed;

    private HashMap<Player, Location<World>> onJoinLocations;

    Task lobbyTimer, gameTimer;

    private int minPlayer = 2;
    public boolean status;
    public boolean canJoin;

    public Session(HuskyArena plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
        players = new ArrayList<>();
        teamBlue = new ArrayList<>();
        teamRed = new ArrayList<>();
        onJoinLocations = new HashMap<>();
        canJoin = true;
    }

    private void sessionAboutToStart() {
        // TODO
        lobbyTimer = Task.builder().execute(() -> sessionStarting()).delay(arena.getLobbyCountdown(), TimeUnit.SECONDS).submit(plugin);
        plugin.getLogger().info("Timer is on.");
    }

    private void sessionStarting() {
        canJoin = false;
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
        if (canJoin != false) {
            onJoinLocations.put(player, player.getLocation());
            players.add(player);
            plugin.getSessionManager().playerSession.put(player, this);
            player.getInventory().clear();
            player.gameMode().set(GameModes.ADVENTURE);
            player.setLocation(arena.getLobbySpawn());
            player.sendMessage(Text.of("You're in session."));
            checkSessionPreReq();
        }
    }

    public void remove(Player player) {
        players.remove(player);
        plugin.getSessionManager().playerSession.remove(player);
        player.setLocation(onJoinLocations.get(player));
        player.sendMessage(Text.of("You've left the session."));
        checkSessionPreReq();
    }

    private void checkSessionPreReq() throws NullPointerException{
        plugin.getLogger().debug("Check session prereq.");
        if (players.size() == minPlayer) {
            sessionAboutToStart();
        } else if (players.size() < minPlayer) {
            try {
                lobbyTimer.cancel();
                plugin.getLogger().info("Timer is off.");
                plugin.getLogger().debug("Timer is off (debugger).");
            } catch (NullPointerException e) {}
        }

    }
}
