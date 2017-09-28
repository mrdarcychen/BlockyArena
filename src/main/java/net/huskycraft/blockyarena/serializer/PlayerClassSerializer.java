package net.huskycraft.blockyarena.serializer;

import com.google.common.reflect.TypeToken;
import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.PlayerClass;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.item.Enchantment;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerClassSerializer implements TypeSerializer<PlayerClass> {

    private BlockyArena plugin;
    private List<Enchantment> enchantments;

    public PlayerClassSerializer(BlockyArena plugin) {
        this.plugin = plugin;
        //this.enchantments = Sponge.getRegistry().getAllOf(Enchantment.class).stream().collect(Collectors.toList());
    }

    @Override
    public PlayerClass deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        return null;
    }

    @Override
    public void serialize(TypeToken<?> type, PlayerClass obj, ConfigurationNode value) throws ObjectMappingException {
        String className = obj.getClassName();
        ArrayList<ItemStack> itemStacks = obj.getItemStacks();

        value.getNode("Name").setValue(className);

        HashMap<String, Object> itemProperties = new HashMap<>();
        for (ItemStack itemStack : itemStacks) {
            itemProperties.put("ItemType", itemStack.getType().getName());
            itemProperties.put("Quantity", itemStack.getQuantity());
            //itemProperties.put("Enchantments", getItemEnchantments(itemStack));
            value.getNode("Inventory", Integer.toString(itemStacks.indexOf(itemStack))).setValue(itemProperties);
        }
    }


    private ItemStack setEnchantmentData(ItemStack itemStack, HashMap<String, Object> itemProperties) {
        if (!itemProperties.containsKey("Enchantments")) return itemStack;

        EnchantmentData enchantmentData = itemStack.getOrCreate(EnchantmentData.class).get();
        HashMap<String, Integer> itemEnchantments = (HashMap) itemProperties.get("Enchantments");
        for (Enchantment enchantment : this.enchantments) {
            if (itemEnchantments.keySet().contains(enchantment.getName())) {
                enchantmentData.set(enchantmentData.enchantments().add(new ItemEnchantment(enchantment, itemEnchantments.get(enchantment.getName()))));
            }
        }
        itemStack.offer(enchantmentData);

        return itemStack;
    }

    private HashMap<String, Integer> getItemEnchantments(ItemStack itemStack) {
        HashMap<String, Integer> enchantments = new HashMap<>();

        if (!itemStack.get(EnchantmentData.class).isPresent()) return null;

        ListValue<ItemEnchantment> data = itemStack.getOrCreate(EnchantmentData.class).get().enchantments();
        for (ItemEnchantment e : data) {
            enchantments.put(e.getEnchantment().getName(), e.getLevel());
        }

        return enchantments;
    }

}