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

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public Vector3d getSpawnRotation() {
        return spawnRotation;
    }
}
