/*
 * This file is part of BlockyArena, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018 HuskyCraft <https://www.huskycraft.net>
 * Copyright (c) 2018 Darcy-Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.huskycraft.blockyarena.listeners;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.utils.Gamer;
import net.huskycraft.blockyarena.utils.GamerStatus;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class ClientConnectionEventListener {

    public static BlockyArena plugin;

    public ClientConnectionEventListener(BlockyArena plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onClientLogin(ClientConnectionEvent.Login event) {
        Player player = event.getTargetUser().getPlayer().get();
        if (!plugin.getGamerManager().hasGamer(player)) {
            plugin.getGamerManager().register(player);
            plugin.getLogger().info("A new gaming profile is created for player " + player.getName());
        } else {
            plugin.getGamerManager().getGamer(player).setPlayer(player);
        }
        plugin.getGamerManager().getGamer(player).setStatus(GamerStatus.AVAILABLE);
    }

    @Listener
    public void onClientLogout(ClientConnectionEvent.Disconnect event) {
        Player player = event.getTargetEntity();
        Gamer gamer = plugin.getGamerManager().getGamer(player);
        if (gamer.getGame() != null) {
            gamer.quit();
        }
        gamer.setStatus(GamerStatus.OFFLINE);
    }

}
