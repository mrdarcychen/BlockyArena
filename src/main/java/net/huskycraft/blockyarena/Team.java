package net.huskycraft.blockyarena;

import java.util.Set;
import java.util.TreeSet;

public class Team {

    public static BlockyArena plugin;

    private Set<Gamer> gamers;
    private Spawn teamSpawn;

    public Team(Spawn teamSpawn) {
        gamers = new TreeSet<>();
        this.teamSpawn = teamSpawn;
    }

    public void add(Gamer gamer) {
        gamers.add(gamer);
    }
}
