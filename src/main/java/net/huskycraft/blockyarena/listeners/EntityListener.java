package net.huskycraft.blockyarena.listeners;

import net.huskycraft.blockyarena.BlockyArena;
import net.huskycraft.blockyarena.games.Game;
import net.huskycraft.blockyarena.games.GameState;
import net.huskycraft.blockyarena.managers.GamersManager;
import net.huskycraft.blockyarena.utils.DamageData;
import net.huskycraft.blockyarena.utils.Gamer;
import net.huskycraft.blockyarena.utils.GamerStatus;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.weather.WeatherEffect;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class EntityListener {

    public static BlockyArena plugin;

    public EntityListener(BlockyArena plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onDamageEntity(DamageEntityEvent event) {
        if (event.getTargetEntity() instanceof Player) {
            Player player = (Player) event.getTargetEntity();
            Gamer victim = GamersManager.getGamer(player.getUniqueId()).get();
            // if the victim is in a game, proceed analysis
            if (victim.getStatus() == GamerStatus.PLAYING) {
                Game game = victim.getGame();
                DamageData damageData = new DamageData(plugin, victim, event.getCause());
                Optional<Gamer> optAttacker = damageData.getAttacker();
                if (game.getGameState() != GameState.STARTED) {
                    if (damageData.getDamageType().getName().equalsIgnoreCase("void")) {
                        victim.spawnAt(game.getArena().getLobbySpawn());
                    }
                    event.setCancelled(true);
                    return;
                }
                // if the game is in grace period or the event will cause death, set cancelled
                if (damageData.getDamageType().getName().equalsIgnoreCase("void")) {
                    event.setCancelled(true);
                    victim.getGame().eliminate(victim, Text.of(damageData.getDeathMessage()));
                    return;
                }
                if (optAttacker.isPresent()) {
                    if (victim.getGame().getTeam(victim).contains(optAttacker.get())) {
                        event.setCancelled(true);
                    }
                }
                if (event.willCauseDeath()) {
                    event.setCancelled(true);
                    victim.getGame().eliminate(victim, Text.of(damageData.getDeathMessage()));

                }
            }
        }
    }
}
