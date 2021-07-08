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
import io.github.mrdarcychen.ServiceProvider;
import io.github.mrdarcychen.arenas.Arena;
import io.github.mrdarcychen.games.FullFledgedGameSession;
import io.github.mrdarcychen.games.GameSession;
import io.github.mrdarcychen.games.MatchRules;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.spongepowered.api.text.Text.of;

public class ChallengeService {

    private static final MatchRules teamMode = new TeamMode(1, 2);

    private final List<ChallengeData> requests = new ArrayList<>();

    void request(ChallengeData request, int expireInSeconds) {
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

    boolean remove(ChallengeData data) {
        return requests.remove(data);
    }

    static class ChallengeData {
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
            rival.sendMessage(CmdChallenge.Messages.invite(challenger.getName()));
        }

        void notifySuccessfulRespond() {
            Optional<Arena> optArena = ServiceProvider.getArenaDispatcher().findBy("1v1");
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
            SessionRegistry.register(session);
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
            challenger.sendMessage(ChatTypes.ACTION_BAR, CmdChallenge.Messages.BE_PATIENT);
        }
    }
}
