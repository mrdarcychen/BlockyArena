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

import io.github.mrdarcychen.games.GameSession;
import io.github.mrdarcychen.games.PlayerManager;
import io.github.mrdarcychen.utils.DamageData;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlayingState extends MatchState {

    private final List<Team> teams;

    public PlayingState(GameSession gameSession, List<Player> players, List<Team> teams) {
        super(gameSession, players);
        this.teams = teams;
        teams.forEach(Team::sendAllToSpawn);
    }

    @Override
    public void dismiss(Player player) {
        players.remove(player);
        announcePlayerDismissal(player.getName());
        eliminate(player, Text.of(player.getName() + " has left the game."));
        super.dismiss(player);
    }

    private void announcePlayerDismissal(String playerName) {
        broadcast(Text.of(playerName + " left the game." +
                "(" + players.size() + "/" + matchRules.getTotalCapacity() + ")"));
    }

    @Override
    public void eliminate(Player player, Text cause) {
        super.eliminate(player, cause);
        teams.forEach(it -> it.eliminate(player));
        List<Team> teamsAlive = teams.stream().filter(Team::hasGamerLeft).collect(Collectors.toList());
        if (teamsAlive.size() == 1) {
            Team winner = teamsAlive.get(0);
            List<Team> losers = teams.stream().filter(it -> it != winner).collect(Collectors.toList());
            gameSession.setMatchState(new StoppingState(gameSession, players, winner, losers));
        }
    }

    public void analyze(DamageEntityEvent event, DamageData damageData) {
        Player victim = damageData.getVictim();
        // if fell into the void, eliminate
        if (damageData.getDamageType().getName().equalsIgnoreCase("void")) {
            event.setCancelled(true);
            showDeathEffect(damageData.getVictim());
            eliminate(victim, Text.of(damageData.getDeathMessage()));
            return;
        }
        // if attacked by teammates, cancel
        if (damageData.getAttacker().isPresent()) {
            Player attacker = damageData.getAttacker().get();
            if (areTeammates(victim, attacker)) {
                attacker.playSound(SoundTypes.ITEM_SHIELD_BLOCK,
                        attacker.getLocation().getPosition(), 50);
                event.setCancelled(true);
            }
            Optional<GameSession> optGame = PlayerManager.getGame(attacker.getUniqueId());
            if (!optGame.isPresent() || !optGame.get().equals(gameSession)) {
                event.setCancelled(true);
            }
            if (teams.stream().anyMatch(team -> team.isEliminated(attacker))) {
                event.setCancelled(true);
            }
        }
        if (event.willCauseDeath()) {
            event.setCancelled(true);
            showDeathEffect(victim);
            damageData.getAttacker().ifPresent(it -> it.playSound(
                    SoundTypes.ENTITY_BAT_DEATH, it.getLocation().getPosition(), 100
            ));
            // eliminate the victim
            eliminate(victim, Text.of(damageData.getDeathMessage()));
        }
    }

    private void showDeathEffect(Player player) {
        ParticleEffect effect = ParticleEffect.builder()
                .type(ParticleTypes.FIREWORKS_SPARK)
                .quantity(200)
                .build();
        player.playSound(SoundTypes.ENTITY_BAT_DEATH, player.getLocation().getPosition(), 100);
        player.spawnParticles(effect, player.getLocation().getPosition());
    }

    private boolean areTeammates(Player a, Player b) {
        for (Team team : teams) {
            if (team.contains(a) && team.contains(b)) {
                return true;
            }
        }
        return false;
    }
}
