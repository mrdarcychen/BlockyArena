package net.huskycraft.blockyarena.managers;

import net.huskycraft.blockyarena.Arena;
import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.Session;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    BlockyArena plugin;

    private List<Session> sessions;

    public SessionManager(BlockyArena plugin) {
        this.plugin = plugin;
        sessions = new ArrayList<>();
    }

    /**
     * Gets an available session.
     * @return null if no session is available
     */
    public Session getAvailableSession() {
        if (sessions.size() != 0) {
            for (Session session : sessions) {
                if (session.canJoin()) {
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
