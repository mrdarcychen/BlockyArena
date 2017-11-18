package net.huskycraft.blockyarena.listeners;

import net.huskycraft.blockyarena.BlockyArena;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class ClientConnectionEventListener {

    private BlockyArena plugin;

    public ClientConnectionEventListener(BlockyArena plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onClientLogin(ClientConnectionEvent.Login event) {
        User user = event.getTargetUser();
        if (!user.getPlayer().get().hasPlayedBefore()) {
            plugin.getGamerManager().register(user);
        }
    }
}
