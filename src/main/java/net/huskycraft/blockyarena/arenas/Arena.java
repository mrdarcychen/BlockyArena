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