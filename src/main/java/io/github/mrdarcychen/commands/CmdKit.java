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

import io.github.mrdarcychen.games.GamersManager;
import io.github.mrdarcychen.utils.KitManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class CmdKit implements CommandExecutor {

    private static final CmdKit INSTANCE = new CmdKit();

    /* enforce the singleton property with a private constructor */
    private CmdKit() {}

    public static CmdKit getInstance() {
        return INSTANCE;
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
            player = (Player)src;
        } else {
            if (!playerArg.isPresent()) {
                return CommandResult.empty();
            }
            player = playerArg.get();
        }
        if (!inGame(player)) {
            player.sendMessage(Text.of("You must be in an active game session to get a kit."));
            return CommandResult.empty();
        }
        String id = args.<String>getOne(Text.of("id")).get();
        if (KitManager.getInstance().get(id) == null) {
            player.sendMessage(Text.of("Kit " + id + " does not exist."));
            return CommandResult.empty();
        }
        KitManager.getInstance().get(id).equip(player);
        return CommandResult.success();
    }

    private boolean inGame(Player player) {
        return GamersManager.getGamer(player.getUniqueId()).get().getGame().isPresent();
    }
}
