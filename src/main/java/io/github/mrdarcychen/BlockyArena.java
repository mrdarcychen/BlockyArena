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
import io.github.mrdarcychen.commands.CommandManager;
import io.github.mrdarcychen.listeners.ClientConnectionEventListener;
import io.github.mrdarcychen.listeners.EntityListener;
import io.github.mrdarcychen.listeners.ServerListener;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

// entry point of the plugin; responsible for initializing the rest of the components
// nothing should depend on this class
@Plugin(id = "blockyarena", name = "BlockyArena")
public final class BlockyArena {

    private static BlockyArena PLUGIN;
    private static ArenaManager arenaManager;

    @Inject
    private static Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    //The path to config/BlockyArena
    private Path configDirectory;

    private Path arenaDirectory;


    @Inject
    @DefaultConfig(sharedRoot = false)
    // The path to the default.conf file
    private Path defaultConfig;

    @Inject
    private BlockyArena() {
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        PLUGIN = this;
        registerTypeSerializers();
        registerListeners();
        createDirectories();
    }

    @Listener
    public void onServerStarting(GameStartingServerEvent event) {
        CommandManager.init();
        ConfigManager.getInstance().load(defaultConfig);
        arenaManager = new ArenaManager(arenaDirectory);
    }

    /*
    creates directories for arenas and classes if they do not exist
    pre: plugin config directory exists (throws IOException if not)
     */
    private void createDirectories() {
        arenaDirectory = Paths.get(configDirectory + "/arenas");
        if (!arenaDirectory.toFile().exists()) {
            try {
                Files.createDirectory(arenaDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    registers event listeners to EventManager
     */
    private void registerListeners() {
        Sponge.getEventManager().registerListeners(this,
                new EntityListener());
        Sponge.getEventManager().registerListeners(this,
                new ClientConnectionEventListener());
        Sponge.getEventManager().registerListeners(this,
                new ServerListener());
    }

    /**
     * Registers all custom TypeSerializers.
     */
    private void registerTypeSerializers() {
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(SpawnPoint.class), new SpawnSerializer());
    }

    public Path getDefaultConfig() {
        return defaultConfig;
    }

    public static ArenaManager getArenaManager() {
        return arenaManager;
    }

    public static BlockyArena getInstance() {
        return PLUGIN;
    }
}
