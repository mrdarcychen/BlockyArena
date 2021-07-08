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

package io.github.mrdarcychen.listeners;

import io.github.mrdarcychen.commands.SessionRegistry;
import io.github.mrdarcychen.games.PlayerManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.util.UUID;

public class ClientConnectionEventListener {

    public ClientConnectionEventListener() {
    }

    @Listener
    public void onGamerLogin(ClientConnectionEvent.Login event) {
        User user = event.getTargetUser();
        UUID uniqueId = user.getUniqueId();
        PlayerManager.register(uniqueId);
    }

    @Listener
    public void onGamerLogout(ClientConnectionEvent.Disconnect event) {
        Player player = event.getTargetEntity();
        PlayerManager.getGame(player.getUniqueId()).ifPresent(game -> game.remove(player));
        PlayerManager.unregister(player.getUniqueId());
    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event) {
        SessionRegistry.terminateAll();
    }

}
