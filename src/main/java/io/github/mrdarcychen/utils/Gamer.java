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

package io.github.mrdarcychen.utils;

import io.github.mrdarcychen.games.Game;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.UUID;

/**
 * A Gamer object stores a player's gaming profile.
 */
public class Gamer {

    private final UUID uniqueId;
    private boolean isOnline;
    private String name;
    private Player player; // the player instance in which this Gamer profile is associated with
    private Game game; // the Game session this player is currently in

    /**
     * Constructs a unique Gamer profile for the given Player.
     *
     * @param uniqueId the {@link UUID} of the associated {@link Player}
     */
    public Gamer(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * Gets the Game this Gamer is currently in.
     *
     * @return the Game this Gamer is currently in
     */
    public Optional<Game> getGame() {
        return Optional.ofNullable(game);
    }

    public PlayerSnapshot takeSnapshot() {
        return new PlayerSnapshot(player);
    }

    /**
     * Toggles custom spectator mode on the player.
     *
     * @param i true to enable spectator mode, false to disable
     */
    public void spectate(boolean i) {
        player.offer(Keys.HEALTH, player.health().getMaxValue());
        player.offer(Keys.FOOD_LEVEL, player.foodLevel().getMaxValue());
        player.offer(Keys.VANISH, i);
        player.offer(Keys.VANISH_IGNORES_COLLISION, i);
        player.offer(Keys.CAN_FLY, i);
        player.offer(Keys.IS_FLYING, i);
        player.offer(Keys.INVULNERABLE, i);
    }

    /**
     * Gets the {@link UUID} of this Gamer.
     *
     * @return the {@link UUID} of this Gamer
     */
    public UUID getUniqueId() {
        return uniqueId;
    }

    /**
     * Sets the client connection status of this Gamer.
     *
     * @param isOnline the client connection status of this Gamer
     */
    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    /**
     * Gets the client connection status of this Gamer
     *
     * @return true if this Gamer is online, false otherwise
     */
    public boolean isOnline() {
        return isOnline;
    }

    /**
     * Sets the name of this Gamer.
     *
     * @param name the name of this Gamer
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the name of this Gamer.
     *
     * @return the name of this Gamer
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the {@link Player} instance associated with this Gamer.
     *
     * @param player the {@link Player} instance to be associated with this Gamer
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Gets the {@link Player} instance associated with this Gamer
     *
     * @return {@link Player} or Optional.empty() if not found
     */
    public Player getPlayer() {
        return player;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
