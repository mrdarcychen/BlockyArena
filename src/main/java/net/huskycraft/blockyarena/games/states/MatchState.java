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

import net.huskycraft.blockyarena.games.Game;
import net.huskycraft.blockyarena.utils.DamageData;
import net.huskycraft.blockyarena.utils.Gamer;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;

import java.util.List;

public abstract class MatchState {

    protected final Game game;
    protected List<Gamer> gamers;

    public MatchState(Game game) {
        this.game = game;
        this.gamers = game.getGamersList();
    }

    /*
     * Called when a new player try to join an arena
     */
    public void recruit(Gamer gamer) {

    }

    /*
     * Called when you quit the game
     */
    public void dismiss(Gamer gamer) {
        gamers.remove(gamer);
        broadcast(Text.of(gamer.getName() + " left the game." +
                "(" + gamers.size() + "/" + game.getTotalCapacity() + ")"));
    }

    /*
     * Called when you kill a @Gamer gamer
     */
    public void eliminate(Gamer gamer, Text cause) {

    }

    public void analyze(DamageEntityEvent event, DamageData damageData) {
        if (damageData.getDamageType().getName().equalsIgnoreCase("void")) {
            damageData.getVictim().getPlayer().setTransform(game.getArena().getLobbySpawn().getTransform());
        }
        event.setCancelled(true);
    }

    /**
     * Broadcasts the given message to all connected Gamers in this Game.
     *
     * @param msg the message to be delivered
     */
    public void broadcast(Text msg) {
        for (Gamer gamer : gamers) {
            // broadcast if the Gamer still has connection
            if (gamer.getGame() == game) {
                gamer.getPlayer().sendMessage(msg);
            }
        }
    }
}
