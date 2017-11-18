package net.huskycraft.blockyarena.managers;

import net.huskycraft.blockyarena.BlockyArena;
import org.spongepowered.api.entity.living.player.Player;

import java.util.SortedMap;
import java.util.TreeMap;

public class GamerManager {

    private BlockyArena plugin;

    private SortedMap<Player, Gamer> gamers;

    public GamerManager(BlockyArena plugin) {
        this.plugin = plugin;
        gamers = new TreeMap<>();
    }

    /**
     * Registers a first join player by creating a unique Gamer profile for the player.
     * @param player a player who has not played before
     */
    public void register(Player player) {
        gamers.put(player, new Gamer(player));
    }

    /**
     * Gets the Gamer profile of the given Player.
     * @param player a player who has been registered on his first join
     * @return the Gamer profile of the given Player
     */
    public Gamer getGamer(Player player) {
        return gamers.get(player);
    }
}
