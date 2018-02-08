package net.huskycraft.blockyarena.commands;

import net.huskycraft.blockyarena.Arena;
import net.huskycraft.blockyarena.BlockyArena;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class CmdCreate implements CommandExecutor {

    public static BlockyArena plugin;

    public CmdCreate() {}

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player)src;
        String id = args.<String>getOne("id").get();

        Arena arena = new Arena(id);
        plugin.getArenaManager().add(arena);

        return CommandResult.success();
    }
}