package net.huskycraft.blockyarena.commands;

import net.huskycraft.blockyarena.Arena;
import net.huskycraft.blockyarena.BlockyArena;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.UUID;

public class CreateCmd implements CommandExecutor {

    private BlockyArena plugin;

    public CreateCmd(BlockyArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Player player = (Player)src;
        UUID uuid = player.getUniqueId();
        String name = args.<String>getOne("name").get();
        Arena arena = new Arena(plugin, name);
        plugin.getArenaManager().arenaCreators.put(uuid, arena);
        player.sendMessage(Text.of("Start creating arena " + name + ".\n" +
                "Use `/arena setspawn <type>` to set spawn points. \n" +
                "Use `/arena done` to save."));

        return CommandResult.success();
    }
}