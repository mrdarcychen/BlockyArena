package net.huskycraft.blockyarena.listeners;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.games.GameState;
import net.huskycraft.blockyarena.utils.Gamer;
import net.huskycraft.blockyarena.utils.GamerStatus;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;

public class EntityListener {

    public static BlockyArena plugin;

    public EntityListener(BlockyArena plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onDamageEntity(DamageEntityEvent event) {
        if (event.getTargetEntity() instanceof Player) {
            Player player = (Player) event.getTargetEntity();
            Gamer gamer = plugin.getGamerManager().getGamer(player);
            if (gamer.getStatus() == GamerStatus.PLAYING) {
                player.sendMessage(Text.of("Damn!"));
                player.sendMessage(Text.of(gamer.getGame() == null));
                player.sendMessage(Text.of(gamer.getGame().getGameState() == null));
                if (gamer.getGame().getGameState() != GameState.IN_PROGRESS) {
                    player.sendMessage(Text.of("Reach 1"));
                    event.setCancelled(true);
                }
                if (event.willCauseDeath()) {
                    player.sendMessage(Text.of("Reach 2"));
                    event.setCancelled(true);
                    gamer.getGame().eliminate(gamer);
                }
            }
        }
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        Player player = event.getTargetEntity();
        Gamer gamer = plugin.getGamerManager().getGamer(player);
        if (gamer.getGame() != null) {
            gamer.getGame().remove(gamer);
        }
    }
}
