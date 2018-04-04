package net.huskycraft.blockyarena.games;

import net.huskycraft.blockyarena.arenas.Spawn;
import net.huskycraft.blockyarena.utils.Gamer;
import net.huskycraft.blockyarena.utils.GamerStatus;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A Team represents a single Gamer or a group of Gamers who cooperate to win a Game.
 */
public class Team {

    private Set<Gamer> gamers;
    private Spawn teamSpawn;
    private Game game;

    /**
     * Constructs a Team with the given in-game Spawn point for this Team.
     *
     * @param teamSpawn the in-game Spawn point for this Team
     */
    public Team(Spawn teamSpawn, Game game) {
        this.teamSpawn = teamSpawn;
        this.game = game;
        gamers = new HashSet<>();
    }

    /**
     * Adds the given Gamer to this Team.
     *
     * @param gamer the Gamer to be added to this Team
     */
    public void add(Gamer gamer) {
        gamers.add(gamer);
    }


    public void sendAllToSpawn() {
        for (Gamer gamer : gamers) {
            gamer.spawnAt(teamSpawn);
        }
    }

    public boolean hasGamerLeft() {
        for (Gamer gamer : gamers) {
            if (gamer.getGame() == game && gamer.getStatus() == GamerStatus.PLAYING) {
                return true;
            }
        }
        return false;
    }

    public void broadcast(Text text) {
        gamers.forEach(gamer -> {
            if (gamer.getGame() == game) {
                gamer.getPlayer().sendMessage(text);
            }
        });
    }

    public void broadcast(Title title) {
        gamers.forEach(gamer -> {
            if (gamer.getGame() == game) {
                gamer.getPlayer().sendTitle(title);
            }
        });
    }

    /**
     * Gets if the Game contains the given Gamer.
     *
     * @param gamer the Gamer to be inspected
     * @return true if the Gamer is on this Team, false otherwise
     */
    public boolean contains(Gamer gamer) {
        return gamers.contains(gamer);
    }

    public Set<Gamer> getGamers() {
        return gamers;
    }

    public String toString() {
        String str = "";
        Iterator<Gamer> gamersItr = gamers.iterator();
        while (gamersItr.hasNext()) {
            str += gamersItr.next().getName();
            if (gamersItr.hasNext()) {
                str += ", ";
            }
        }
        return str;
    }
}
