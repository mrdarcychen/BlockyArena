package net.huskycraft.blockyarena;

import java.util.Set;
import java.util.TreeSet;

public class Team {

    public static BlockyArena plugin;

    private TeamType teamType;
    private Set<Gamer> gamers;
    private Spawn teamSpawn;

    public Team(TeamType teamType, Spawn teamSpawn) {
        this.teamType = teamType;
        this.teamSpawn = teamSpawn;
        gamers = new TreeSet<>();
    }

    public void add(Gamer gamer) {
        gamers.add(gamer);
    }
}
