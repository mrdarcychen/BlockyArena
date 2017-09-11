package net.huskycraft.huskyarena;

import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class SessionManager {

    HuskyArena plugin;

    public ArrayList<Session> sessions;

    public HashMap<Player, Session> playerSession;

    public SessionManager(HuskyArena plugin) {
        this.plugin = plugin;
        sessions = new ArrayList<>();
        playerSession = new HashMap<>();

    }

    public Session getAvailableSession() {
        if (sessions.size() != 0) {
            for (Session session : sessions) {
                if (session.status == false) {
                    return session;
                }
            }
        }

        Arena arena = plugin.getArenaManager().getAvailableArena();
        Session session = new Session(plugin, arena);
        sessions.add(session);
        return session;
    }
}
