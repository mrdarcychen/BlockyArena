package net.huskycraft.blockyarena;

import com.google.inject.Inject;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Game {

    @Inject
    public static BlockyArena plugin;
    protected Arena arena;
    protected Set<Gamer> gamers;
    protected TeamMode teamMode;
    protected GameState gameState;
    protected Team teamA, teamB;
    private Task timer;

    /**
     * Constructs a Game with the given team mode and an arena.
     * The GameState is set to RECRUITING by default.
     * @param arena an enabled arena
     */
    public Game(TeamMode teamMode, Arena arena) {
        this.teamMode = teamMode;
        this.arena = arena;
        gamers = new HashSet<>();
        gameState = GameState.RECRUITING;
        timer = Task.builder().submit(plugin);
    }

    /**
     * Adds the given gamer to this Game.
     */
    public void add(Gamer gamer) {
        gamers.add(gamer);
        gamer.saveInventory(); // TODO: saves the inventory of the gamer and clear it
        gamer.setLastLocation();
        gamer.spawnAt(arena.getLobbySpawn()); // TODO: spawns the gamer at the lobby spawn
        gamer.getPlayer().gameMode().set(GameModes.SURVIVAL);
        checkPreCond();
    }

    /**
     * Removes the given gamer from this Game.
     */
    public void remove(Gamer gamer) {
        gamers.remove(gamer);
        gamer.retrieveInventory();
        gamer.setLocation(gamer.getLastLocation());
        gamer.getPlayer().gameMode().set(GameModes.SURVIVAL);
        checkPreCond();
    }

    /**
     * Eliminates the given gamer from this Game.
     */
    public void eliminate(Gamer gamer) {
        gamer.spawnAt(arena.getSpectatorSpawn());
        gamer.getPlayer().gameMode().set(GameModes.SPECTATOR);
    }

    private void startingCountdown(int second) {
        if (second == 0) {
            timer.cancel();
            gameState = GameState.IN_PROGRESS;
        } else {
            Title title = Title.builder()
                    .title(Text.of(second)).fadeIn(2).fadeOut(2).stay(16).build();
            for (Gamer gamer : gamers) {
                Player player = gamer.getPlayer();
                player.sendTitle(title);
                player.playSound(SoundTypes.BLOCK_DISPENSER_DISPENSE, player.getHeadRotation(), 100);
            }
        }
        timer = Task.builder()
                .execute(() -> startingCountdown(second - 1)).delay(1, TimeUnit.SECONDS).submit(plugin);
    }

    /**
     * Checks the precondition of the Game.
     * Starts the starting timer if the precondition is met, otherwise cancels the timer.
     */
    private void checkPreCond() {
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
        } else {
            timer.cancel();
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
}