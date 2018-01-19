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

    /**
     * Gets an available arena.
     * @return null if no arena is available
     */
    public Arena getAvailableArena() {
        for (Arena arena : arenas) {
            if (arena.getState() == ArenaState.ENABLE) {
                return arena;
            }
        }
        return null;
    }

    /**
     * Adds the given Arena to the tracking list.
     * @param arena an Arena with any given state
     */
    public void add(Arena arena) {
        arenas.add(arena);
    }
}