package net.huskycraft.blockyarena.games;

import net.huskycraft.blockyarena.utils.Gamer;
import net.huskycraft.blockyarena.utils.GamerStatus;
import net.huskycraft.blockyarena.arenas.Spawn;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A Team represents a single Gamer or a group of Gamers who cooperate to win a Game.
 */
public class Team {

    private List<Gamer> gamers;
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
        gamers = new ArrayList<>();
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

    public void broadcast(Text msg) {
        for (Gamer gamer : gamers) {
            if (gamer.getGame() == game) {
                gamer.getPlayer().sendMessage(msg);
            }
        }
    }
}
