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

package io.github.mrdarcychen.games.states;

import io.github.mrdarcychen.arenas.SpawnPoint;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A Team represents a single Gamer or a group of Gamers who cooperate to win a Game.
 */
public class Team {

    private final Map<Player, Boolean> players; // gamers and whether eliminated or not
    private final SpawnPoint startPoint;

    public Team(SpawnPoint startPoint) {
        this.startPoint = startPoint;
        players = new HashMap<>();
    }

    public void add(Player player) {
        players.put(player, false);
    }

    public void eliminate(Player player) {
        players.replace(player, true);
    }

    public void sendAllToSpawn() {
        players.keySet().forEach(it -> it.setTransform(startPoint.getTransform()));
    }

    public boolean hasGamerLeft() {
        return players.values().stream().anyMatch(it -> !it);
    }

    public void broadcast(Text text) {
        players.keySet().forEach(it -> it.sendMessage(text));
    }

    public void broadcast(Title title) {
        players.keySet().forEach(it -> it.sendTitle(title));
    }

    public boolean isEliminated(Player player) {
        return players.getOrDefault(player, false);
    }

    public boolean contains(Player player) {
        return players.containsKey(player);
    }

    public Set<Player> getPlayers() {
        return players.keySet();
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        Iterator<Player> playersItr = players.keySet().iterator();
        while (playersItr.hasNext()) {
            str.append(playersItr.next().getName());
            if (playersItr.hasNext()) {
                str.append(", ");
            }
        }
        return str.toString();
    }
}
