/*
 * Copyright 2021 Darcy Chen <mrdarcychen@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */

package io.github.mrdarcychen.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Reads and writes configurations from a persistent storage.
 */
public class ConfigManager {

    private final Path rootConfigDir;

    public ConfigManager(Path rootConfigDir) {
        this.rootConfigDir = rootConfigDir;
        createRootDirectory();
        createSubDirectories();
    }

    private void createRootDirectory() {
        if (!rootConfigDir.toFile().exists()) {
            try {
                Files.createDirectory(rootConfigDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // creates directories for arenas and classes if they do not exist
    private void createSubDirectories() {
        List<Path> directories = Arrays.asList(getArenaDir(), getKitDir());
        for (Path dir : directories) {
            if (!dir.toFile().exists()) {
                try {
                    Files.createDirectory(dir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Path getArenaDir() {
        return Paths.get(rootConfigDir + "/arenas");
    }

    public Path getKitDir() {
        return Paths.get(rootConfigDir + "/kits");
    }
}
