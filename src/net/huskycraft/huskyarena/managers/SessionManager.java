package net.huskycraft.huskyarena.managers;

import net.huskycraft.huskyarena.Arena;
import net.huskycraft.huskyarena.HuskyArena;
import net.huskycraft.huskyarena.Session;
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
        if (arena == null) return null;
        Session session = new Session(plugin, arena);
        sessions.add(session);
        return session;
    }
}
