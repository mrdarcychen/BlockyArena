package net.huskycraft.blockyarena.commands;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.utils.GamerStatus;
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
        if (plugin.getGamerManager().getGamer(player).getStatus() != GamerStatus.PLAYING) {
            player.sendMessage(Text.of("You are not allowed to get any kit when you are not in a game."));
            return CommandResult.empty();
        }
        String id = args.<String>getOne(Text.of("id")).get();
        if (plugin.getKitManager().get(id) == null) {
            player.sendMessage(Text.of("The given kit " + id + " does not exist."));
            return CommandResult.empty();
        }
        plugin.getKitManager().get(id).equip(player);
        return CommandResult.success();
    }
}
