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

import io.github.mrdarcychen.games.PlayerManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

public class CmdQuit implements CommandExecutor {

    public static final CommandSpec SPEC = CommandSpec.builder()
            .executor(new CmdQuit())
            .build();

    private CmdQuit() {
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;
        if (!PlayerManager.isPlaying(player.getUniqueId())) {
            player.sendMessage(ChatTypes.ACTION_BAR, Messages.NOT_IN_GAME_SESSION);
            return CommandResult.empty();
        }
        PlayerManager.getGame(player.getUniqueId()).ifPresent(game -> game.remove(player));
        player.sendMessage(ChatTypes.ACTION_BAR, Messages.LEFT_SESSION);
        return CommandResult.success();
    }

    private static class Messages {
        static final Text NOT_IN_GAME_SESSION = Text
                .builder("You are currently not in a minigame.")
                .color(TextColors.RED).build();

        static final Text LEFT_SESSION = Text
                .builder("You have left the minigame.")
                .color(TextColors.GREEN).build();
    }
}
