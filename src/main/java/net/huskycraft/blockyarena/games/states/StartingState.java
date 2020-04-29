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

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.games.Game;
import net.huskycraft.blockyarena.games.Team;
import net.huskycraft.blockyarena.utils.Gamer;

public class StartingState extends MatchState {

    private Task task;
    private int tMinus;

    public StartingState(Game game, List<Gamer> gamers, int countdown) {
        super(game);
        this.gamers = gamers;
        tMinus = countdown;
        task = Task.builder()
                .interval(1, TimeUnit.SECONDS)
                .execute(() -> {
                    if (tMinus == 0) {
                        task.cancel();
                        Team teamA = new Team(game.getArena().getTeamSpawnA(), game);
                        Team teamB = new Team(game.getArena().getTeamSpawnB(), game);
                        Iterator<Gamer> gamersItr = gamers.iterator();
                        while (gamersItr.hasNext()) {
                            teamA.add(gamersItr.next());
                            teamB.add(gamersItr.next());
                        }
                        game.setMatchState(new PlayingState(game, teamA, teamB));
                    } else {
                        Title title = Title.builder()
                                .title(Text.of(tMinus)).fadeIn(2).fadeOut(2).stay(16).build();
                        for (Gamer gamer : gamers) {
                            Player player = gamer.getPlayer();
                            player.sendTitle(title);
                            player.playSound(SoundTypes.BLOCK_DISPENSER_DISPENSE, player.getHeadRotation(), 100);
                        }
                        System.out.println("TMINUS: " + tMinus); // TODO: DELETE
                        tMinus--;
                    }
                }).submit(BlockyArena.getInstance());
    }

    @Override
    public void dismiss(Gamer gamer) {
        super.dismiss(gamer);
        // if fall below min requirement, new entering state
        if (gamers.size() <= game.getTeamMode().getCapacity() * 2) {
            task.cancel();
            broadcast(Text.of("Waiting for more players to join ..."));
            game.setMatchState(new EnteringState(game));
        }
    }
}
