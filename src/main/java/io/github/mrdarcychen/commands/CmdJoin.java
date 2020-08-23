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
import io.github.mrdarcychen.arenas.Arena;
import io.github.mrdarcychen.games.GameSession;
import io.github.mrdarcychen.games.MatchRules;
import io.github.mrdarcychen.games.PlayerManager;
import io.github.mrdarcychen.games.SimpleGameSession;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.spongepowered.api.command.args.GenericArguments.*;

public class CmdJoin implements CommandExecutor {

    private static final List<GameSession> GAME_SESSIONS = new ArrayList<>();

    public static final CommandSpec SPEC = CommandSpec.builder()
            .arguments(
                    onlyOne(string(Text.of("mode"))),
                    optionalWeak(flags().valueFlag(playerOrSource(Text.of("player")), "p")
                            .buildWith(none()))
            )
            .executor(new CmdJoin())
            .build();

    /* enforce the singleton property with a private constructor */
    private CmdJoin() {
    }

    private static GameSession getGame(String str) {
        String mode = str.toLowerCase();
        Predicate<GameSession> criteria = (it) -> it.canJoin() && it.getTeamMode().toString().equals(mode);
        Optional<GameSession> optGame = GAME_SESSIONS.stream().filter(criteria).findAny();
        if (optGame.isPresent()) {
            return optGame.get();
        }
        Optional<Arena> optArena = BlockyArena.getArenaManager().findArena(mode);
        if (optArena.isPresent()) {
            Arena arena = optArena.get();
            int teamSize = 1;
            int teamCount = 2;
            if ("2v2".equals(mode)) {
                teamSize = 2;
            }
            if ("ffa".equals(mode)) {
                teamCount = (int) arena.getStartPoints().count();
            }
            MatchRules matchRules = new TeamMode(teamSize, teamCount);
            GameSession gameSession = new SimpleGameSession(matchRules, optArena.get());
            GAME_SESSIONS.add(gameSession);
            return gameSession;
        }
        return null;
    }

    public static void remove(GameSession gameSession) {
        BlockyArena.getArenaManager().makeAvailable(gameSession.getArena());
        GAME_SESSIONS.remove(gameSession);
    }

    /**
     * Sends the given player to an active Game.
     */
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
        Optional<String> optMode = args.getOne("mode");

        if (!optMode.isPresent()) {
            return CommandResult.empty();
        }
        if (PlayerManager.isPlaying(player.getUniqueId())) {
            player.sendMessage(Text.of("You're already in a game. Use /ba" +
                    " quit to leave the current game session."));
            return CommandResult.empty();
        }
        GameSession gameSession = getGame(optMode.get());
        if (gameSession == null) {
            player.sendMessage(Text.of("There's no available arena at this time."));
            return CommandResult.empty();
        }
        gameSession.add(player);
        return CommandResult.success();
    }
}
