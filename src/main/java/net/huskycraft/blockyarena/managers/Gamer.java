package net.huskycraft.blockyarena.managers;

import org.spongepowered.api.entity.living.player.Player;

/**
 * The Gamer class represents a player's gaming profile.
 */
public class Gamer {

    private Player player;

    /**
     * Constructs a Gamer profile.
     */
    public Gamer(Player player) {
        this.player = player;
    }
}
