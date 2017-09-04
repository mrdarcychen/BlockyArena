package net.huskycraft.huskyarena;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
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
    private Location<World> lobbySpawn;
    private Location<World> redTeamSpawn;
    private Location<World> blueTeamSpawn;

    // dynamic configurations

    private boolean arenaStatus;
    private int redTeamSize;
    private int blueTeamSize;

    public Arena(HuskyArena plugin, String name, Location<World> lobbySpawn, Location<World> redTeamSpawn,
                 Location<World> blueTeamSpawn) {

        this.plugin = plugin;
        this.arenaName = name;
        this.lobbySpawn = lobbySpawn;
        this.redTeamSpawn = redTeamSpawn;
        this.blueTeamSpawn = blueTeamSpawn;

        initConfig();
    }

    public Arena(Path arenaConfig) {

        this.arenaConfig = arenaConfig;
        loader = HoconConfigurationLoader.builder().setPath(arenaConfig).build();

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
            rootNode.getNode("arena-name").setValue(arenaName);
            rootNode.getNode("lobby-countdown").setValue(10);
            rootNode.getNode("game-countdown").setValue(60);
            rootNode.getNode("max-deaths").setValue(1);

            rootNode.getNode("Locations", "lobby-spawn").setValue(lobbySpawn);
            rootNode.getNode("Locations", "red-team-spawn").setValue(redTeamSpawn);
            rootNode.getNode("Locations", "blue-team-spawn").setValue(blueTeamSpawn);
            loader.save(rootNode);

        } catch (IOException e) {
            plugin.getLogger().warn("Error initializing arena config.");
        }
    }

    private void loadConfig() {
        try {
            arenaName = rootNode.getNode("arena-name").getString();
            lobbyCountdown = rootNode.getNode("lobby-countdown").getInt();
            gameCountdown = rootNode.getNode("game-countdown").getInt();
            maxDeaths = rootNode.getNode("max-deaths").getInt();

            lobbySpawn = rootNode.getNode("Locations", "lobby-spawn").getValue(TypeToken.of(Location.class));
            redTeamSpawn = rootNode.getNode("Locations", "red-team-spawn").getValue(TypeToken.of(Location.class));
            blueTeamSpawn = rootNode.getNode("Locations", "blue-team-spawn").getValue(TypeToken.of(Location.class));
        } catch (Exception e) {
            plugin.getLogger().warn("Error loading arena config.");
        }
    }
}
