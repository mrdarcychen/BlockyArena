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

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import net.huskycraft.blockyarena.BlockyArena;

/**
 * An ArenaManager keeps track of the available Arenas in the server.
 */
public class ArenaManager {

    private Map<String, Arena> arenas; // the list of Arenas available in the server

    public ArenaManager() {
        arenas = new HashMap<>();
        loadArenas();
    }

    /**
     * Reconstructs arenas from all arena config files in standard format.
     */
    private void loadArenas() {
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(BlockyArena.getInstance().getArenaDir(), "*.conf");
            for (Path path : stream) {
                Arena arena = new Arena(path);
                arenas.put(arena.getID(), arena);
            }
        } catch (IOException e) {
        	BlockyArena.getInstance().getLogger().warn("Error loading existing arena configs.");
        }
    }

    /**
     * Gets an available arena from the list.
     * @return an enabled arena from the list, null if no arena is available
     */
    public Arena getArena() {
        for (Arena arena : arenas.values()) {
            if (arena.getState() == ArenaState.AVAILABLE) {
                return arena;
            }
        }
        return null;
    }

    /**
     * Gets the Arena with the given id.
     * @param id the identifier of the Arena
     * @return the Arena with the given id
     */
    public Arena getArena(String id) {
        return arenas.get(id);
    }

    /**
     * Adds the given Arena to the tracking list.
     * @param arena an Arena with any given state
     */
    public void add(Arena arena) {
        arenas.put(arena.getID(), arena);
    }

    /**
     * Removes the {@link Arena} and its config file if exists.
     *
     * @param id the id of the Arena
     */
    public void remove(String id) {
        arenas.remove(id);
        Path path = Paths.get(BlockyArena.getInstance().getArenaDir().toString() + File.separator + id + ".conf");
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new IllegalArgumentException(id + " does not exist.");
        }
    }
}