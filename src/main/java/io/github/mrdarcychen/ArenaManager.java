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
import io.github.mrdarcychen.arenas.Arena;
import io.github.mrdarcychen.arenas.SpawnPoint;
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

    private final List<Arena> arenas = new ArrayList<>();
    private final Path configDirectory;

    ArenaManager(Path configDirectory) {
        this.configDirectory = configDirectory;
        loadArenas();
    }

    // load all arenas from config files
    void loadArenas() {
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(configDirectory, "*.conf");
            for (Path file : stream) {
                ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader
                        .builder().setPath(file).build();
                ConfigurationNode rootNode = loader.load();
                parseArenaFrom(rootNode).ifPresent(arenas::add);
            }
        } catch (IOException e) {
            System.err.println("An I/O error occurred while loading arena configs.");
        }
    }

    private Optional<Arena> parseArenaFrom(ConfigurationNode rootNode) {
        TypeToken<SpawnPoint> spawnToken = TypeToken.of(SpawnPoint.class);
        String name = rootNode.getNode("name").getString();
        Arena.Builder builder = new Arena.Builder(name);
        Arena output = null;
        try {
            builder.setLobbySpawn(rootNode.getNode("lobbySpawn").getValue(spawnToken));
            builder.setSpectatorSpawn(rootNode.getNode("spectatorSpawn").getValue(spawnToken));
            for (ConfigurationNode node : rootNode.getNode("startPoints").getChildrenList()) {
                builder.addStartPoint(node.getValue(spawnToken));
            }
            output = builder.build();
        } catch (ObjectMappingException | IllegalStateException e) {
            System.err.println("Failed to load arena " + name + " : " + e.getMessage());
        }
        return Optional.ofNullable(output);
    }

    public Optional<Arena> findArena(String mode) {
        Predicate<Arena> criteria;
        switch (mode.toLowerCase()) {
            case "1v1":
            case "2v2":
                criteria = (it -> !it.isBusy());
                break;
            case "ffa":
                criteria = (it) -> !it.isBusy() && it.getStartPoints().count() > 2;
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

    public Optional<Arena> findArena() {
        return arenas.stream().filter(it -> !it.isBusy()).findAny();
    }

    // will overwrite existing arena if same name provided
    public void add(Arena arena) {
        Path file = Paths.get(configDirectory + File.separator + arena.getName() + ".conf");
        try {
            if (!file.toFile().exists()) {
                Files.createFile(file);
            }
        } catch (IOException e) {
            System.err.println("Error creating arena config file for " + arena.getName());
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
            System.err.println("Error writing arena config.");
        }
        arenas.add(arena);
    }

    public void remove(String name) {
        arenas.removeIf(it -> it.getName().equals(name));
        Path path = Paths.get(configDirectory + File.separator + name + ".conf");
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new IllegalArgumentException(name + " does not exist.");
        }
    }
}
