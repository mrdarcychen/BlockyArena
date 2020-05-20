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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.huskycraft.blockyarena.arenas.SpawnPoint;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;

import net.huskycraft.blockyarena.utils.Gamer;
import net.huskycraft.blockyarena.utils.GamerStatus;
import org.spongepowered.api.world.World;

/**
 * A Team represents a single Gamer or a group of Gamers who cooperate to win a Game.
 */
public class Team {

    private Set<Gamer> gamers;
    private SpawnPoint startPoint;
    private Game game;

    public Team(SpawnPoint startPoint, Game game) {
        this.startPoint = startPoint;
        this.game = game;
        gamers = new HashSet<>();
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
            gamer.getPlayer().setTransform(startPoint.getTransform());
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

    public void broadcast(Text text) {
        gamers.forEach(gamer -> {
            if (gamer.getGame() == game) {
                gamer.getPlayer().sendMessage(text);
            }
        });
    }

    public void broadcast(Title title) {
        gamers.forEach(gamer -> {
            if (gamer.getGame() == game) {
                gamer.getPlayer().sendTitle(title);
            }
        });
    }

    /**
     * Gets if the Game contains the given Gamer.
     *
     * @param gamer the Gamer to be inspected
     * @return true if the Gamer is on this Team, false otherwise
     */
    public boolean contains(Gamer gamer) {
        return gamers.contains(gamer);
    }

    public Set<Gamer> getGamers() {
        return gamers;
    }

    public String toString() {
        String str = "";
        Iterator<Gamer> gamersItr = gamers.iterator();
        while (gamersItr.hasNext()) {
            str += gamersItr.next().getName();
            if (gamersItr.hasNext()) {
                str += ", ";
            }
        }
        return str;
    }
}
