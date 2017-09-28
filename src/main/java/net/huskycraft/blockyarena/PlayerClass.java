package net.huskycraft.blockyarena;

import org.spongepowered.api.item.inventory.Inventory;

public class PlayerClass {

    private BlockyArena plugin;
    private String className;
    private Inventory classInventory;

    public PlayerClass(BlockyArena plugin, String className, Inventory classInventory) {
        this.plugin = plugin;
        this.className = className;
        this.classInventory = classInventory;
    }
}
