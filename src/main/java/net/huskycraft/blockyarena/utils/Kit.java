package net.huskycraft.blockyarena.utils;

import net.huskycraft.blockyarena.BlockyArena;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.property.SlotPos;

import java.util.*;

/**
 * A Kit represents a player's equipments and a collection of ItemStacks that appear in the hotbar and main grid.
 */
public class Kit {

    public static BlockyArena plugin;

    private Map<SlotPos, ItemStack> main;
    private Optional<ItemStack> headwear, chestplate, leggings, boots, offHand;

    /**
     * Constructs a Kit based on the given player's inventory.
     *
     * @param player the player whose inventory is referenced by this Kit
     */
    public Kit(Player player) {
        main = new TreeMap<>();
        PlayerInventory inventory = (PlayerInventory)player.getInventory();
        Iterable<Slot> mainSlots = inventory.getMain().slots();
        for (Slot slot : mainSlots) {
            if (slot.peek().isPresent()) {
                main.put(slot.getInventoryProperty(SlotPos.class).get(), slot.peek().get());
            }
        }
        headwear = inventory.getEquipment().getSlot(EquipmentTypes.HEADWEAR).get().peek();
        chestplate = inventory.getEquipment().getSlot(EquipmentTypes.CHESTPLATE).get().peek();
        leggings = inventory.getEquipment().getSlot(EquipmentTypes.LEGGINGS).get().peek();
        boots = inventory.getEquipment().getSlot(EquipmentTypes.BOOTS).get().peek();
        offHand = inventory.getEquipment().getSlot(EquipmentTypes.OFF_HAND).get().peek();
    }

    /**
     * Equips the given Player with this Kit. This function does not clear the original inventory of the given player,
     * but may override existing slots that are conflicted with this Kit.
     *
     * @param player the Player to be equipped
     */
    public void equip(Player player) {
        PlayerInventory inventory = (PlayerInventory)player.getInventory();
        for (SlotPos slotPos : main.keySet()) {
            inventory.getMain().set(slotPos, main.get(slotPos));
        }
        if (headwear.isPresent()) {
            inventory.getEquipment().set(EquipmentTypes.HEADWEAR, headwear.get().copy());
        }
        if (chestplate.isPresent()) {
            inventory.getEquipment().set(EquipmentTypes.CHESTPLATE, chestplate.get().copy());
        }
        if (leggings.isPresent()) {
            inventory.getEquipment().set(EquipmentTypes.LEGGINGS, leggings.get().copy());
        }
        if (boots.isPresent()) {
            inventory.getEquipment().set(EquipmentTypes.BOOTS, boots.get().copy());
        }
        if (offHand.isPresent()) {
            inventory.getEquipment().set(EquipmentTypes.OFF_HAND, offHand.get().copy());
        }
    }
}