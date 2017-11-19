package net.huskycraft.blockyarena;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class Session {

    private Set<Gamer> gamers;

    public Session() {
        gamers = new HashSet();
    }

    /**
     * Adds the given gamer to the session.
     */
    public void add(Gamer gamer) {
        gamers.add(gamer);
        // TODO: backups the gamer's inventory and clear it
        // TODO: sends the gamer to the waiting area
    }

    /**
     * Removes the given gamer from the session.
     */
    public void remove(Gamer gamer) {
        gamers.remove(gamer);
        // TODO: sends the gamer to his join location / main lobby
        // TODO: gives back the player's inventory
    }
}
