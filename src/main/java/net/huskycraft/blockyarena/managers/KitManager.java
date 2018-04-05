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
package net.huskycraft.blockyarena.managers;

import com.google.common.reflect.TypeToken;
import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.arenas.Arena;
import net.huskycraft.blockyarena.utils.Kit;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * A KitManager manages all predefined gaming Kits.
 */
public class KitManager {

    public static BlockyArena plugin;
    private Map<String, Kit> kits;

    public KitManager(BlockyArena plugin) {
        this.plugin = plugin;
        kits = new HashMap<>();
        loadKits();
    }

    private void loadKits() {
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(plugin.getKitDir(), "*.conf");
            for (Path path : stream) {
                ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader
                        .builder().setPath(path).build();
                ConfigurationNode rootNode = loader.load();
                String id = rootNode.getNode("id").getString();
                try {
                    Kit kit = rootNode.getValue(TypeToken.of(Kit.class));
                    kits.put(kit.getId(), kit);
                } catch (InvalidDataException e) {
                    plugin.getLogger().warn("Kit " + id + " cannot be loaded because it contains " +
                            "unknown items.");
                }
                loader.save(rootNode);
            }
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the given Kit with the given id associated with it.
     *
     * @param kit the Kit to be added
     * @param id the id associated with the Kit
     */
    public void add(Kit kit, String id) {
        kits.put(id, kit);
        plugin.getLogger().warn(id + " has been added to kit manager.");
        Path path = Paths.get(plugin.getKitDir().toString() + File.separator + id + ".conf");
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader
                .builder().setPath(path).build();
        try {
            ConfigurationNode rootNode = loader.load();
            rootNode.setValue(TypeToken.of(Kit.class), kit);
            loader.save(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the Kit with the given id.
     *
     * @param id the id of the Kit
     * @return the Kit with the given id
     */
    public Kit get(String id) {
        return kits.get(id);
    }

    /**
     * Removes the {@link Kit} and its config file if exists.
     *
     * @param id the id of the Kit
     */
    public void remove(String id) {
        kits.remove(id);
        Path path = Paths.get(plugin.getKitDir().toString() + File.separator + id + ".conf");
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new IllegalArgumentException(id + " does not exist.");
        }
    }
}
