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
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.weather.Lightning;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayingState extends MatchState {

    private List<Team> teams;

    public PlayingState(Game game, List<Gamer> gamers, List<Team> teams) {
        super(game, gamers);
        System.out.println("In Playing State");
        this.teams = teams;
        teams.forEach(Team::sendAllToSpawn);
    }

    @Override
    public void dismiss(Gamer gamer) {
        super.dismiss(gamer);
        gamers.remove(gamer);
        broadcast(Text.of(gamer.getName() + " left the game." +
                "(" + gamers.size() + "/" + game.getTotalCapacity() + ")"));
        eliminate(gamer, Text.of(gamer.getPlayer().getName() + " has left the game."));
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
            spawnLightningOn(damageData.getVictim());
            eliminate(victim, Text.of(damageData.getDeathMessage()));
            return;
        }
        // if attacked by teammates, cancel
        if (damageData.getAttacker().isPresent()) {
            if (areTeammates(victim, damageData.getAttacker().get())) {
                event.setCancelled(true);
            }
        }
        if (event.willCauseDeath()) {
            event.setCancelled(true);
            spawnLightningOn(victim);
            // eliminate the victim
            eliminate(victim, Text.of(damageData.getDeathMessage()));
        }
    }

    private void spawnLightningOn(Gamer gamer) {
        World extent = gamer.getPlayer().getLocation().getExtent();
        Vector3d position = gamer.getPlayer().getLocation().getPosition();
        Lightning lightning = (Lightning) extent.createEntity(EntityTypes.LIGHTNING, position);
        lightning.setEffect(true); // TODO: cannot make lightning act as an effect
        extent.spawnEntity(lightning);
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
