package net.huskycraft.blockyarena;

import java.util.List;
import java.util.Set;

public abstract class Game {

    protected List<Gamer> gamers;
    protected Set<Gamer> teamA;
    protected Set<Gamer> teamB;
    protected Set<Gamer> teamC;
    protected Set<Gamer> teamD;

    protected Arena arena;

    public Game(Session session) {
        arena = session.getArena();
    }

    /**
     * Eliminates the gamer from the game.
     */
    public void eliminate(Gamer gamer) {
        gamers.remove(gamer);
        // TODO: send gamer to lobby / spectator spawn
    }
}
