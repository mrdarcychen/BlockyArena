package net.huskycraft.blockyarena.games;

import net.huskycraft.blockyarena.*;
import net.huskycraft.blockyarena.arenas.Arena;
import net.huskycraft.blockyarena.arenas.ArenaState;
import net.huskycraft.blockyarena.utils.Gamer;
import net.huskycraft.blockyarena.utils.GamerStatus;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * A Game represents a specific session dedicated to a single duel.
 */
public class Game {

    public static BlockyArena plugin;
    protected Arena arena; // the Arena associated with this Game
    protected List<Gamer> gamers; // the list of Gamers in this Game with corresponding connection status
    protected TeamMode teamMode;
    protected GameState gameState;
    protected Team teamA, teamB;
    private Task timer;

    /**
     * Constructs a Game with the given team mode and an arena.
     * The GameState is set to RECRUITING by default.
     * @param arena an enabled arena
     */
    public Game(BlockyArena plugin, TeamMode teamMode, Arena arena, int numTeams) {
        this.plugin = plugin;
        this.teamMode = teamMode;
        this.arena = arena;
        gamers = new ArrayList<>();
        gameState = GameState.RECRUITING;
        arena.setState(ArenaState.OCCUPIED);
    }

    /**
     * Adds the given Gamer to this Game. Assumes that the given Gamer has already prepared for joining a Game by
     * saving properties and updating status.
     *
     * @param gamer the Gamer to be added to this Game
     */
    public void add(Gamer gamer) {
        // reject if the game reaches maximum capacity or is currently not recruiting
        if (gamers.size() == teamMode.getCapacity() * 2 || gameState != GameState.RECRUITING) {
            gamer.getPlayer().sendMessage(Text.of("Unable to join the game at this time."));
            return;
        }
        gamers.add(gamer);
        broadcast(Text.of(gamer.getName() + " joined the game."));
        inspect();
    }

    /**
     * Removes the given gamer from this Game.
     *
     * @param gamer the gamer to be removed from this Game
     */
    public void remove(Gamer gamer) {
        // if the game is in progress, eliminate before removing the player
        if (gameState == GameState.RECRUITING || gameState == GameState.STARTING) {
            gamers.remove(gamer);
            broadcast(Text.of(gamer.getName() + " left the game."));
            inspect();
        } else if (gameState == GameState.STARTED) {
            eliminate(gamer, Text.of(gamer.getPlayer().getName() + " disconnected."));
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
        Text deathText = Text.builder("YOU DIED!")
                .color(TextColors.RED).build();
        Title deathTitle = Title.builder()
                .title(deathText).fadeOut(2).stay(16).build();
        gamer.getPlayer().sendTitle(deathTitle);
        gamer.spectate(this);
        checkStoppingCondition();
    }

    /**
     * Inspects the current condition of the Game. The starting countdown of this game is triggered if the number of
     * players matches the starting condition of the associated game mode, otherwise the countdown will be canceled.
     * This Game is terminated if there is no player active in the session.
     */
    private void inspect() {
        // terminate if there is no active gamer
        if (gamers.isEmpty()) {
            terminate();
            return;
        }
        boolean canSolo = teamMode.getCapacity() * 2 == gamers.size();
        boolean canDoubles = teamMode.getCapacity() * 2 == gamers.size();
        if (canSolo || canDoubles) {
            teamA = new Team(arena.getTeamSpawnA(), this);
            teamB = new Team(arena.getTeamSpawnB(), this);
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
            broadcast(Text.of("Waiting for more players to join ..."));
        }
    }
    
    /**
     * Checks to see if this Game should stop based on the current condition.
     *
     * A Game should stop when either one of the Team has no player alive.
     */
    private void checkStoppingCondition() {
        if (!teamA.hasGamerLeft() || !teamB.hasGamerLeft()) {
            onGameStopping();            
        }
    }

    /**
     * Executed as soon as the Game has started.
     */
    public void onGameStarted() {
        teamA.sendAllToSpawn();
        teamB.sendAllToSpawn();
        gameState = GameState.STARTED;
    }

    /**
     * Executed when the Game is stopping.
     */
    public void onGameStopping() {
        gameState = GameState.STOPPING;
        Title victory = Title.builder()
                .title(Text.builder("VICTORY!").color(TextColors.GOLD).build())
                .build();
        Title gameOver = Title.builder()
                .title(Text.builder("GAME OVER!").color(TextColors.RED).build())
                .build();
        if (teamA.hasGamerLeft()) {
            teamA.broadcast(victory);
            teamB.broadcast(gameOver);
        } else if (teamB.hasGamerLeft()) {
            teamB.broadcast(victory);
            teamA.broadcast(gameOver);
        }
        Task.builder().execute(() -> terminate()).delay(3, TimeUnit.SECONDS).submit(plugin);
    }

    private void startingCountdown(int second) {
        if (second == 0) {
            timer.cancel();
            onGameStarted();
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
     * Broadcasts the given message to all connected Gamers in this Game.
     *
     * @param msg the message to be delivered
     */
    public void broadcast(Text msg) {
        for (Gamer gamer : gamers) {
            // broadcast if the Gamer still has connection
            if (gamer.getGame() == this) {
                gamer.getPlayer().sendMessage(msg);
            }
        }
    }

    /**
     * Terminates this Game permanently.
     */
    public void terminate() {
        for (Gamer gamer : gamers) {
            // remove if the gamer still has connection
            if (gamer.getGame() == this) {
                gamer.quit();
            }
        }
        arena.setState(ArenaState.AVAILABLE);
        plugin.getGameManager().remove(this);
    }

    public Arena getArena() {
        return arena;
    }

    public Team getTeam(Gamer gamer) {
        if (teamA.contains(gamer)) {
            return teamA;
        } else if (teamB.contains(gamer)) {
            return teamB;
        }
        return null;
    }
}