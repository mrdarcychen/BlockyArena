package net.huskycraft.blockyarena.managers;

import net.huskycraft.blockyarena.Arena;
import net.huskycraft.blockyarena.ArenaState;
import net.huskycraft.blockyarena.BlockyArena;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * An ArenaManager keeps track of the available Arenas in the server.
 */
public class ArenaManager {

    public static BlockyArena plugin;

    private Set<Arena> arenas; // the set of Arenas available in the server

    public ArenaManager() {
        arenas = new HashSet<>();
        loadArenas();
    }

    /**
     * Reconstructs arenas from all arena config files in standard format.
     */
    private void loadArenas() {
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(plugin.getArenaDir(), "*.conf");
            for (Path path : stream) {
                arenas.add(new Arena(path));
            }
        } catch (IOException e) {
            plugin.getLogger().warn("Error loading existing arena configs.");
        }
    }

    /*
    returns a registered, loaded, and unpaired arena.
    returns null if no arena presents
     */
//    public Arena getAvailableArena() {
//        //loops through all loaded arenas, returns the first unpaired arena
//        if (loadedArenas.size() != 0) {
//            for (Arena arena : loadedArenas) {
//                if (!arena.getStatus()) {
//                    return arena;
//                }
//            }
//        }
//
//        //if no arena is loaded or all loaded arenas are paired, loads and returns a new arena
//        for (Path path : arenaFiles.keySet()) {
//            if (!arenaFiles.get(path)) {
//                Arena arena = new Arena(plugin, path);
//                arenaFiles.replace(path, true);
//                loadedArenas.add(arena);
//                return arena;
//            }
//        }
//
//        registerArenas();
//        return null;
//    }

    /**
     * Gets an available arena.
     *
     * @return null if no arena is available
     */
    public Arena getAvailableArena() {
        for (Arena arena : arenas) {
            // TODO: need to verfiy
            if (!arena.getState().equals(ArenaState.ENABLE)) {
                return arena;
            }
        }
        return null;
    }

    /**
     * Marks the given arena as a work in progress and links its creator.
     * @param creator the player created the arena
     * @param arena the arena that is still work-in-progress
     */
    public void addPendingArena(Player creator, Arena arena) {
        pendingArenas.put(creator, arena);
    }

    /**
     * Gets the work-in-progress arena created by the given creator.
     * @param creator a creator who has a work-in-progress arena
     * @return null if the given player has no work-in-progress arena
     */
    public Arena getPendingArena(Player creator) {
        return pendingArenas.get(creator);
    }

    /**
     * Sets the creator's arena as completed and removes it from the pending list.
     * @param creator a creator who has a work-in-progress arena
     * @return null if the given payer has no work-in-progress arena
     */
    public void removePendingArena(Player creator) {
        pendingArenas.remove(creator);
    }

    /**
     * Adds the given Arena to the tracking list.
     * @param arena an Arena with any given state
     */
    public void add(Arena arena) {
        arenas.add(arena);
    }
}