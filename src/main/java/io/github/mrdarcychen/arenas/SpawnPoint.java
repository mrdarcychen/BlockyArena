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
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Represents a specific spawn location with head rotation for a Team.
 */
public class SpawnPoint {

    private final Transform<World> transform;

    /**
     * Constructs a Team with the location of the spawn point and the rotation of the spawn point
     *
     * @param location the location that a player spawns
     * @param rotation the heading of a player who spawns at the spawn location
     */
    @Deprecated
    public SpawnPoint(Location<World> location, Vector3d rotation) {
        transform = new Transform<>(location.getExtent(), location.getPosition(), rotation);
    }

    private SpawnPoint(Transform<World> transform) {
        this.transform = transform;
    }

    @Deprecated
    public Location getSpawnLocation() {
        return transform.getLocation();
    }

    @Deprecated
    public Vector3d getSpawnRotation() {
        return transform.getRotation();
    }

    public Transform<World> getTransform() {
        return transform;
    }

    public static SpawnPoint of(Transform<World> transform) {
        return new SpawnPoint(transform);
    }
}
