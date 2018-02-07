package net.huskycraft.blockyarena;

import java.util.Set;
import java.util.TreeSet;

public class Team {

    public static BlockyArena plugin;

    private Set<Gamer> gamers;
    private Spawn teamSpawn;

    public Team(Spawn teamSpawn) {
        this.teamSpawn = teamSpawn;
        gamers = new TreeSet<>();
    }

    public void add(Gamer gamer) {
        gamers.add(gamer);
    }
}
