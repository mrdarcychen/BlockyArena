package net.huskycraft.blockyarena.utils;

import net.huskycraft.blockyarena.arenas.Spawn;
import net.huskycraft.blockyarena.games.Game;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

/**
 * A Gamer object stores a player's gaming profile.
 */
public class Gamer {

    private final UUID uniqueId;
    private boolean isOnline;
    private String name;
    private Player player; // the player instance in which this Gamer profile is associated with

    private Game game; // the Game session this player is currently in
    private GamerStatus status; // the gaming status of this player

    private Location savedLocation; // the saved location of the player for record
    private Kit kit; // the original inventory of the player for record

    /**
     * Constructs a unique Gamer profile for the given Player.
     *
     * @param uniqueId the {@link UUID} of the associated {@link Player}
     */
    public Gamer(UUID uniqueId) {
        this.uniqueId = uniqueId;
        status = GamerStatus.AVAILABLE;
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
    public void spawnAt(Spawn spawn) {
        player.setLocationAndRotation(spawn.getSpawnLocation(), spawn.getSpawnRotation());
    }

    /**
     * Sets the location of this Gamer.
     *
     * @param location the location to set
     */
    public void setLocation(Location location) {
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
    public Game getGame() {
        return game;
    }

    /**
     * Sets the GamerStatus of this Gamer.
     *
     * @param status the GamerStatus of this Gamer
     */
    public void setStatus(GamerStatus status) {
        this.status = status;
    }

    /**
     * Gets the GamerStatus of this Gamer.
     *
     * @return the GamerStatus of this Gamer
     */
    public GamerStatus getStatus() {
        return status;
    }

    /**
     * Joins this Gamer to the given Game.
     *
     * @param game the Game in which the Gamer is about to join
     */
    public void join(Game game) {
        this.game = game;
        saveLocation();
        saveInventory();
        player.getInventory().clear();  // TODO: allow bringing personal kit
        player.sendMessage(Text.of("Sending you to " + game.getArena().getID() + " ..."));
        spawnAt(game.getArena().getLobbySpawn());
        // TODO: refer to game logistics for the following parameters
        player.offer(Keys.GAME_MODE, GameModes.SURVIVAL);
        player.offer(Keys.HEALTH, player.get(Keys.MAX_HEALTH).get());
        player.offer(Keys.FOOD_LEVEL, 20);
        setStatus(GamerStatus.PLAYING);
        game.add(this);
    }

    /**
     * Quits from the Game in which the Gamer is currently in.
     */
    public void quit() {
        game.remove(this);
        this.game = null;
        retrieveInventory();
        setLocation(getSavedLocation());
        setStatus(GamerStatus.AVAILABLE);
        player.offer(Keys.GAME_MODE, GameModes.SURVIVAL);
    }

    /**
     * Spectates the given game. Sets the gamemode to be SPECTATOR and teleports to the spectator spawn of the Game.
     * @param game the game the gamer about to spectate
     */
    public void spectate(Game game) {
        player.offer(Keys.GAME_MODE, GameModes.SPECTATOR);
        setStatus(GamerStatus.SPECTATING);
        spawnAt(game.getArena().getSpectatorSpawn());
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
}
