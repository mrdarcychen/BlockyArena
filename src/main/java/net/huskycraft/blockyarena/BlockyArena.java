package net.huskycraft.blockyarena;

import com.google.inject.Inject;
import net.huskycraft.blockyarena.commands.*;
import net.huskycraft.blockyarena.listeners.EntityListener;
import net.huskycraft.blockyarena.managers.ArenaManager;
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


@Plugin(id = "blockyarena", name = "BlockyArena", version = "0.1.0")
public class BlockyArena {

    @Inject
    public Logger logger;

    public Logger getLogger() {
        return logger;
    }

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

    public ArenaManager arenaManager;
    public SessionManager sessionManager;

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        createArenaDir();
        registerCommands();
        Sponge.getEventManager().registerListeners(this, new EntityListener(this));
        arenaManager = new ArenaManager(this);
        sessionManager = new SessionManager(this);

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

    private void registerCommands() {

        CommandSpec createCmd = CommandSpec.builder()
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("name")))
                .executor(new CreateCmd(this))
                .permission("net.huskycraft.blockyarena.admin")
                .build();

        CommandSpec setSpawnCmd = CommandSpec.builder()
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("type"))))
                .executor(new SetSpawnCmd(this))
                .permission("net.huskycraft.blockyarena.admin")
                .build();

        CommandSpec doneCmd = CommandSpec.builder()
                .executor(new DoneCmd(this))
                .permission("net.huskycraft.blockyarena.admin")
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
                .build();

        Sponge.getCommandManager().register(this, arenaCommandSpec, "net.huskycraft/blockyarena", "arena");

    }
}