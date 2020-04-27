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

package net.huskycraft.blockyarena.games;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.arenas.Arena;
import net.huskycraft.blockyarena.arenas.ArenaState;
import net.huskycraft.blockyarena.managers.ConfigManager;
import net.huskycraft.blockyarena.utils.Gamer;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager.StackFrame;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkShapes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.Color;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A Game represents a specific session dedicated to a single duel.
 */
public class Game {

    protected Arena arena; // the Arena associated with this Game
    protected List<Gamer> gamers; // the list of Gamers in this Game with corresponding connection status
    protected TeamMode teamMode;
    protected GameState gameState;
    protected Team teamA, teamB;
    private Task timer;



    /**
     * Constructs a Game with the given team mode and an arena.
     * The GameState is set to RECRUITING by default.
     * @param arena an enabled arena
     */
    public Game(TeamMode teamMode, Arena arena, int numTeams) {
        this.teamMode = teamMode;
        this.arena = arena;
        gamers = new ArrayList<>();
        gameState = GameState.RECRUITING;
        arena.setState(ArenaState.OCCUPIED);
    }

    /**
     * Adds the given Gamer to this Game. Assumes that the given Gamer has already prepared for joining a Game by
     * saving properties and updating status.
     *
     * @param gamer the Gamer to be added to this Game
     */
    public void add(Gamer gamer) {
        // reject if the game reaches maximum capacity or is currently not recruiting
        if (gamers.size() == teamMode.getCapacity() * 2 || gameState != GameState.RECRUITING) {
            gamer.getPlayer().sendMessage(Text.of("Unable to join the game at this time."));
            return;
        }
        //If a player just joined the session, and the arena is full, we won't broadcast.
        else
        {
        	checkForBroadcast(gamer);
        }
        gamers.add(gamer);
        broadcast(Text.of(gamer.getName() + " joined the game. " +
                "(" + gamers.size() + "/" + teamMode.getCapacity() * 2 + ")"));
        inspect();
        
        
    }

    /*
     * Check for config and game type before broadcasting
     */
    private void checkForBroadcast(Gamer gamer) {
    	
    	//if config allow you to broadcast message when arena is created
    	if(ConfigManager.getInstance().allowBroadcast())
    	{
    		switch(teamMode)
    		{
			case DOUBLES:
				broadcastToAllPlayers((Text)Text.builder("[BlockyArena] " + gamer.getName() +" joins a doubles arena ! type /ba doubles to join !").color(TextColors.GOLD).build());
				break;
			case SOLO:
				broadcastToAllPlayers((Text)Text.builder("[BlockyArena] " + gamer.getName() +" joins a solo arena ! type /ba solo to join !").color(TextColors.GOLD).build());
				break;
			default:
				break;
    		}
    		
    	}
		
	}

	/**
     * Removes the given gamer from this Game.
     *
     * @param gamer the gamer to be removed from this Game
     */
    public void remove(Gamer gamer) {
        // if the game is in progress, eliminate before removing the player
        if (gameState == GameState.RECRUITING || gameState == GameState.STARTING) {
            gamers.remove(gamer);
            broadcast(Text.of(gamer.getName() + " left the game." +
                    "(" + gamers.size() + "/" + teamMode.getCapacity() * 2 + ")"));
            inspect();
        } else if (gameState == GameState.STARTED) {
            eliminate(gamer, Text.of(gamer.getPlayer().getName() + " disconnected."));
        }
    }

    /**
     * Eliminates the given gamer from this Game.
     *
     * @param gamer the Gamer to be eliminated
     * @param cause the reason of elimination
     */
    public void eliminate(Gamer gamer, Text cause) {
        broadcast(cause);
        Text deathText = Text.builder("YOU DIED!")
                .color(TextColors.RED).build();
        Title deathTitle = Title.builder()
                .title(deathText).fadeOut(2).stay(16).build();
        gamer.getPlayer().sendTitle(deathTitle);
        gamer.spectate(this);
        checkStoppingCondition();
    }

    /**
     * Inspects the current condition of the Game. The starting countdown of this game is triggered if the number of
     * players matches the starting condition of the associated game mode, otherwise the countdown will be canceled.
     * This Game is terminated if there is no player active in the session.
     */
    private void inspect() {
        // terminate if there is no active gamer
        if (gamers.isEmpty()) {
            terminate();
            return;
        }
        boolean canSolo = teamMode.getCapacity() * 2 == gamers.size();
        boolean canDoubles = teamMode.getCapacity() * 2 == gamers.size();
        if (canSolo || canDoubles) {
            teamA = new Team(arena.getTeamSpawnA(), this);
            teamB = new Team(arena.getTeamSpawnB(), this);
            Iterator<Gamer> gamersItr = gamers.iterator();
            while (gamersItr.hasNext()) {
                teamA.add(gamersItr.next());
                teamB.add(gamersItr.next());
            }
            gameState = GameState.STARTING;
            startingCountdown(ConfigManager.getInstance().getLobbyCountdown());
        } else if (timer != null) {
            timer.cancel();
            gameState = GameState.RECRUITING;
            broadcast(Text.of("Waiting for more players to join ..."));
            
        }
        //When player is in the lobby session and join a session
        else {
        	
        	
        }

    }
    
    /**
     * Checks to see if this Game should stop based on the current condition.
     *
     * A Game should stop when either one of the Team has no player alive.
     */
    private void checkStoppingCondition() {
        if (teamA.hasGamerLeft() && !teamB.hasGamerLeft()) {
            onGameStopping(teamA, teamB);
        } else if (teamB.hasGamerLeft() && !teamA.hasGamerLeft()) {
            onGameStopping(teamB, teamA);
        }
    }

    /**
     * Executed as soon as the Game has started.
     */
    public void onGameStarted() {
        teamA.sendAllToSpawn();
        teamB.sendAllToSpawn();
        gameState = GameState.STARTED;
    }

    /**
     * Executed when the Game is stopping.
     */
    public void onGameStopping(Team winner, Team loser) {
        gameState = GameState.STOPPING;

        Title victory = Title.builder()
                .title(Text.builder("VICTORY!")
                        .color(TextColors.GOLD)
                        .style(TextStyles.BOLD)
                        .build())
                .subtitle(Text.of(winner.toString() + " won the game."))
                .fadeIn(1).stay(60).fadeOut(2)
                .build();
        Title gameOver = Title.builder()
                .title(Text.builder("GAME OVER!")
                        .color(TextColors.RED)
                        .style(TextStyles.BOLD)
                        .build())
                .subtitle(Text.of(winner.toString() + " won the game."))
                .fadeIn(1).stay(60).fadeOut(2)
                .build();

        winner.broadcast(victory);
        loser.broadcast(gameOver);
        
        
        /*
         * FEEL FREE TO REMOVE IF NEEDED
         * 
         */
        
        List<Color> colors = Lists.newArrayList(Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_CYAN, Color.DARK_GREEN, Color.DARK_MAGENTA,
                Color.GRAY, Color.GREEN, Color.LIME, Color.MAGENTA, Color.NAVY, Color.PINK, Color.PURPLE, Color.RED, Color.WHITE, Color.YELLOW);
        Collections.shuffle(colors);
        
        FireworkEffect fireworkEffect = FireworkEffect.builder()
                .colors(colors.get(0), colors.get(1), colors.get(2))
                .shape(FireworkShapes.STAR)
                .build();

      Player p = winner.getGamers().iterator().next().getPlayer();
        
        
        Entity firework = p.getWorld().createEntity(EntityTypes.FIREWORK,p.getLocation().getPosition());
        
        firework.offer(Keys.FIREWORK_EFFECTS, Lists.newArrayList(fireworkEffect));
        firework.offer(Keys.FIREWORK_FLIGHT_MODIFIER, 2);

        
        try (StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);

        	   p.getWorld().spawnEntity(firework);
           
            
        }
        
        Task.builder().execute(() -> terminate()).delay(4, TimeUnit.SECONDS).submit(BlockyArena.getInstance());
    }

    private void startingCountdown(int second) {
        if (second == 0) {
            timer.cancel();
            onGameStarted();
        } else {
            Title title = Title.builder()
                    .title(Text.of(second)).fadeIn(2).fadeOut(2).stay(16).build();
            for (Gamer gamer : gamers) {
                Player player = gamer.getPlayer();
                player.sendTitle(title);
                player.playSound(SoundTypes.BLOCK_DISPENSER_DISPENSE, player.getHeadRotation(), 100);
            }
            timer = Task.builder()
                    .execute(() -> startingCountdown(second - 1)).delay(1, TimeUnit.SECONDS).submit(BlockyArena.getInstance());
        }
    }

    /**
     * Gets the state of the Game.
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Gets the mode of the team.
     */
    public TeamMode getTeamMode() {
        return teamMode;
    }

    /**
     * Broadcasts the given message to all connected Gamers in this Game.
     *
     * @param msg the message to be delivered
     */
    public void broadcast(Text msg) {
        for (Gamer gamer : gamers) {
            // broadcast if the Gamer still has connection
            if (gamer.getGame() == this) {
                gamer.getPlayer().sendMessage(msg);
            }
        }
    }
    
    /**
     * Broadcasts the given message to all players in the server
     *
     * @param msg the message to be delivered
     */
	public void broadcastToAllPlayers(Text msg) {
		MessageChannel.TO_ALL.send(msg);
	}

    /**
     * Terminates this Game permanently.
     */
    public void terminate() {
        for (Gamer gamer : gamers) {
            // remove if the gamer still has connection
            if (gamer.getGame() == this) {
                gamer.quit();
            }
        }
        arena.setState(ArenaState.AVAILABLE);
        BlockyArena.getGameManager().remove(this);
    }

    public Arena getArena() {
        return arena;
    }

    public Team getTeam(Gamer gamer) {
        if (teamA.contains(gamer)) {
            return teamA;
        } else if (teamB.contains(gamer)) {
            return teamB;
        }
        return null;
    }
}