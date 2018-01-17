package net.huskycraft.blockyarena;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Arena {

    public static BlockyArena plugin;

    private Path arenaConfig; // the config file of this Arena
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode rootNode;

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
    }

    /**
     * Reconstructs an Arena from an existing arena config file.
     */
    public Arena(Path arenaConfig) {
        this.arenaConfig = arenaConfig;
        loader = HoconConfigurationLoader.builder().setPath(arenaConfig).build();

        loadConfig();
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
            // write teamspawns to config
            loader.save(rootNode);
        } catch (IOException e) {
            plugin.getLogger().warn("Error writing arena config.");
        }

    }

    private void loadConfig() {
        try {
            rootNode = loader.load();

            String worldName = rootNode.getNode("World-Name").getString();
            if (!Sponge.getServer().getWorld(worldName).isPresent()) {
                plugin.getLogger().warn(worldName + " is not present.");
            }
            extent = Sponge.getServer().getWorld(worldName).get();
            arenaName = rootNode.getNode("Arena-Name").getString();
            lobbyCountdown = rootNode.getNode("Lobby-Countdown").getInt();
            gameCountdown = rootNode.getNode("Game-Countdown").getInt();
            maxDeaths = rootNode.getNode("Max-Deaths").getInt();
            minPlayer = rootNode.getNode("Min-Player").getInt();

            lobbySpawn = new Location(extent,
                    rootNode.getNode("Locations", "Lobby-Spawn", "X").getDouble(),
                    rootNode.getNode("Locations", "Lobby-Spawn", "Y").getDouble(),
                    rootNode.getNode("Locations", "Lobby-Spawn", "Z").getDouble());

            redTeamSpawn = new Location(extent,
                    rootNode.getNode("Locations", "Red-Team-Spawn", "X").getDouble(),
                    rootNode.getNode("Locations", "Red-Team-Spawn", "Y").getDouble(),
                    rootNode.getNode("Locations", "Red-Team-Spawn", "Z").getDouble());

            blueTeamSpawn = new Location(extent,
                    rootNode.getNode("Locations", "Blue-Team-Spawn", "X").getDouble(),
                    rootNode.getNode("Locations", "Blue-Team-Spawn", "Y").getDouble(),
                    rootNode.getNode("Locations", "Blue-Team-Spawn", "Z").getDouble());

        } catch (Exception e) {
            plugin.getLogger().warn("Error loading arena config.");
        }
    }

    public void setWorld(World world) {

        try {
            rootNode = loader.load();
            rootNode.getNode("World-Name").setValue(world.getName());
            loader.save(rootNode);
        } catch (IOException e) {
            plugin.getLogger().warn("Error saving spawn world extent.");
        }
    }

    public void setLobbySpawn(Location<World> spawn) {
        this.lobbySpawn = spawn;

        double lobbySpawnX = spawn.getX();
        double lobbySpawnY = spawn.getY();
        double lobbySpawnZ = spawn.getZ();

        try {
            rootNode = loader.load();
            rootNode.getNode("Locations", "Lobby-Spawn", "X").setValue(lobbySpawnX);
            rootNode.getNode("Locations", "Lobby-Spawn", "Y").setValue(lobbySpawnY);
            rootNode.getNode("Locations", "Lobby-Spawn", "Z").setValue(lobbySpawnZ);
            loader.save(rootNode);
        } catch (IOException e) {
            plugin.getLogger().warn("Error saving lobby spawn.");
        }
    }

    public void setRedTeamSPawn(Location<World> spawn) {
        this.redTeamSpawn = spawn;

        double redTeamSpawnX = spawn.getX();
        double redTeamSpawnY = spawn.getY();
        double redTeamSpawnZ = spawn.getZ();

        try {
            rootNode = loader.load();
            rootNode.getNode("Locations", "Red-Team-Spawn", "X").setValue(redTeamSpawnX);
            rootNode.getNode("Locations", "Red-Team-Spawn", "Y").setValue(redTeamSpawnY);
            rootNode.getNode("Locations", "Red-Team-Spawn", "Z").setValue(redTeamSpawnZ);
            loader.save(rootNode);
        } catch (IOException e) {
            plugin.getLogger().warn("Error saving red team spawn.");
        }
    }

    public void setBlueTeamSpawn(Location<World> spawn) {
        this.blueTeamSpawn = spawn;

        double blueTeamSpawnX = spawn.getX();
        double blueTeamSpawnY = spawn.getY();
        double blueTeamSpawnZ = spawn.getZ();

        try {
            rootNode = loader.load();
            rootNode.getNode("Locations", "Blue-Team-Spawn", "X").setValue(blueTeamSpawnX);
            rootNode.getNode("Locations", "Blue-Team-Spawn", "Y").setValue(blueTeamSpawnY);
            rootNode.getNode("Locations", "Blue-Team-Spawn", "Z").setValue(blueTeamSpawnZ);
            loader.save(rootNode);
        } catch (IOException e) {
            plugin.getLogger().warn("Error saving blue team spawn.");
        }
    }

    public void setIsOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public Location getLobbySpawn() {
        return lobbySpawn;
    }

    public Location getRedSpawn() {
        return redTeamSpawn;
    }

    public Location getBlueSpawn() {
        return blueTeamSpawn;
    }

    public int getLobbyCountdown() {
        return lobbyCountdown;
    }

    public int getGameCountdown() {
        return gameCountdown;
    }

    public int getMinPlayer() {
        return minPlayer;
    }
}