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
import io.github.mrdarcychen.arenas.ArenaManager;
import io.github.mrdarcychen.arenas.SpawnPoint;
import io.github.mrdarcychen.arenas.SpawnSerializer;
import io.github.mrdarcychen.listeners.ClientConnectionEventListener;
import io.github.mrdarcychen.listeners.EntityListener;
import io.github.mrdarcychen.listeners.ServerListener;
import io.github.mrdarcychen.managers.CommandManager;
import io.github.mrdarcychen.managers.ConfigManager;
import io.github.mrdarcychen.utils.Kit;
import io.github.mrdarcychen.utils.KitManager;
import io.github.mrdarcychen.utils.KitSerializer;
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

@Plugin(id = "blockyarena", name = "BlockyArena")
public final class BlockyArena {

	private static BlockyArena PLUGIN;

	@Inject
	private Logger logger;

	@Inject
	@ConfigDir(sharedRoot = false)
	//The path to config/BlockyArena
	private Path configDir;

	private Path arenaDir, kitDir;


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
       
    	CommandManager.getInstance().registerCommands();
        ConfigManager.getInstance().load();
        ArenaManager am = ArenaManager.getInstance();
        am.loadArenas();
        KitManager.getInstance().loadKits();
    }

    /*
    creates directories for arenas and classes if they do not exist
    pre: plugin config directory exists (throws IOException if not)
     */
    private void createDirectories() {
        arenaDir = Paths.get(getConfigDir().toString() + "/arenas");
        kitDir = Paths.get(getConfigDir().toString() + "/kits");

        List<Path> directories = Arrays.asList(arenaDir, kitDir);
        for (Path dir : directories) {
            try {
                if (!dir.toFile().exists()) {
                    Files.createDirectory(dir);
                }
            } catch (IOException e) {
                logger.warn("Error creating directory for "
                        + dir.getFileName().toString());
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
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(SpawnPoint.class), new SpawnSerializer(this));
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Kit.class), new KitSerializer(this));
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDefaultConfig() {
        return defaultConfig;
    }

    public Path getConfigDir() {
        return configDir;
    }
    
	public Path getArenaDir() {
		return arenaDir;
	}

	public Path getKitDir() {
		return kitDir;
	}



	public static BlockyArena getInstance() {
		return PLUGIN;
	}
}
