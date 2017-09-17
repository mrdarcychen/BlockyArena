package net.huskycraft.huskyarena;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Arena {

    HuskyArena plugin;

    private Path arenaConfig;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode rootNode;

    // fixed configurations

    private String arenaName;
    private int lobbyCountdown;
    private int gameCountdown;

    private int maxDeaths;
    private int minPlayer;
    private Location<World> lobbySpawn;
    private Location<World> redTeamSpawn;
    private Location<World> blueTeamSpawn;
    private World extent;

    // dynamic configurations

    private boolean status; //true if arena is paired with session, false otherwise
    private int redTeamSize;
    private int blueTeamSize;

    public Arena(HuskyArena plugin, String name) {

        this.plugin = plugin;
        this.arenaName = name;
        this.status = false;

        lobbyCountdown = 10;
        gameCountdown = 60;
        maxDeaths = 1;
        minPlayer = 2;
        initConfig();
    }

    public Arena(HuskyArena plugin, Path arenaConfig) {

        this.plugin = plugin;
        this.arenaConfig = arenaConfig;
        loader = HoconConfigurationLoader.builder().setPath(arenaConfig).build();
        this.status = false;

        loadConfig();
    }

    private void initConfig() {

        // creates a new config file in arenas directory

        arenaConfig = Paths.get(plugin.getArenaDir().toString() + File.separator + arenaName + ".conf");

        try {
            if (!arenaConfig.toFile().exists()) {
                Files.createFile(arenaConfig);
            }
        } catch (IOException e) {
            plugin.getLogger().warn("Error creating arena config.");
        }

        // initializes configuration nodes for the new config file

        loader = HoconConfigurationLoader.builder().setPath(arenaConfig).build();

        try {

            rootNode = loader.load();
            rootNode.getNode("Arena-Name").setValue(arenaName);
            rootNode.getNode("Lobby-Countdown").setValue(lobbyCountdown);
            rootNode.getNode("Game-Countdown").setValue(gameCountdown);
            rootNode.getNode("Max-Deaths").setValue(maxDeaths);
            rootNode.getNode("Min-Player").setValue(minPlayer);
            loader.save(rootNode);

        } catch (IOException e) {
            plugin.getLogger().warn("Error initializing arena config.");
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

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
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