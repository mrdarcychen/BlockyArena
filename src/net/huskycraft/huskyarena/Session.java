package net.huskycraft.huskyarena;

import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Session {

    private HuskyArena plugin;

    private Arena arena;

    private ArrayList<Player> players;  //all players in the session
    private ArrayList<Player> teamBlue; //players in team blue
    private ArrayList<Player> teamRed;  //players in team red

    private HashMap<Player, Location<World>> onJoinLocations;

    Task lobbyTimer, gameTimer;

    private int minPlayer;
    public boolean canJoin; //canJoin is true when in lobby wait period, false when the game starts

    public Session(HuskyArena plugin, Arena arena) {

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
            sessionStarting();
        } else {
            for (Player player : players) {
                player.sendTitle(Title.builder().title(Text.of(countdown)).fadeIn(2).fadeOut(2).stay(16).build());
                player.playSound(SoundTypes.BLOCK_DISPENSER_DISPENSE, player.getHeadRotation(), 100);
            }
            lobbyTimer = Task.builder().execute(() -> countdown(countdown - 1)).delay(1, TimeUnit.SECONDS).submit(plugin);
        }
    }

    private void sessionStarting() {
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
        gameTimer.cancel();
        for (Player player : players) {
            player.setLocation(onJoinLocations.get(player));
            player.sendMessage(Text.of("Session is over."));
            plugin.getSessionManager().playerSession.remove(player);
        }
        sessionStopped();
    }

    private void sessionStopped() {
        teamBlue.clear();
        teamRed.clear();
        players.clear();
        onJoinLocations.clear();
        canJoin = true;
    }

    private void checkSessionCondition() {
        if (teamRed.size() == 0 && teamBlue.size() != 0) {
            for (Player player : players) {
                player.sendMessage(Text.of("Team blue won!"));
            }
        } else if (teamRed.size() != 0 && teamBlue.size() == 0) {
            for (Player player : players) {
                player.sendMessage(Text.of("Team red won!"));
            }
        }
        sessionStopping();
    }

    private void checkStartingPreCond() {
        if (players.size() == minPlayer) {
            countdown(arena.getLobbyCountdown());
        } else if (players.size() < minPlayer) {
            try {
                lobbyTimer.cancel();
            } catch (NullPointerException e) {
            }
        }
    }

    public void add(Player player) {
        if (canJoin) {
            onJoinLocations.put(player, player.getLocation());
            players.add(player);
            plugin.getSessionManager().playerSession.put(player, this);
            player.gameMode().set(GameModes.ADVENTURE);
            player.setLocation(arena.getLobbySpawn());
            player.sendMessage(Text.of("You're in session."));
            checkStartingPreCond();
        }
    }

    public void remove(Player player) {
        players.remove(player);
        if (teamBlue.contains(player)) teamBlue.remove(player);
        if (teamRed.contains(player)) teamRed.remove(player);
        plugin.getSessionManager().playerSession.remove(player);
        player.setLocation(onJoinLocations.get(player));
        player.sendMessage(Text.of("You've left the session."));
        if (canJoin) {
            checkStartingPreCond();
        } else {
            checkSessionCondition();
        }
    }

    public void eliminate(Player player) {
        if (teamRed.contains(player)) {
            teamRed.remove(player);
        } else if (teamBlue.contains(player)) {
            teamBlue.remove(player);
        }
        player.sendMessage(Text.of("You were killed."));
        //player.health().set(player.getHealthData().maxHealth().getMaxValue());
        player.setLocation(arena.getLobbySpawn());
        checkSessionCondition();
    }
}

