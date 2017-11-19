package net.huskycraft.blockyarena.commands;

import net.huskycraft.blockyarena.Arena;
import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.PlayerClass;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.type.GridInventory;
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
        String object = args.<String>getOne("object").get();
        String name = args.<String>getOne("name").get();

        switch (object) {
            case "arena":
                UUID uuid = player.getUniqueId();
                Arena arena = new Arena(plugin, name);
                plugin.getArenaManager().addPendingArena(player, arena);
                player.sendMessage(Text.of("Start creating arena " + name + ".\n" +
                        "Use `/arena setspawn <type>` to set spawn points. \n" +
                        "Use `/arena done` to save."));
                break;
            case "class":
                PlayerClass playerClass = new PlayerClass(plugin, name, player.getInventory().query(GridInventory.class));
                plugin.getPlayerClassManager().addPlayerClass(playerClass);
                player.sendMessage(Text.of("Successfully created player class " + name));
                break;
            default:
                player.sendMessage(Text.of("Invalid type of object."));
                return CommandResult.empty();
        }
        return CommandResult.success();
    }
}