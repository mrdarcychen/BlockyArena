package net.huskycraft.huskyarena.Commands;

import net.huskycraft.huskyarena.HuskyArena;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class DoneCmd implements CommandExecutor{

    HuskyArena plugin;

    public DoneCmd(HuskyArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player)src;
        if (!plugin.getArenaManager().arenaCreators.containsKey(player.getUniqueId())) {
            player.sendMessage(Text.of("You're not currently creating an arena."));
            return CommandResult.empty();
        }
        plugin.getArenaManager().arenaCreators.remove(player.getUniqueId());
        player.sendMessage(Text.of("Done creating arena."));

        return CommandResult.success();
    }
}
