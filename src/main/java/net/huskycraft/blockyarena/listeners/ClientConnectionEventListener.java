package net.huskycraft.blockyarena.listeners;

import net.huskycraft.blockyarena.BlockyArena;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class ClientConnectionEventListener {

    public static BlockyArena plugin;

    public ClientConnectionEventListener(BlockyArena plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onClientLogin(ClientConnectionEvent.Login event) {
        User user = event.getTargetUser();
        if (!plugin.getGamerManager().hasGamer(user.getPlayer().get())) {
            plugin.getGamerManager().register(user.getPlayer().get());
            plugin.getLogger().info("A new gaming profile is created for player " + user.getName());
        }
    }
}
