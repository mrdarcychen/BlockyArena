/*
 * Copyright 2021 Darcy Chen <mrdarcychen@gmail.com> and the contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.github.mrdarcychen.games.states;

import io.github.mrdarcychen.ServiceProvider;
import io.github.mrdarcychen.games.GameSession;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StartingState extends MatchState {

    private final Timer timer;
    private final int countdown;

    public StartingState(GameSession gameSession, List<Player> players) {
        super(gameSession, players);
        this.countdown = ServiceProvider.getConfigManager().getLobbyCountdown();
        timer = startCountdownTimer();
    }

    private Timer startCountdownTimer() {
        final Timer timer;
        timer = new Timer(countdown, tMinus -> {
            if (tMinus == 0) {
                gameSession.setMatchState(new PlayingState(gameSession, players, partition()));
                return;
            }
            Title title = Title.builder().title(Text.of(tMinus)).fadeIn(2)
                    .fadeOut(2).stay(16).build();
            players.forEach(player -> {
                player.sendTitle(title);
                player.playSound(SoundTypes.BLOCK_NOTE_HAT, player.getLocation().getPosition(), 100);
            });
        });
        return timer;
    }

    @Override
    public void dismiss(Player player) {
        super.dismiss(player);
        // if fall below min requirement, new entering state
        if (players.size() <= matchRules.getTotalCapacity()) {
            timer.cancel();
            broadcast(Messages.WAITING_FOR_PLAYERS);
            gameSession.setMatchState(new EnteringState(gameSession, players));
        }
    }

    private void announcePlayerDismissal(String playerName) {
        broadcast(Text.of(playerName + " left the game." +
                "(" + players.size() + "/" + matchRules.getTotalCapacity() + ")"));
    }

    private List<Team> partition() {
        List<Team> teams = new ArrayList<>();
        gameSession.getArena().getStartPoints()
                .limit(matchRules.getTeamCount())
                .forEach(point -> teams.add(new Team(point)));
        Iterator<Player> playersItr = players.iterator();
        int playersLeft = players.size();
        while (playersItr.hasNext()) {
            int teamNum = playersLeft % teams.size();
            teams.get(teamNum).add(playersItr.next());
            playersLeft--;
        }
        return teams;
    }

    private static final class Messages {
        static final Text WAITING_FOR_PLAYERS = Text
                .builder("Waiting for more players to join...")
                .color(TextColors.GOLD).build();
    }
}
