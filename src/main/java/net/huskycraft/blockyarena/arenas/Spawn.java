package net.huskycraft.blockyarena.arenas;

import com.flowpowered.math.vector.Vector3d;
import com.google.inject.Inject;
import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.utils.Gamer;
import org.slf4j.Logger;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A Spawn object represents a specific spawn location with head rotation for a Team.
 */
public class Spawn {

    public static BlockyArena plugin;

    @Inject
    private Logger logger;
    private Location<World> spawnLocation; // the spawn location of the team
    private Vector3d spawnRotation; // the spawn rotation of the team

    /**
     * Constructs a Team with the location of the spawn point and the rotation of the spawn point
     * @param spawnLocation the location that a player spawns
     * @param spawnRotation the heading of a player who spawns at the spawn location
     */
    public Spawn(Location<World> spawnLocation, Vector3d spawnRotation) {
        this.spawnLocation = spawnLocation;
        this.spawnRotation = spawnRotation;
    }

    /**
     * Spawns the given gamer at the spawn location of the team.
     */
    public void spawn(Gamer gamer) {
        gamer.getPlayer().setLocationAndRotation(spawnLocation, spawnRotation);
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public Vector3d getSpawnRotation() {
        return spawnRotation;
    }
}
