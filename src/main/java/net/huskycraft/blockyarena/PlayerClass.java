package net.huskycraft.blockyarena;

import com.google.common.reflect.TypeToken;
import net.huskycraft.blockyarena.serializer.PlayerClassSerializer;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.type.GridInventory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayerClass {

    private BlockyArena plugin;
    private String className;
    private Path classConfig;
    private ConfigurationNode rootNode;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private PlayerClassSerializer playerClassSerializer;
    private ArrayList<ItemStack> itemStacks;

    public PlayerClass(BlockyArena plugin, String className, Inventory inventory) {
        this.plugin = plugin;
        this.className = className;
        this.playerClassSerializer = new PlayerClassSerializer(plugin);
        decodeInventory(inventory);

        initConfig();
    }

    public PlayerClass(BlockyArena plugin, Path classConfig) {
        this.plugin = plugin;
        this.classConfig = classConfig;
        this.playerClassSerializer = new PlayerClassSerializer(plugin);
        loader = HoconConfigurationLoader.builder().setPath(classConfig).build();

        loadConfig();
    }

    private void loadConfig() {
        try {
            rootNode = loader.load();
            ArrayList<ItemStack> itemStacks = new ArrayList<>();

            className = rootNode.getNode("Name").getString();

            HashMap<String, Object> itemProperties;
            ItemStack itemStack;
            for (ConfigurationNode index : rootNode.getNode("Inventory").getChildrenList()) {
                itemProperties = index.getValue(TypeToken.of(HashMap.class));
                itemStack = ItemStack.builder()
                        .itemType((ItemType) itemProperties.get("ItemType"))
                        .quantity((int) itemProperties.get("Quantity"))
                        .build();
                //itemStack = setEnchantmentData(itemStack, itemProperties);
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
            playerClassSerializer.serialize(TypeToken.of(PlayerClass.class), this, rootNode);
            loader.save(rootNode);

        } catch (ObjectMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
}
