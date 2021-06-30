/*
 * Copyright 2021 Darcy Chen <mrdarcychen@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */

package io.github.mrdarcychen.utils.storage;

import com.google.common.reflect.TypeToken;
import io.github.mrdarcychen.BlockyArena;
import io.github.mrdarcychen.arenas.Arena;
import io.github.mrdarcychen.arenas.SpawnPoint;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Loads arenas from and to a persistent storage.
 */
public class ArenaFileLoader extends FileLoader<Arena> {

    /**
     * Constructs a new ArenaFileLoader for loading and writing arenas.
     */
    public ArenaFileLoader() {
        super(BlockyArena.getConfigManager().getArenaDir());
    }

    @Override
    protected Optional<Arena> parseFrom(ConfigurationNode root) throws RuntimeException {
        TypeToken<SpawnPoint> spawnToken = TypeToken.of(SpawnPoint.class);
        String name = root.getNode("name").getString();
        Arena.Builder builder = new Arena.Builder(name);
        Arena output;
        try {
            builder.setLobbySpawn(root.getNode("lobbySpawn").getValue(spawnToken));
            builder.setSpectatorSpawn(root.getNode("spectatorSpawn").getValue(spawnToken));
            for (ConfigurationNode node : root.getNode("startPoints").getChildrenList()) {
                builder.addStartPoint(node.getValue(spawnToken));
            }
            output = builder.build();
        } catch (ObjectMappingException e) {
            throw new RuntimeException(name + " is not loaded due to incorrect formatting.");
        } catch (IllegalStateException e) {
            throw new RuntimeException(name + " is not loaded due to missing arena components.");
        }
        return Optional.of(output);
    }

    @Override
    protected void writeTo(ConfigurationNode root, Arena arena) throws ObjectMappingException {
        root.getNode("name").setValue(arena.getName());
        List<SpawnPoint> startPoints = arena.getStartPoints().collect(Collectors.toList());
        for (int i = 0; i < startPoints.size(); i++) {
            root.getNode("startPoints").getNode(i).setValue(TypeToken.of(SpawnPoint.class), startPoints.get(i));
        }
        root.getNode("lobbySpawn").setValue(TypeToken.of(SpawnPoint.class), arena.getLobbySpawn());
        root.getNode("spectatorSpawn").setValue(TypeToken.of(SpawnPoint.class), arena.getSpectatorSpawn());
    }
}
