package net.huskycraft.blockyarena.commands;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.utils.Gamer;
import net.huskycraft.blockyarena.utils.GamerStatus;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CmdQuit implements CommandExecutor{

    public static BlockyArena plugin;

    public CmdQuit(BlockyArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;
        Gamer gamer = plugin.getGamerManager().getGamer(player);
        player.sendMessage(Text.of("Your status is " + gamer.getStatus().toString()));
        if (gamer.getStatus() != GamerStatus.INGAME) {
            player.sendMessage(Text.of("You're not in any game."));
            return CommandResult.empty();
        }
        try {
            gamer.getGame().remove(gamer);
        } catch (NullPointerException e) {
            player.sendMessage(Text.of("You're not in any game."));
            return CommandResult.empty();
        }
        player.sendMessage(Text.of("You left the game."));
        return CommandResult.success();
    }
}
