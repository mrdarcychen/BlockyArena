package net.huskycraft.huskyarena;

import net.huskycraft.huskyarena.HuskyArena;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import java.io.File;
import java.io.IOException;

public class Arena {

    HuskyArena plugin;
    private File arenaConfig;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode rootNode;

    public Arena(HuskyArena plugin, String name, int lobbyCountdown, int gameCountdown) {

        this.plugin = plugin;
        arenaConfig = new File(plugin.getArenaDir().toFile(), name + ".conf");
        loader = HoconConfigurationLoader.builder().setFile(arenaConfig).build();

        try {
            rootNode = loader.load();
            rootNode.getNode("arena-name").setValue(name);
            rootNode.getNode("lobby-countdown").setValue(lobbyCountdown);
            rootNode.getNode("game-countdown").setValue(gameCountdown);
            rootNode.getNode("red-team-size").setValue(null);
            rootNode.getNode("blue-team-size").setValue(null);
            rootNode.getNode("max-deaths").setValue(1);

            rootNode.getNode("Locations", "lobby-spawn").setValue(null);
            rootNode.getNode("Locations", "red-team-spawn").setValue(null);
            rootNode.getNode("Locations", "blue-team-spawn").setValue(null);
            loader.save(rootNode);
        } catch (IOException e) {
            plugin.getLogger().warn("Error loading arena config file.");
        }
    }
}
