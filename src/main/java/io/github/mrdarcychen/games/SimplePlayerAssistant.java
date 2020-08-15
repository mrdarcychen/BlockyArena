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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimplePlayerAssistant implements PlayerAssistant {

    private List<PlayerSnapshot> snapshots;
    private Match match;

    public SimplePlayerAssistant(Match match) {
        snapshots = new ArrayList<>();
        this.match = match;
    }


    @Override
    public void recruit(Player player) {
        snapshots.add(new PlayerSnapshot(player));
        PlayerManager.setGame(player.getUniqueId(), match);
        maxFoodAndHealth(player);
        player.offer(Keys.GAME_MODE, GameModes.SURVIVAL);
        setSpectatorProperties(player, false);
        // send player to lobby
        Arena arena = match.getArena();
        player.sendMessage(Text.of("Sending you to " + match.getArena().getName() + " ..."));
        Transform<World> lobby = arena.getLobbySpawn().getTransform();
        player.setTransform(lobby);
    }

    @Override
    public void dismiss(Player player) {
        snapshots.stream().filter(it -> it.getPlayer().equals(player)).findAny()
                .ifPresent(PlayerSnapshot::restore);
        PlayerManager.clearGame(player.getUniqueId()); // TODO
        maxFoodAndHealth(player);
        setSpectatorProperties(player, false);
    }

    @Override
    public void eliminate(Player player) {
        maxFoodAndHealth(player);
        setSpectatorProperties(player, true);
        player.setTransform(match.getArena().getSpectatorSpawn().getTransform());
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
    public void dismissAll() {
        snapshots.forEach(PlayerSnapshot::restore);
        snapshots.stream()
                .map(snapshot -> snapshot.getPlayer().getUniqueId())
                .forEach(PlayerManager::clearGame);
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
}
