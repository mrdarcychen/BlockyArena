package net.huskycraft.blockyarena.commands;

import net.huskycraft.blockyarena.*;
import net.huskycraft.blockyarena.games.Game;
import net.huskycraft.blockyarena.games.TeamMode;
import net.huskycraft.blockyarena.managers.GamersManager;
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
        Gamer gamer = GamersManager.getGamer(player.getUniqueId()).get();
        try {
            TeamMode teamMode = TeamMode.valueOf(args.<String>getOne("mode").get().toUpperCase());
            if (GamersManager.getGamer(player.getUniqueId()).get().getStatus() == GamerStatus.PLAYING) {
                player.sendMessage(Text.of("You've already joined a game!"));
                return CommandResult.empty();
            } else {
                Game game = plugin.getGameManager().getGame(teamMode);
                if (game == null) {
                    player.sendMessage(Text.of("There is no available arena at this time."));
                    return CommandResult.empty();
                }
                gamer.join(game);
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage(Text.of("You've entered an invalid team mode!"));
            return CommandResult.empty();
        }

        return CommandResult.success();
    }
}
