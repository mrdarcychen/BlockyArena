package net.huskycraft.blockyarena;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PvPSession extends Session{

    private BlockyArena plugin;

    private Arena arena;

    private ArrayList<Player> players;  //all players in the PvPSession
    private ArrayList<Player> teamBlue; //players in team blue
    private ArrayList<Player> teamRed;  //players in team red

    private HashMap<Player, Location<World>> onJoinLocations;

    Task lobbyTimer, gameTimer;

    private int minPlayer;
    public boolean canJoin; //canJoin is true when in lobby wait period, false when the game starts

    public PvPSession(BlockyArena plugin, Arena arena) {

        this.plugin = plugin;
        this.arena = arena;
        arena.setStatus(true);
        players = new ArrayList<>();
        onJoinLocations = new HashMap<>();
        teamBlue = new ArrayList<>();
        teamRed = new ArrayList<>();
        minPlayer = arena.getMinPlayer();
        canJoin = true;
    }

    private void countdown(int countdown) {
        if (countdown == 0) {
            PvPSessionStarting();
        } else {
            for (Player player : players) {
                player.sendTitle(Title.builder().title(Text.of(countdown)).fadeIn(2).fadeOut(2).stay(16).build());
                player.playSound(SoundTypes.BLOCK_DISPENSER_DISPENSE, player.getHeadRotation(), 100);
            }
            lobbyTimer = Task.builder().execute(() -> countdown(countdown - 1)).delay(1, TimeUnit.SECONDS).submit(plugin);
        }
    }

    private void PvPSessionStarting() {
        canJoin = false;
        Collections.shuffle(players);
        for (Player player : players) {
            if (players.indexOf(player) % 2 == 0) {
                teamBlue.add(player);
                player.sendMessage(Text.of("You're in blue team!"));
                player.setLocation(arena.getBlueSpawn());
            } else {
                teamRed.add(player);
                player.sendMessage(Text.of("You're in red team!"));
                player.setLocation(arena.getRedSpawn());
            }
        }

        PvPSessionInProgress();
    }

    private void PvPSessionInProgress() {
        // TODO
        gameTimer = Task.builder().execute(() -> PvPSessionStopping()).delay(arena.getGameCountdown(), TimeUnit.SECONDS).submit(plugin);
    }

    /**
     * Calculating player stats and announce winner.
     */
    private void PvPSessionStopping() {
        gameTimer.cancel();
        for (Player player : players) {
            player.setLocation(onJoinLocations.get(player));
            player.sendMessage(Text.of("PvPSession is over. "));
            plugin.getSessionManager().playerSession.remove(player);
            player.offer(player.getHealthData().set(Keys.HEALTH, 20.0));
            player.getInventory().clear();
        }
        PvPSessionStopped();
    }

    private void PvPSessionStopped() {
        teamBlue.clear();
        teamRed.clear();
        players.clear();
        lobbyTimer = null;
        gameTimer = null;
        onJoinLocations.clear();
        canJoin = true;
    }

    private void checkPvPSessionCondition() {
        if (teamRed.size() == 0 && teamBlue.size() != 0) {
            for (Player player : players) {
                player.sendMessage(Text.of("Team blue won!"));
            }
            PvPSessionStopping();
        } else if (teamRed.size() != 0 && teamBlue.size() == 0) {
            for (Player player : players) {
                player.sendMessage(Text.of("Team red won!"));
            }
            PvPSessionStopping();
        }

    }

    private void checkStartingPreCond() {
        if (players.size() == minPlayer && lobbyTimer == null) {
            countdown(arena.getLobbyCountdown());
        } else if (players.size() < minPlayer) {
            try {
                lobbyTimer.cancel();
                lobbyTimer = null;
            } catch (NullPointerException e) {
            }
        }
    }

    public void add(Player player) {
        if (canJoin) {
            player.getHealthData().health();
            onJoinLocations.put(player, player.getLocation());
            player.offer(player.getHealthData().set(Keys.HEALTH, 20.0));
            players.add(player);
            plugin.getSessionManager().playerSession.put(player, this);
            player.offer(player.gameMode().set(GameModes.ADVENTURE));
            player.setLocation(arena.getLobbySpawn());
            player.sendMessage(Text.of("Waiting for the game to start..."));
            checkStartingPreCond();
        }
    }

    public void remove(Player player) {
        players.remove(player);
        if (teamBlue.contains(player)) teamBlue.remove(player);
        if (teamRed.contains(player)) teamRed.remove(player);
        plugin.getSessionManager().playerSession.remove(player);
        player.setLocation(onJoinLocations.get(player));
        player.getInventory().clear();
        player.sendMessage(Text.of("You've left the PvPSession."));
        player.offer(player.getHealthData().set(Keys.HEALTH, 20.0));
        if (canJoin) {
            checkStartingPreCond();
        } else {
            checkPvPSessionCondition();
        }
    }

    public void onPlayerDeath(Player player) {
        if (teamRed.contains(player)) {
            teamRed.remove(player);
        } else if (teamBlue.contains(player)) {
            teamBlue.remove(player);
        }
        player.sendMessage(Text.of("You were killed."));
        player.offer(player.getHealthData().set(Keys.HEALTH, 20.0));
        player.setLocation(arena.getLobbySpawn());
        player.getInventory().clear();
        checkPvPSessionCondition();
    }
}

