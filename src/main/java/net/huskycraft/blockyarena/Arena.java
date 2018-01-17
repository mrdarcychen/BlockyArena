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

public class Arena {

    public static BlockyArena plugin;

    private Path arenaConfig; // the config file of this Arena

    private String ID;
    private TeamSpawn teamSpawnA; // the TeamSpawn data for team A
    private TeamSpawn teamSpawnB; // the TeamSpawn data for team B

    /**
     * Constructs an Arena with a new ID and two TeamSpawns.
     */
    public Arena(String ID, TeamSpawn teamSpawnA, TeamSpawn teamSpawnB) {
        this.ID = ID;
        this.teamSpawnA = teamSpawnA;
        this.teamSpawnB = teamSpawnB;

        arenaConfig = Paths.get(plugin.getArenaDir().toString() + File.separator + ID + ".conf");
        try {
            if (!arenaConfig.toFile().exists()) {
                Files.createFile(arenaConfig);
            }
        } catch (IOException e) {
            plugin.getLogger().warn("Error creating arena config file for " + ID);
        }
        writeConfig(arenaConfig);
    }

    /**
     * Reconstructs an Arena from an existing arena config file.
     */
    public Arena(Path arenaConfig) {
        this.arenaConfig = arenaConfig;
        readConfig(arenaConfig);
    }


    /**
     * Updates the given arena config file based on the object's data fields.
     * @param arenaConfig
     */
    public void writeConfig(Path arenaConfig) {
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader
                .builder().setPath(arenaConfig).build();

        try {
            ConfigurationNode rootNode = loader.load();
            rootNode.getNode("id").setValue(ID);
            rootNode.getNode("teamSpawnA").setValue(TypeToken.of(TeamSpawn.class), teamSpawnA);
            rootNode.getNode("teamSpawnB").setValue(TypeToken.of(TeamSpawn.class), teamSpawnB);
            loader.save(rootNode);
        } catch (IOException e) {
            plugin.getLogger().warn("Error writing arena config.");
        } catch (ObjectMappingException e) {

        }

    }

    /**
     * Updates the fields of this object by reading the given config file.
     * @param arenaConfig
     */
    public void readConfig(Path arenaConfig) {
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader
                .builder().setPath(arenaConfig).build();

        try {
            ConfigurationNode rootNode = loader.load();
            ID = rootNode.getNode("id").getString();
            teamSpawnA = rootNode.getNode("teamSpawnA").getValue(TypeToken.of(TeamSpawn.class));
            teamSpawnB = rootNode.getNode("teamSpawnB").getValue(TypeToken.of(TeamSpawn.class));
            loader.save(rootNode);
        } catch (IOException e) {
            plugin.getLogger().warn("Error reading arena config.");
        } catch (ObjectMappingException e) {

        }
    }
}