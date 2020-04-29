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

import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;

import net.huskycraft.blockyarena.games.Game;
import net.huskycraft.blockyarena.utils.DamageData;
import net.huskycraft.blockyarena.utils.Gamer;

public class EnteringState extends MatchState {

    public EnteringState(Game game) {
        super(game);
    }

    @Override
    public void recruit(Gamer gamer) {
        if (gamers.size() == game.getTeamMode().getCapacity() * 2) { // TODO: starting condition, need to change to accommodate more modes
            gamer.getPlayer().sendMessage(Text.of("Unable to join the game at this time."));
            return;
        }
        gamers.add(gamer);
        broadcast(Text.of(gamer.getName() + " joined the game. " + "(" +
                gamers.size() + "/" + game.getTeamMode().getCapacity() * 2 + ")"));
        boolean canSolo = game.getTeamMode().getCapacity() * 2 == gamers.size();
        boolean canDoubles = game.getTeamMode().getCapacity() * 2 == gamers.size();
        if (canSolo || canDoubles) {
            game.setMatchState(new StartingState(game, gamers, 15));
        }
    }

    @Override
    public void dismiss(Gamer gamer) {
        super.dismiss(gamer);
        // if no one is left, cancel timer and go directly to leaving
        if (gamers.isEmpty()) {
            game.setMatchState(new LeavingState(game));
        }
    }

    public void analyze(DamageEntityEvent event, DamageData damageData) {
        super.analyze(event, damageData);
    }
}
