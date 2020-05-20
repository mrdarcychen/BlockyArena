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
