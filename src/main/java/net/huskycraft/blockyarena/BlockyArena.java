package net.huskycraft.blockyarena;

import com.google.inject.Inject;
import net.huskycraft.blockyarena.commands.*;
import net.huskycraft.blockyarena.listeners.EntityListener;
import net.huskycraft.blockyarena.managers.ArenaManager;
import net.huskycraft.blockyarena.managers.GameManager;
import net.huskycraft.blockyarena.managers.GamerManager;
import net.huskycraft.blockyarena.managers.PlayerClassManager;
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
    private GameManager gameManager;
    private PlayerClassManager playerClassManager;
    private GamerManager gamerManager;

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
        arenaManager = new ArenaManager();
        gameManager = new GameManager();
        playerClassManager = new PlayerClassManager(this);
        gamerManager = new GamerManager(this);
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
        CommandSpec cmdCreate = CommandSpec.builder()
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("id"))))
                .executor(new CmdCreate())
                .permission("blockyarena.create")
                .build();

//        CommandSpec cmdGetClass = CommandSpec.builder()
//                .arguments(
//                        GenericArguments.onlyOne(GenericArguments.string(Text.of("name"))))
//                .executor(new GetClassCmd(this))
//                .permission("blockyarena.getclass")
//                .build();

        CommandSpec cmdJoin = CommandSpec.builder()
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("mode")))
                )
                .executor(new CmdJoin())
                .build();

        CommandSpec cmdQuit = CommandSpec.builder()
                .executor(new CmdQuit(this))
                .build();

        CommandSpec cmdEdit = CommandSpec.builder()
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("id"))),
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("type"))),
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("param")))
                )
                .executor(new CmdEdit())
                .build();

        CommandSpec arenaCommandSpec = CommandSpec.builder()
                .child(cmdEdit, "edit")
                .child(cmdCreate, "create")
                .child(cmdJoin, "join")
                .child(cmdQuit, "quit")
                //.child(cmdGetClass, "getclass")
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

    public GameManager getGameManager() {
        return gameManager;
    }

    public PlayerClassManager getPlayerClassManager() {
        return playerClassManager;
    }

    public GamerManager getGamerManager() {
        return gamerManager;
    }
}