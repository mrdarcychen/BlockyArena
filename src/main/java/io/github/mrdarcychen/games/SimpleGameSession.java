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

import io.github.mrdarcychen.BlockyArena;
import io.github.mrdarcychen.arenas.Arena;
import io.github.mrdarcychen.games.states.EnteringState;
import io.github.mrdarcychen.games.states.MatchState;
import io.github.mrdarcychen.utils.DamageData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import java.util.ArrayList;

/**
 * A Game represents a specific session dedicated to a single duel.
 */
public class SimpleGameSession implements GameSession {

    private final MatchRules matchRules;
    protected Arena arena;
    private MatchState state;
    private final PlayerAssistant playerAssistant;

    public SimpleGameSession(MatchRules matchRules, Arena arena) {
        this.arena = arena;
        this.matchRules = matchRules;
        arena.setBusy(true);
        playerAssistant = new SimplePlayerAssistant(this);
        state = new EnteringState(this, new ArrayList<>());
        Sponge.getEventManager().registerListeners(BlockyArena.getInstance(), new EventListener());
    }

    @Override
    public void add(Player player) {
        state.recruit(player);
    }

    @Override
    public void remove(Player player) {
        state.dismiss(player);
    }

    @Override
    public boolean canJoin() {
        return state instanceof EnteringState;
    }

    @Override
    public Arena getArena() {
        return arena;
    }

    @Override
    public void setMatchState(MatchState state) {
        this.state = state;
    }

    @Override
    public MatchRules getTeamMode() {
        return matchRules;
    }

    @Override
    public PlayerAssistant getPlayerAssistant() {
        return playerAssistant;
    }

    public class EventListener {
        @Listener
        public void onDamageEntity(DamageEntityEvent event) {
            if (event.getTargetEntity() instanceof Player) {
                Player player = (Player) event.getTargetEntity();
                if (playerAssistant.contains(player)) {
                    System.out.println("Proceed to analysis of " + player.getName());
                    state.analyze(event, new DamageData(player, event.getCause()));
                }
            }
        }
    }
}
