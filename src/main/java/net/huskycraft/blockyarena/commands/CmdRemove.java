package net.huskycraft.blockyarena.commands;

import net.huskycraft.blockyarena.BlockyArena;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class CmdRemove implements CommandExecutor {

    public static final CmdRemove instance = new CmdRemove();

    private CmdRemove() {}

    public static CmdRemove getInstance() {
        return instance;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String type = args.<String>getOne("type").get().toLowerCase();
        String id = args.<String>getOne("id").get().toLowerCase();
        switch (type) {

            case "arena":
                try {
                    BlockyArena.getArenaManager().remove(id);
                    break;
                } catch (IllegalArgumentException e) {
                    src.sendMessage(Text.of(e.getMessage()));
                    return CommandResult.empty();
                }

            case "kit":
                try {
                    BlockyArena.getKitManager().remove(id);
                    break;
                } catch (IllegalArgumentException e) {
                    src.sendMessage(Text.of(e.getMessage()));
                    return CommandResult.empty();
                }

            default:
                src.sendMessage(Text.of(type + " is not a valid type."));
                return CommandResult.empty();
        }
        src.sendMessage(Text.of(id + " has been removed."));
        return CommandResult.success();
    }
}
