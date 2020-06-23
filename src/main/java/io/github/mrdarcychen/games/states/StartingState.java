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

package io.github.mrdarcychen.games.states;

import io.github.mrdarcychen.games.Game;
import io.github.mrdarcychen.games.Team;
import io.github.mrdarcychen.games.Timer;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StartingState extends MatchState {

    private final Timer timer;

    public StartingState(Game game, List<Player> players, int countdown) {
        super(game, players);
        timer = new Timer(countdown, tMinus -> {
            if (tMinus == 0) {
                game.setMatchState(new PlayingState(game, players, partition()));
                return;
            }
            Title title = Title.builder().title(Text.of(tMinus)).fadeIn(2)
                    .fadeOut(2).stay(16).build();
            players.forEach(player -> {
                player.sendTitle(title);
                player.playSound(SoundTypes.BLOCK_NOTE_HAT, player.getLocation().getPosition(), 100);
            });
        });
    }

    @Override
    public void dismiss(Player player) {
        super.dismiss(player);
        players.remove(player);
        announcePlayerDismissal(player.getName());
        // if fall below min requirement, new entering state
        if (players.size() <= teamMode.getTotalCapacity()) {
            timer.cancel();
            broadcast(Text.of("Waiting for more players to join ..."));
            game.setMatchState(new EnteringState(game, players));
        }
    }

    private void announcePlayerDismissal(String playerName) {
        broadcast(Text.of(playerName + " left the game." +
                "(" + players.size() + "/" + teamMode.getTotalCapacity() + ")"));
    }

    private List<Team> partition() {
        List<Team> teams = new ArrayList<>();
        game.getArena().getStartPoints()
                .limit(teamMode.getTeamCount())
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
}
