package net.huskycraft.blockyarena;

import com.google.inject.Inject;
import net.huskycraft.blockyarena.commands.*;
import net.huskycraft.blockyarena.listeners.EntityListener;
import net.huskycraft.blockyarena.managers.ArenaManager;
import net.huskycraft.blockyarena.managers.PlayerClassManager;
import net.huskycraft.blockyarena.managers.SessionManager;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Plugin(id = "blockyarena", name = "BlockyArena", version = "0.3.0")
public class BlockyArena {
    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    private Path arenaDir, classDir;

    private ArenaManager arenaManager;
    private SessionManager sessionManager;
    private PlayerClassManager playerClassManager;

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        createDirectories();
        createManagers();
        registerCommands();
        registerListeners();
    }

    /*
    creates managers for the plugin
     */
    private void createManagers() {
        arenaManager = new ArenaManager(this);
        sessionManager = new SessionManager(this);
        playerClassManager = new PlayerClassManager(this);
    }

    /*
    creates directories for arenas and classes if they do not exist
    pre: plugin config directory exists (throws IOException if not)
     */
    private void createDirectories() {
        arenaDir = Paths.get(getConfigDir().toString() + "/arenas");
        classDir = Paths.get(getConfigDir().toString() + "/classes");

        List<Path> directories = Arrays.asList(arenaDir, classDir);
        for (Path dir : directories) {
            try {
                if (!dir.toFile().exists()) {
                    Files.createDirectory(dir);
                }
            } catch (IOException e) {
                logger.warn("Error creating directory for " + dir.getFileName().toString());
            }
        }
    }

    /*
    registers event listeners to EventManager
     */
    private void registerListeners() {
        Sponge.getEventManager().registerListeners(this, new EntityListener(this));
    }

    /*
    registers user commands to CommandManager
     */
    private void registerCommands() {
        CommandSpec createCmd = CommandSpec.builder()
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("object"))),
                        GenericArguments.remainingJoinedStrings(Text.of("name")))
                .executor(new CreateCmd(this))
                .permission("blockyarena.create")
                .build();

        CommandSpec getClassCmd = CommandSpec.builder()
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("name"))))
                .executor(new GetClassCmd(this))
                .permission("blockyarena.getclass")
                .build();

        CommandSpec setSpawnCmd = CommandSpec.builder()
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("type"))))
                .executor(new SetSpawnCmd(this))
                .permission("blockyarena.setspawn")
                .build();

        CommandSpec doneCmd = CommandSpec.builder()
                .executor(new DoneCmd(this))
                .permission("blockyarena.done")
                .build();

        CommandSpec joinCmd = CommandSpec.builder()
                .executor(new JoinCmd(this))
                .build();

        CommandSpec quitCmd = CommandSpec.builder()
                .executor(new QuitCmd(this))
                .build();

        CommandSpec arenaCommandSpec = CommandSpec.builder()
                .child(createCmd, "create")
                .child(setSpawnCmd, "setspawn")
                .child(doneCmd, "done")
                .child(joinCmd, "join")
                .child(quitCmd, "quit")
                .child(getClassCmd, "getclass")
                .build();

        Sponge.getCommandManager()
                .register(this, arenaCommandSpec, "blockyarena", "arena");
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDefaultConfig() {
        return defaultConfig;
    }

    public Path getConfigDir() {
        return configDir;
    }

    public Path getArenaDir() {
        return arenaDir;
    }

    public Path getClassDir() {
        return classDir;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public PlayerClassManager getPlayerClassManager() {
        return playerClassManager;
    }
}