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

import io.github.mrdarcychen.arenas.Arena;
import io.github.mrdarcychen.games.states.MatchState;
import org.spongepowered.api.entity.living.player.Player;

public interface GameSession {
    void add(Player player);

    void remove(Player player);

    boolean canJoin();

    Arena getArena();

    void setMatchState(MatchState state);

    MatchRules getTeamMode();

    PlayerAssistant getPlayerAssistant();
}
