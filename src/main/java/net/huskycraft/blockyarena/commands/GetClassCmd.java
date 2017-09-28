package net.huskycraft.blockyarena.commands;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.PlayerClass;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class GetClassCmd implements CommandExecutor{

    private BlockyArena plugin;

    public GetClassCmd(BlockyArena plugin) {
        this.plugin = plugin;
    }
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;
        String className = args.<String>getOne(Text.of("name")).get();
        PlayerClass playerClass = plugin.getPlayerClassManager().getPlayerClass(className);
        playerClass.offerItemStacksTo(player);
        return CommandResult.success();
    }
}
