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
import io.github.mrdarcychen.games.PlayerManager;
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
import org.spongepowered.api.util.Tristate;

import java.util.Optional;

import static org.spongepowered.api.command.args.GenericArguments.*;

public class CmdKit implements CommandExecutor {

    public static final CommandSpec SPEC = CommandSpec.builder()
            .arguments(
                    onlyOne(string(Text.of("id"))),
                    optionalWeak(flags().valueFlag(playerOrSource(Text.of("player")), "p")
                            .buildWith(none()))
            )
            .permission("blockyarena.kit")
            .executor(new CmdKit())
            .build();
    private static final Text CANNOT_PICK_KIT = Text.builder("You're not allowed to pick a kit at this moment.")
            .color(TextColors.RED).build();
    private static final Text NO_KIT_AVAILABLE = Text.builder("The kit you specified does not exist.")
            .color(TextColors.RED).build();

    private CmdKit() {
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player;
        Optional<Player> playerArg = args.getOne(Text.of("player"));
        if (src instanceof Player) {
            if (playerArg.isPresent() && src != playerArg.get()) {
                src.sendMessage(Text.of("This must be executed using a command block."));
                return CommandResult.empty();
            }
            player = (Player) src;
        } else {
            if (!playerArg.isPresent()) {
                return CommandResult.empty();
            }
            player = playerArg.get();
        }
        if (!PlayerManager.isWaiting(player.getUniqueId())) {
            player.sendMessage(Text.of(CANNOT_PICK_KIT));
            return CommandResult.empty();
        }
        String id = args.<String>getOne(Text.of("id")).get();
        boolean hasPermissionToRetrieveSpecifiedKit = player
                .getPermissionValue(player.getActiveContexts(), "blockyarena.kit." + id)
                .asBoolean();
        if (!hasPermissionToRetrieveSpecifiedKit) {
            Text noPerm = Text.builder("You don't have the permission to retrieve that kit.")
                    .color(TextColors.RED).build();
            player.sendMessage(ChatTypes.ACTION_BAR, noPerm);
            return CommandResult.empty();
        }
        Optional<Kit> optKit = BlockyArena.getKitDispatcher().get(id);
        if (!optKit.isPresent()) {
            player.sendMessage(NO_KIT_AVAILABLE);
            return CommandResult.empty();
        }
        optKit.get().equip(player);
        return CommandResult.success();
    }
}
