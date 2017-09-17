package net.huskycraft.huskyarena.commands;

import net.huskycraft.huskyarena.HuskyArena;
import net.huskycraft.huskyarena.Session;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class JoinCmd implements CommandExecutor{

    HuskyArena plugin;

    public JoinCmd(HuskyArena plugin) {
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

        return CommandResult.success();
    }
}
