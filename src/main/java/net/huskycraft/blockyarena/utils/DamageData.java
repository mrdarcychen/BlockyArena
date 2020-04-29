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

package net.huskycraft.blockyarena.utils;

import net.huskycraft.blockyarena.games.GamersManager;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.BlockDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;

import java.util.Optional;

/**
 * A DamageData represents the information about a specific damage dealt on a Gamer.
 */
public class DamageData {

    private final Gamer victim;
    private Optional<Gamer> attacker;
    private DamageType damageType;

    public DamageData(Gamer victim, Cause cause) {
        this.victim = victim;
        Optional<DamageSource> optDamageSourcecause = cause.first(DamageSource.class);
        if (optDamageSourcecause.isPresent()) {
            DamageSource damageSource = optDamageSourcecause.get();
            damageType = damageSource.getType();

            if (damageSource instanceof IndirectEntityDamageSource) {
                IndirectEntityDamageSource indirectEntityDamageSource =
                        (IndirectEntityDamageSource) damageSource;
                Entity indirectSource = indirectEntityDamageSource.getIndirectSource();
                if (indirectSource instanceof Player) {
                    Player player = (Player) indirectSource;

                    attacker = Optional.of(GamersManager.getGamer(player.getUniqueId()).get());
                }
//                Optional<Player> owner = cause.getContext().get(EventContextKeys.OWNER).get()
//                        .getPlayer();

            } else if (damageSource instanceof EntityDamageSource) {
                EntityDamageSource entityDamageSource = (EntityDamageSource) damageSource;
                Entity directSource = entityDamageSource.getSource();
                if (directSource instanceof Player) {
                    Player player = (Player) directSource;
                    attacker = Optional.of(GamersManager.getGamer(player.getUniqueId()).get());
                }
            } else if (damageSource instanceof BlockDamageSource) {
                // TODO

            }
        }
    }

    public String getDeathMessage() {
        if (damageType.getName().equalsIgnoreCase("attack")) {
            return victim.getName() + " was killed by " + attacker.get().getName() + ".";
        } else if (damageType.getName().equalsIgnoreCase("fall")) {
            return victim.getName() + " fell from a high place.";
        } else if (damageType.getName().equalsIgnoreCase("fire")) {
            return victim.getName() + " was burned to death.";
        } else if (damageType.getName().equalsIgnoreCase("magic")) {
            return victim.getName() + " was killed by " + attacker.get().getName() + "'s magic.";
        } else if (damageType.getName().equalsIgnoreCase("void")) {
            return victim.getName() + " fell into the void.";
        }
        return victim.getName() + " died.";
    }

    public Optional<Gamer> getAttacker() {
        return attacker;
    }

    public Gamer getVictim() {
        return victim;
    }

    public DamageType getDamageType() {
        return damageType;
    }
}