//package net.huskycraft.blockyarena.managers;
//
//import net.huskycraft.blockyarena.BlockyArena;
//import net.huskycraft.blockyarena.PlayerClass;
//import org.spongepowered.api.Sponge;
//import org.spongepowered.api.item.Enchantment;
//
//import java.io.IOException;
//import java.nio.file.DirectoryStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class PlayerClassManager {
//
//    private BlockyArena plugin;
//    private ArrayList<PlayerClass> playerClasses;
//    private HashMap<Path, Boolean> playerClassFiles;
//    private List<Enchantment> enchantments;
//
//    public PlayerClassManager(BlockyArena plugin) {
//        this.plugin = plugin;
//        playerClasses = new ArrayList<>();
//        playerClassFiles = new HashMap<>();
//        enchantments = Sponge.getRegistry().getAllOf(Enchantment.class).stream().collect(Collectors.toList());
//        loadPlayerClasses();
//    }
//
//    private void loadPlayerClasses() {
//        try {
//            DirectoryStream<Path> stream = Files.newDirectoryStream(plugin.getClassDir(), "*.conf");
//            for (Path path : stream) {
//                PlayerClass playerClass = new PlayerClass(plugin, path);
//                playerClasses.add(playerClass);
//            }
//        } catch (IOException e) {
//            plugin.getLogger().warn("Error loading existing arena configs.");
//        }
//    }
//
//    public void addPlayerClass(PlayerClass playerClass) {
//        playerClasses.add(playerClass);
//    }
//
//    public PlayerClass getPlayerClass(String className) {
//        for (PlayerClass playerClass : playerClasses) {
//            if (playerClass.getClassName().equals(className)) {
//                return playerClass;
//            }
//        }
//        return null;
//    }
//}
