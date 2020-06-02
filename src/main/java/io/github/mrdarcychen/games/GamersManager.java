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

import io.github.mrdarcychen.utils.Gamer;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * The manager that manages {@link Gamer}s.
 */
public class GamersManager {

    private static final Set<Gamer> gamers = new HashSet<>();

    /**
     * Registers a new Gamer with the given {@link UUID}.
     *
     * @param uniqueId the {@link UUID} of the Gamer
     */
    public static void register(UUID uniqueId) {
        gamers.add(new Gamer(uniqueId));
    }

    /**
     * Gets a registered {@link Gamer} by their {@link UUID}.
     *
     * m uniqueId the {@link UUID} of this {@link Gamer}
     * @return {@link Gamer} or Optional.empty() if not found
     */
    public static Optional<Gamer> getGamer(UUID uniqueId) {
        for (Gamer gamer : gamers) {
            if (gamer.getUniqueId().equals(uniqueId)) {
                return Optional.of(gamer);
            }
        }
        return Optional.empty();
    }

    public static boolean isInGame(Player player) {
        Optional<Gamer> optGamer = getGamer(player.getUniqueId());
        return optGamer.map(gamer -> gamer.getGame().isPresent()).orElse(false);
    }
}
