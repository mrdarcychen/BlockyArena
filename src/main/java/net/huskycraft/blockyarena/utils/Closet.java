package net.huskycraft.blockyarena.utils;

import net.huskycraft.blockyarena.BlockyArena;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A Closet stores items that may appear in a player's hotbar, grid inventory, and equipment inventory.
 */
public class Closet {

    public static BlockyArena plugin;

    private List<ItemStack> hotbar;
    private List<ItemStack> main;
    private Optional<ItemStack> offHand;
    private Optional<ItemStack> helmet, chestplate, leggings, boots;

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
        helmet = inv.getEquipment().getSlot(EquipmentTypes.HEADWEAR).get().peek();
        chestplate = inv.getEquipment().getSlot(EquipmentTypes.CHESTPLATE).get().peek();
        leggings = inv.getEquipment().getSlot(EquipmentTypes.LEGGINGS).get().peek();
        boots = inv.getEquipment().getSlot(EquipmentTypes.BOOTS).get().peek();
        offHand = inv.getOffhand().peek();
    }

    public void equip(Player player) {
        PlayerInventory inv = (PlayerInventory) player.getInventory();

        for (ItemStack itemStack : hotbar) {
            inv.getHotbar().offer(itemStack.copy());
        }
        for (ItemStack itemStack : main) {
            inv.getMainGrid().offer(itemStack.copy());
        }
        if (helmet.isPresent()) player.setHelmet(helmet.get().copy());
        if (chestplate.isPresent()) player.setChestplate(chestplate.get().copy());
        if (leggings.isPresent()) player.setLeggings(leggings.get().copy());
        if (boots.isPresent()) player.setBoots(boots.get().copy());
        if (offHand.isPresent()) inv.getOffhand().offer(offHand.get().copy());
    }
}