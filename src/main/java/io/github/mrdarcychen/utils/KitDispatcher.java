/*
 * Copyright 2021 Darcy Chen <mrdarcychen@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */

package io.github.mrdarcychen.utils;

import io.github.mrdarcychen.utils.storage.FileLoader;
import io.github.mrdarcychen.utils.storage.KitFileLoader;

import java.nio.file.Path;
import java.util.*;

/**
 * Manages all custom kits defined by server admin.
 */
public class KitDispatcher {

    private final Set<Kit> kits;
    private final FileLoader<Kit> loader;

    public KitDispatcher() {
        loader = new KitFileLoader();
        kits = loader.parseAll();
    }

    /**
     * Adds the given Kit with the given id associated with it.
     *
     * @param kit the Kit to be added
     */
    public void add(Kit kit) {
        kits.add(kit);
        loader.write(kit.getId(), kit);
    }

    /**
     * Gets the Kit with the given id.
     *
     * @param id the id of the Kit
     * @return the Kit with the given id
     */
    public Optional<Kit> get(String id) {
        return kits.stream().filter(kit -> id.equals(kit.getId())).findAny();
    }

    /**
     * Removes the {@link Kit} and its config file if exists.
     *
     * @param id the id of the Kit
     */
    public void remove(String id) {
        kits.removeIf(kit -> kit.getId().equals(id));
        loader.remove(id);
    }
}
