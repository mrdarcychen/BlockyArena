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

package net.huskycraft.blockyarena.utils;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.world.World;

public class PlayerSnapshot {

    private final Player player;
    private final Transform<World> transform;
    private final Kit kit;
    private final GameMode gameMode;
    private final double health;
    private final int food;

    public PlayerSnapshot(Player player) {
        this.player = player;
        this.transform = player.getTransform();
        kit = new Kit(player, null);
        gameMode = player.gameMode().get();
        System.out.println(gameMode);
        health = player.health().get();
        food = player.foodLevel().get();
    }

    public void restore() {
        player.setTransform(transform);
        kit.equip(player);
        System.out.println(player.getName() + " currently " + player.gameMode().get());
        player.gameMode().set(gameMode);
        player.offer(Keys.GAME_MODE, gameMode);
        System.out.println(gameMode + " -> " + player.gameMode().get());
        player.health().set(health);
        player.foodLevel().set(food);
    }

    public Player getPlayer() {
        return player;
    }
}
