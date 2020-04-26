package net.huskycraft.blockyarena.managers;

import com.google.inject.Inject;
import net.huskycraft.blockyarena.BlockyArena;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.config.DefaultConfig;

import java.io.IOException;

public class ConfigManager {

    //Singleton pattern
    private static final ConfigManager INSTANCE = new ConfigManager();

	private ConfigManager() {

	}

	public static ConfigManager getInstance() {
		return INSTANCE;
    }

    // rootNode for config
    private ConfigurationNode rootNode;

    // The HOCON Loader for our config !
    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    public void load() {
        loader = HoconConfigurationLoader.builder().setPath(BlockyArena.getInstance().getDefaultConfig()).build();

        //If file does not exist, we create it.
        if (!BlockyArena.getInstance().getDefaultConfig().toFile().exists()) {
            BlockyArena.getInstance().getLogger().error("default.conf doesnt exist !!");
            BlockyArena.getInstance().getLogger().error("Creating default.conf for you.");

            this.rootNode = loader.createEmptyNode(ConfigurationOptions.defaults());
            this.rootNode.getNode("timers", "lobby", "cooldownSec").setValue(15);
            this.rootNode.getNode("general", "broadcast", "join").setValue(true);

            try {
                loader.save(rootNode);
            } catch (IOException e) {
                // handle error
            }
        } else {
            BlockyArena.getInstance().getLogger().warn("default.conf does exist !!");
            BlockyArena.getInstance().getLogger().warn("Loading config !");

            try {
                this.rootNode = loader.load();
                loader.save(rootNode);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
			}
	    }

    /*
     * Reload the configuration file !
     */
    public void reloadConfiguration() {
        try {
            this.rootNode = loader.load();
            loader.save(rootNode);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BlockyArena.getInstance().getLogger().info("Configuration reloaded !");
    }
	
	public ConfigurationNode getRootNode() {
		return this.rootNode;
	}

    /*
     * Convenience method to get a node from config file
     */
    public ConfigurationNode getConfNode(Object... path) {
        return this.rootNode.getNode(path);
    }

}
