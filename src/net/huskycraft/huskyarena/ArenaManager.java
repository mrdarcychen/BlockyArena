package net.huskycraft.huskyarena;

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

    public ArrayList<GameSession> sessions;

    public HashMap<UUID, Arena> arenaCreators;

    public ArenaManager(HuskyArena plugin) {

        this.plugin = plugin;
        arenaFiles = new HashMap<>();
        arenaCreators = new HashMap<>();
        loadedArenas = new ArrayList<>();
        sessions = new ArrayList<>();
        registerArenas();
    }

    public GameSession getAvailableSession() {
        if (sessions.size() != 0) {
            for (GameSession session : sessions) {
                if (session.status == false) {
                    return session;
                }
            }
        }

        Arena arena = getAvailableArena();
        GameSession session = new GameSession(plugin, arena);
        sessions.add(session);
        return session;
    }
    public void initiateSession() {
        Arena arena = getAvailableArena();
        GameSession session = getAvailableSession();
    }

    public void registerArenas() {
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(plugin.getArenaDir(), "*.conf");
            for (Path path : stream) {
                arenaFiles.put(path, false);
            }
        } catch (IOException e) {
            plugin.getLogger().warn("Error loading existing arena configs.");
        }
    }

    private Arena getAvailableArena() {
        //loops through all loaded arenas
        if (loadedArenas.size() != 0) {
            for (Arena arena : loadedArenas) {
                if (arena.getStatus() == false) {
                    return arena;
                }
            }
        }

        //if no arena is loaded or all loaded arenas are in use, loads a new arena
        for (Path path : arenaFiles.keySet()) {
            if (arenaFiles.get(path) == false) {
                Arena arena = new Arena(plugin, path);
                arenaFiles.replace(path, true);
                loadedArenas.add(arena);
                return arena;
            }
        }
        return null;
    }
}