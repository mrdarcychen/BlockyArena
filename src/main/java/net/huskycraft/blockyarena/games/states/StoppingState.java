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

package net.huskycraft.blockyarena.games.states;

import net.huskycraft.blockyarena.games.Game;
import net.huskycraft.blockyarena.games.Team;
import net.huskycraft.blockyarena.games.Timer;
import net.huskycraft.blockyarena.utils.Gamer;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Title;

import java.util.List;

public class StoppingState extends MatchState {

    public StoppingState(Game game, List<Gamer> gamers, Team winner, List<Team> losers) {
        super(game, gamers);
        Title victory = Title.builder()
                .title(Text.builder("VICTORY!")
                        .color(TextColors.GOLD)
                        .style(TextStyles.BOLD)
                        .build())
                .subtitle(Text.of(winner.toString() + " won the game."))
                .fadeIn(1).stay(60).fadeOut(2)
                .build();
        Title gameOver = Title.builder()
                .title(Text.builder("GAME OVER!")
                        .color(TextColors.RED)
                        .style(TextStyles.BOLD)
                        .build())
                .subtitle(Text.of(winner.toString() + " won the game."))
                .fadeIn(1).stay(60).fadeOut(2)
                .build();

        winner.broadcast(victory);
        winner.getGamers().iterator().next().getPlayer().playSound(SoundTypes.ENTITY_PLAYER_LEVELUP, winner.getGamers().iterator().next().getPlayer().getHeadRotation(), 100);
        losers.forEach(it -> {
            it.broadcast(gameOver);
            it.getGamers().forEach(gamer -> {
                gamer.getPlayer().playSound(SoundTypes.BLOCK_GLASS_BREAK, gamer.getPlayer().getHeadRotation(), 100);
            });
        });

        System.out.println("IN STOPPING STATE");
        gamers.forEach(it -> System.out.println(it.getName() + " is in list"));
        new Timer(5, (tMinus) -> {
            if (tMinus == 0) {
                game.setMatchState(new LeavingState(game, gamers));
            }
        });
    }
}
