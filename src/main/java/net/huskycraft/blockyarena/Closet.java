package net.huskycraft.blockyarena;

import net.huskycraft.blockyarena.BlockyArena;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A Closet stores items that may appear in a player's hotbar, grid inventory, and equipment inventory.
 */
public class Closet {

    public static BlockyArena plugin;

    private Optional<ItemStack> helmet, chestplate, leggings, boots; // ItemStacks in the equipment inventory
    private List<ItemStack> hotbar; // ItemStacks in the hotbar inventory
    private List<ItemStack> main; // ItemStacks in the main inventory

    /**
     * Constructs a Closet based on the inventory of the given player.
     */
    public Closet(Player player) {
        hotbar = new ArrayList<>();
        main = new ArrayList<>();
        PlayerInventory inv = (PlayerInventory) player.getInventory();
        Iterable<Slot> hotbarSlots = inv.getHotbar().slots();
        for (Slot slot : hotbarSlots) {
            if (slot.peek().isPresent()) {
                hotbar.add(slot.peek().get());
            }
        }
        Iterable<Slot> mainSlots = inv.getMainGrid().slots();
        for (Slot slot : mainSlots) {
            if (slot.peek().isPresent()) {
                main.add(slot.peek().get());
            }
        }
        helmet = player.getHelmet();
        chestplate = player.getChestplate();
        leggings = player.getLeggings();
        boots = player.getBoots();
    }

    /**
     * Gives all ItemStacks stored in the Closet to the given player.
     * This function will not replace any item in the player's inventory.
     */
    public void equipAll(Player player) {
        equipArmors(player);
        equipHotbar(player);
        equipMain(player);
    }

    /**
     * Gives ItemStacks stored in the hotbar inventory to the given player.
     * This function will not replace any item in the player's inventory.
     */
    public void equipHotbar(Player player) {
        PlayerInventory inv = (PlayerInventory) player.getInventory();
        for (ItemStack itemStack : hotbar) {
            inv.getHotbar().offer(itemStack.copy());
        }
    }

    /**
     * Gives ItemStacks stored in the main grid inventory to the given player.
     * This function will not replace any item in the player's inventory.
     */
    public void equipMain(Player player) {
        PlayerInventory inv = (PlayerInventory) player.getInventory();
        for (ItemStack itemStack : main) {
            inv.getMainGrid().offer(itemStack.copy());
        }
    }

    /**
     * Give ItemStacks stored in the equipment inventory to the given player.
     * This function will not replace any item in the player's inventory.
     */
    public void equipArmors(Player player) {
        if (helmet.isPresent()) player.setHelmet(helmet.get().copy());
        if (chestplate.isPresent()) player.setChestplate(chestplate.get().copy());
        if (leggings.isPresent()) player.setLeggings(leggings.get().copy());
        if (boots.isPresent()) player.setBoots(boots.get().copy());
    }
}
