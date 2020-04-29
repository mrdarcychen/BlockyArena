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

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.games.GamersManager;
import net.huskycraft.blockyarena.utils.GamerStatus;

public class CmdKit implements CommandExecutor {

    private static final CmdKit INSTANCE = new CmdKit();

    /* enforce the singleton property with a private constructor */
    private CmdKit() {}

    public static CmdKit getInstance() {
        return INSTANCE;
    }


    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player)src;
        if (GamersManager.getGamer(player.getUniqueId()).get().getStatus() != GamerStatus.PLAYING) {
            player.sendMessage(Text.of("You are not allowed to get any kit when you are not in a game."));
            return CommandResult.empty();
        }
        String id = args.<String>getOne(Text.of("id")).get();
        if (BlockyArena.getKitManager().get(id) == null) {
            player.sendMessage(Text.of("The given kit " + id + " does not exist."));
            return CommandResult.empty();
        }
        BlockyArena.getKitManager().get(id).equip(player);
        return CommandResult.success();
    }
}
