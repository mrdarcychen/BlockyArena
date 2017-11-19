package net.huskycraft.blockyarena;


import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.HashSet;

public class DeathMatch extends Game {

    public DeathMatch(Session session) {
        super(session);
        teamA = new HashSet<>();
        teamB = new HashSet<>();
        drawTeams();
    }

    /**
     * Makes two teams with randomly chose gamers and sends them to their spawn points.
     */
    private void drawTeams() {
        Collections.shuffle(gamers);
        for (Gamer gamer : gamers) {
            if (gamers.indexOf(gamer) % 2 == 0) {
                teamA.add(gamer);
                gamer.getPlayer().sendMessage(Text.of("You're on team A!"));
                gamer.getPlayer().setLocation(arena.getBlueSpawn());
            } else {
                teamB.add(gamer);
                gamer.getPlayer().sendMessage(Text.of("You're on team B!"));
                gamer.getPlayer().setLocation(arena.getRedSpawn());
            }
        }
    }






}
