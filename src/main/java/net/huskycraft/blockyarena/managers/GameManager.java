/*
 * This file is part of BlockyArena, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018 HuskyCraft <https://www.huskycraft.net>
 * Copyright (c) 2018 Darcy-Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
        Game game = new Game(plugin, teamMode, arena);
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