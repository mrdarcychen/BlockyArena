/*
 * Copyright 2017-2020 The BlockyArena Contributors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.github.mrdarcychen.games;

import io.github.mrdarcychen.utils.PlayerSnapshot;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;

public class SimplePlayerAssistant implements PlayerAssistant {

    private List<PlayerSnapshot> snapshots;

    public SimplePlayerAssistant() {
        snapshots = new ArrayList<>();
    }


    @Override
    public void prepare(Player player) {
        snapshots.add(new PlayerSnapshot(player));
    }

    @Override
    public void restore(Player player) {
        snapshots.stream().filter(it -> it.getPlayer().equals(player)).findAny()
                .ifPresent(PlayerSnapshot::restore);
    }

    @Override
    public void restoreAll() {
        snapshots.forEach(PlayerSnapshot::restore);
    }
}
