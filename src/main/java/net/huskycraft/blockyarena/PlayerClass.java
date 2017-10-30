package net.huskycraft.blockyarena;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.meta.ItemEnchantment;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.Enchantment;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerClass {

    private BlockyArena plugin;
    private String className;
    private Path classConfig;
    private ConfigurationNode rootNode;
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    private ArrayList<ItemStack> itemStacks;

    public PlayerClass(BlockyArena plugin, String className, Inventory inventory) {
        this.plugin = plugin;
        this.className = className;
        decodeInventory(inventory);

        initConfig();
    }

    public PlayerClass(BlockyArena plugin, Path classConfig) {
        this.plugin = plugin;
        this.classConfig = classConfig;
        loader = HoconConfigurationLoader.builder().setPath(classConfig).build();
        itemStacks = new ArrayList<>();

        loadConfig();
    }

    private void loadConfig() {
        try {
            rootNode = loader.load();

            className = rootNode.getNode("Name").getString();

            for (ConfigurationNode index : rootNode.getNode("Inventory").getChildrenMap().values()) {
                ItemStack itemStack = ItemStack.builder()
                        .itemType(index.getNode("ItemType").getValue(TypeToken.of(ItemType.class)))
                        .quantity(index.getNode("Quantity").getInt())
                        .build();
                EnchantmentData enchantmentData = itemStack.getOrCreate(EnchantmentData.class)
                        .get();
                for (ConfigurationNode itemEnch : index.getNode("Enchantments", "ItemEnchantments")
                             .getChildrenList()) {
                    Enchantment e = itemEnch.getNode("Enchantment").getValue(TypeToken.of
                            (Enchantment.class));
                    int level = itemEnch.getNode("Level").getInt();
                    enchantmentData.set(enchantmentData.enchantments().add(new ItemEnchantment
                            (e, level)));
                }
                itemStack.offer(enchantmentData);
                itemStacks.add(itemStack);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    public void decodeInventory(Inventory inventory) {
        itemStacks = new ArrayList<>();
        Iterable<Slot> slotIterable= inventory.slots();
        for (Slot slot : slotIterable) {
            if (slot.peek().isPresent()) {
                itemStacks.add(slot.peek().get());
            }
        }
    }

    private void initConfig() {
        classConfig = Paths.get(plugin.getClassDir().toString() + File.separator + className + ".conf");

        try {
            if (!classConfig.toFile().exists()) {
                Files.createFile(classConfig);
            }
        } catch (IOException e) {
            plugin.getLogger().warn("Error creating class config.");
        }

        loader = HoconConfigurationLoader.builder().setPath(classConfig).build();

        try {
            rootNode = loader.load();
            rootNode.getNode("Name").setValue(className);
            for (int i = 0; i < itemStacks.size(); i++) {
                ItemStack itemStack = itemStacks.get(i);
                EnchantmentData enchantmentData = itemStack.getOrCreate(EnchantmentData.class).get();
                rootNode.getNode("Inventory", Integer.toString(i), "ItemType")
                        .setValue(itemStack.getType().getName());
                rootNode.getNode("Inventory", Integer.toString(i), "Quantity")
                        .setValue(itemStack.getQuantity());

                rootNode.getNode("Inventory", Integer.toString(i), "Enchantments")
                        .setValue(TypeToken.of(EnchantmentData.class), enchantmentData);
//                        .setValue(getItemEnchantments(itemStack));

            }
            loader.save(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    public String getClassName() {
        return className;
    }

    public ArrayList<ItemStack> getItemStacks() {
        return itemStacks;
    }

    public void offerItemStacksTo(Player player) {
        plugin.getLogger().info("Size: " + itemStacks.size());
        for (ItemStack itemStack : itemStacks) {
            player.getInventory().offer(itemStack.copy());
        }
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

//    private ItemStack setEnchantmentData(ItemStack itemStack, Map<String, Integer>
//            itemProperties) {
//        EnchantmentData enchantmentData = itemStack.getOrCreate(EnchantmentData.class).get();
//        for (String s : itemProperties.keySet()) {
//            for (Enchantment enchantment : enchantments) {
//                if (enchantment.getName().equals(s)) {
//                    enchantmentData.set(enchantmentData.enchantments().add(new ItemEnchantment
//                            (enchantment, itemProperties.get(s))));
//                }
//            }
//        }
//        itemStack.offer(enchantmentData);
//        return itemStack;
//    }
}
