/*
 * Copyright 2021 Darcy Chen <mrdarcychen@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */

package io.github.mrdarcychen.utils.storage;

import com.google.common.reflect.TypeToken;
import io.github.mrdarcychen.ServiceProvider;
import io.github.mrdarcychen.utils.Kit;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.Optional;

public class KitFileLoader extends FileLoader<Kit> {

    /**
     * Constructs a new ArenaFileLoader for loading and writing arenas.
     */
    public KitFileLoader() {
        super(ServiceProvider.getConfigManager().getKitDir());
    }

    @Override
    protected Optional<Kit> parseFrom(ConfigurationNode root) throws RuntimeException {
        String id = root.getNode("id").getString();
        try {
            Kit kit = root.getValue(TypeToken.of(Kit.class));
            return Optional.of(kit);
        } catch (ObjectMappingException e) {
            System.err.println("Kit " + id + " can't be loaded because it contains unknown items.");
        }
        return Optional.empty();
    }

    @Override
    protected void writeTo(ConfigurationNode root, Kit kit) throws ObjectMappingException {
        root.setValue(TypeToken.of(Kit.class), kit);
    }
}
