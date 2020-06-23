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

import io.github.mrdarcychen.games.Game;
import io.github.mrdarcychen.games.PlayerManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class CmdQuit implements CommandExecutor {

    private static final CmdQuit INSTANCE = new CmdQuit();

    /* enforce the singleton property with a private constructor */
    private CmdQuit() {
    }

    public static CmdQuit getInstance() {
        return INSTANCE;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;
        if (!PlayerManager.isPlaying(player.getUniqueId())) {
            player.sendMessage(Text.of("You're not in any minigame."));
            return CommandResult.empty();
        }
        PlayerManager.getGame(player.getUniqueId()).ifPresent(game -> game.remove(player));
        player.sendMessage(Text.of("You left the minigame."));
        return CommandResult.success();
    }
}
