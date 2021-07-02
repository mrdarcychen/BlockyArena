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
import io.github.mrdarcychen.utils.Kit;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import static org.spongepowered.api.command.args.GenericArguments.onlyOne;
import static org.spongepowered.api.command.args.GenericArguments.string;

public class CmdCreate implements CommandExecutor {

    public static final CommandSpec SPEC = CommandSpec.builder()
            .arguments(
                    onlyOne(string(Text.of("type"))),
                    onlyOne(string(Text.of("id"))))
            .executor(new CmdCreate())
            .permission("blockyarena.create")
            .build();

    private CmdCreate() {
    }

    private Text command(String id, String spawnType) {
        return Text.builder("/ba edit " + id + " spawn " + spawnType)
                .color(TextColors.AQUA).build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("This command must be executed by a player."));
            return CommandResult.empty();
        }
        Player player = (Player) src;
        String type = args.<String>getOne("type").get();
        String kitId = args.<String>getOne("id").get();



        switch (type) {
            case "arena":
                CmdEdit.expectBuilder(player, kitId);
                Text lobby = Text.builder("\nStand on the lobby spawn point and execute\n ")
                        .append(command(kitId, "lobby")).build();
                Text spectator = Text.builder("\nStand on the spectator spawn point and execute \n ")
                        .append(command(kitId, "spectator")).build();
                Text start = Text.builder("\nStand on a new team start point and execute \n ")
                        .append(command(kitId, "start")).build();
                Text done = Text.builder("\nOnce you're finished, execute ").append(
                        Text.builder("/ba edit " + kitId + " save\n").color(TextColors.AQUA).build()
                ).build();
                Text instruction = Text.builder()
                        .append(Text.of("\nYou're about to configure a new arena interactively."))
                        .append(lobby).append(spectator).append(start).append(done).build();
                player.sendMessage(MessageBroker.wrap(instruction));
                return CommandResult.success();
            case "kit":
                Kit kit = new Kit(player, kitId);
                BlockyArena.getKitDispatcher().add(kit);
                Text notification = Text
                        .builder("\nA new kit has been created base on your current inventory.\n")
                        .color(TextColors.GREEN).build();
                player.sendMessage(MessageBroker.wrap(notification));
                return CommandResult.success();
            default:
                player.sendMessage(ChatTypes.ACTION_BAR,
                        Text.builder("Invalid argument <type>. Must be either arena or kit.")
                                .color(TextColors.RED).build());
        }
        return CommandResult.empty();
    }
}
