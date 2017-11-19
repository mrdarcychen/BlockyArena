package net.huskycraft.blockyarena;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

/**
 * The Gamer class represents a player's gaming profile.
 */
public class Gamer {

    private User user;

    private Session session;

    /**
     * Constructs a unique Gamer profile for the given user.
     */
    public Gamer(User user) {
        this.user = user;
    }

    public Player getPlayer() {
        return user.getPlayer().get();
    }

    /**
     * Sets the session the player is currently in.
     */
    public void setSession(Session session) {
        this.session = session;
    }

    /**
     * Gets the session the player is currently in, if there is one.
     * @return null if the player is not in any session
     */
    public Session getSession() {
        return session;
    }
}
