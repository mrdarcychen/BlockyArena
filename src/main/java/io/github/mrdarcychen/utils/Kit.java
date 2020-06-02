/*
 * Copyright 2017-2020 The BlockyArena Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.mrdarcychen.utils;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.property.SlotIndex;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * A Kit represents a player's equipments and a collection of ItemStacks that appear in the hotbar and main grid.
 */
public class Kit {

    private final String id;
    private final Map<SlotIndex, ItemStack> main;
    private final Optional<ItemStack> headwear;
    private final Optional<ItemStack> chestplate;
    private final Optional<ItemStack> leggings;
    private final Optional<ItemStack> boots;
    private final Optional<ItemStack> offHand;

    /**
     * Constructs a Kit based on the given player's inventory.
     *
     * @param player the player whose inventory is referenced by this Kit
     */
    public Kit(Player player, String id) {
        this.id = id;
        main = new TreeMap<>();
        PlayerInventory inventory = (PlayerInventory) player.getInventory();
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
        player.getInventory().clear();
        PlayerInventory inventory = (PlayerInventory) player.getInventory();
        for (SlotIndex slotIndex : main.keySet()) {
            inventory.getMain().set(slotIndex, main.get(slotIndex).copy());
        }
        headwear.ifPresent(itemStack -> player.setHelmet(itemStack.copy()));
        chestplate.ifPresent(itemStack -> player.setChestplate(itemStack.copy()));
        leggings.ifPresent(itemStack -> player.setLeggings(itemStack.copy()));
        boots.ifPresent(itemStack -> player.setBoots(itemStack.copy()));
        offHand.ifPresent(itemStack -> inventory.getOffhand().set(itemStack.copy()));
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