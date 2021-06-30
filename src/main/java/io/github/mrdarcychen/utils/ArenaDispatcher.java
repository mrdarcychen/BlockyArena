/*
 * Copyright 2021 Darcy Chen <mrdarcychen@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */

package io.github.mrdarcychen.utils;

import io.github.mrdarcychen.arenas.Arena;
import io.github.mrdarcychen.utils.storage.ArenaFileLoader;
import io.github.mrdarcychen.utils.storage.FileLoader;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class ArenaDispatcher {

    private final Set<Arena> arenas;
    private final FileLoader<Arena> loader;

    public ArenaDispatcher() {
        loader = new ArenaFileLoader();
        arenas = loader.parseAll();
    }

    public Optional<Arena> findBy(String mode) {
        return findBy(mode, "");
    }

    public Optional<Arena> findBy(String mode, String arenaName) {
        Predicate<Arena> criteria = it -> false;
        if (mode.equals("ffa")) {
            criteria = it -> it.getStartPoints().count() > 2 && !it.isBusy();

        }
        if (mode.equals("1v1") || mode.equals("2v2")) {
            criteria = it -> !it.isBusy();
        }
        Optional<Arena> optArena;
        if (arenaName.isEmpty()) {
            optArena = arenas.stream()
                    .filter(criteria)
                    .findAny();
        } else {
            optArena = arenas.stream()
                    .filter(it -> arenaName.equals(it.getName()))
                    .filter(criteria)
                    .findAny();
        }
        optArena.ifPresent(arena -> arena.setBusy(true));
        return optArena;
    }

    public void register(Arena arena) {
        arenas.add(arena);
        loader.write(arena.getName(), arena);
    }

    /**
     * Removes the Arena with the given name.
     *
     * @param name the name of the Arena to be removed
     */
    public void unregister(String name) {
        arenas.removeIf(arena -> name.equals(arena.getName()));
        loader.remove(name);
    }
}
