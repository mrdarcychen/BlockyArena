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
import io.github.mrdarcychen.PlatformRegistry;
import io.github.mrdarcychen.arenas.Arena;
import io.github.mrdarcychen.games.FullFledgedGameSession;
import io.github.mrdarcychen.games.GameSession;
import io.github.mrdarcychen.games.MatchRules;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.spongepowered.api.command.args.GenericArguments.onlyOne;
import static org.spongepowered.api.command.args.GenericArguments.player;
import static org.spongepowered.api.text.Text.of;

public class ChallengeService {

    private static final MatchRules teamMode = new TeamMode(1, 2);

    private final List<ChallengeData> requests = new ArrayList<>();
    private final CommandExecutor requestExec = (src, args) -> {
        if (!(src instanceof Player)) {
            src.sendMessage(of("You must be a player to execute this command."));
            return CommandResult.empty();
        }
        Player initiator = (Player) src;
        Optional<Player> optRival = args.getOne(of("player"));
        if (!optRival.isPresent()) {
            initiator.sendMessage(of("The player you specified is invalid."));
            return CommandResult.empty();
        }
        Player rival = optRival.get();
        if (rival == src) {
            initiator.sendMessage(of("You cannot challenge yourself."));
            return CommandResult.empty();
        }
        ChallengeData data = new ChallengeData(initiator, rival);
        request(data, 15);
        return CommandResult.success();
    };
    private final CommandExecutor respondExec = (src, args) -> {
        if (!(src instanceof Player)) {
            src.sendMessage(of("You must be a player to execute this command."));
            return CommandResult.empty();
        }
        Player you = (Player) src;
        Optional<Player> optChallenger = args.getOne(of("player"));
        if (!optChallenger.isPresent()) {
            you.sendMessage(of("The player you specified is invalid."));
            return CommandResult.empty();
        }
        Player challenger = optChallenger.get();
        ChallengeData data = new ChallengeData(challenger, you);
        if (requests.remove(data)) {
            data.notifySuccessfulRespond();
            return CommandResult.success();
        }
        you.sendMessage(of("You did not receive a challenge from that player."));
        return CommandResult.empty();
    };
    private final CommandSpec respondSpec = CommandSpec.builder()
            .arguments(onlyOne(player(of("player"))))
            .executor(respondExec)
            .build();
    private final CommandSpec challenge = CommandSpec.builder()
            .child(respondSpec, "accept")
            .arguments(onlyOne(player(of("player"))))
            .executor(requestExec)
            .build();

    public CommandCallable getCommandCallable() {
        return challenge;
    }

    private void request(ChallengeData request, int expireInSeconds) {
        // TODO: handle situations where player is in a game or afk
        if (requests.contains(request)) {
            request.notifyDuplicateRequest();
            return;
        }
        ChallengeData flipped = request.flip();
        if (requests.remove(flipped)) {
            flipped.notifySuccessfulRespond();
            return;
        }
        requests.add(request);
        PlatformRegistry.schedule(
                Task.builder()
                        .delay(expireInSeconds, TimeUnit.SECONDS)
                        .execute(() -> {
                            if (requests.remove(request)) {
                                request.notifyExpiry();
                            }
                        })
        );
        request.notifySuccessfulRequest();
    }

    private class ChallengeData {
        private final Player challenger; // challenger
        private final Player rival; // rival

        ChallengeData(Player challenger, Player rival) {
            this.challenger = challenger;
            this.rival = rival;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChallengeData that = (ChallengeData) o;
            return Objects.equals(challenger, that.challenger) &&
                    Objects.equals(rival, that.rival);
        }

        @Override
        public int hashCode() {
            return Objects.hash(challenger, rival);
        }

        ChallengeData flip() {
            return new ChallengeData(rival, challenger);
        }

        void notifySuccessfulRequest() {
            challenger.sendMessage(of("You've challenged ", rival.getName(), ". Please wait for acceptance."));
            rival.sendMessage(of("You're challenged by ", challenger.getName(), ". Accept in 15 seconds."));
        }

        void notifySuccessfulRespond() {
            Optional<Arena> optArena = BlockyArena.getArenaManager().findArena("1v1");
            if (!optArena.isPresent()) {
                String msg = "No arena is available at this time.";
                challenger.sendMessage(of(msg));
                rival.sendMessage(of(msg));
                return;
            }
            challenger.sendMessage(of(rival.getName(), " has accepted your challenge!"));
            rival.sendMessage(of("You've accepted the challenge from ", challenger.getName(), "!"));
            GameSession session = new FullFledgedGameSession(teamMode, optArena.get());
            CmdJoin.register(session);
            session.add(challenger);
            session.add(rival);
        }

        void notifyExpiry() {
            challenger.sendMessage(of("Your challenge to ", rival.getName(), " has expired."));
            rival.sendMessage(of("The challenge from ", challenger.getName(), " has expired."));
        }

        public void notifyDuplicateRequest() {
            challenger.sendMessage(of("Please wait for your rival's acceptance."));
        }
    }
}
