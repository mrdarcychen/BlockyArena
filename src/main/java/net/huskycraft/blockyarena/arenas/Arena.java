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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Stream;

/**
 * An Arena represents an specific region for dueling.
 */
public class Arena {

    private String name;
    private Deque<SpawnPoint> startPoints;
    private SpawnPoint lobbySpawn;
    private SpawnPoint spectatorSpawn;
    private boolean isBusy = false;

    private Arena(String name, SpawnPoint lobbySpawn, SpawnPoint spectatorSpawn,
                  Deque<SpawnPoint> startPoints) {
        this.name = name;
        this.lobbySpawn = lobbySpawn;
        this.spectatorSpawn = spectatorSpawn;
        this.startPoints = startPoints;
    }

    public Stream<SpawnPoint> getStartPoints() {
        return startPoints.stream(); // return a defensive copy
    }

    public SpawnPoint getLobbySpawn() {
        return lobbySpawn;
    }

    public SpawnPoint getSpectatorSpawn() {
        return spectatorSpawn;
    }

    public void setBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public String getName() {
        return name;
    }

    /*
    - will support undo action
     */
    public static class Builder {
        private Deque<SpawnPoint> startPoints = new ArrayDeque<>();
        private SpawnPoint lobbySpawn;
        private SpawnPoint spectatorSpawn = lobbySpawn;
        private String name;

        public Builder(String name) {
            this.name = name;
        }

        public Builder addStartPoint(SpawnPoint spawnPoint) {
            startPoints.add(spawnPoint);
            return this;
        }

        /**
         * Removes the most recently added start point.
         */
        public Builder undo() {
            startPoints.pollLast();
            return this;
        }

        public Builder setLobbySpawn(SpawnPoint spawnPoint) {
            lobbySpawn = spawnPoint;
            return this;
        }

        public Builder setSpectatorSpawn(SpawnPoint spawnPoint) {
            spectatorSpawn = spawnPoint;
            return this;
        }

        public Arena build() {
            return new Arena(name, lobbySpawn, spectatorSpawn, startPoints);
        }
    }
}