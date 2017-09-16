package net.huskycraft.huskyarena.managers;

import net.huskycraft.huskyarena.Arena;
import net.huskycraft.huskyarena.HuskyArena;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ArenaManager {

    private HuskyArena plugin;

    public HashMap<Path, Boolean> arenaFiles;   //false represents file not loaded

    public ArrayList<Arena> loadedArenas;   //arenas loaded from the config files in arenaConfig directory

    public HashMap<UUID, Arena> arenaCreators;

    public ArenaManager(HuskyArena plugin) {
        this.plugin = plugin;
        arenaFiles = new HashMap<>();
        arenaCreators = new HashMap<>();
        loadedArenas = new ArrayList<>();
        registerArenas();
    }

    private void registerArenas() {
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(plugin.getArenaDir(), "*.conf");
            for (Path path : stream) {
                if (!arenaFiles.containsKey(path)) {
                    arenaFiles.put(path, false);
                }
            }
        } catch (IOException e) {
            plugin.getLogger().warn("Error loading existing arena configs.");
        }
    }

    /*
    returns a registered, loaded, and unpaired arena.
    returns null if no arena presents
     */

    public Arena getAvailableArena() {
        //loops through all loaded arenas, returns the first unpaired arena
        if (loadedArenas.size() != 0) {
            for (Arena arena : loadedArenas) {
                if (!arena.getStatus()) {
                    return arena;
                }
            }
        }

        //if no arena is loaded or all loaded arenas are paired, loads and returns a new arena
        for (Path path : arenaFiles.keySet()) {
            if (!arenaFiles.get(path)) {
                Arena arena = new Arena(plugin, path);
                arenaFiles.replace(path, true);
                loadedArenas.add(arena);
                return arena;
            }
        }

        registerArenas();
        return null;
    }
}