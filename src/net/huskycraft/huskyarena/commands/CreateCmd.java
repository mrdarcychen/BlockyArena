package net.huskycraft.huskyarena.commands;

import com.sun.media.jfxmedia.events.PlayerEvent;
import net.huskycraft.huskyarena.Arena;
import net.huskycraft.huskyarena.HuskyArena;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CreateCmd implements CommandExecutor {

    private HuskyArena plugin;

    int step = 0;
    Player player;

    public CreateCmd(HuskyArena plugin) {
        this.plugin = plugin;


    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Player player = (Player)src;
        String name = args.<String>getOne("name").get();
        Arena arena = new Arena(plugin, name);
        player.sendMessage(Text.of("Successfully create arena. Go to config for custom configuration."));



        }

        return CommandResult.success();
    }
}
