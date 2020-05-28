/*
 * Copyright 2017-2020 The BlockyArena Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.huskycraft.blockyarena.commands;

import net.huskycraft.blockyarena.arenas.Arena;
import net.huskycraft.blockyarena.arenas.ArenaManager;
import net.huskycraft.blockyarena.arenas.SpawnPoint;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;

public class CmdEdit implements CommandExecutor{

    private static final CmdEdit INSTANCE = new CmdEdit();
    private Map<Player, Arena.Builder> builders = new HashMap<>();


    /* enforce the singleton property with a private constructor */
    private CmdEdit() {
    }

    public static CmdEdit getInstance() {
        return INSTANCE;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player)src;
        String id = args.<String>getOne("id").get();
        String type = args.<String>getOne("type").get();
        String param = args.<String>getOne("param").orElse("");

        Arena.Builder builder = builders.get(player);
        if (builder == null) {
            player.sendMessage(Text.of("Arena " + id + " does not exist."));
            return CommandResult.empty();
        }
        if ("save".equals(type)) {
            Arena arena = builder.build();
            ArenaManager.getInstance().add(arena);
        } else {
                switch (param) {
                    case "a":
                        builder.addStartPoint(SpawnPoint.of(player.getTransform()));
                        player.sendMessage(Text.of("Spawn point A is set."));
                        break;
                    case "b":
                        builder.addStartPoint(SpawnPoint.of(player.getTransform()));
                        player.sendMessage(Text.of("Spawn point B is set."));
                        break;
                    case "lobby":
                        builder.setLobbySpawn(SpawnPoint.of(player.getTransform()));
                        player.sendMessage(Text.of("Lobby spawn point is set."));
                        break;
                    case "spectator":
                        builder.setSpectatorSpawn(SpawnPoint.of(player.getTransform()));
                        player.sendMessage(Text.of("Spectator spawn point is set."));
                        break;
                    default:
                        player.sendMessage(Text.of("Incorrect spawn type parameter."));
                        return CommandResult.empty();
                }
        }
        return CommandResult.success();
    }
    public void expectBuilder(Player player, String arenaName) {
        Arena.Builder builder = new Arena.Builder(arenaName);
        builders.put(player, builder);
    }
}
