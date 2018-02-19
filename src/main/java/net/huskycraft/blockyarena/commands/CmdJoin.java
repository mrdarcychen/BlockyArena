package net.huskycraft.blockyarena.commands;

import net.huskycraft.blockyarena.*;
import net.huskycraft.blockyarena.games.Game;
import net.huskycraft.blockyarena.games.TeamMode;
import net.huskycraft.blockyarena.utils.Gamer;
import net.huskycraft.blockyarena.utils.GamerStatus;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CmdJoin implements CommandExecutor{

    public static BlockyArena plugin;

    public CmdJoin(BlockyArena plugin) {
        this.plugin = plugin;
    }

    /**
     * Sends the given player to an active Game.
     */
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player)src;
        Gamer gamer = plugin.getGamerManager().getGamer(player);
        try {
            TeamMode teamMode = TeamMode.valueOf(args.<String>getOne("mode").get().toUpperCase());
            plugin.getLogger().warn("status ? " + gamer.getStatus().toString());
            if (plugin.getGamerManager().getGamer(player).getStatus() == GamerStatus.PLAYING) {
                player.sendMessage(Text.of("You've already joined a game!"));
                return CommandResult.empty();
            } else {
                plugin.getLogger().warn("Reach A");
                Game game = plugin.getGameManager().getGame(teamMode);
                plugin.getLogger().warn("Reach B");
                gamer.join(game);
                plugin.getLogger().warn("Reach C");
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage(Text.of("You've entered an invalid team mode!"));
            return CommandResult.empty();
        }

        return CommandResult.success();
    }
}
