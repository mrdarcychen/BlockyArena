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

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.arenas.Arena;
import net.huskycraft.blockyarena.arenas.ArenaState;
import net.huskycraft.blockyarena.games.states.EnteringState;
import net.huskycraft.blockyarena.games.states.MatchState;
import net.huskycraft.blockyarena.utils.DamageData;
import net.huskycraft.blockyarena.utils.Gamer;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Title;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * A Game represents a specific session dedicated to a single duel.
 */
public class Game {

    protected Arena arena;
    protected TeamMode teamMode;
    private MatchState state;

    public Game(TeamMode teamMode, Arena arena) {
        this.teamMode = teamMode;
        this.arena = arena;
        arena.setState(ArenaState.OCCUPIED);
        state = new EnteringState(this);
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

    public TeamMode getTeamMode() {
        return teamMode;
    }

    public Arena getArena() {
        return arena;
    }

    public void setMatchState(MatchState state) {
        this.state = state;
    }
}