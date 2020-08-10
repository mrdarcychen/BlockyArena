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

package io.github.mrdarcychen.arenas;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.World;

import java.util.UUID;

public class SpawnSerializer implements TypeSerializer<SpawnPoint> {

    public SpawnSerializer() {
    }

    @Override
    public SpawnPoint deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        UUID extentUUID = value.getNode("extent").getValue(TypeToken.of(UUID.class));
        if (!Sponge.getServer().getWorld(extentUUID).isPresent()) {
            System.err.println("Cannot find extent with UUID " + extentUUID.toString());
        }
        World extent = Sponge.getServer().getWorld(extentUUID).get();
        Vector3d position = value.getNode("position").getValue(TypeToken.of(Vector3d.class));
        Vector3d rotation = value.getNode("rotation").getValue(TypeToken.of(Vector3d.class));
        return SpawnPoint.of(new Transform<>(extent, position, rotation));
    }

    @Override
    public void serialize(TypeToken<?> type, SpawnPoint obj, ConfigurationNode value) throws ObjectMappingException {
        Transform<World> transform = obj.getTransform();
        value.getNode("extent").setValue(TypeToken.of(UUID.class), transform.getExtent().getUniqueId());
        value.getNode("position").setValue(TypeToken.of(Vector3d.class), transform.getPosition());
        value.getNode("rotation").setValue(TypeToken.of(Vector3d.class), transform.getRotation());
    }
}