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

import io.github.mrdarcychen.ConfigManager;
import io.github.mrdarcychen.games.Match;
import io.github.mrdarcychen.utils.DamageData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;

import java.util.List;

public class EnteringState extends MatchState {

    public EnteringState(Match match, List<Player> players) {
        super(match, players);
    }

    @Override
    public void recruit(Player player) {
        if (fullCapacityReached()) {
            notifyPlayerFailedToJoin(player);
            return;
        }
        super.recruit(player);
        broadcastRecruitMessage(player);
        if (fullCapacityReached()) {
            match.setMatchState(new StartingState(match, players, ConfigManager.getInstance().getLobbyCountdown()));
        }
    }

    private void notifyPlayerFailedToJoin(Player player) {
        player.sendMessage(Text.of("Unable to join the game at this time."));
    }

    private boolean fullCapacityReached() {
        return players.size() == matchRules.getTotalCapacity();
    }

    private void broadcastRecruitMessage(Player player) {
        broadcast(Text.of(player.getName() + " joined the game. " + "(" +
                players.size() + "/" + matchRules.getTotalCapacity() + ")"));
    }

    @Override
    public void dismiss(Player player) {
        super.dismiss(player);
        players.remove(player);
        broadcastDismissMessage(player);
        // if no one is left, cancel timer and go directly to leaving
        if (players.isEmpty()) {
            match.setMatchState(new LeavingState(match, players));
        }
    }

    private void broadcastDismissMessage(Player player) {
        broadcast(Text.of(player.getName() + " left the game." +
                "(" + players.size() + "/" + matchRules.getTotalCapacity() + ")"));
    }

    public void analyze(DamageEntityEvent event, DamageData damageData) {
        super.analyze(event, damageData);
    }
}
