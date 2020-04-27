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

package net.huskycraft.blockyarena.listeners;

import java.util.UUID;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.games.GamersManager;
import net.huskycraft.blockyarena.utils.Gamer;

public class ClientConnectionEventListener {


    public ClientConnectionEventListener() {
    }

    @Listener
    public void onGamerLogin(ClientConnectionEvent.Login event) {
        User user = event.getTargetUser();
        UUID uniqueId = user.getUniqueId();
        if (!GamersManager.getGamer(uniqueId).isPresent()) {
            GamersManager.register(uniqueId);

        }
        Gamer gamer = GamersManager.getGamer(uniqueId).get();
        gamer.setOnline(true);
        gamer.setName(user.getName());
        gamer.setPlayer(user.getPlayer().get());
        
        BlockyArena.getInstance().getLogger().debug("A new player logged in !");
    }

    @Listener
    public void onGamerLogout(ClientConnectionEvent.Disconnect event) {
        Player player = event.getTargetEntity();
        Gamer gamer = GamersManager.getGamer(player.getUniqueId()).get();
        gamer.setOnline(false);
        if (gamer.getGame() != null) {
            gamer.quit();
            BlockyArena.getInstance().getLogger().debug("A player disconnected !");
        }
    }

}
