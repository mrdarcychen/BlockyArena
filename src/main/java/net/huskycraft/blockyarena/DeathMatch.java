package net.huskycraft.blockyarena;

import org.spongepowered.api.text.Text;

import java.util.*;

public class DeathMatch {

    private Arena arena;

    private List<Gamer> gamers;
    private List<Gamer> teamA;
    private List<Gamer> teamB;

    public DeathMatch(Session session) {
        arena = session.getArena();
        teamA = new ArrayList<>();
        teamB = new ArrayList<>();
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

    /**
     * Eliminates the gamer from the game.
     */
    public void eliminate(Gamer gamer) {
        gamers.remove(gamer);
        // TODO: send gamer to lobby / spectator spawn
    }






}
