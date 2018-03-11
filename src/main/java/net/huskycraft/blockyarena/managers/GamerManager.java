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
package net.huskycraft.blockyarena.managers;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.utils.Gamer;
import org.spongepowered.api.entity.living.player.Player;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

public class GamerManager {

    public static BlockyArena plugin;

    private SortedMap<UUID, Gamer> gamers;

    public GamerManager(BlockyArena plugin) {
        this.plugin = plugin;
        gamers = new TreeMap<>();
    }

    /**
     * Registers a first join player by creating a unique Gamer profile for the player.
     * @param player a Player who has not played before
     */
    public void register(Player player) {
        gamers.put(player.getUniqueId(), new Gamer(player));
    }

    /**
     * Gets the Gamer profile of the given Player.
     * @param player a user who has been registered on his first join
     * @return the Gamer profile of the given Player
     */
    public Gamer getGamer(Player player) {
        return gamers.get(player.getUniqueId());
    }

    public boolean hasGamer(Player player) {
        return gamers.containsKey(player.getUniqueId());
    }
}
