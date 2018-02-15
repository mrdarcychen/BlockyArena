package net.huskycraft.blockyarena.utils;

import net.huskycraft.blockyarena.arenas.Spawn;
import net.huskycraft.blockyarena.games.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;

/**
 * The Gamer class represents a player's gaming profile.
 */
public class Gamer {

    private Player player;

    private Game game;
    private GamerStatus status;

    private Location lastLocation;
    private Closet closet;

    /**
     * Constructs a unique Gamer profile for the given user.
     */
    public Gamer(Player player) {
        this.player = player;
        status = GamerStatus.AVAILABLE;
    }

    /**
     * Saves and then clear the inventory of this Gamer.
     */
    public void saveInventory() {
        closet = new Closet(player);
    }

    /**
     * Retrieves the most recently saved inventory of this Gamer.
     */
    public void retrieveInventory() {
        closet.equip(player);
    }

    /**
     * Spawns this Gamer at the given Spawn point.
     */
    public void spawnAt(Spawn spawn) {
        player.setLocationAndRotation(spawn.getSpawnLocation(), spawn.getSpawnRotation());
    }

    public void setLocation(Location location) {
        player.setLocation(location);
    }

    public void setLastLocation() {
        this.lastLocation = player.getLocation();
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the session the player is currently in.
     */
    public void setGame(Game game) {
        this.game = game;
        setStatus(GamerStatus.INGAME);
    }

    /**
     * Gets the Game this gamer is currently in.
     * @return the game this gamer is currently in, null if not in any Game
     */
    public Game getGame() {
        return game;
    }

    public void setStatus(GamerStatus status) {
        this.status = status;
    }

    public GamerStatus getStatus() {
        return status;
    }
}
