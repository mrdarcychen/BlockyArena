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

    public ArrayList<Path> arenaFiles;
    public HashMap<UUID, Arena> arenaCreators;

    public ArenaManager(HuskyArena plugin) {

        this.plugin = plugin;
        arenaFiles = new ArrayList<>();
        arenaCreators = new HashMap<>();
        registerArenas();
    }

    private void registerArenas() {
        arenaFiles = new ArrayList<>();

        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(plugin.getArenaDir(), "*.conf");
            for (Path path : stream) {
                arenaFiles.add(path);
            }
        } catch (IOException e) {
            plugin.getLogger().warn("Error loading existing arena configs.");
        }
    }
}
