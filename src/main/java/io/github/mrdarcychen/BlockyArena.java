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

package io.github.mrdarcychen;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import io.github.mrdarcychen.arenas.SpawnPoint;
import io.github.mrdarcychen.arenas.SpawnSerializer;
import io.github.mrdarcychen.commands.*;
import io.github.mrdarcychen.listeners.ClientConnectionEventListener;
import io.github.mrdarcychen.listeners.EntityListener;
import io.github.mrdarcychen.utils.*;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;

/**
 * The entry point for the plugin, invoked by Sponge.
 */
@Plugin(id = "blockyarena", name = "BlockyArena", version = "0.7.0")
public final class BlockyArena {

    private static BlockyArena PLUGIN;

    @Inject
    private static Logger logger;
    private static ConfigManager configManager;
    private static ArenaDispatcher arenaDispatcher;
    private static KitDispatcher kitDispatcher;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDirectory;

    @Inject
    private BlockyArena() {
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static ArenaDispatcher getArenaDispatcher() {
        return arenaDispatcher;
    }

    public static KitDispatcher getKitDispatcher() {
        return kitDispatcher;
    }

    public static BlockyArena getInstance() {
        return PLUGIN;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        PLUGIN = this;
        registerTypeSerializers();
        registerListeners();
        configManager = new ConfigManager(configDirectory);
    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        arenaDispatcher = new ArenaDispatcher();
        kitDispatcher = new KitDispatcher();
        ChallengeService challengeService = new ChallengeService();
        CommandSpec rootCmd = CommandSpec.builder()
                .child(CmdEdit.SPEC, "edit")
                .child(CmdCreate.SPEC, "create")
                .child(CmdRemove.SPEC, "remove")
                .child(CmdJoin.SPEC, "join")
                .child(CmdKit.SPEC, "kit")
                .child(CmdQuit.SPEC, "quit")
                .child(challengeService.getCommandCallable(), "challenge")
                .build();

        PlatformRegistry.registerCommands(rootCmd);
    }

    /*
    registers event listeners to EventManager
     */
    private void registerListeners() {
        Sponge.getEventManager().registerListeners(this,
                new EntityListener());
        Sponge.getEventManager().registerListeners(this,
                new ClientConnectionEventListener());
    }

    private void registerTypeSerializers() {
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(SpawnPoint.class), new SpawnSerializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Kit.class), new KitSerializer());
    }
}
