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

public class JoinCmd implements CommandExecutor{

    BlockyArena plugin;

    public JoinCmd(BlockyArena plugin) {
        this.plugin = plugin;
    }

    /*
    send player to an active session
     */
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;
        if (plugin.getSessionManager().playerSession.containsKey(player)) {
            player.sendMessage(Text.of("You've already joined a session!"));
            return CommandResult.empty();
        } else {
            try {
                Session session = plugin.getSessionManager().getAvailableSession();
                session.add(player);
            } catch (NullPointerException e) {
                player.sendMessage(Text.of("No available arena."));
            }
        }

        player.getInventory().clear();
        return CommandResult.success();
    }
}
