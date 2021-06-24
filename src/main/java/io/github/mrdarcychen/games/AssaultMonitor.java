package io.github.mrdarcychen.games;

import io.github.mrdarcychen.BlockyArena;
import io.github.mrdarcychen.utils.DamageData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;

// part of the officiating crew, referee
class AssaultMonitor {

    EventListener listener = new EventListener();
    GameSession gameSession;

    public AssaultMonitor(GameSession gameSession) {
        this.gameSession = gameSession;
        Sponge.getEventManager().registerListeners(BlockyArena.getInstance(), listener);
    }

    // post match procedure
    public void terminate() {
        Sponge.getEventManager().unregisterListeners(listener);
    }

    public class EventListener {
        @Listener
        public void onDamageEntity(DamageEntityEvent event) {
            if (event.getTargetEntity() instanceof Player) {
                Player player = (Player) event.getTargetEntity();
                if (gameSession.getPlayerAssistant().contains(player)) {
                    gameSession.getState().analyze(event, new DamageData(player, event.getCause()));
                }
            }
        }

        @Listener
        public void onDropItem(final DropItemEvent event, @First Player player) {
            if (gameSession.getPlayerAssistant().contains(player)) {
                event.setCancelled(true);
            }
        }
    }
}
