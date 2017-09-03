package net.huskycraft.huskyarena;

import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Plugin(id = "huskyarena", name = "HuskyArena")
public class HuskyArena {

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;

    public Path getConfigDir() {
        return configDir;
    }

    private Path arenaDir;

    public Path getArenaDir() {
        return arenaDir;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        createArenaDir();
    }

    private void createArenaDir() {
        arenaDir = Paths.get(getConfigDir().toString() + "/arenas");
        try {
            if (!arenaDir.toFile().exists()) {
                Files.createDirectory(arenaDir);
            }
        } catch (IOException e) {
            logger.warn("Error creating arenas directory");
        }
    }
}
