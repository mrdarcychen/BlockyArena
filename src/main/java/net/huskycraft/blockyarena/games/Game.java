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

import net.huskycraft.blockyarena.arenas.Arena;
import net.huskycraft.blockyarena.games.states.EnteringState;
import net.huskycraft.blockyarena.games.states.MatchState;
import net.huskycraft.blockyarena.utils.DamageData;
import net.huskycraft.blockyarena.utils.Gamer;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * A Game represents a specific session dedicated to a single duel.
 */
public class Game {

    protected Arena arena;
    protected int teamSize;
    //The current state this game is on
    private MatchState state;
    private List<Gamer> gamersList;
    private final long totalCapacity;

    public Game(int teamSize, Arena arena) {
        this.arena = arena;
        this.teamSize = teamSize;
        this.setGamersList(new ArrayList<Gamer>());
        arena.setBusy(true);
        state = new EnteringState(this);
        totalCapacity = teamSize * arena.getStartPoints().count();
    }

    public void add(Gamer gamer) {
        state.recruit(gamer);
    }

    public void remove(Gamer gamer) {
        state.dismiss(gamer);
    }

    public void analyze(DamageEntityEvent event, DamageData damageData) {
        state.analyze(event, damageData);
    }

    public boolean canJoin() {
        return state instanceof EnteringState;
    }

    public long getTotalCapacity() {
        return totalCapacity;
    }

    public Arena getArena() {
        return arena;
    }

    public void setMatchState(MatchState state) {
        this.state = state;
    }

	public List<Gamer> getGamersList() {
		return gamersList;
	}

	public void setGamersList(List<Gamer> gamersList) {
		this.gamersList = gamersList;
	}

	public int getTeamSize() {
        return teamSize;
    }
}