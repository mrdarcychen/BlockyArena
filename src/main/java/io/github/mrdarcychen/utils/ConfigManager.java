/*
 * Copyright 2021 Darcy Chen <mrdarcychen@gmail.com> and the contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.github.mrdarcychen.utils;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

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
    private final Path defaultConfig;

    private int lobbyCountdown = 15;

    public ConfigManager(Path rootConfigDir, Path defaultConfig) {
        this.rootConfigDir = rootConfigDir;
        this.defaultConfig = defaultConfig;
        loadDefaultConfig();
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

    private void loadDefaultConfig() {
        ConfigurationLoader<CommentedConfigurationNode> loader =
                HoconConfigurationLoader.builder().setPath(defaultConfig).build();

        if (!defaultConfig.toFile().exists()) initDefaultConfig(loader);

        try {
            ConfigurationNode rootNode = loader.load();
            lobbyCountdown = rootNode.getNode("timers", "lobby", "countdownSeconds").getInt();
            loader.save(rootNode);
        } catch (IOException e) {
            System.err.println("An I/O error occurred while loading default config.");
        }
    }

    private void initDefaultConfig(ConfigurationLoader<CommentedConfigurationNode> loader) {
        try {
            ConfigurationNode rootNode = loader.createEmptyNode(ConfigurationOptions.defaults());
            rootNode.getNode("timers", "lobby", "countdownSeconds").setValue(15);
            loader.save(rootNode);
        } catch (IOException e) {
            System.err.println("Failed to create default config for BlockyArena.");
        }
    }

    public int getLobbyCountdown() {
        return lobbyCountdown;
    }

    public Path getArenaDir() {
        return Paths.get(rootConfigDir + "/arenas");
    }

    public Path getKitDir() {
        return Paths.get(rootConfigDir + "/kits");
    }
}
