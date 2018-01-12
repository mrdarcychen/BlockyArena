package net.huskycraft.blockyarena;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Set;
import java.util.TreeSet;

/**
 * The Team class represents a group of gamers in a Game that share the same spawn point.
 */
public class Team {

    private Location<World> spawnLocation; // the spawn location of the team
    private Vector3d spawnRotation; // the spawn rotation of the team
    private Set<Gamer> gamers; // the set of gamers in the team

    /**
     * Constructs a Team with the location of the spawn point and the rotation of the spawn point
     * @param spawnLocation the location that a player spawns
     * @param spawnRotation the heading of a player who spawns at the spawn location
     */
    public Team(Location<World> spawnLocation, Vector3d spawnRotation) {
        this.spawnLocation = spawnLocation;
        this.spawnRotation = spawnRotation;
        gamers = new TreeSet<>();
    }

    /**
     * Adds the given gamer to this Team and send the gamer to the spawn location.
     */
    public void add(Gamer gamer) {
        gamers.add(gamer);
        spawn(gamer);
    }

    /**
     * Spawns the given gamer at the spawn location of the team.
     */
    public void spawn(Gamer gamer) {
        gamer.getPlayer().setLocationAndRotation(spawnLocation, spawnRotation);
    }
}
