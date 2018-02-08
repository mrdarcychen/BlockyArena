package net.huskycraft.blockyarena.listeners;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.GameState;
import net.huskycraft.blockyarena.Gamer;
import net.huskycraft.blockyarena.GamerStatus;
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
            Gamer gamer = plugin.getGamerManager().getGamer(player);
            if (gamer.getStatus() == GamerStatus.INGAME) {
                event.setCancelled(true);
                if (gamer.getGame().getGameState() != GameState.IN_PROGRESS && event.willCauseDeath()) {
                    gamer.getGame().eliminate(gamer);
                }
            }
        }
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        Player player = (Player) event.getTargetEntity();
        Gamer gamer = plugin.getGamerManager().getGamer(player);
        if (gamer.getGame() != null) {
            gamer.getGame().remove(gamer);
        }
    }
}
