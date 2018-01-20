package net.huskycraft.blockyarena.commands;

import com.google.inject.Inject;
import net.huskycraft.blockyarena.Arena;
import net.huskycraft.blockyarena.BlockyArena;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class CmdEdit implements CommandExecutor{
    @Inject
    public static BlockyArena plugin;
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player)src;
        String id = args.<String>getOne("id").get();
        String type = args.<String>getOne("type").get();
        String param = args.<String>getOne("param").get();

        Arena arena = plugin.getArenaManager().getArena(id);
        if (arena != null) {
            switch (type) {
                case "spawn":
                    switch (param) {
                        case "a":
                            arena.setTeamSpawnA(player.getLocation(), player.getHeadRotation());
                        case "b":
                            arena.setTeamSpawnB(player.getLocation(), player.getHeadRotation());
                        case "lobby":
                            arena.setLobbySpawn(player.getLocation(), player.getHeadRotation());
                    }
            }
        }
        return CommandResult.success();
    }
}
