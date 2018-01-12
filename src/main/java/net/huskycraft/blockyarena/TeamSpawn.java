package net.huskycraft.blockyarena;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Set;
import java.util.TreeSet;

/**
 * The Team class represents a group of gamers in a Game that share the same spawn point.
 */
public class TeamSpawn {

    private Location<World> spawnLocation; // the spawn location of the team
    private Vector3d spawnRotation; // the spawn rotation of the team

    /**
     * Constructs a Team with the location of the spawn point and the rotation of the spawn point
     * @param spawnLocation the location that a player spawns
     * @param spawnRotation the heading of a player who spawns at the spawn location
     */
    public TeamSpawn(Location<World> spawnLocation, Vector3d spawnRotation) {
        this.spawnLocation = spawnLocation;
        this.spawnRotation = spawnRotation;
    }

    /**
     * Spawns the given gamer at the spawn location of the team.
     */
    public void spawn(Gamer gamer) {
        gamer.getPlayer().setLocationAndRotation(spawnLocation, spawnRotation);
    }
}
