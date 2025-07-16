package github.eremiyuh.rPGPlugin.manager;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EvokerSkillManager {

    private final JavaPlugin plugin;
    private final Map<UUID, Long> lastUsed = new HashMap<>();
    private final long cooldown = 60_000;
    private final String targetWorld = "world_rpg";

    public EvokerSkillManager(JavaPlugin plugin) {
        this.plugin = plugin;
        startSkillLoop();
    }

    private void startSkillLoop() {
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorld(targetWorld);
                if (world == null) return;

                long now = System.currentTimeMillis();

                for (LivingEntity entity : world.getLivingEntities()) {
                    if (!(entity instanceof Evoker evoker)) continue;
                    if (!evoker.isValid() || evoker.isDead()) continue;

                    UUID id = evoker.getUniqueId();

                    long last = lastUsed.getOrDefault(id, 0L);
                    long elapsed = now - last;

                    if (elapsed >= cooldown) {
                        activateHeal(evoker);
                        lastUsed.put(id, now);
                    } else if (elapsed >= cooldown - 10_000 && elapsed < cooldown - 9_000) {
                        sendBoast(evoker);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // every second
    }

    private void activateHeal(Evoker evoker) {
        double maxHealth = evoker.getAttribute(Attribute.MAX_HEALTH).getValue();
        double current = evoker.getHealth();
        double healAmount = Math.min(1000, maxHealth - current);

        if (healAmount > 0) {
            evoker.setHealth(current + healAmount);

            // Healing effect: particles
            evoker.getWorld().spawnParticle(Particle.HEART, evoker.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5);

            // Healing sound
            evoker.getWorld().playSound(evoker.getLocation(), Sound.ENTITY_EVOKER_PREPARE_WOLOLO, 1.5f, 1f);

            // Message to nearby players
            for (Player player : evoker.getWorld().getPlayers()) {
                if (player.getLocation().distanceSquared(evoker.getLocation()) <= 60 * 60) {
                    player.sendMessage("§a" + evoker.getName() + " chants: \"Life... returns to me.\"");
                }
            }
        }
    }

    private void sendBoast(Evoker evoker) {
        evoker.getWorld().playSound(evoker.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, 1f, 0.8f);

        for (Player player : evoker.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(evoker.getLocation()) <= 60 * 60) {
                player.sendMessage("§e" + evoker.getName() + " mutters: \"The ritual nears its climax...\"");
            }
        }
    }
}
