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

package net.huskycraft.blockyarena.arenas;

import com.google.common.reflect.TypeToken;
import net.huskycraft.blockyarena.BlockyArena;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * An ArenaManager keeps track of the available Arenas in the server.
 */
public class ArenaManager {

	private static ArenaManager INSTANCE;
	
    private List<Arena> arenas; // the list of Arenas available in the server
    private Path configDir;


    public ArenaManager() {
        arenas = new ArrayList<>();
        configDir = BlockyArena.getInstance().getArenaDir();
        loadArenas();
    }

	public static ArenaManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ArenaManager();
        }
		return INSTANCE;
	}

    /**
     * Reconstructs arenas from all arena config files in standard format.
     */
    public void loadArenas() {
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(
                    BlockyArena.getInstance().getArenaDir(), "*.conf");
            for (Path file : stream) {
                ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader
                        .builder().setPath(file).build();
                try {
                    ConfigurationNode rootNode = loader.load();
                    String name = rootNode.getNode("name").getString();
                    Arena.Builder builder = new Arena.Builder(name);
                    builder.setLobbySpawn(rootNode.getNode("lobbySpawn")
                            .getValue(TypeToken.of(SpawnPoint.class)));
                    builder.setSpectatorSpawn(rootNode.getNode("spectatorSpawn")
                            .getValue(TypeToken.of(SpawnPoint.class)));
                    for (ConfigurationNode node : rootNode.getNode("startPoints").getChildrenList()) {
                        builder.addStartPoint(node.getValue(TypeToken.of(SpawnPoint.class)));
                    }
                    loader.save(rootNode);
                    Arena arena = builder.build();
                    arenas.add(arena);
                } catch (ObjectMappingException e) {
                    BlockyArena.getInstance().getLogger().warn("Error reading arena config.");
                }
            }
        } catch (IOException e) {
        	BlockyArena.getInstance().getLogger().warn("Error loading existing arena configs.");
        }
    }

    public Optional<Arena> findArena() {
        return arenas.stream().filter(it -> !it.isBusy()).findAny();
    }
    
    public Optional<Arena> findArena(String mode) {
        Predicate<Arena> criteria;
        switch (mode.toLowerCase()) {
            case "1v1": case "2v2": criteria = (it -> !it.isBusy()); break;
            case "ffa": criteria = (it) -> !it.isBusy() && it.getStartPoints().count() > 2;
                break;
            default:
                criteria = (it) -> false;
        }
        return arenas.stream()
                .filter(criteria)
                .findAny();
    }

    public Optional<Arena> getArena(String name) {
        return arenas.stream().filter(it -> it.getName().equals(name)).findFirst();
    }

    // will overwrite existing arena if same name provided
    public void add(Arena arena) {
        Path file = Paths.get(configDir.toString() + File.separator + arena.getName() + ".conf");
        try {
            if (!file.toFile().exists()) {
                Files.createFile(file);
            }
        } catch (IOException e) {
            BlockyArena.getInstance().getLogger()
                    .warn("Error creating arena config file for " + arena.getName());
            return;
        }

        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader
                .builder().setPath(file).build();

        try {
            ConfigurationNode rootNode = loader.load();
            rootNode.getNode("name").setValue(arena.getName());
            List<SpawnPoint> startPoints = arena.getStartPoints().collect(Collectors.toList());
            for (int i = 0; i < startPoints.size(); i++) {
                rootNode.getNode("startPoints").getNode(i).setValue(TypeToken.of(SpawnPoint.class), startPoints.get(i));
            }
            rootNode.getNode("lobbySpawn").setValue(TypeToken.of(SpawnPoint.class), arena.getLobbySpawn());
            rootNode.getNode("spectatorSpawn").setValue(TypeToken.of(SpawnPoint.class), arena.getSpectatorSpawn());
            loader.save(rootNode);
        } catch (IOException | ObjectMappingException e) {
            BlockyArena.getInstance().getLogger().warn("Error writing arena config.");
        }
        arenas.add(arena);
    }

    public void remove(String name) {
        arenas.removeIf(it -> it.getName().equals(name));
        Path path = Paths.get(configDir.toString() + File.separator + name + ".conf");
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new IllegalArgumentException(name + " does not exist.");
        }
    }
}
