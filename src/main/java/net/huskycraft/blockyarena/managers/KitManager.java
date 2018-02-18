package net.huskycraft.blockyarena.managers;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.utils.Kit;

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
    }

    /**
     * Gets the Kit with the given id.
     *
     * @param id the id of the Kit
     * @return the Kit with the given id
     */
    public Kit get(String id) {
        plugin.getLogger().warn("Contains " + id + "? " + kits.containsKey(id));
        return kits.get(id);
    }
}
