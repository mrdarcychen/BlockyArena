package net.huskycraft.blockyarena.arenas;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import net.huskycraft.blockyarena.BlockyArena;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.world.Location;

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
    private Spawn lobbySpawn; // the spawn point for all players waiting in the queue
    private Spawn spectatorSpawn; // the spawn point for eliminated players

    private ArenaState state;

    /**
     * Constructs an arena with only an ID.
     * @param ID a unique identification code of the Arena
     *           assuming that no existing arena has the same ID
     */
    public Arena(BlockyArena plugin, String ID) {
        this(plugin, ID, null, null, null, null);
        state = ArenaState.INCOMPLETE;
    }

    /**
     * Constructs a regular arena with an ID and three spawns.
     * @param ID a unique identification code of the Arena
     *           assuming that no existing arena has the same ID
     * @param teamSpawnA a spawn point for team A
     * @param teamSpawnB a spawn point for team B
     * @param lobbySpawn an optional common spawn point for all players
     */
    public Arena(BlockyArena plugin, String ID, Spawn teamSpawnA, Spawn teamSpawnB, Spawn lobbySpawn, Spawn spectatorSpawn) {
        this.plugin = plugin;
        this.ID = ID;
        this.teamSpawnA = teamSpawnA;
        this.teamSpawnB = teamSpawnB;
        this.lobbySpawn = lobbySpawn;
        this.spectatorSpawn = spectatorSpawn;

        config = Paths.get(plugin.getArenaDir().toString() + File.separator + ID + ".conf");
        try {
            if (!config.toFile().exists()) {
                Files.createFile(config);
            }
        } catch (IOException e) {
            plugin.getLogger().warn("Error creating arena config file for " + ID);
        }
        writeConfig();
    }

    /**
     * Reconstructs an arena from an existing arena config file.
     * @param config a config file storing an arena's data in standard format
     */
    public Arena(BlockyArena plugin, Path config) {
        this.plugin = plugin;
        this.config = config;
        readConfig(config);
        state = ArenaState.ENABLE;
    }

    /**
     * Updates the given arena config file based on the object's data fields.
     */
    public void writeConfig() {
        if (state == ArenaState.INCOMPLETE) {
            plugin.getLogger().warn(ID + " is incomplete. Cancel writing to config.");
        } else {
            ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader
                    .builder().setPath(config).build();

            try {
                ConfigurationNode rootNode = loader.load();
                rootNode.getNode("id").setValue(ID);
                rootNode.getNode("teamSpawnA").setValue(TypeToken.of(Spawn.class), teamSpawnA);
                rootNode.getNode("teamSpawnB").setValue(TypeToken.of(Spawn.class), teamSpawnB);
                rootNode.getNode("lobbySpawn").setValue(TypeToken.of(Spawn.class), lobbySpawn);
                rootNode.getNode("spectatorSpawn").setValue(TypeToken.of(Spawn.class), spectatorSpawn);
                loader.save(rootNode);
            } catch (IOException e) {
                plugin.getLogger().warn("Error writing arena config.");
            } catch (ObjectMappingException e) {

            }
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
            plugin.getLogger().warn("ID is " + ID);
            teamSpawnA = rootNode.getNode("teamSpawnA").getValue(TypeToken.of(Spawn.class));
            plugin.getLogger().warn("TeamSpawnA: " + String.valueOf(teamSpawnA == null));
            teamSpawnB = rootNode.getNode("teamSpawnB").getValue(TypeToken.of(Spawn.class));
            lobbySpawn = rootNode.getNode("lobbySpawn").getValue(TypeToken.of(Spawn.class));
            plugin.getLogger().warn("LobbySpawn: " + String.valueOf(lobbySpawn == null));
            spectatorSpawn = rootNode.getNode("spectatorSpawn").getValue(TypeToken.of(Spawn.class));
            loader.save(rootNode);
        } catch (IOException e) {
            plugin.getLogger().warn("Error reading arena config.");
        } catch (ObjectMappingException e) {

        }
    }

    public String getID() {
        return ID;
    }

    public ArenaState getState() {
        return state;
    }

    public void setTeamSpawnA(Location location, Vector3d rotation) {
        teamSpawnA = new Spawn(location, rotation);
        updateArenaState();
    }

    public void setTeamSpawnB(Location location, Vector3d rotation) {
        teamSpawnB = new Spawn(location, rotation);
        updateArenaState();
    }

    public void setLobbySpawn(Location location, Vector3d rotation) {
        lobbySpawn = new Spawn(location, rotation);
        updateArenaState();
    }

    public void setSpectatorSpawn(Location location, Vector3d rotation) {
        spectatorSpawn = new Spawn(location, rotation);
        updateArenaState();
    }

    public Spawn getTeamSpawnA() {
        return teamSpawnA;
    }

    public Spawn getTeamSpawnB() {
        return teamSpawnB;
    }

    public Spawn getLobbySpawn() {
        return lobbySpawn;
    }

    public Spawn getSpectatorSpawn() {
        return spectatorSpawn;
    }

    public void updateArenaState() {
        if (teamSpawnA != null && teamSpawnB != null && lobbySpawn != null && spectatorSpawn != null) {
            state = ArenaState.ENABLE;
            writeConfig();
            plugin.getLogger().warn("Arena is enabled!");
        }
    }
}