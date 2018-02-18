package net.huskycraft.blockyarena;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import net.huskycraft.blockyarena.arenas.Spawn;
import net.huskycraft.blockyarena.arenas.SpawnSerializer;
import net.huskycraft.blockyarena.commands.*;
import net.huskycraft.blockyarena.listeners.ClientConnectionEventListener;
import net.huskycraft.blockyarena.listeners.EntityListener;
import net.huskycraft.blockyarena.managers.ArenaManager;
import net.huskycraft.blockyarena.managers.GameManager;
import net.huskycraft.blockyarena.managers.GamerManager;
import net.huskycraft.blockyarena.managers.KitManager;
import net.huskycraft.blockyarena.utils.Kit;
import net.huskycraft.blockyarena.utils.KitSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Plugin(id = "blockyarena", name = "BlockyArena", version = "0.4.0")
public class BlockyArena {
    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    private Path arenaDir, kitDir;

    private ArenaManager arenaManager;
    private GameManager gameManager;
    private GamerManager gamerManager;
    private KitManager kitManager;

    @Listener
    public void onServerStarting(GameStartingServerEvent event) {
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Spawn.class), new SpawnSerializer(this));
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Kit.class), new KitSerializer(this));
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
        gameManager = new GameManager(this);
        gamerManager = new GamerManager(this);
        kitManager = new KitManager(this);
    }

    /*
    creates directories for arenas and classes if they do not exist
    pre: plugin config directory exists (throws IOException if not)
     */
    private void createDirectories() {
        arenaDir = Paths.get(getConfigDir().toString() + "/arenas");
        kitDir = Paths.get(getConfigDir().toString() + "/kits");

        List<Path> directories = Arrays.asList(arenaDir, kitDir);
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
        Sponge.getEventManager().registerListeners(this, new ClientConnectionEventListener(this));
    }

    /*
    registers user commands to CommandManager
     */
    private void registerCommands() {
        CommandSpec cmdCreate = CommandSpec.builder()
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("type"))),
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("id"))))
                .executor(new CmdCreate(this))
                .permission("blockyarena.create")
                .build();

        CommandSpec cmdJoin = CommandSpec.builder()
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("mode")))
                )
                .executor(new CmdJoin(this))
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
                .executor(new CmdEdit(this))
                .build();

        CommandSpec cmdKit = CommandSpec.builder()
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("id"))))
                .executor(new CmdKit(this))
                .build();

        CommandSpec arenaCommandSpec = CommandSpec.builder()
                .child(cmdEdit, "edit")
                .child(cmdCreate, "create")
                .child(cmdJoin, "join")
                .child(cmdQuit, "quit")
                .child(cmdKit, "kit")
                .build();

        Sponge.getCommandManager()
                .register(this, arenaCommandSpec, "blockyarena", "arena", "ba");
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

    public Path getKitDir() {
        return kitDir;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public GamerManager getGamerManager() {
        return gamerManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }
}