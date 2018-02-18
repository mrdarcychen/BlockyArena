package net.huskycraft.blockyarena.managers;

import com.google.common.reflect.TypeToken;
import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.utils.Kit;
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
                Kit kit = rootNode.getValue(TypeToken.of(Kit.class));
                kits.put(kit.getId(), kit);
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
}
