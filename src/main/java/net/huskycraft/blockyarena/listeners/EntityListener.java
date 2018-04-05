/*
 * This file is part of BlockyArena, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018 HuskyCraft <https://www.huskycraft.net>
 * Copyright (c) 2018 Darcy-Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.huskycraft.blockyarena.listeners;

import com.flowpowered.math.vector.Vector3d;
import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.games.Game;
import net.huskycraft.blockyarena.games.GameState;
import net.huskycraft.blockyarena.managers.GamersManager;
import net.huskycraft.blockyarena.utils.DamageData;
import net.huskycraft.blockyarena.utils.Gamer;
import net.huskycraft.blockyarena.utils.GamerStatus;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.weather.WeatherEffect;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class EntityListener {

    public static BlockyArena plugin;

    public EntityListener(BlockyArena plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onDamageEntity(DamageEntityEvent event) {
        if (event.getTargetEntity() instanceof Player) {
            Player player = (Player) event.getTargetEntity();
            Gamer victim = GamersManager.getGamer(player.getUniqueId()).get();
            // if the victim is in a game, proceed analysis
            if (victim.getStatus() == GamerStatus.PLAYING) {
                Game game = victim.getGame();
                DamageData damageData = new DamageData(plugin, victim, event.getCause());
                Optional<Gamer> optAttacker = damageData.getAttacker();
                if (game.getGameState() != GameState.STARTED) {
                    if (damageData.getDamageType().getName().equalsIgnoreCase("void")) {
                        victim.spawnAt(game.getArena().getLobbySpawn());
                    }
                    event.setCancelled(true);
                    return;
                }
                // if the game is in grace period or the event will cause death, set cancelled
                if (damageData.getDamageType().getName().equalsIgnoreCase("void")) {
                    event.setCancelled(true);
                    spawnLightning(victim);
                    victim.getGame().eliminate(victim, Text.of(damageData.getDeathMessage()));
                    return;
                }
                if (optAttacker.isPresent()) {
                    if (victim.getGame().getTeam(victim).contains(optAttacker.get())) {
                        event.setCancelled(true);
                    }
                }
                if (event.willCauseDeath()) {
                    event.setCancelled(true);
                    // spawn light bolt
                    spawnLightning(victim);
                    // eliminate the victim
                    victim.getGame().eliminate(victim, Text.of(damageData.getDeathMessage()));

                }
            }
        }
    }
    private void spawnLightning(Gamer gamer) {
        World extent = gamer.getPlayer().getLocation().getExtent();
        Vector3d position = gamer.getPlayer().getLocation().getPosition();
        Entity lightning = extent.createEntity(EntityTypes.LIGHTNING, position.add(0, 1, 0));
        lightning.damage(0.0, DamageSources.GENERIC);
        extent.spawnEntity(lightning);
    }
}
