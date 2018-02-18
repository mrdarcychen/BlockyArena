package net.huskycraft.blockyarena.commands;

import net.huskycraft.blockyarena.BlockyArena;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CmdKit implements CommandExecutor {

    public static BlockyArena plugin;

    public CmdKit(BlockyArena plugin) {
        this.plugin = plugin;
    }
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player)src;
        // TODO: add game check
        String id = args.<String>getOne(Text.of("id")).get();
        if (plugin.getKitManager().get(id) == null) {
            player.sendMessage(Text.of(id + " does not exist."));
            return CommandResult.empty();
        }
        plugin.getKitManager().get(id).equip(player);
        return CommandResult.success();
    }
}
