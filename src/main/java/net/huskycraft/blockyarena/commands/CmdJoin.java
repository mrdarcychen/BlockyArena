package net.huskycraft.blockyarena.commands;

import net.huskycraft.blockyarena.*;
import net.huskycraft.blockyarena.managers.GamerManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CmdJoin implements CommandExecutor{

    public static BlockyArena plugin;

    public CmdJoin() {}

    /**
     * Sends the given player to an active Game.
     */
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;
        Gamer gamer = plugin.getGamerManager().getGamer(player);
        TeamMode teamMode = TeamMode.valueOf(args.<String>getOne("mode").get());
        if (teamMode == null) {
            player.sendMessage(Text.of("You've entered an invalid team mode!"));
        }
        if (plugin.getGamerManager().getGamer(player).getStatus() == GamerStatus.INGAME) {
            player.sendMessage(Text.of("You've already joined a session!"));
            return CommandResult.empty();
        } else {
            try {
                Game game = plugin.getGameManager().getGame(teamMode);
                game.add(gamer);
            } catch (NullPointerException e) {
                player.sendMessage(Text.of("No available arena."));
            }
        }
        return CommandResult.success();
    }
}