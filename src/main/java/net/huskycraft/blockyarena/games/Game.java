package net.huskycraft.blockyarena.games;

import net.huskycraft.blockyarena.*;
import net.huskycraft.blockyarena.arenas.Arena;
import net.huskycraft.blockyarena.utils.Gamer;
import net.huskycraft.blockyarena.utils.GamerStatus;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * A Game represents a specific session dedicated to a single duel.
 */
public class Game {

    public static BlockyArena plugin;
    protected Arena arena; // the Arena associated with this Game
    protected Map<Gamer, Boolean> gamers; // the list of Gamers in this Game with corresponding connection status
    protected TeamMode teamMode;
    protected GameState gameState;
    protected Team teamA, teamB;
    private Task timer;

    /**
     * Constructs a Game with the given team mode and an arena.
     * The GameState is set to RECRUITING by default.
     * @param arena an enabled arena
     */
    public Game(BlockyArena plugin, TeamMode teamMode, Arena arena) {
        this.plugin = plugin;
        this.teamMode = teamMode;
        this.arena = arena;
        gamers = new HashMap<>();
        gameState = GameState.RECRUITING;
    }

    /**
     * Adds the given gamer to this Game.
     */
    public void add(Gamer gamer) {
        broadcast(Text.of(gamer.getName() + " joined the game."));
        gamers.put(gamer, true);
        gamer.saveInventory();
        gamer.saveLocation();
        gamer.setStatus(GamerStatus.PLAYING);
        gamer.setGame(this);
        gamer.spawnAt(arena.getLobbySpawn());
        gamer.getPlayer().gameMode().set(GameModes.SURVIVAL);
        gamer.getPlayer().sendMessage(Text.of("Sending you to " + arena.getID()));
        if (gameState == GameState.RECRUITING || gameState == GameState.STARTING) {
            checkStartingCondition();
        }
    }

    /**
     * Removes the given gamer from this Game.
     *
     * @param gamer the gamer to be removed from this Game
     */
    public void remove(Gamer gamer) {
        // if the game is in progress, eliminate before removing the player
        if (gameState == GameState.IN_PROGRESS) {
            eliminate(gamer, Text.of(gamer.getPlayer().getName() + " disconnected."));
        }
        broadcast(Text.of(gamer.getName() + " left the game."));
        gamer.setGame(null);
        gamer.retrieveInventory();
        gamer.setLocation(gamer.getSavedLocation());
        gamer.getPlayer().sendMessage(Text.of("You are sent to the saved location."));
        gamer.setStatus(GamerStatus.AVAILABLE);
        gamer.getPlayer().gameMode().set(GameModes.SURVIVAL);
        if (gameState == GameState.RECRUITING || gameState == GameState.STARTING) {
            gamers.remove(gamer);
            checkStartingCondition();
        }
    }

    /**
     * Eliminates the given gamer from this Game.
     *
     * @param gamer the Gamer to be eliminated
     * @param cause the reason of elimination
     */
    public void eliminate(Gamer gamer, Text cause) {
        broadcast(cause);
        gamer.spawnAt(arena.getSpectatorSpawn());
        gamer.getPlayer().gameMode().set(GameModes.SPECTATOR);
        gamer.setStatus(GamerStatus.SPECTATING);
        checkStoppingCondition();

    }

    /**
     * Checks to see if this Game should stop based on the current condition.
     *
     * A Game should stop when either one of the Team has no player alive.
     */
    private void checkStoppingCondition() {
        if (!teamA.hasGamerLeft() || !teamB.hasGamerLeft()) {
            if (teamA.hasGamerLeft()) {
                broadcast(Text.of("Team A won the game!"));
            } else if (teamB.hasGamerLeft()) {
                broadcast(Text.of("Team B won the game!"));
            }
            onGameStopping();
        }
    }


    public void onGameStopping() {
        // TODO: broadcast results
        // TODO: remove
        for (Gamer gamer : gamers.keySet()) {
            // if the gamer still has connection, remove
            if (gamers.get(gamer)) {
                gamer.setGame(null);
            }
        }
    }

    private void startingCountdown(int second) {
        if (second == 0) {
            timer.cancel();
            teamA.sendAllToSpawn();
            teamB.sendAllToSpawn();
            gameState = GameState.IN_PROGRESS;
        } else {
            Title title = Title.builder()
                    .title(Text.of(second)).fadeIn(2).fadeOut(2).stay(16).build();
            for (Gamer gamer : gamers) {
                Player player = gamer.getPlayer();
                player.sendTitle(title);
                player.playSound(SoundTypes.BLOCK_DISPENSER_DISPENSE, player.getHeadRotation(), 100);
            }
            timer = Task.builder()
                    .execute(() -> startingCountdown(second - 1)).delay(1, TimeUnit.SECONDS).submit(plugin);
        }
    }

    /**
     * Checks to see if this Game should start based on the current condition.
     *
     * Starts the starting timer if the precondition is met, otherwise cancels the timer.
     */
    private void checkStartingCondition() {
        plugin.getLogger().warn("Check precond.");
        boolean canSolo = teamMode == TeamMode.SOLO && gamers.size() == 2;
        boolean canDoubles = teamMode == TeamMode.DOUBLES && gamers.size() == 4;
        if (canSolo || canDoubles) {
            teamA = new Team(arena.getTeamSpawnA());
            teamB = new Team(arena.getTeamSpawnB());
            Iterator<Gamer> gamersItr = gamers.iterator();
            while (gamersItr.hasNext()) {
                teamA.add(gamersItr.next());
                teamB.add(gamersItr.next());
            }
            gameState = GameState.STARTING;
            startingCountdown(10);
        } else if (timer != null) {
            timer.cancel();
            gameState = GameState.RECRUITING;
        }
    }

    /**
     * Gets the state of the Game.
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Gets the mode of the team.
     */
    public TeamMode getTeamMode() {
        return teamMode;
    }

    /**
     * Checks if the gamer is in this Game.
     * @return true if the gamer is in this game, false otherwise
     */
    public boolean hasGamer(Gamer gamer) {
        return gamers.contains(gamer);
    }

    /**
     * Broadcasts the given message to all connected Gamers in this Game.
     *
     * @param msg the message to be delivered
     */
    public void broadcast(Text msg) {
        for (Gamer gamer : gamers.keySet()) {
            if (gamers.get(gamer)) {
                gamer.getPlayer().sendMessage(msg);
            }
        }
    }
}