package net.huskycraft.blockyarena.listeners;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.utils.Gamer;
import net.huskycraft.blockyarena.utils.GamerStatus;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class ClientConnectionEventListener {

    public static BlockyArena plugin;

    public ClientConnectionEventListener(BlockyArena plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onClientLogin(ClientConnectionEvent.Login event) {
        Player player = event.getTargetUser().getPlayer().get();
        if (!plugin.getGamerManager().hasGamer(player)) {
            plugin.getGamerManager().register(player);
            plugin.getLogger().info("A new gaming profile is created for player " + player.getName());
        } else {
            plugin.getGamerManager().getGamer(player).setPlayer(player);
        }
        plugin.getGamerManager().getGamer(player).setStatus(GamerStatus.AVAILABLE);
    }

    @Listener
    public void onClientLogout(ClientConnectionEvent.Disconnect event) {
        Player player = event.getTargetEntity();
        Gamer gamer = plugin.getGamerManager().getGamer(player);
        if (gamer.getGame() != null) {
            gamer.quit();
        }
        gamer.setStatus(GamerStatus.OFFLINE);
    }

}
