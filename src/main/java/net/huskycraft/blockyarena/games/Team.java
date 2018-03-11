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
package net.huskycraft.blockyarena.games;

import net.huskycraft.blockyarena.utils.Gamer;
import net.huskycraft.blockyarena.utils.GamerStatus;
import net.huskycraft.blockyarena.arenas.Spawn;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A Team represents a single Gamer or a group of Gamers who cooperate to win a Game.
 */
public class Team {

    private List<Gamer> gamers;
    private Spawn teamSpawn;
    private Game game;

    /**
     * Constructs a Team with the given in-game Spawn point for this Team.
     *
     * @param teamSpawn the in-game Spawn point for this Team
     */
    public Team(Spawn teamSpawn, Game game) {
        this.teamSpawn = teamSpawn;
        this.game = game;
        gamers = new ArrayList<>();
    }

    /**
     * Adds the given Gamer to this Team.
     *
     * @param gamer the Gamer to be added to this Team
     */
    public void add(Gamer gamer) {
        gamers.add(gamer);
    }


    public void sendAllToSpawn() {
        for (Gamer gamer : gamers) {
            gamer.spawnAt(teamSpawn);
        }
    }

    public boolean hasGamerLeft() {
        for (Gamer gamer : gamers) {
            if (gamer.getGame() == game && gamer.getStatus() == GamerStatus.PLAYING) {
                return true;
            }
        }
        return false;
    }

    public void broadcast(Text msg) {
        for (Gamer gamer : gamers) {
            if (gamer.getGame() == game) {
                gamer.getPlayer().sendMessage(msg);
            }
        }
    }
}
