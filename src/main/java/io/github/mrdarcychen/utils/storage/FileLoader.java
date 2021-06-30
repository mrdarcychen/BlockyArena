/*
 * Copyright 2021 Darcy Chen <mrdarcychen@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */

package io.github.mrdarcychen.utils.storage;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class FileLoader<T> {

    private static final String FILE_PATTERN = "*.conf";

    private final Path directory;

    protected FileLoader(Path directory) {
        this.directory = directory;
    }

    /**
     * Reconstructs a list of Objects from the files in the given directory.
     *
     * @return a list of reconstructed objects; empty if there's none
     */
    public Set<T> parseAll() {
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(directory, FILE_PATTERN);
            Set<T> objects = new HashSet<>();
            stream.forEach(file -> parseFrom(file).ifPresent(objects::add));
            return objects;
        } catch (IOException e) {
            System.err.println("An I/O error occurred while loading arena configs.");
        }
        return Collections.emptySet();
    }

    private Optional<T> parseFrom(Path file) {
        ConfigurationLoader<CommentedConfigurationNode> loader =
                HoconConfigurationLoader.builder().setPath(file).build();
        ConfigurationNode rootNode = null;
        try {
            rootNode = loader.load();
            return parseFrom(rootNode);
        } catch (IOException e) {
            System.err.println("Failed to parse file " + file.getFileName());
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        } finally {
            attemptSaving(loader, rootNode);
        }
        return Optional.empty();
    }

    public void write(String fileName, T object) {
        Path file = Paths.get(directory + File.separator + fileName + ".conf");
        try {
            if (!file.toFile().exists()) {
                Files.createFile(file);
            }
        } catch (IOException e) {
            System.err.println("Error creating config file " + fileName + ".conf");
            return;
        }
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader
                .builder().setPath(file).build();
        ConfigurationNode root = null;
        try {
            root = loader.load();
            writeTo(root, object);
        } catch (IOException | ObjectMappingException e) {
            e.printStackTrace();
        } finally {
            attemptSaving(loader, root);
        }
    }

    /**
     * Removes the file with the given fileName.
     *
     * @param fileName the fileName of the file to be removed
     */
    public void remove(String fileName) {
        Path file = Paths.get(directory + File.separator + fileName + ".conf");
        try {
            Files.delete(file);
        } catch (IOException e) {
            System.err.println("File " + fileName + ".conf doesn't exist.");
        }
    }

    private void attemptSaving(ConfigurationLoader<CommentedConfigurationNode> loader,
                               ConfigurationNode rootNode) {
        try {
            loader.save(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract Optional<T> parseFrom(ConfigurationNode root) throws RuntimeException;

    protected abstract void writeTo(ConfigurationNode root, T object) throws ObjectMappingException;
}
