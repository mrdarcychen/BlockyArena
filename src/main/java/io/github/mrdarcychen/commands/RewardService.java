package io.github.mrdarcychen.commands;

import io.github.mrdarcychen.ConfigManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

public class RewardService {
    
    public static void offer(Player player) {
        String kitName = ConfigManager.getInstance().getRewardKitName();
        Sponge.getCommandManager().process(Sponge.getServer().getConsole(),
                "kit resetusage " + player.getName() + " " + kitName);
        Sponge.getCommandManager().process(player, "kit " + kitName);
    }
}
