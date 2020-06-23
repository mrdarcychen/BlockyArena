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

package io.github.mrdarcychen.games;

import io.github.mrdarcychen.arenas.Arena;
import io.github.mrdarcychen.games.states.EnteringState;
import io.github.mrdarcychen.games.states.MatchState;
import io.github.mrdarcychen.utils.DamageData;
import io.github.mrdarcychen.utils.PlayerSnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * A Game represents a specific session dedicated to a single duel.
 */
public class Game {

    private final List<PlayerSnapshot> snapshots;
    private final TeamMode teamMode;
    protected Arena arena;
    private MatchState state;

    public Game(TeamMode teamMode, Arena arena) {
        this.arena = arena;
        this.teamMode = teamMode;
        arena.setBusy(true);
        snapshots = new ArrayList<>();
        state = new EnteringState(this, new ArrayList<>());
    }

    public void add(Player player) {
        state.recruit(player);
    }

    public void remove(Player player) {
        state.dismiss(player);
    }

    public void analyze(DamageEntityEvent event, DamageData damageData) {
        state.analyze(event, damageData);
    }

    public void addSnapshot(PlayerSnapshot snapshot) {
        snapshots.add(snapshot);
    }

    public void restoreSnapshotOf(Player player) {
        snapshots.stream().filter(it -> it.getPlayer().equals(player)).findAny()
                .ifPresent(PlayerSnapshot::restore);
    }

    public void restoreSnapshots() {
        snapshots.forEach(PlayerSnapshot::restore);
    }

    public boolean canJoin() {
        return state instanceof EnteringState;
    }

    public Arena getArena() {
        return arena;
    }

    public void setMatchState(MatchState state) {
        this.state = state;
    }

    public TeamMode getTeamMode() {
        return teamMode;
    }
}