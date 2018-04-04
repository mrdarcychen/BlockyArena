package net.huskycraft.blockyarena.managers;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.utils.Gamer;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

/**
 * The manager that manages {@link Gamer}s.
 */
public class GamersManager {

    public static final BlockyArena plugin = BlockyArena.getPlugin();

    private static Set<Gamer> gamers = new HashSet<>();

    /**
     * Registers a new Gamer with the given {@link UUID}.
     *
     * @param uniqueId the {@link UUID} of the Gamer
     */
    public static void register(UUID uniqueId) {
        gamers.add(new Gamer(uniqueId));
    }

    /**
     * Gets a registered {@link Gamer} by their {@link UUID}.
     *
     * m uniqueId the {@link UUID} of this {@link Gamer}
     * @return {@link Gamer} or Optional.empty() if not found
     */
    public static Optional<Gamer> getGamer(UUID uniqueId) {
        for (Gamer gamer : gamers) {
            if (gamer.getUniqueId().equals(uniqueId)) {
                return Optional.of(gamer);
            }
        }
        return Optional.empty();
    }
}
