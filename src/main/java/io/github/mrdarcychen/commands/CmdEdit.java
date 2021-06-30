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

package io.github.mrdarcychen.commands;

import io.github.mrdarcychen.BlockyArena;
import io.github.mrdarcychen.arenas.Arena;
import io.github.mrdarcychen.arenas.SpawnPoint;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;

import static org.spongepowered.api.command.args.GenericArguments.*;
import static org.spongepowered.api.text.Text.of;

public class CmdEdit implements CommandExecutor {

    public static final CommandSpec SPEC = CommandSpec.builder()
            .arguments(
                    onlyOne(string(Text.of("id"))),
                    onlyOne(string(Text.of("type"))),
                    optional(onlyOne(string(Text.of("param"))))
            )
            .executor(new CmdEdit())
            .permission("blockyarena.edit")
            .build();

    private static final Map<Player, Arena.Builder> builders = new HashMap<>();

    private CmdEdit() {
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;
        String id = args.<String>getOne("id").get();
        String type = args.<String>getOne("type").get();
        String param = args.<String>getOne("param").orElse("");

        Arena.Builder builder = builders.get(player);
        if (builder == null) {
            player.sendMessage(of("Arena " + id + " does not exist."));
            return CommandResult.empty();
        }
        if ("save".equals(type)) {
            Arena arena;
            try {
                arena = builder.build();
            } catch (IllegalStateException e) {
                player.sendMessage(of("Saving failed: " + e.getMessage()));
                return CommandResult.empty();
            }
            BlockyArena.getArenaDispatcher().register(arena);
            player.sendMessage(of("Success! Arena " + arena.getName() + " is now in operation."));
        } else {
            switch (param) {
                case "start":
                    builder.addStartPoint(SpawnPoint.of(player.getTransform()));
                    player.sendMessage(of("Start point for team #" + builder
                            .getStartPointCount() + " has been added to " + id + "."));
                    break;
                case "lobby":
                    builder.setLobbySpawn(SpawnPoint.of(player.getTransform()));
                    player.sendMessage(of("Lobby spawn point has been set for " + id + "."));
                    break;
                case "spectator":
                    builder.setSpectatorSpawn(SpawnPoint.of(player.getTransform()));
                    player.sendMessage(of("Spectator spawn point has been set for " + id + "."));
                    break;
                default:
                    player.sendMessage(of("<type> must be lobby, spectator, or start."));
                    return CommandResult.empty();
            }
        }
        return CommandResult.success();
    }

    public static void expectBuilder(Player player, String arenaName) {
        Arena.Builder builder = new Arena.Builder(arenaName);
        builders.put(player, builder);
    }
}
