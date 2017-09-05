package net.huskycraft.huskyarena.commands;

import net.huskycraft.huskyarena.Arena;
import net.huskycraft.huskyarena.HuskyArena;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

public class SetLobbySpawnCmd implements CommandExecutor{

    HuskyArena plugin;

    public SetLobbySpawnCmd(HuskyArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Player player = (Player)src;
        UUID uuid = player.getUniqueId();
        Arena arena = plugin.getArenaManager().arenaCreators.get(uuid);
        Location<World> lobbyspawn = player.getLocation();
        arena.setLobbySpawn(lobbyspawn);


        return CommandResult.success();
    }
}
