package net.huskycraft.huskyarena.Listeners;

import net.huskycraft.huskyarena.HuskyArena;
import net.huskycraft.huskyarena.Session;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;

import java.util.Optional;

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
