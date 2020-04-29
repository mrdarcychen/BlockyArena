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
package net.huskycraft.blockyarena.utils;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.property.SlotIndex;

import net.huskycraft.blockyarena.BlockyArena;

/**
 * A Kit represents a player's equipments and a collection of ItemStacks that appear in the hotbar and main grid.
 */
public class Kit {

    public static BlockyArena plugin;

    private String id;
    private Map<SlotIndex, ItemStack> main;
    private Optional<ItemStack> headwear, chestplate, leggings, boots, offHand;

    /**
     * Constructs a Kit based on the given player's inventory.
     *
     * @param player the player whose inventory is referenced by this Kit
     */
    public Kit(Player player, String id) {
        this.id = id;
        main = new TreeMap<>();
        PlayerInventory inventory = (PlayerInventory)player.getInventory();
        Iterable<Slot> mainSlots = inventory.getMain().slots();
        int index = 0;
        for (Slot slot : mainSlots) {
            if (slot.peek().isPresent()) {
                main.put(SlotIndex.of(index), slot.peek().get());
            }
            index++;
        }
        headwear = inventory.getEquipment().getSlot(EquipmentTypes.HEADWEAR).get().peek();
        chestplate = inventory.getEquipment().getSlot(EquipmentTypes.CHESTPLATE).get().peek();
        leggings = inventory.getEquipment().getSlot(EquipmentTypes.LEGGINGS).get().peek();
        boots = inventory.getEquipment().getSlot(EquipmentTypes.BOOTS).get().peek();
        offHand = inventory.getOffhand().peek();
    }

    /**
     * Constructs a Kit with groups of ItemStacks that may appear in a PlayerInventory.
     *
     * @param id the id of this Kit
     * @param main the main grid of this Kit
     * @param headwear the headwear of this Kit
     * @param chestplate the chestplate of this Kit
     * @param leggings the leggins of this Kit
     * @param boots the boots of this Kit
     * @param offHand the offhand ItemStack of this Kit
     */
    public Kit(String id, Map<SlotIndex, ItemStack> main, Optional<ItemStack> headwear, Optional<ItemStack> chestplate,
               Optional<ItemStack> leggings, Optional<ItemStack> boots, Optional<ItemStack> offHand) {
        this.id = id;
        this.main = main;
        this.headwear = headwear;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.offHand = offHand;
    }

    /**
     * Equips the given Player with this Kit. This function does not clear the original inventory of the given player,
     * but may override existing slots that are conflicted with this Kit.
     *
     * @param player the Player to be equipped
     */
    public void equip(Player player) {
        PlayerInventory inventory = (PlayerInventory)player.getInventory();
        for (SlotIndex slotIndex : main.keySet()) {
            inventory.getMain().set(slotIndex, main.get(slotIndex).copy());
        }
        if (headwear.isPresent()) {
            player.setHelmet(headwear.get().copy());
        }
        if (chestplate.isPresent()) {
            player.setChestplate(chestplate.get().copy());
        }
        if (leggings.isPresent()) {
            player.setLeggings(leggings.get().copy());
        }
        if (boots.isPresent()) {
            player.setBoots(boots.get().copy());
        }
        if (offHand.isPresent()) {
            inventory.getOffhand().set(offHand.get().copy());
        }
    }

    /**
     * Gets the id of this Kit.
     *
     * @return the id of this Kit
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the main grid of this Kit in Map.
     *
     * @return the main grid of this Kit in Map
     */
    public Map<SlotIndex, ItemStack> getMain() {
        return main;
    }

    /**
     * Gets the headwear of this Kit.
     *
     * @return the headwear of this Kit
     */
    public Optional<ItemStack> getHeadwear() {
        return headwear;
    }

    /**
     * Gets the chestplate of this Kit.
     *
     * @return the chestplate of this Kit
     */
    public Optional<ItemStack> getChestplate() {
        return chestplate;
    }

    /**
     * Gets the leggings of this Kit.
     *
     * @return the leggings of this Kit
     */
    public Optional<ItemStack> getLeggings() {
        return leggings;
    }

    /**
     * Gets the boots of this Kit.
     *
     * @return the boots of this Kit
     */
    public Optional<ItemStack> getBoots() {
        return boots;
    }

    /**
     * Gets the offhand ItemStack of this Kit.
     *
     * @return the offhand ItemStack of this kit
     */
    public Optional<ItemStack> getOffHand() {
        return offHand;
    }
}