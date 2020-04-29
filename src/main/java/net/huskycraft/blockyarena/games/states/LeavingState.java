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

package net.huskycraft.blockyarena.games.states;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.arenas.ArenaState;
import net.huskycraft.blockyarena.games.Game;
import net.huskycraft.blockyarena.utils.Gamer;

public class LeavingState extends MatchState {

    public LeavingState(Game game) {
        super(game);
        for (Gamer gamer : gamers) {
            // remove if the gamer still has connection
            if (gamer.getGame() == game) {
                gamer.quit();
            }
        }
        game.getArena().setState(ArenaState.AVAILABLE);
        BlockyArena.getGameManager().remove(game);
    }

}
