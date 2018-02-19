package net.huskycraft.blockyarena.arenas;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import net.huskycraft.blockyarena.BlockyArena;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.UUID;

public class SpawnSerializer implements TypeSerializer<Spawn> {

    public static BlockyArena plugin;

    public SpawnSerializer(BlockyArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public Spawn deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        UUID extentUUID = value.getNode("extent").getValue(TypeToken.of(UUID.class));
        if (!Sponge.getServer().getWorld(extentUUID).isPresent()) {
            plugin.getLogger().warn("Cannot find extent with UUID " + extentUUID.toString());
        }
        Extent extent = Sponge.getServer().getWorld(extentUUID).get();
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
}