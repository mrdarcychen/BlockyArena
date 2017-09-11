package net.huskycraft.huskyarena.Commands;


import net.huskycraft.huskyarena.Arena;
import net.huskycraft.huskyarena.HuskyArena;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.UUID;

public class CreateCmd implements CommandExecutor {

    private HuskyArena plugin;

    public CreateCmd(HuskyArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Player player = (Player)src;
        UUID uuid = player.getUniqueId();
        String name = args.<String>getOne("name").get();
        Arena arena = new Arena(plugin, name);
        plugin.getArenaManager().arenaCreators.put(uuid, arena);
        player.sendMessage(Text.of("Successfully create arena."));

        return CommandResult.success();
    }
}