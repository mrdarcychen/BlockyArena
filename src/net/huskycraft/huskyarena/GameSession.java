package net.huskycraft.huskyarena;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GameSession {

    private HuskyArena plugin;

    private Arena arena;

    private ArrayList<Player> players;
    private ArrayList<Player> blueTeam;
    private ArrayList<Player> redTeam;

    public boolean status;

    public GameSession(HuskyArena plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
        players = new ArrayList<>();
    }

    public Arena getArena() {
        return arena;
    }

    public void add(Player player) {
        players.add(player);
        player.setLocation(arena.getLobbySpawn());
    }
}
