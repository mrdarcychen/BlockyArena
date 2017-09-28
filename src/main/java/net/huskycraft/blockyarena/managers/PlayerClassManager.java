package net.huskycraft.blockyarena.managers;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.PlayerClass;

import java.util.ArrayList;

public class PlayerClassManager {

    private BlockyArena plugin;
    private ArrayList<PlayerClass> playerClasses;

    public PlayerClassManager(BlockyArena plugin) {
        this.plugin = plugin;
        playerClasses = new ArrayList<>();
    }

    public void addPlayerClass(PlayerClass playerClass) {
        playerClasses.add(playerClass);
    }

    public PlayerClass getPlayerClass(String className) {
        for (PlayerClass playerClass : playerClasses) {
            if (playerClass.getClassName().equals(className)) {
                return playerClass;
            }
        }
        return null;
    }
}
