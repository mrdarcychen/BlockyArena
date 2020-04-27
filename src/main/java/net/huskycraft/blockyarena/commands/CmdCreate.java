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

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.arenas.Arena;
import net.huskycraft.blockyarena.utils.Kit;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CmdCreate implements CommandExecutor {

    private static final CmdCreate INSTANCE = new CmdCreate();

    /* enforce the singleton property with a private constructor */
    private CmdCreate() {
    }

    public static CmdCreate getInstance() {
        return INSTANCE;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player)src;
        String type = args.<String>getOne("type").get();
        String id = args.<String>getOne("id").get();
        switch (type) {
            case "arena":
                Arena arena = new Arena(id);
                BlockyArena.getArenaManager().add(arena);
                player.sendMessage(Text.of(id + " is added on file. Start configuring it by typing /ba edit"));
                return CommandResult.success();
            case "kit":
                Kit kit = new Kit(player, id);
                BlockyArena.getKitManager().add(kit, id);
                player.sendMessage(Text.of(id + " is added on file."));
                return CommandResult.success();
            default:
                player.sendMessage(Text.of("Invalid argument."));
        }
        return CommandResult.empty();
    }
}