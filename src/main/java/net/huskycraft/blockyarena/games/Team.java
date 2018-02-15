package net.huskycraft.blockyarena.games;

import net.huskycraft.blockyarena.utils.Gamer;
import net.huskycraft.blockyarena.utils.GamerStatus;
import net.huskycraft.blockyarena.arenas.Spawn;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private List<Gamer> gamers;
    private Spawn teamSpawn;

    public Team(Spawn teamSpawn) {
        this.teamSpawn = teamSpawn;
        gamers = new ArrayList<>();
    }

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
            if (gamer.getStatus() == GamerStatus.INGAME) {
                return true;
            }
        }
        return false;
    }
}
