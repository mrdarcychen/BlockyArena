package net.huskycraft.blockyarena.commands;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.Session;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class QuitCmd implements CommandExecutor{

    BlockyArena plugin;

    public QuitCmd(BlockyArena plugin) {
        this.plugin = plugin;
    }
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;
        try {
            Session session = plugin.getSessionManager().playerSession.get(player);
            session.remove(player);
        } catch (NullPointerException e) {
            player.sendMessage(Text.of("You're not in any session."));
            return CommandResult.empty();
        }

        player.getInventory().clear();
        player.sendMessage(Text.of("You quit the session."));
        return CommandResult.success();
    }
}
