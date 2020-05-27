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

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import net.huskycraft.blockyarena.games.Game;
import net.huskycraft.blockyarena.games.GamersManager;
import net.huskycraft.blockyarena.utils.DamageData;
import net.huskycraft.blockyarena.utils.Gamer;
import net.huskycraft.blockyarena.utils.GamerStatus;

import java.util.Optional;

public class EntityListener {


    public EntityListener() {
    }
    
	@Listener
	public void onCommand(SendCommandEvent event, @First Player p) 
	{
		
		Gamer gamer = GamersManager.getGamer(p.getUniqueId()).get();

		Optional<Game> optGame = gamer.getGame();
		
		String command = event.getCommand();

		if (optGame.isPresent())
		{
				//The player is currently playing !
				if (gamer.getGame().isPresent())
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
					
				}
		}
	}

    @Listener
    public void onDamageEntity(DamageEntityEvent event) {
        if (event.getTargetEntity() instanceof Player) {
            Player player = (Player) event.getTargetEntity();
            Gamer victim = GamersManager.getGamer(player.getUniqueId()).get();
            // if the victim is in a game, proceed analysis
            Optional<Game> optGame = victim.getGame();
            if (optGame.isPresent()) {
                DamageData damageData = new DamageData(victim, event.getCause());
                optGame.get().analyze(event, damageData);
            }
        }
    }
}
