package net.huskycraft.blockyarena;

import org.spongepowered.api.entity.living.player.User;

/**
 * The Gamer class represents a player's gaming profile.
 */
public class Gamer {

    private User user;

    /**
     * Constructs a unique Gamer profile for the given user.
     */
    public Gamer(User user) {
        this.user = user;
    }
}
