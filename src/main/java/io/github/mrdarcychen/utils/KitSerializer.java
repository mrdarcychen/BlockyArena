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

import com.google.common.reflect.TypeToken;
import io.github.mrdarcychen.BlockyArena;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;

import java.util.HashMap;
import java.util.Map;

public class KitSerializer implements TypeSerializer<Kit> {

    public KitSerializer() {}

    @Override
    public Kit deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        String id = value.getNode("id").getString();
        ItemStack headwear = value.getNode("headwear").getValue(TypeToken.of(ItemStack.class));
        ItemStack chestplate = value.getNode("chestplate").getValue(TypeToken.of(ItemStack.class));
        ItemStack leggings = value.getNode("leggings").getValue(TypeToken.of(ItemStack.class));
        ItemStack boots = value.getNode("boots").getValue(TypeToken.of(ItemStack.class));
        ItemStack offHand = value.getNode("offHand").getValue(TypeToken.of(ItemStack.class));
        Map<SlotIndex, ItemStack> main = new HashMap<>();
        for (ConfigurationNode indexNode : value.getNode("main").getChildrenMap().values()) {
            SlotIndex index = SlotIndex.of(indexNode.getKey());
            ItemStack itemStack = indexNode.getValue(TypeToken.of(ItemStack.class));
            main.put(index, itemStack);
        }
        return new Kit(id, main, headwear, chestplate, leggings, boots, offHand);
    }

    @Override
    public void serialize(TypeToken<?> type, Kit obj, ConfigurationNode value) throws ObjectMappingException {
        value.getNode("id").setValue(obj.getId());
        value.getNode("headwear").setValue(TypeToken.of(ItemStack.class), obj.getHeadwear().orElse(null));
        value.getNode("chestplate").setValue(TypeToken.of(ItemStack.class), obj.getChestplate().orElse(null));
        value.getNode("leggings").setValue(TypeToken.of(ItemStack.class), obj.getLeggings().orElse(null));
        value.getNode("boots").setValue(TypeToken.of(ItemStack.class), obj.getBoots().orElse(null));
        value.getNode("offHand").setValue(TypeToken.of(ItemStack.class), obj.getOffHand().orElse(null));
        for (SlotIndex index : obj.getMain().keySet()) {
            ConfigurationNode itemNode = value.getNode("main").getNode(index.getValue().toString());
            itemNode.setValue(TypeToken.of(ItemStack.class), obj.getMain().get(index));
        }
    }
}
