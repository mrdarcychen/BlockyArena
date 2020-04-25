package net.huskycraft.blockyarena.games;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import net.huskycraft.blockyarena.utils.Gamer;

/**
 * The manager that manages {@link Gamer}s.
 */
public class GamersManager {

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
