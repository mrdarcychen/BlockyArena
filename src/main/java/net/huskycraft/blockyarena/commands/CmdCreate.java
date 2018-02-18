package net.huskycraft.blockyarena.commands;

import net.huskycraft.blockyarena.arenas.Arena;
import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.utils.Kit;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CmdCreate implements CommandExecutor {

    public static BlockyArena plugin;

    public CmdCreate(BlockyArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player)src;
        String type = args.<String>getOne("type").get();
        String id = args.<String>getOne("id").get();
        switch (type) {
            case "arena":
                Arena arena = new Arena(plugin, id);
                plugin.getArenaManager().add(arena);
                player.sendMessage(Text.of(id + " is added on file. Start configuring it by typing /ba edit"));
                return CommandResult.success();
            case "kit":
                Kit kit = new Kit(player, id);
                plugin.getKitManager().add(kit, id);
                player.sendMessage(Text.of(id + " is added on file."));
                return CommandResult.success();
            default:
                player.sendMessage(Text.of("Invalid argument."));
        }
        return CommandResult.empty();
    }
}