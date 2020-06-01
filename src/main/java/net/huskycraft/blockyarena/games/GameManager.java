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

import net.huskycraft.blockyarena.arenas.Arena;
import net.huskycraft.blockyarena.arenas.ArenaManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * The GameManager manages all registered Game session in the server.
 */
public class GameManager {

	private static final GameManager INSTANCE = new GameManager();
	
    private List<Game> games; // a list of active games in the server

    private GameManager() {
    	games = new ArrayList<>();
    }

	public static GameManager getInstance() {
		return INSTANCE;
	}
    
    /**
     * Gets an available active Game from the list based on the given team mode.
     * 
     * If there no available game for the given type, we create another Game object
     * @return null if no Game is available
     */
    public Game getGame(String str) {
        String mode = str.toLowerCase();
        Predicate<Game> criteria = (it) -> it.canJoin() && it.getTeamMode().toString().equals(mode);
        Optional<Game> optGame = games.stream().filter(criteria).findAny();
        if (optGame.isPresent()) {
            return optGame.get();
        }
        Optional<Arena> optArena = ArenaManager.getInstance().findArena(mode);
        if (optArena.isPresent()) {
            Arena arena = optArena.get();
            int teamSize = 1;
            int teamCount = 2;
            if ("2v2".equals(mode)) {
                teamSize = 2;
            }
            if ("ffa".equals(mode)) {
                teamCount = (int) arena.getStartPoints().count();
            }
            TeamMode teamMode = new TeamMode(teamSize, teamCount);
            Game game = new Game(teamMode, optArena.get());
            games.add(game);
            return game;
        }
        return null;
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