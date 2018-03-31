package net.huskycraft.blockyarena.listeners;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.managers.GamersManager;
import net.huskycraft.blockyarena.utils.Gamer;
import net.huskycraft.blockyarena.utils.GamerStatus;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.util.UUID;

public class ClientConnectionEventListener {

    public static BlockyArena plugin;

    public ClientConnectionEventListener(BlockyArena plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onGamerLogin(ClientConnectionEvent.Login event) {
        User user = event.getTargetUser();
        UUID uniqueId = user.getUniqueId();
        if (!GamersManager.getGamer(uniqueId).isPresent()) {
            GamersManager.register(uniqueId);

        }
        Gamer gamer = GamersManager.getGamer(uniqueId).get();
        gamer.setOnline(true);
        gamer.setName(user.getName());
        gamer.setPlayer(user.getPlayer().get());
    }

    @Listener
    public void onGamerLogout(ClientConnectionEvent.Disconnect event) {
        Player player = event.getTargetEntity();
        Gamer gamer = GamersManager.getGamer(player.getUniqueId()).get();
        gamer.setOnline(false);
        if (gamer.getGame() != null) {
            gamer.quit();
        }
    }

}
