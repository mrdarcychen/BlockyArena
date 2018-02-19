package net.huskycraft.blockyarena.utils;

import com.google.common.reflect.TypeToken;
import net.huskycraft.blockyarena.BlockyArena;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class KitSerializer implements TypeSerializer<Kit> {

    public static BlockyArena plugin;

    public KitSerializer(BlockyArena plugin) {
        this.plugin = plugin;
    }

    @Override
    public Kit deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        String id = value.getNode("id").getString();
        Optional<ItemStack> headwear = Optional.of(value.getNode("headwear").getValue(TypeToken.of(ItemStack.class)));
        Optional<ItemStack> chestplate = Optional.of(value.getNode("chestplate").getValue(TypeToken.of(ItemStack.class)));
        Optional<ItemStack> leggings = Optional.of(value.getNode("leggings").getValue(TypeToken.of(ItemStack.class)));
        Optional<ItemStack> boots = Optional.of(value.getNode("boots").getValue(TypeToken.of(ItemStack.class)));
        Optional<ItemStack> offHand = Optional.of(value.getNode("offHand").getValue(TypeToken.of(ItemStack.class)));
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
        value.getNode("headwear").setValue(TypeToken.of(ItemStack.class), obj.getHeadwear().get());
        value.getNode("chestplate").setValue(TypeToken.of(ItemStack.class), obj.getChestplate().get());
        value.getNode("leggings").setValue(TypeToken.of(ItemStack.class), obj.getLeggings().get());
        value.getNode("boots").setValue(TypeToken.of(ItemStack.class), obj.getBoots().get());
        value.getNode("offHand").setValue(TypeToken.of(ItemStack.class), obj.getOffHand().get());
        for (SlotIndex index : obj.getMain().keySet()) {
            ConfigurationNode itemNode = value.getNode("main").getNode(index.getValue().toString());
            itemNode.setValue(TypeToken.of(ItemStack.class), obj.getMain().get(index));
        }
    }
}
