package net.huskycraft.blockyarena.commands;

import net.huskycraft.blockyarena.arenas.Arena;
import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.managers.GamersManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CmdEdit implements CommandExecutor{

    public static BlockyArena plugin;

    public CmdEdit(BlockyArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player)src;
        String id = args.<String>getOne("id").get();
        String type = args.<String>getOne("type").get();
        String param = args.<String>getOne("param").get();

        Arena arena = plugin.getArenaManager().getArena(id);
        if (arena == null) {
            player.sendMessage(Text.of("Arena " + id + " does not exist."));
            return CommandResult.empty();
        }
        switch (type) {
            case "spawn":
                switch (param) {
                    case "a":
                        arena.setTeamSpawnA(player.getLocation(), player.getHeadRotation());
                        player.sendMessage(Text.of("Spawn point A is set."));
                        break;
                    case "b":
                        arena.setTeamSpawnB(player.getLocation(), player.getHeadRotation());
                        player.sendMessage(Text.of("Spawn point B is set."));
                        break;
                    case "lobby":
                        arena.setLobbySpawn(player.getLocation(), player.getHeadRotation());
                        player.sendMessage(Text.of("Lobby spawn point is set."));
                        break;
                    case "spectator":
                        arena.setSpectatorSpawn(player.getLocation(), player.getHeadRotation());
                        player.sendMessage(Text.of("Spectator spawn point is set."));
                        break;
                    default:
                        player.sendMessage(Text.of("Incorrect spawn type parameter."));
                        return CommandResult.empty();
                }
        }
        return CommandResult.success();
    }
}
