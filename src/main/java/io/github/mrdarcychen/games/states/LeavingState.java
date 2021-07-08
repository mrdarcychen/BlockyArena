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

package io.github.mrdarcychen.games.states;

import io.github.mrdarcychen.commands.SessionRegistry;
import io.github.mrdarcychen.games.GameSession;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

public class LeavingState extends MatchState {

    private static final Text GAME_CONCLUDED = Text
            .builder("The minigame has concluded.")
            .color(TextColors.GREEN).build();

    public LeavingState(GameSession gameSession, List<Player> players) {
        super(gameSession, players);
        players.forEach((player -> player.sendMessage(ChatTypes.ACTION_BAR, GAME_CONCLUDED)));
        SessionRegistry.remove(gameSession);
    }
}
