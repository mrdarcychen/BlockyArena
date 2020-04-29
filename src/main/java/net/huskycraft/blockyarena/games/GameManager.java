/*
 * Copyright 2017-2020 The BlockyArena Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.huskycraft.blockyarena.games;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.arenas.Arena;

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
            boolean isActive = game.canJoin();
            boolean isGivenType = game.getTeamMode() == teamMode;
            if (isActive && isGivenType) {
                return game;
            }
        }
        // if there is no active Game, initialize a new Game
        Arena arena = BlockyArena.getArenaManager().getArena();
        // if there is no available Arena, no Game can be instantiated and null is returned
        if (arena == null) return null;
        Game game = new Game(teamMode, arena);
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