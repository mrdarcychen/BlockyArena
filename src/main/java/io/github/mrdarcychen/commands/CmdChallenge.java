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
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.spongepowered.api.command.args.GenericArguments.onlyOne;
import static org.spongepowered.api.command.args.GenericArguments.player;
import static org.spongepowered.api.text.Text.of;

public class CmdChallenge {

    private static final Map<Player, Player> matches = new HashMap<>(); // A challenged by B

    public static final CommandSpec accept = CommandSpec.builder()
            .arguments(onlyOne(player(of("player"))))
            .executor(((src, args) -> {
                if (!(src instanceof Player)) {
                    return CommandResult.empty();
                }
                args.<Player>getOne(of("player")).ifPresent(player -> {
                    if (matches.containsKey(player)) {
                        if (matches.get(player).equals(src)) {
                            matchAccepted((Player) src, player);
                        }
                    } else {
                        src.sendMessage(of("There's no challenge from that person."));
                    }
                });
                return CommandResult.empty();
            }))
            .build();

    public static final CommandSpec challenge = CommandSpec.builder()
            .child(accept, "accept")
            .arguments(onlyOne(player(of("player"))))
            .executor(((src, args) -> {
                if (!(src instanceof Player)) {
                    return CommandResult.empty();
                }
                args.<Player>getOne(of("player")).ifPresent(player -> {
                    if (player == src) {
                        src.sendMessage(of("You cannot challenge yourself."));
                    } else if (matches.get(player) == src) {// if you challenge a player who has already challenged you earlier, accept the challenge instead
                        matchAccepted((Player) src, player);
                    } else {
                        matches.put((Player) src, player);
                        player.sendMessage(of("You're challenged by ", src.getName(), ". Accept in 15 seconds."));
                        PlatformRegistry.schedule(
                                Task.builder()
                                        .delay(15, TimeUnit.SECONDS)
                                        .execute(() -> {
                                            if (matches.containsKey(src)) {
                                                src.sendMessage(of("Challenge expired."));
                                                matches.get(src).sendMessage(of("Challenge from ", src.getName(), " has expired."));
                                                matches.remove(src);
                                            }
                                        }));
                    }
                });
                return CommandResult.success();
            }))
            .build();

    private static void matchAccepted(Player a, Player b) {
        b.sendMessage(of("Challenged accepted!"));
        a.sendMessage(of(b.getName(), " has accepted your challenge!"));
        matches.remove(a);
    }
}
