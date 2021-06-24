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
import io.github.mrdarcychen.utils.PlayerSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SimplePlayerAssistant implements PlayerAssistant {

    private final Map<Player, PlayerSnapshot> snapshots;
    private final GameSession gameSession;
    private static final Function<String, Text> SENDING_TO_ARENA = (arena) -> Text
            .builder("Sending you to arena ")
            .append(Text.builder(arena + "...").color(TextColors.GRAY).build())
            .color(TextColors.GRAY).build();

    public SimplePlayerAssistant(GameSession gameSession) {
        snapshots = new HashMap<>();
        this.gameSession = gameSession;
    }

    @Override
    public void recruit(Player player) {
        snapshots.put(player, new PlayerSnapshot(player));
        player.getInventory().clear();
        PlayerManager.setGame(player.getUniqueId(), gameSession);
        maxFoodAndHealth(player);
        player.offer(Keys.GAME_MODE, GameModes.SURVIVAL);
        setSpectatorProperties(player, false);
        moveToLobby(player);
    }

    private void moveToLobby(Player player) {
        Arena arena = gameSession.getArena();
        player.sendMessage(SENDING_TO_ARENA.apply(arena.getName()));
        Transform<World> lobby = arena.getLobbySpawn().getTransform();
        player.setTransform(lobby);
    }

    @Override
    public void eliminate(Player player) {
        maxFoodAndHealth(player);
        setSpectatorProperties(player, true);
        player.setTransform(gameSession.getArena().getSpectatorSpawn().getTransform());
        showEliminateScreen(player);
    }

    private void showEliminateScreen(Player player) {
        Text deathText = Text.builder("YOU DIED!")
                .color(TextColors.RED).build();
        Title deathTitle = Title.builder()
                .title(deathText).fadeOut(2).stay(16).build();
        player.sendTitle(deathTitle);
    }

    @Override
    public void dismiss(Player player) {
        snapshots.get(player).restore(player);
        setLeaveGamePropertiesFor(player);
    }

    @Override
    public void dismissAll() {
        snapshots.forEach((player, playerSnapshot) -> {
            playerSnapshot.restore(player);
            setLeaveGamePropertiesFor(player);
        });
    }

    private void setLeaveGamePropertiesFor(Player player) {
        PlayerManager.clearGame(player.getUniqueId());
        maxFoodAndHealth(player);
        setSpectatorProperties(player, false);
    }

    @Override
    public void setSpectatorProperties(Player player, boolean status) {
        player.offer(Keys.VANISH, status);
        player.offer(Keys.VANISH_IGNORES_COLLISION, status);
        player.offer(Keys.CAN_FLY, status);
        player.offer(Keys.IS_FLYING, status);
        player.offer(Keys.INVULNERABLE, status);
    }

    @Override
    public void maxFoodAndHealth(Player player) {
        player.offer(Keys.HEALTH, player.health().getMaxValue());
        player.offer(Keys.FOOD_LEVEL, player.foodLevel().getMaxValue());
    }

    @Override
    public boolean contains(Player player) {
        return snapshots.containsKey(player);
    }
}
