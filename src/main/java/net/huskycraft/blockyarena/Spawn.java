package net.huskycraft.blockyarena;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

/**
 * A Spawn object represents a specific spawn location with head rotation for a Team.
 */
public class Spawn implements TypeSerializer<Spawn> {

    public static BlockyArena plugin;

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

    @Override
    public Spawn deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        UUID extentUUID = value.getNode("extent").getValue(TypeToken.of(UUID.class));
        if (!Sponge.getServer().getWorld(extentUUID).isPresent()) return null;
        World extent = Sponge.getServer().getWorld(extentUUID).get();
        Location<World> spawnLocation = new Location(
                extent,
                new Vector3d(value.getNode("location").getValue(TypeToken.of(Vector3d.class))));
        Vector3d spawnRotation = value.getNode("rotation").getValue(TypeToken.of(Vector3d.class));
        return new Spawn(spawnLocation, spawnRotation);
    }

    @Override
    public void serialize(TypeToken<?> type, Spawn obj, ConfigurationNode value) throws ObjectMappingException {
        value.getNode("extent").setValue(TypeToken.of(UUID.class), obj.getSpawnLocation().getExtent().getUniqueId());
        value.getNode("location").setValue(TypeToken.of(Vector3d.class), obj.getSpawnLocation().getPosition());
        value.getNode("rotation").setValue(TypeToken.of(Vector3d.class), obj.getSpawnRotation());
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public Vector3d getSpawnRotation() {
        return spawnRotation;
    }
}
