package net.huskycraft.blockyarena;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;

import java.util.ArrayList;

public class PlayerClass {

    private BlockyArena plugin;
    private String className;
    private ArrayList<ItemStack> itemStacks;

    public PlayerClass(BlockyArena plugin, String className, Inventory inventory) {
        this.plugin = plugin;
        this.className = className;
        itemStacks = new ArrayList<>();
        decodeInventory(inventory);
    }

    public void decodeInventory(Inventory inventory) {
        Iterable<Slot> slotIterable= inventory.slots();
        for (Slot slot : slotIterable) {
            if (slot.peek().isPresent()) {
                itemStacks.add(slot.peek().get());
            }
        }
    }

    public String getClassName() {
        return className;
    }

    public void offerItemStacksTo(Player player) {
        for (ItemStack itemStack : itemStacks) {
            player.getInventory().offer(itemStack);
        }
    }
}
