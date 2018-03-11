/*
 * This file is part of BlockyArena, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018 HuskyCraft <https://www.huskycraft.net>
 * Copyright (c) 2018 Darcy-Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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