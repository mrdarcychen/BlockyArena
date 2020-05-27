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

import java.util.Optional;
import java.util.UUID;

import net.huskycraft.blockyarena.arenas.Arena;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;

import net.huskycraft.blockyarena.arenas.SpawnPoint;
import net.huskycraft.blockyarena.games.Game;

/**
 * A Gamer object stores a player's gaming profile.
 */
public class Gamer {

    private final UUID uniqueId;
    private boolean isOnline;
    private String name;
    private Player player; // the player instance in which this Gamer profile is associated with

    private Game game; // the Game session this player is currently in

    private Location<World> savedLocation; // the saved location of the player for record
    private Kit kit; // the original inventory of the player for record

    /**
     * Constructs a unique Gamer profile for the given Player.
     *
     * @param uniqueId the {@link UUID} of the associated {@link Player}
     */
    public Gamer(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * Saves the current inventory of this Gamer.
     */
    public void saveInventory() {
        kit = new Kit(player, null);
    }

    /**
     * Retrieves the most recently saved inventory of this Gamer. This will replace the current inventory of the Player.
     */
    public void retrieveInventory() {
        player.getInventory().clear();
        kit.equip(player);
    }

    /**
     * Spawns this Gamer at the given Spawn point.
     *
     * @param spawn the Spawn point where this Gamer is going to be at
     */
    public void spawnAt(SpawnPoint spawn) {
        player.setVelocity(new Vector3d(0.0, 0.0, 0.0)); // TODO: doesn't work
        player.setLocationAndRotation(spawn.getSpawnLocation(), spawn.getSpawnRotation());
    }

    /**
     * Sets the location of this Gamer.
     *
     * @param location the location to set
     */
    public void setLocation(Location location) {
        player.setVelocity(new Vector3d(0.0, 0.0, 0.0)); // TODO: doesn't work
        player.setLocation(location);
    }

    /**
     * Saves the current location of this Gamer for record.
     */
    public void saveLocation() {
        this.savedLocation = player.getLocation();
    }

    /**
     * Gets the most recently saved location of this Gamer.
     *
     * @return the most recently saved location of this Gamer
     */
    public Location getSavedLocation() {
        return savedLocation;
    }

    /**
     * Gets the Game this Gamer is currently in.
     *
     * @return the Game this Gamer is currently in
     */
    public Optional<Game> getGame() {
        return Optional.ofNullable(game);
    }

    /**
     * Joins this Gamer to the given Game.
     *
     * @param game the Game in which the Gamer is about to join
     */
    @Deprecated
    public void join(Game game) {
//        this.game = game;
//        saveLocation();
//        saveInventory();
//        player.getInventory().clear();  // TODO: allow bringing personal kit
//        player.sendMessage(Text.of("Sending you to " + game.getArena().getName() + " ..."));
//        Arena arena = game.getArena();
//        SpawnPoint spawnPoint = arena.getLobbySpawn();
//        player.setTransform(spawnPoint.getTransform());
//        // TODO: refer to game logistics for the following parameters
//        player.offer(Keys.GAME_MODE, GameModes.SURVIVAL);
//        player.offer(Keys.HEALTH, player.get(Keys.MAX_HEALTH).get());
//        player.offer(Keys.FOOD_LEVEL, 20);
//        setStatus(GamerStatus.PLAYING);
        // game.add(this);
    }

    /**
     * Quits from the Game in which the Gamer is currently in.
     */
    @Deprecated
    public void quit() {
//        this.game = null;
//        retrieveInventory();
//        setLocation(getSavedLocation());
//        setStatus(GamerStatus.AVAILABLE);
//        player.offer(Keys.GAME_MODE, GameModes.SURVIVAL);
//        player.sendMessage(Text.of("you have your gamemode changed to survival !"));
//        player.offer(Keys.HEALTH, player.get(Keys.MAX_HEALTH).get());
//        player.offer(Keys.FOOD_LEVEL, 20);
    }

    /**
     * Spectates the given game. Sets the gamemode to be SPECTATOR and teleports to the spectator spawn of the Game.
     * @param game the game the gamer about to spectate
     */
    @Deprecated
    public void spectate(Game game) {
//        player.offer(Keys.GAME_MODE, GameModes.SPECTATOR);
//        setStatus(GamerStatus.SPECTATING);
//        player.setTransform(game.getArena().getSpectatorSpawn().getTransform());
    }

    /**
     * Gets the {@link UUID} of this Gamer.
     *
     * @return the {@link UUID} of this Gamer
     */
    public UUID getUniqueId() {
        return uniqueId;
    }

    /**
     * Sets the client connection status of this Gamer.
     *
     * @param isOnline the client connection status of this Gamer
     */
    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    /**
     * Gets the client connection status of this Gamer
     *
     * @return true if this Gamer is online, false otherwise
     */
    public boolean isOnline() {
        return isOnline;
    }

    /**
     * Sets the name of this Gamer.
     *
     * @param name the name of this Gamer
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the name of this Gamer.
     *
     * @return the name of this Gamer
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the {@link Player} instance associated with this Gamer.
     *
     * @param player the {@link Player} instance to be associated with this Gamer
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Gets the {@link Player} instance associated with this Gamer
     *
     * @return {@link Player} or Optional.empty() if not found
     */
    public Player getPlayer() {
        return player;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
