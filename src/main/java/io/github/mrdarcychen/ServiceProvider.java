package io.github.mrdarcychen;

import io.github.mrdarcychen.utils.ArenaDispatcher;
import io.github.mrdarcychen.utils.ConfigManager;
import io.github.mrdarcychen.utils.KitDispatcher;

public class ServiceProvider {

    public static ConfigManager configManager;
    public static ArenaDispatcher arenaDispatcher;
    public static KitDispatcher kitDispatcher;

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static ArenaDispatcher getArenaDispatcher() {
        return arenaDispatcher;
    }

    public static KitDispatcher getKitDispatcher() {
        return kitDispatcher;
    }
}
