package net.huskycraft.blockyarena.managers;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.Gamer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

import java.util.SortedMap;
import java.util.TreeMap;

public class GamerManager {

    private BlockyArena plugin;

    private SortedMap<User, Gamer> gamers;

    public GamerManager(BlockyArena plugin) {
        this.plugin = plugin;
        gamers = new TreeMap<>();
    }

    /**
     * Registers a first join player by creating a unique Gamer profile for the player.
     * @param user a user who has not played before
     */
    public void register(User user) {
        gamers.put(user, new Gamer(user));
    }

    /**
     * Gets the Gamer profile of the given Player.
     * @param user a user who has been registered on his first join
     * @return the Gamer profile of the given Player
     */
    public Gamer getGamer(User user) {
        return gamers.get(user);
    }
}
