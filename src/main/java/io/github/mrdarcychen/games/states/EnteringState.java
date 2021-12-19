/*
 * Copyright 2021 Darcy Chen <mrdarcychen@gmail.com> and the contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.github.mrdarcychen.games.states;

import io.github.mrdarcychen.games.GameSession;
import io.github.mrdarcychen.utils.DamageData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.function.BiFunction;

public class EnteringState extends MatchState {

    public EnteringState(GameSession gameSession, List<Player> players) {
        super(gameSession, players);
    }

    @Override
    public void recruit(Player player) {
        if (fullCapacityReached()) {
            notifyPlayerFailedToJoin(player);
            return;
        }
        super.recruit(player);
        broadcastRecruitMessage(player);
        if (fullCapacityReached()) {
            proceedToStartingState();
        }
    }

    private void proceedToStartingState() {
        gameSession.setMatchState(new StartingState(gameSession, players));
    }

    private void notifyPlayerFailedToJoin(Player player) {
        player.sendMessage(ChatTypes.ACTION_BAR, Messages.UNABLE_TO_JOIN);
    }

    private boolean fullCapacityReached() {
        return players.size() == matchRules.getTotalCapacity();
    }

    private void broadcastRecruitMessage(Player player) {
        broadcast(Messages.BROADCAST_JOIN.apply(player.getName(), getCapacityIndication()));
        player.sendMessage(ChatTypes.ACTION_BAR, Messages.PICK_KIT);
    }

    @Override
    public void dismiss(Player player) {
        super.dismiss(player);
        // if no one is left, cancel timer and go directly to leaving
        if (players.isEmpty()) {
            gameSession.setMatchState(new LeavingState(gameSession, players));
        }
    }

    public void analyze(DamageEntityEvent event, DamageData damageData) {
        super.analyze(event, damageData);
    }

    private static final class Messages {
        static final Text UNABLE_TO_JOIN = Text
                .builder("All available game sessions are full at the moment.")
                .color(TextColors.GOLD).build();
        static final Text PICK_KIT = Text
                .builder("Pick a kit with ")
                .append(Text.builder("/ba kit <name>").color(TextColors.GREEN).build())
                .build();
        static final BiFunction<String, String, Text> BROADCAST_JOIN = (name, indicator) -> Text
                .builder(name + " has joined the game. " + indicator)
                .color(TextColors.GREEN).build();
    }
}
