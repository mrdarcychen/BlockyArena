package net.huskycraft.huskyarena.listeners;

import net.huskycraft.huskyarena.HuskyArena;
import net.huskycraft.huskyarena.Session;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;

public class EntityListener {

    HuskyArena plugin;

    public EntityListener(HuskyArena plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onPlayerDeath(DamageEntityEvent event) {
        if (event.getTargetEntity() instanceof Player && event.willCauseDeath()) {
            Player player = (Player) event.getTargetEntity();
            if (plugin.getSessionManager().playerSession.containsKey(player)) {
                event.setCancelled(true);
                Session session = plugin.getSessionManager().playerSession.get(player);
                session.eliminate(player, event);
            }
        }
    }
}
