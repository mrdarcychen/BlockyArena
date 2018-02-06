package net.huskycraft.blockyarena;

import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * A Session represents a specific block of time dedicated to the time before, during, and after the Game.
 * Each Session has a designated TeamType and GameType, and keeps track of all players' inventories.
 */
public class Session {

    public static BlockyArena plugin;

    private Arena arena; // the Arena associated with this Session
    private Set<Gamer> gamers; // the set of gamers currently in this session
    private Map<Gamer, Closet> closets; // the map of closets that store the inventories of the gamers

    private TeamType teamType; // the type of the team designated for this session
    private SessionState state; // the current state of the sessions

    private Task timer;

    /**
     * Constructs a default Session with an Arena. TeamType is set to default.
     * @param arena an Arena that has not been assigned to any other Session
     */
    public Session(Arena arena) {
        this(arena, TeamType.SOLO);
    }

    /**
     * Constructs a regular Session with the given Arena and TeamType.
     * @param arena an Arena that has not been assigned to any other Session
     * @param teamType a type of the team
     */
    public Session(Arena arena, TeamType teamType) {
        this.arena = arena;
        this.teamType = teamType;
        // TODO: GAMETYPE
        gamers = new HashSet<>();
        closets = new HashMap<>();
    }

    /**
     * Adds the given gamer to this session.
     */
    public void add(Gamer gamer) {
        gamers.add(gamer);
        // backups the gamer's inventory and clear it
        closets.put(gamer, new Closet(gamer.getPlayer()));
        gamer.getPlayer().getInventory().clear();
        gamer.setLocation(arena.getLobbySpawn());
        checkPreCond();
    }

    /**
     * Removes the given gamer from the session.
     */
    public void remove(Gamer gamer) {
        gamers.remove(gamer);
        closets.get(gamer).equip(gamer.getPlayer());
        gamer.getPlayer().setLocation(gamer.getLastLocation());
        if (state == SessionState.ACTIVE) {
            checkPreCond();
        }
    }

    /**
     * Displays the countdown title in seconds to all gamers. Starts the game when countdown is 0.
     * @param second the seconds remaining for the countdown
     * @return true if the countdown is over, false otherwise
     */
    private void countdown(int second) {
        if (second == 0) {
            timer.cancel();
            // TODO: start the game
        } else {
            Title title = Title.builder()
                    .title(Text.of(second)).fadeIn(2).fadeOut(2).stay(16).build();
            for (Gamer gamer : gamers) {
                Player player = gamer.getPlayer();
                player.sendTitle(title);
                player.playSound(SoundTypes.BLOCK_DISPENSER_DISPENSE, player.getHeadRotation(), 100);
            }
        }
        timer = Task.builder()
                .execute(() -> countdown(second - 1)).delay(1, TimeUnit.SECONDS).submit(plugin);
    }

    public Arena getArena() {
        return arena;
    }

    public TeamType getTeamType() {
        return teamType;
    }

    public SessionState getState() {
        return state;
    }

    /**
     * Checks the precondition of the session. If criterion is met, countdown starts.
     */
    private void checkPreCond() {
        boolean canSolo = teamType == TeamType.SOLO && gamers.size() == 2;
        boolean canDouble = teamType == TeamType.DOUBLE && gamers.size() == 4;
        if (canSolo || canDouble) {
            countdown(10);
        } else {
            timer.cancel();
        }
    }
}
