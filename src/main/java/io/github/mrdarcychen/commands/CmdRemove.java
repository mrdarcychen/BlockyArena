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
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import static org.spongepowered.api.command.args.GenericArguments.onlyOne;
import static org.spongepowered.api.command.args.GenericArguments.string;

public class CmdRemove implements CommandExecutor {

    public static final CommandSpec SPEC = CommandSpec.builder()
            .arguments(
                    onlyOne(string(Text.of("type"))),
                    onlyOne(string(Text.of("id"))))
            .executor(new CmdRemove())
            .permission("blockyarena.remove")
            .build();

    private CmdRemove() {
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String type = args.<String>getOne("type").get().toLowerCase();
        String id = args.<String>getOne("id").get().toLowerCase();
        switch (type) {
            case "arena":
                try {
                    BlockyArena.getArenaManager().remove(id);
                    break;
                } catch (IllegalArgumentException e) {
                    src.sendMessage(Text.of(e.getMessage()));
                    return CommandResult.empty();
                }
            default:
                src.sendMessage(Text.of("<type> must be arena."));
                return CommandResult.empty();
        }
        src.sendMessage(Text.of(id + " has been removed."));
        return CommandResult.success();
    }
}
