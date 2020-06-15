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

import io.github.mrdarcychen.games.Game;
import io.github.mrdarcychen.managers.ConfigManager;
import io.github.mrdarcychen.utils.DamageData;
import io.github.mrdarcychen.utils.Gamer;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;

import java.util.List;

public class EnteringState extends MatchState {

    public EnteringState(Game game, List<Gamer> gamers) {
        super(game, gamers);
    }

    @Override
    public void recruit(Gamer gamer) {
        if (fullCapacityReached()) {
            notifyPlayerFailedToJoin(gamer);
            return;
        }
        super.recruit(gamer);
        //Utils.broadcastToEveryone("the arena : " + game.getArena().getID() +" is used !!", TextColors.GREEN);
        broadcastRecruitMessage(gamer);
        if (fullCapacityReached()) {
            game.setMatchState(new StartingState(game, gamers, ConfigManager.getInstance().getLobbyCountdown()));
        }
    }

    private void notifyPlayerFailedToJoin(Gamer gamer) {
        gamer.getPlayer().sendMessage(Text.of("Unable to join the game at this time."));
    }

    private boolean fullCapacityReached() {
        return gamers.size() == teamMode.getTotalCapacity();
    }

    private void broadcastRecruitMessage(Gamer gamer) {
        broadcast(Text.of(gamer.getName() + " joined the game. " + "(" +
                gamers.size() + "/" + teamMode.getTotalCapacity() + ")"));
    }

    @Override
    public void dismiss(Gamer gamer) {
        super.dismiss(gamer);
        gamers.remove(gamer);
        broadcastDismissMessage(gamer);
        // if no one is left, cancel timer and go directly to leaving
        if (gamers.isEmpty()) {
            game.setMatchState(new LeavingState(game, gamers));
        }
    }

    private void broadcastDismissMessage(Gamer gamer) {
        broadcast(Text.of(gamer.getName() + " left the game." +
                "(" + gamers.size() + "/" + teamMode.getTotalCapacity() + ")"));
    }

    public void analyze(DamageEntityEvent event, DamageData damageData) {
        super.analyze(event, damageData);
    }
}
