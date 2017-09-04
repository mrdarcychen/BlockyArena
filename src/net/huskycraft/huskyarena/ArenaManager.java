package net.huskycraft.huskyarena;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class ArenaManager {

    private HuskyArena plugin;

    public ArrayList<Path> arenaFiles;

    public ArenaManager(HuskyArena plugin) {

        this.plugin = plugin;

        plugin.getLogger().info("works properly");
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
