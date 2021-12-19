/*
 * Copyright 2021 Darcy Chen <mrdarcychen@gmail.com> and the contributors
 * SPDX-License-Identifier: Apache-2.0
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
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;

/**
 * The entry point for the plugin, invoked by Sponge.
 */
@Plugin(
        id = "blockyarena",
        name = "BlockyArena",
        version = "0.7.0",
        description = "A deathmatch plugin for Minecraft servers"
)
public final class BlockyArena {

    private static BlockyArena PLUGIN;

    @Inject
    private static Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDirectory;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path defaultConfig;

    @Inject
    public BlockyArena() {
    }

    public static BlockyArena getInstance() {
        return PLUGIN;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        PLUGIN = this;
        registerTypeSerializers();
        registerListeners();
        ServiceProvider.configManager = new ConfigManager(configDirectory, defaultConfig);
    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        ServiceProvider.arenaDispatcher = new ArenaDispatcher();
        ServiceProvider.kitDispatcher = new KitDispatcher();
        ChallengeService challengeService = new ChallengeService();
        CommandSpec rootCmd = CommandSpec.builder()
                .child(CmdEdit.SPEC, "edit")
                .child(CmdCreate.SPEC, "create")
                .child(CmdRemove.SPEC, "remove")
                .child(CmdJoin.SPEC, "join")
                .child(CmdKit.SPEC, "kit")
                .child(CmdQuit.SPEC, "quit")
                .child(CmdChallenge.SPEC, "challenge")
                .build();

        Sponge.getCommandManager().register(getInstance(), rootCmd, "blockyarena", "ba");
    }

    /*
    registers event listeners to EventManager
     */
    private void registerListeners() {
        Sponge.getEventManager().registerListeners(this, new EntityListener());
        Sponge.getEventManager().registerListeners(this, new ClientConnectionEventListener());
    }

    private void registerTypeSerializers() {
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(SpawnPoint.class), new SpawnSerializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Kit.class), new KitSerializer());
    }
}
