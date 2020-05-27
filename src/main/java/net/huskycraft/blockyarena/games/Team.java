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

import java.util.*;

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

    private Map<Gamer, Boolean> gamers; // gamers and whether eliminated or not
    private SpawnPoint startPoint;
    private Game game;

    public Team(SpawnPoint startPoint, Game game) {
        this.startPoint = startPoint;
        this.game = game;
        gamers = new HashMap<>();
    }

    /**
     * Adds the given Gamer to this Team.
     *
     * @param gamer the Gamer to be added to this Team
     */
    public void add(Gamer gamer) {
        gamers.put(gamer, false);
    }

    public void eliminate(Gamer gamer) {
        gamers.replace(gamer, true);
    }

    public void sendAllToSpawn() {
        gamers.keySet().forEach(it -> it.getPlayer().setTransform(startPoint.getTransform()));
    }

    public boolean hasGamerLeft() {
        return gamers.values().stream().anyMatch(it -> !it);
    }

    public void broadcast(Text text) {
        gamers.keySet().forEach(it -> it.getPlayer().sendMessage(text));
    }

    public void broadcast(Title title) {
        gamers.keySet().forEach(it -> it.getPlayer().sendTitle(title));
    }

    public boolean isEliminated(Gamer gamer) {
        return gamers.get(gamer);
    }

    /**
     * Gets if the Game contains the given Gamer.
     *
     * @param gamer the Gamer to be inspected
     * @return true if the Gamer is on this Team, false otherwise
     */
    public boolean contains(Gamer gamer) {
        return gamers.containsKey(gamer);
    }

    public Set<Gamer> getGamers() {
        return gamers.keySet();
    }

    public String toString() {
        String str = "";
        Iterator<Gamer> gamersItr = gamers.keySet().iterator();
        while (gamersItr.hasNext()) {
            str += gamersItr.next().getName();
            if (gamersItr.hasNext()) {
                str += ", ";
            }
        }
        return str;
    }
}
