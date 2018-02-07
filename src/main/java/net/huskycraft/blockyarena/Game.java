package net.huskycraft.blockyarena;

import com.google.inject.Inject;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

import java.util.HashSet;
import java.util.Set;

public abstract class Game {

    @Inject
    public static BlockyArena plugin;
    private Arena arena;
    private Set<Gamer> gamers;
    private TeamMode teamMode;
    private GameState gameState;

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
    }

    /**
     * Removes the given gamer from this Game.
     */
    public void remove(Gamer gamer) {
        gamers.remove(gamer);
        gamer.retrieveInventory(); // TODO: gives the original inventory back to the gamer
        gamer.setLocation(gamer.getLastLocation()); // TODO: spawns the gamer at the location before joining the game
        gamer.getPlayer().gameMode().set(GameModes.SURVIVAL);
    }

    /**
     * Eliminates the given gamer from this Game.
     */
    public void eliminate(Gamer gamer) {
        gamer.spawnAt(arena.getSpectatorSpawn()); // TODO: spawns the gamer at the spectator spawn
        gamer.getPlayer().gameMode().set(GameModes.SPECTATOR);
    }
}