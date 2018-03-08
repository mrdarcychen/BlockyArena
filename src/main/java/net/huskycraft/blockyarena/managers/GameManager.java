package net.huskycraft.blockyarena.managers;

import net.huskycraft.blockyarena.*;
import net.huskycraft.blockyarena.arenas.Arena;
import net.huskycraft.blockyarena.games.Game;
import net.huskycraft.blockyarena.games.GameState;
import net.huskycraft.blockyarena.games.TeamMode;

import java.util.ArrayList;
import java.util.List;

/**
 * The GameManager manages all registered Game session in the server.
 */
public class GameManager {

    public static BlockyArena plugin;

    private List<Game> games; // a list of active games in the server

    public GameManager(BlockyArena plugin) {
        this.plugin = plugin;
        games = new ArrayList<>();
    }

    /**
     * Gets an available active Game from the list based on the given team mode.
     * @return null if no Game is available
     */
    public Game getGame(TeamMode teamMode) {
        for (Game game : games) {
            boolean isActive = game.getGameState() == GameState.RECRUITING;
            boolean isGivenType = game.getTeamMode() == teamMode;
            if (isActive && isGivenType) {
                return game;
            }
        }
        // if there is no active Game, initialize a new Game
        Arena arena = plugin.getArenaManager().getArena();
        // if there is no available Arena, no Game can be instantiated and null is returned
        if (arena == null) return null;
        Game game = new Game(plugin, teamMode, arena, 2); // TODO: customize number of teams
        games.add(game);
        return game;
    }

    /**
     * Removes the given Game from the database.
     *
     * @param game the Game to be removed
     */
    public void remove(Game game) {
        games.remove(game);
    }
}