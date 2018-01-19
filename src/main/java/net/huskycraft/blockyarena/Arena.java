package net.huskycraft.blockyarena;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * An Arena represents an specific region for dueling.
 */
public class Arena {

    public static BlockyArena plugin;

    private Path config; // the config file of the Arena

    private String ID; // the unique identification code of the Arena
    private Spawn teamSpawnA; // the spawn point for team A
    private Spawn teamSpawnB; // the spawn point for team B
    private Spawn lobbySpawn; // the optional common spawn point for all players

    /**
     * Constructs an arena with an ID and two team spawns.
     * @param ID a unique identification code of the Arena
     *           assuming that no existing arena has the same ID
     * @param teamSpawnA a spawn point for team A
     * @param teamSpawnB a spawn point for team B
     */
    public Arena(String ID, Spawn teamSpawnA, Spawn teamSpawnB) {
        this(ID, teamSpawnA, teamSpawnB, null);
    }

    /**
     * Constructs a regular arena with an ID and three spawns.
     * @param ID a unique identification code of the Arena
     *           assuming that no existing arena has the same ID
     * @param teamSpawnA a spawn point for team A
     * @param teamSpawnB a spawn point for team B
     * @param lobbySpawn an optional common spawn point for all players
     */
    public Arena(String ID, Spawn teamSpawnA, Spawn teamSpawnB, Spawn lobbySpawn) {
        this.ID = ID;
        this.teamSpawnA = teamSpawnA;
        this.teamSpawnB = teamSpawnB;
        this.lobbySpawn = lobbySpawn;

        config = Paths.get(plugin.getArenaDir().toString() + File.separator + ID + ".conf");
        try {
            if (!config.toFile().exists()) {
                Files.createFile(config);
            }
        } catch (IOException e) {
            plugin.getLogger().warn("Error creating arena config file for " + ID);
        }
        writeConfig(config);
    }

    /**
     * Reconstructs an arena from an existing arena config file.
     * @param config a config file storing an arena's data in standard format
     */
    public Arena(Path config) {
        this.config = config;
        readConfig(config);
    }

    /**
     * Updates the given arena config file based on the object's data fields.
     * @param config
     */
    public void writeConfig(Path config) {
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader
                .builder().setPath(config).build();

        try {
            ConfigurationNode rootNode = loader.load();
            rootNode.getNode("id").setValue(ID);
            rootNode.getNode("teamSpawnA").setValue(TypeToken.of(Spawn.class), teamSpawnA);
            rootNode.getNode("teamSpawnB").setValue(TypeToken.of(Spawn.class), teamSpawnB);
            rootNode.getNode("lobbySpawn").setValue(TypeToken.of(Spawn.class), lobbySpawn);
            loader.save(rootNode);
        } catch (IOException e) {
            plugin.getLogger().warn("Error writing arena config.");
        } catch (ObjectMappingException e) {

        }
    }

    /**
     * Updates the fields of this object by reading the given config file.
     * @param config
     */
    public void readConfig(Path config) {
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader
                .builder().setPath(config).build();

        try {
            ConfigurationNode rootNode = loader.load();
            ID = rootNode.getNode("id").getString();
            teamSpawnA = rootNode.getNode("teamSpawnA").getValue(TypeToken.of(Spawn.class));
            teamSpawnB = rootNode.getNode("teamSpawnB").getValue(TypeToken.of(Spawn.class));
            lobbySpawn = rootNode.getNode("lobbySpawn").getValue(TypeToken.of(Spawn.class));
            loader.save(rootNode);
        } catch (IOException e) {
            plugin.getLogger().warn("Error reading arena config.");
        } catch (ObjectMappingException e) {

        }
    }
}