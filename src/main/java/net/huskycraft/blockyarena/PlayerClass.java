package net.huskycraft.blockyarena;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

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
        itemStacks = new ArrayList<>();
        decodeInventory(inventory);
    }

    public void decodeInventory(Inventory inventory) {
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

    public void offerItemStacksTo(Player player) {
        for (ItemStack itemStack : itemStacks) {
            player.getInventory().offer(itemStack);
        }
    }
}
