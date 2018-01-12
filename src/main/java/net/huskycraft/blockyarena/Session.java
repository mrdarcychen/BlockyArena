package net.huskycraft.blockyarena;

import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The Session class represents a specific block of time dedicated to the preparation of a Game, the Game itself, and
 * the aftermath of a Game.
 */
public abstract class Session {

    public static BlockyArena plugin;

    private Arena arena; // an Arena associated with this Session
    private Set<Gamer> gamers; // a set of gamers currently in this session
    private List<Closet> closets; // a list of closets that store the inventories of the gamers
    private Task timer; // a countdown timer before the start of the game

    private boolean canJoin;

    /**
     * Constructs a Session with an Arena.
     * @param arena an Arena that has not been assigned to any other Session
     */
    public Session(Arena arena) {
        this.arena = arena;
        gamers = new HashSet<>();
        closets = new ArrayList<>();
    }

    /**
     * Adds the given gamer to this session.
     */
    public void add(Gamer gamer) {
        gamers.add(gamer);
        // backups the gamer's inventory and clear it
        closets.add(new Closet(gamer.getPlayer()));
        gamer.getPlayer().getInventory().clear();
        // TODO: sends the gamer to the waiting area
        checkPreCond();
    }

    /**
     * Removes the given gamer from the session.
     */
    public void remove(Gamer gamer) {
        gamers.remove(gamer);
        // TODO: send the gamer to his join location / main lobby
        // TODO: give back the player's inventory
        if (canJoin) {
            checkPreCond();
        }
    }

    /**
     * Checks the precondition of the game.
     * Starts the countdown if the precondition is met, cancels the countdown otherwise.
     */
    public abstract void checkPreCond();

    /**
     * Displays the countdown title in seconds to all gamers. Starts the game when countdown is 0.
     * @param second the seconds remaining for the countdown
     * @return true if the countdown is over, false otherwise
     */
    private void countdown(int second) {
        if (second == 0) {
            timer = null;
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

    /**
     * Returns true if the session allows more gamers to join, false otherwise.
     */
    public boolean canJoin() {
        return canJoin;
    }

    public Arena getArena() {
        return arena;
    }
}
