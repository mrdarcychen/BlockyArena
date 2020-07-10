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

import io.github.mrdarcychen.PlatformRegistry;
import org.spongepowered.api.command.spec.CommandSpec;

public class CommandManager {

    public static void init() {
        CommandSpec rootCmd = CommandSpec.builder()
                .child(CmdEdit.SPEC, "edit")
                .child(CmdCreate.SPEC, "create")
                .child(CmdRemove.SPEC, "remove")
                .child(CmdJoin.SPEC, "join")
                .child(CmdQuit.SPEC, "quit")
                .child(CmdKit.SPEC, "kit")
                .build();

        PlatformRegistry.registerCommands(rootCmd);
    }
}
