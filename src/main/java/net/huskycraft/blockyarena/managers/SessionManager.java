package net.huskycraft.blockyarena.managers;

import net.huskycraft.blockyarena.Arena;
import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.Session;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class SessionManager {

    BlockyArena plugin;

    public ArrayList<Session> sessions;

    public HashMap<Player, Session> playerSession;

    public SessionManager(BlockyArena plugin) {
        this.plugin = plugin;
        sessions = new ArrayList<>();
        playerSession = new HashMap<>();

    }

    /*
    returns a session that matches an arena and is still in lobby wait period
     */
    public Session getAvailableSession() {
        if (sessions.size() != 0) {
            for (Session session : sessions) {
                if (session.canJoin) {
                    return session;
                }
            }
        }

        Arena arena = plugin.getArenaManager().getAvailableArena();
        if (arena == null) return null;
        Session session = new Session(plugin, arena);
        sessions.add(session);
        return session;
    }
}
