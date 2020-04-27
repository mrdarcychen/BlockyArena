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

package net.huskycraft.blockyarena;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import net.huskycraft.blockyarena.arenas.ArenaManager;
import net.huskycraft.blockyarena.arenas.Spawn;
import net.huskycraft.blockyarena.arenas.SpawnSerializer;
import net.huskycraft.blockyarena.commands.CmdCreate;
import net.huskycraft.blockyarena.commands.CmdEdit;
import net.huskycraft.blockyarena.commands.CmdJoin;
import net.huskycraft.blockyarena.commands.CmdKit;
import net.huskycraft.blockyarena.commands.CmdQuit;
import net.huskycraft.blockyarena.commands.CmdRemove;
import net.huskycraft.blockyarena.games.GameManager;
import net.huskycraft.blockyarena.listeners.ClientConnectionEventListener;
import net.huskycraft.blockyarena.listeners.EntityListener;
import net.huskycraft.blockyarena.managers.ConfigManager;
import net.huskycraft.blockyarena.utils.Kit;
import net.huskycraft.blockyarena.utils.KitManager;
import net.huskycraft.blockyarena.utils.KitSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

@Plugin(id = "blockyarena", name = "BlockyArena")
public final class BlockyArena {

	private static BlockyArena PLUGIN;
	
	private static ArenaManager arenaManager;

	private static GameManager gameManager;

	private static KitManager kitManager;

	/*
	 * Handle all of configuration !
	 */
	private ConfigManager confManager;

	@Inject
	private Logger logger;

	@Inject
	@DefaultConfig(sharedRoot = false)
	//The path to the default.conf file
	private Path defaultConfig;

	@Inject
	@ConfigDir(sharedRoot = false)
	//The default path to this plugin dir : /config/blockyarena
	private Path configDir;

	private Path arenaDir;

	private Path kitDir;

	@Inject
	private BlockyArena() {
	}

	@Listener
	public void onPreInit(GamePreInitializationEvent event) {

		PLUGIN = this;

		registerTypeSerializers();
		registerCommands();
		registerListeners();
		createDirectories();

		createConfigManager();

		getLogger().warn("PreInit finished ! !");
	}

	@Listener
	public void onServerStarting(GameStartingServerEvent event) {
		createManagers();
		getLogger().warn("Started !");
	}

	/*
	 * creates managers for the plugin Maybe using Singleton Instance for others ??
	 */
	private void createManagers() {
		arenaManager = new ArenaManager();
		gameManager = new GameManager();
		kitManager = new KitManager();
	}

	// Create The Config Manager, and load config if they exist
	private void createConfigManager() {
		confManager = ConfigManager.getInstance();
		confManager.load();

	}

	/*
	 * creates directories for arenas and classes if they do not exist pre: plugin
	 * config directory exists (throws IOException if not)
	 */
	private void createDirectories() {
		arenaDir = Paths.get(getConfigDir().toString() + "/arenas");
		kitDir = Paths.get(getConfigDir().toString() + "/kits");
		// The path to the "default.conf" configuration
		defaultConfig = Paths.get(getConfigDir().toString(), "default.conf");

		List<Path> directories = Arrays.asList(arenaDir, kitDir);
		for (Path dir : directories) {
			try {
				if (!dir.toFile().exists()) {
					Files.createDirectory(dir);
				}
			} catch (IOException e) {
				logger.warn("Error creating directory for " + dir.getFileName().toString());
			}
		}
	}

	/*
	 * registers event listeners to EventManager
	 */
	private void registerListeners() {
		Sponge.getEventManager().registerListeners(this, new EntityListener());
		Sponge.getEventManager().registerListeners(this, new ClientConnectionEventListener());
	}

	/*
	 * registers user commands to CommandManager
	 */
	private void registerCommands() {
		CommandSpec cmdCreate = CommandSpec.builder()
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("type"))),
						GenericArguments.onlyOne(GenericArguments.string(Text.of("id"))))
				.executor(CmdCreate.getInstance()).permission("blockyarena.create").build();

		CommandSpec cmdRemove = CommandSpec.builder()
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("type"))),
						GenericArguments.onlyOne(GenericArguments.string(Text.of("id"))))
				.executor(CmdRemove.getInstance()).permission("blockyarena.remove").build();

		CommandSpec cmdJoin = CommandSpec.builder()
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("mode"))))
				.executor(CmdJoin.getInstance()).build();

		CommandSpec cmdQuit = CommandSpec.builder().executor(CmdQuit.getInstance()).build();

		CommandSpec cmdEdit = CommandSpec.builder()
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("id"))),
						GenericArguments.onlyOne(GenericArguments.string(Text.of("type"))),
						GenericArguments.onlyOne(GenericArguments.string(Text.of("param"))))
				.executor(CmdEdit.getInstance()).permission("blockyarena.edit").build();

		CommandSpec cmdKit = CommandSpec.builder()
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("id"))))
				.executor(CmdKit.getInstance()).build();

		CommandSpec arenaCommandSpec = CommandSpec.builder().child(cmdEdit, "edit").child(cmdCreate, "create")
				.child(cmdRemove, "remove").child(cmdJoin, "join").child(cmdQuit, "quit").child(cmdKit, "kit").build();

		Sponge.getCommandManager().register(BlockyArena.getInstance(), arenaCommandSpec, "blockyarena", "arena", "ba");
	}

	/**
	 * Registers all custom TypeSerializers.
	 */
	private void registerTypeSerializers() {
		TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Spawn.class), new SpawnSerializer(this));
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

	public static ArenaManager getArenaManager() {
		return arenaManager;
	}

	public static GameManager getGameManager() {
		return gameManager;
	}

	public static KitManager getKitManager() {
		return kitManager;
	}

	public static BlockyArena getInstance() {
		return PLUGIN;
	}

}