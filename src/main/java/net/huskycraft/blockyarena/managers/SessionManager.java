package net.huskycraft.blockyarena.managers;

import net.huskycraft.blockyarena.*;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    public static BlockyArena plugin;

    private List<Session> sessions;

    public SessionManager() {
        sessions = new ArrayList<>();
    }

    /**
     * Gets an available active session from the list.
     * @return null if no session is available
     */
    public Session getActiveSession(TeamType teamType) {
        for (Session session : sessions) {
            boolean isActive = session.getState() == SessionState.ACTIVE;
            boolean isGivenType = session.getTeamType() == teamType;
            if (isActive && isGivenType) {
                return session;
            }
        }

        // if no session is active
        Arena arena = plugin.getArenaManager().getAvailableArena();
        if (arena == null) return null;
        Session session = new Session(arena, teamType);
        sessions.add(session);
        return session;
    }
}