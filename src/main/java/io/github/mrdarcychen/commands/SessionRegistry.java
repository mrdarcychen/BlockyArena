package io.github.mrdarcychen.commands;

import io.github.mrdarcychen.games.GameSession;

import java.util.ArrayList;
import java.util.List;

public class SessionRegistry {

    public static final List<GameSession> GAME_SESSIONS = new ArrayList<>();

    public static void remove(GameSession gameSession) {
        GAME_SESSIONS.remove(gameSession);
        gameSession.terminate();
    }

    public static void register(GameSession gameSession) {
        GAME_SESSIONS.add(gameSession);
    }

    public static void terminateAll() {
        GAME_SESSIONS.forEach(GameSession::terminate);
    }
}
