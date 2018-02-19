package net.huskycraft.blockyarena.utils;

import net.huskycraft.blockyarena.arenas.Spawn;
import net.huskycraft.blockyarena.games.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;

import java.util.UUID;

/**
 * A Gamer object stores a player's gaming profile.
 */
public class Gamer {

    private final UUID uuid;
    private Player player; // the player instance in which this Gamer profile is associated with

    private Game game; // the Game session this player is currently in
    private GamerStatus status; // the gaming status of this player

    private Location savedLocation; // the saved location of the player for record
    private Kit kit; // the original inventory of the player for record

    /**
     * Constructs a unique Gamer profile for the given Player.
     *
     * @param player the Player instance to be associated with this Gamer profile
     */
    public Gamer(Player player) {
        this.uuid = player.getUniqueId();
        this.player = player;
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
     * Gets the Player instance associated with this Gamer.
     *
     * @return the Player instance of this Gamer
     */
    public Player getPlayer() {
        return player;
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
     * Gets the name of this Gamer.
     *
     * @return the name of this Gamer
     */
    public String getName() {
        return player.getName();
    }

    /**
     * Joins this Gamer to the given Game.
     *
     * @param game the Game in which the Gamer is about to join
     */
    public void join(Game game) {
        saveLocation();
        saveInventory();
        game.add(this);
        this.game = game;
        setStatus(GamerStatus.PLAYING);
        player.getInventory().clear();  // TODO: allow bringing personal kit
    }

    /**
     * Quits from the Game in which the Gamer is currently in.
     */
    public void quit() {
        game.remove(this);
        this.game = null;
        setStatus(GamerStatus.AVAILABLE);
        retrieveInventory();
        setLocation(getSavedLocation());
    }

    /**
     * Sets the given Player as the new Player instance associated with this Gamer
     * @param player the Player instance to be associated with this Gamer
     */
    public void setPlayer(Player player) {
        this.player = player;
    }
}
