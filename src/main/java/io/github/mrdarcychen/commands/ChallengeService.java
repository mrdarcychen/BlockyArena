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
import io.github.mrdarcychen.games.PlayerManager;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.format.TextStyles;

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
            initiator.sendMessage(ChatTypes.ACTION_BAR, Messages.INVALID_PLAYER);
            return CommandResult.empty();
        }
        Player rival = optRival.get();
        if (rival == src) {
            initiator.sendMessage(ChatTypes.ACTION_BAR, Messages.CHALLENGE_SELF);
            return CommandResult.empty();
        }
        if (PlayerManager.isPlaying(rival.getUniqueId())) {
            initiator.sendMessage(ChatTypes.ACTION_BAR, Messages.PLAYER_BUSY);
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
            you.sendMessage(ChatTypes.ACTION_BAR, Messages.INVALID_PLAYER);
            return CommandResult.empty();
        }
        Player challenger = optChallenger.get();
        ChallengeData data = new ChallengeData(challenger, you);
        if (requests.remove(data)) {
            data.notifySuccessfulRespond();
            return CommandResult.success();
        }
        you.sendMessage(ChatTypes.ACTION_BAR, Messages.NO_CHALLENGE_RECEIVED);
        return CommandResult.empty();
    };
    private final CommandSpec respondSpec = CommandSpec.builder()
            .arguments(onlyOne(player(of("player"))))
            .executor(respondExec)
            .permission("blockyarena.play")

            .build();
    private final CommandSpec challenge = CommandSpec.builder()
            .child(respondSpec, "accept")
            .arguments(onlyOne(player(of("player"))))
            .executor(requestExec)
            .permission("blockyarena.play")
            .build();

    public CommandCallable getCommandCallable() {
        return challenge;
    }

    private void request(ChallengeData request, int expireInSeconds) {
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
            Text delivered = Text
                    .builder("\nYour challenge request has been delivered.")
                    .color(TextColors.GREEN)
                    .build();
            Text wait = Text.of("\nPlease wait for " + rival.getName() + "'s acceptance.");
            Text challengeSent = Text.builder()
                    .append(delivered)
                    .append(wait)
                    .build();
            challenger.sendMessage(of(MessageBroker.wrap(challengeSent)));
            rival.sendMessage(Messages.invite(challenger.getName()));
        }

        void notifySuccessfulRespond() {
            Optional<Arena> optArena = BlockyArena.getArenaDispatcher().findBy("1v1");
            if (!optArena.isPresent()) {
                String msg = "No arena is available at this time.";
                challenger.sendMessage(of(msg));
                rival.sendMessage(of(msg));
                return;
            }
            challenger.sendMessage(ChatTypes.ACTION_BAR,
                    of(rival.getName(), " has accepted your challenge!"));
            rival.sendMessage(ChatTypes.ACTION_BAR,
                    of("You've accepted the challenge from ", challenger.getName(), "!"));
            GameSession session = new FullFledgedGameSession(teamMode, optArena.get());
            CmdJoin.register(session);
            session.add(challenger);
            session.add(rival);
        }

        void notifyExpiry() {
            challenger.sendMessage(ChatTypes.ACTION_BAR,
                    of("Your challenge to ", rival.getName(), " has expired."));
            rival.sendMessage(ChatTypes.ACTION_BAR,
                    of("The challenge from ", challenger.getName(), " has expired."));
        }

        public void notifyDuplicateRequest() {
            challenger.sendMessage(ChatTypes.ACTION_BAR, Messages.BE_PATIENT);
        }
    }

    private static final class Messages {
        public static final Text PLAYER_BUSY = Text
                .builder("That player is busy at the moment. Try again later.")
                .color(TextColors.RED).build();

        static final Text INVALID_PLAYER = Text
                .builder("The player you specified is not a valid server player.")
                .color(TextColors.RED).build();

        static final Text CHALLENGE_SELF = Text
                .builder("Nice try, but good spirit! Challenge someone else instead!")
                .color(TextColors.RED).build();

        static final Text NO_CHALLENGE_RECEIVED = Text
                .builder("You did not receive a challenge from that player.")
                .color(TextColors.RED).build();

        static final Text BE_PATIENT = Text
                .builder("Please wait for your rival's acceptance.")
                .color(TextColors.RED).build();

        static final Text invite(String name) {
            Text click = Text.builder("Click here")
                    .onClick(TextActions.runCommand("/ba challenge accept " + name))
                    .format(TextFormat.of(TextStyles.UNDERLINE))
                    .color(TextColors.GOLD).build();
            Text post = Text.of(" to accept in the next 15 seconds.");
            return MessageBroker.wrap(Text.builder()
                    .append(Text.of("\n" + name + " wants to challenge you to a duel. \n"))
                    .append(click).append(post).build());
        }

    }
}
