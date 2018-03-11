/*
 * This file is part of BlockyArena, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018 HuskyCraft <https://www.huskycraft.net>
 * Copyright (c) 2018 Darcy-Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.huskycraft.blockyarena.commands;

import net.huskycraft.blockyarena.arenas.Arena;
import net.huskycraft.blockyarena.BlockyArena;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CmdEdit implements CommandExecutor{

    public static BlockyArena plugin;

    public CmdEdit(BlockyArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player)src;
        String id = args.<String>getOne("id").get();
        String type = args.<String>getOne("type").get();
        String param = args.<String>getOne("param").get();

        Arena arena = plugin.getArenaManager().getArena(id);
        if (arena == null) {
            player.sendMessage(Text.of("Arena " + id + " does not exist."));
            return CommandResult.empty();
        }
        switch (type) {
            case "spawn":
                switch (param) {
                    case "a":
                        arena.setTeamSpawnA(player.getLocation(), player.getHeadRotation());
                        player.sendMessage(Text.of("Spawn point A is set."));
                        break;
                    case "b":
                        arena.setTeamSpawnB(player.getLocation(), player.getHeadRotation());
                        player.sendMessage(Text.of("Spawn point B is set."));
                        break;
                    case "lobby":
                        arena.setLobbySpawn(player.getLocation(), player.getHeadRotation());
                        player.sendMessage(Text.of("Lobby spawn point is set."));
                        break;
                    case "spectator":
                        arena.setSpectatorSpawn(player.getLocation(), player.getHeadRotation());
                        player.sendMessage(Text.of("Spectator spawn point is set."));
                        break;
                    default:
                        player.sendMessage(Text.of("Incorrect spawn type parameter."));
                        return CommandResult.empty();
                }
        }
        return CommandResult.success();
    }
}
