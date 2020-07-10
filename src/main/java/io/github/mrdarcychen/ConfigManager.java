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

package io.github.mrdarcychen;

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.config.DefaultConfig;

import java.io.IOException;
import java.nio.file.Path;

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

    public void load(Path defaultConfigDir) {
        loader = HoconConfigurationLoader.builder().setPath(defaultConfigDir).build();

        //If file does not exist, we create it.
        if (!defaultConfigDir.toFile().exists()) {
            Utility.info("Creating a default config for BlockyArena.");
            this.rootNode = loader.createEmptyNode(ConfigurationOptions.defaults());
            this.rootNode.getNode("timers", "lobby", "cooldownSec").setValue(15);


            try {
                loader.save(rootNode);
            } catch (IOException e) {
                // handle error
            }


        } else {
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

        Utility.info("Configuration reloaded for BlockyArena!");
    }

    /*
     * Convenience method to get a node from config file
     */
    public ConfigurationNode getConfNode(Object... path) {
        return this.rootNode.getNode(path);
    }

    // return preset countdown time in seconds
    public int getLobbyCountdown() {
        return rootNode.getNode("timers", "lobby", "cooldownSec").getInt();
    }
}
