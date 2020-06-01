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

import com.flowpowered.math.vector.Vector3d;
import net.huskycraft.blockyarena.games.Game;
import net.huskycraft.blockyarena.games.Team;
import net.huskycraft.blockyarena.utils.DamageData;
import net.huskycraft.blockyarena.utils.Gamer;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.weather.Lightning;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlayingState extends MatchState {

    private List<Team> teams;

    public PlayingState(Game game, List<Gamer> gamers, List<Team> teams) {
        super(game, gamers);
        this.teams = teams;
        teams.forEach(Team::sendAllToSpawn);
    }

    @Override
    public void dismiss(Gamer gamer) {
        gamers.remove(gamer);
        broadcast(Text.of(gamer.getName() + " left the game." +
                "(" + gamers.size() + "/" + teamMode.getTotalCapacity() + ")"));
        eliminate(gamer, Text.of(gamer.getPlayer().getName() + " has left the game."));
        super.dismiss(gamer);
    }

    @Override
    public void eliminate(Gamer gamer, Text cause) {
        super.eliminate(gamer, cause);
        teams.forEach(it -> it.eliminate(gamer));
        List<Team> teamsAlive = teams.stream().filter(Team::hasGamerLeft).collect(Collectors.toList());
        if (teamsAlive.size() == 1) {
            Team winner = teamsAlive.get(0);
            List<Team> losers = teams.stream().filter(it -> it != winner).collect(Collectors.toList());
            game.setMatchState(new StoppingState(game, gamers, winner, losers));
        }
    }

    public void analyze(DamageEntityEvent event, DamageData damageData) {
        Gamer victim = damageData.getVictim();
        // if fell into the void, eliminate
        if (damageData.getDamageType().getName().equalsIgnoreCase("void")) {
            event.setCancelled(true);
            showDeathEffect(damageData.getVictim());
            eliminate(victim, Text.of(damageData.getDeathMessage()));
            return;
        }
        // if attacked by teammates, cancel
        if (damageData.getAttacker().isPresent()) {
            Gamer attacker = damageData.getAttacker().get();
            if (areTeammates(victim, attacker)) {
                attacker.getPlayer().playSound(SoundTypes.ITEM_SHIELD_BLOCK,
                        attacker.getPlayer().getLocation().getPosition(), 50);
                event.setCancelled(true);
            }
            Optional<Game> optGame = attacker.getGame();
            if (!optGame.isPresent() || !optGame.get().equals(game)) {
                event.setCancelled(true);
            }
            if (teams.stream().anyMatch(team -> team.isEliminated(attacker))) {
                event.setCancelled(true);
            }
        }
        if (event.willCauseDeath()) {
            event.setCancelled(true);
            showDeathEffect(victim);
            // eliminate the victim
            eliminate(victim, Text.of(damageData.getDeathMessage()));
        }
    }

    private void showDeathEffect(Gamer gamer) {
        ParticleEffect effect = ParticleEffect.builder()
                .type(ParticleTypes.FIREWORKS_SPARK)
                .quantity(200)
                .build();
        Player player = gamer.getPlayer();
        player.playSound(SoundTypes.ENTITY_BAT_DEATH, player.getLocation().getPosition(), 100);
        player.spawnParticles(effect, player.getLocation().getPosition());
    }

    private boolean areTeammates(Gamer a, Gamer b) {
        for (Team team : teams) {
            if (team.contains(a) && team.contains(b)) {
                return true;
            }
        }
        return false;
    }
}
