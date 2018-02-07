package net.huskycraft.blockyarena.listeners;

import net.huskycraft.blockyarena.BlockyArena;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class EntityListener {

    BlockyArena plugin;

    public EntityListener(BlockyArena plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onDamageEntity(DamageEntityEvent event) {
        if (event.getTargetEntity() instanceof Player) {
            Player player = (Player) event.getTargetEntity();
            if (plugin.getSessionManager().playerSession.containsKey(player)) {
                if (plugin.getSessionManager().playerSession.get(player).canJoin) {
                    event.setCancelled(true);
                } else if (event.willCauseDeath()) {
                    event.setCancelled(true);
                    Session session = plugin.getSessionManager().playerSession.get(player);
                    session.onPlayerDeath(player);
                }
            }
        }
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        Player player = (Player) event.getTargetEntity();
        if (plugin.getSessionManager().playerSession.containsKey(player)) {
            Session session = plugin.getSessionManager().playerSession.get(player);
            session.remove(player);
        }
    }
}
