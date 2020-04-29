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
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.World;

public class PlayingState extends MatchState {

    private Team teamA;
    private Team teamB;

    public PlayingState(Game game, Team teamA, Team teamB) {
        super(game);
        this.teamA = teamA;
        this.teamB = teamB;
        teamA.sendAllToSpawn();
        teamB.sendAllToSpawn();
    }

    @Override
    public void dismiss(Gamer gamer) {
        super.dismiss(gamer);
        eliminate(gamer, Text.of(gamer.getPlayer().getName() + " has left the game."));
    }

    @Override
    public void eliminate(Gamer gamer, Text cause) {
        broadcast(cause);
        Text deathText = Text.builder("YOU DIED!")
                .color(TextColors.RED).build();
        Title deathTitle = Title.builder()
                .title(deathText).fadeOut(2).stay(16).build();
        gamer.getPlayer().sendTitle(deathTitle);
        gamer.spectate(game);
        if (teamA.hasGamerLeft() && !teamB.hasGamerLeft()) {
            game.setMatchState(new StoppingState(game, teamA, teamB));
        } else if (teamB.hasGamerLeft() && !teamA.hasGamerLeft()) {
            game.setMatchState(new StoppingState(game, teamB, teamA));
        }
    }

    public void analyze(DamageEntityEvent event, DamageData damageData) {
        Gamer victim = damageData.getVictim();
        if (damageData.getDamageType().getName().equalsIgnoreCase("void")) {
            event.setCancelled(true);
            spawnLightningOn(damageData.getVictim());
            eliminate(victim, Text.of(damageData.getDeathMessage()));
            return;
        }
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
        Entity lightning = extent.createEntity(EntityTypes.LIGHTNING, position.add(0, 1, 0));
        lightning.damage(0.0, DamageSources.GENERIC);
        extent.spawnEntity(lightning);
    }

    private boolean areTeammates(Gamer a, Gamer b) {
        Team[] teams = { teamA, teamB };
        for (Team team : teams) {
            if (team.contains(a) && team.contains(b)) {
                return true;
            }
        }
        return false;
    }
}
