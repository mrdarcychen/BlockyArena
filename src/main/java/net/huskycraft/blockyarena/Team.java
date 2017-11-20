package net.huskycraft.blockyarena;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private List<Gamer> gamers;

    public Team() {
        gamers = new ArrayList<>();
    }

    public void add(Gamer gamer) {
        gamers.add(gamer);
    }
}
