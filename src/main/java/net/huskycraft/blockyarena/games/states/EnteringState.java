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

import net.huskycraft.blockyarena.arenas.Arena;
import net.huskycraft.blockyarena.arenas.SpawnPoint;
import net.huskycraft.blockyarena.games.Game;
import net.huskycraft.blockyarena.managers.ConfigManager;
import net.huskycraft.blockyarena.utils.DamageData;
import net.huskycraft.blockyarena.utils.Gamer;
import net.huskycraft.blockyarena.utils.GamerStatus;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;

public class EnteringState extends MatchState {

    public EnteringState(Game game) {
        super(game, game.getGamersList());
    }

    @Override
    public void recruit(Gamer gamer) {
        if (gamers.size() == game.getTotalCapacity()) { // TODO: starting condition, need to change to accommodate more modes
            gamer.getPlayer().sendMessage(Text.of("Unable to join the game at this time."));
            return;
        }
        gamers.add(gamer);
        super.recruit(gamer);
        //Utils.broadcastToEveryone("the arena : " + game.getArena().getID() +" is used !!", TextColors.GREEN);
        broadcast(Text.of(gamer.getName() + " joined the game. " + "(" +
                gamers.size() + "/" + game.getTotalCapacity() + ")"));
        if (gamers.size() == game.getTotalCapacity()) {
            game.setMatchState(new StartingState(game, gamers, ConfigManager.getInstance().getLobbyCountdown()));
        }
    }

    @Override
    public void dismiss(Gamer gamer) {
        super.dismiss(gamer);
        gamers.remove(gamer);
        broadcast(Text.of(gamer.getName() + " left the game." +
                "(" + gamers.size() + "/" + game.getTotalCapacity() + ")"));
        // if no one is left, cancel timer and go directly to leaving
        if (gamers.isEmpty()) {
            game.setMatchState(new LeavingState(game, gamers));
        }
    }

    public void analyze(DamageEntityEvent event, DamageData damageData) {
        super.analyze(event, damageData);
    }
}
