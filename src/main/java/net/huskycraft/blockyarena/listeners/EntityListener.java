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

package net.huskycraft.blockyarena.listeners;

import com.flowpowered.math.vector.Vector3d;
import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.games.Game;
import net.huskycraft.blockyarena.games.GameState;
import net.huskycraft.blockyarena.games.GamersManager;
import net.huskycraft.blockyarena.managers.ConfigManager;
import net.huskycraft.blockyarena.utils.DamageData;
import net.huskycraft.blockyarena.utils.Gamer;
import net.huskycraft.blockyarena.utils.GamerStatus;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class EntityListener {


    public EntityListener() {
    }
    
    @Listener
    public void onReload(GameReloadEvent event)
    {
    	ConfigManager.getInstance().reloadConfiguration();
    }
    
	@Listener
	public void onCommand(SendCommandEvent event, @First Player p) 
	{
		
		Gamer gamer = GamersManager.getGamer(p.getUniqueId()).get();

		Game game = gamer.getGame();
		
		String command = event.getCommand();

		if (game != null)
		{
				//The player is currently playing !
				if (gamer.getStatus() == GamerStatus.PLAYING)
				{
						//If the player doesnt have the permission to bypass !!
						if(!p.hasPermission("blockyarena.bypass.command"))
						{
							if (!(command.equalsIgnoreCase("ba") || command.equalsIgnoreCase("arena") || command.equalsIgnoreCase("blockyarena")))
							{
							event.setCancelled(true);
							p.sendMessage(ChatTypes.CHAT,
									(Text) Text.builder("[BLOCKYARENA] Only command you can do is : /ba or /arena or /blockyarena !")
											.color(TextColors.RED).build());
							}
							else
							{
								//ba or arena or blockyarena command is issued ! So we can move on and proceed event !
							}
						}
						else
						{
							//Player can bypass permission so we move on
						}
					
				}
		}
	}

    @Listener
    public void onDamageEntity(DamageEntityEvent event) {
        if (event.getTargetEntity() instanceof Player) {
            Player player = (Player) event.getTargetEntity();
            Gamer victim = GamersManager.getGamer(player.getUniqueId()).get();
            // if the victim is in a game, proceed analysis
            if (victim.getStatus() == GamerStatus.PLAYING) {
                Game game = victim.getGame();
                DamageData damageData = new DamageData(BlockyArena.getInstance(), victim, event.getCause());
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
