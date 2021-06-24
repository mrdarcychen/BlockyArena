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

package io.github.mrdarcychen.games;

import io.github.mrdarcychen.games.states.EnteringState;
import io.github.mrdarcychen.games.states.MatchState;
import io.github.mrdarcychen.games.states.StartingState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerManager {

    private static final Map<UUID, GameSession> players = new HashMap<>();

    /**
     * Registers a new Gamer with the given {@link UUID}.
     *
     * @param uniqueId the {@link UUID} of the Gamer
     */
    public static void register(UUID uniqueId) {
        players.replace(uniqueId, null);
    }

    public static void unregister(UUID uniqueId) {
        players.remove(uniqueId);
    }

    public static Optional<GameSession> getGame(UUID uniqueId) {
        return Optional.ofNullable(players.getOrDefault(uniqueId, null));
    }

    public static void setGame(UUID uniqueId, GameSession gameSession) {
        players.putIfAbsent(uniqueId, gameSession);
    }

    public static void clearGame(UUID uniqueId) {
        players.remove(uniqueId);
    }

    public static boolean isPlaying(UUID uniqueId) {
        return players.containsKey(uniqueId);
    }

    /**
     * Checks to see if the given player is waiting in the lobby.
     * @param uniqueId the id of the player
     * @return true if the player is waiting in the lobby
     */
    public static boolean isWaiting(UUID uniqueId) {
        if (isPlaying(uniqueId)) {
            MatchState state = players.get(uniqueId).getState();
            return (state instanceof EnteringState || state instanceof StartingState);
        }
        return false;
    }
}
