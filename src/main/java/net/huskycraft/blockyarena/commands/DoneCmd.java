package net.huskycraft.blockyarena.commands;

import net.huskycraft.blockyarena.Arena;
import net.huskycraft.blockyarena.BlockyArena;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class DoneCmd implements CommandExecutor{

    BlockyArena plugin;

    public DoneCmd(BlockyArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player)src;
        if (plugin.getArenaManager().getPendingArena(player) == null) {
            player.sendMessage(Text.of("You're not currently creating an arena."));
            return CommandResult.empty();
        }
        plugin.getArenaManager().removePendingArena(player);
        player.sendMessage(Text.of("Done creating arena."));

        return CommandResult.success();
    }
}
