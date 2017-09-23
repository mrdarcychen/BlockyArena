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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class SetSpawnCmd implements CommandExecutor{

    BlockyArena plugin;

    public SetSpawnCmd(BlockyArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Player player = (Player)src;

        if (!plugin.getArenaManager().arenaCreators.containsKey(player.getUniqueId())) {
            player.sendMessage(Text.of("You're not currently creating an arena."));
            return CommandResult.empty();
        }

        Arena arena = plugin.getArenaManager().arenaCreators.get(player.getUniqueId());
        Location<World> spawn = player.getLocation();

        World world = spawn.getExtent();
        arena.setWorld(world);

        String type = args.getOne("type").get().toString();
        switch (type) {
            case "lobby": arena.setLobbySpawn(spawn); break;
            case "red": arena.setRedTeamSPawn(spawn); break;
            case "blue": arena.setBlueTeamSpawn(spawn); break;
            default:
                player.sendMessage(Text.of("Invalid spawn type."));
                return CommandResult.empty();
        }
        player.sendMessage(Text.of("Successfully set " + type + " spawn at " + spawn.getPosition().round().toInt()));

        return CommandResult.success();
    }
}
