package github.eremiyuh.rPGPlugin.manager;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EvokerSkillManager {

    private final JavaPlugin plugin;
    private final Map<UUID, Long> lastUsed = new HashMap<>();
    private final Set<UUID> boasted = new HashSet<>(); // Tracks boasted Evokers
    private final long cooldown = 10_000;
    private final String targetWorld = "world_rpg";
    List<LivingEntity> spawnedVindicators = new ArrayList<>();

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
                    if (!evoker.isValid() || evoker.isDead()) {
                        lastUsed.remove(evoker.getUniqueId());
                        boasted.remove(evoker.getUniqueId());
                        continue;
                    }

                    UUID id = evoker.getUniqueId();
                    long last = lastUsed.getOrDefault(id, 0L);
                    long elapsed = now - last;

                    if (elapsed >= cooldown) {
                        activateSkill(evoker);
                        lastUsed.put(id, now);
                        boasted.remove(id); // Reset boast for next cycle
                    } else if (elapsed >= cooldown - 5000 && !boasted.contains(id)) {
                        sendBoast(evoker);
                        boasted.add(id); // Mark as boasted this cycle
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

    private void activateSkill(Evoker evoker) {
        Location center = evoker.getLocation();
        double radius = 30;

        // Sound and particle effects
        center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2f, 1f);
        center.getWorld().spawnParticle(Particle.EXPLOSION, center.add(0, 1, 0), 1);
        center.getWorld().spawnParticle(Particle.CLOUD, center, 100, 3, 1, 3, 0.1);
        center.getWorld().spawnParticle(Particle.ENCHANT, center.add(0, 2, 0), 50, 1, 1, 1, 0);


        // Push players in XZ radius
        for (Player player : evoker.getWorld().getPlayers()) {
            Location playerLoc = player.getLocation().clone();
            Location evokerLoc = center.clone();
            playerLoc.setY(0);
            evokerLoc.setY(0);

            if (playerLoc.distanceSquared(evokerLoc) <= radius * radius) {
                @NotNull Vector push = player.getLocation().toVector().subtract(center.toVector()).normalize().multiply(2);
                push.setY(1.0); // Add upward knockback
                player.setVelocity(push);
                double maxCoord = Math.max(Math.abs(evokerLoc.getX()), Math.abs(evokerLoc.getZ()));
                int damage = (int) (maxCoord / 100.0);
                player.damage(damage);
                player.sendMessage("§a" + evoker.getName() + ": \"Shinra Tensei!\"");
            }
        }




        // 🔥 Summon 4 Vindicators in front of the Ravager
        Location ravagerLoc = evoker.getLocation();
        Vector forward = ravagerLoc.getDirection().setY(0).normalize();
        Vector right = new Vector(-forward.getZ(), 0, forward.getX());
        World world = evoker.getWorld();

// Base spawn location 3 blocks in front of the Ravager
        Location base = ravagerLoc.clone().add(forward.clone().multiply(3));

        for (int i = -1; i <= 2; i++) {
            Vector offset = right.clone().multiply(i); // spreads them left/right
            Location spawnLoc = base.clone().add(offset);

            // Set Y to ground level + 1
            spawnLoc.setY(world.getHighestBlockYAt(spawnLoc) + 1);

            Vindicator vindicator = (Vindicator) world.spawnEntity(spawnLoc, EntityType.VINDICATOR);
            vindicator.setCustomNameVisible(true);
            vindicator.setPersistent(true);
            vindicator.setRemoveWhenFarAway(false);
            vindicator.setCanPickupItems(false);
            spawnedVindicators.add(vindicator);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (LivingEntity entity : spawnedVindicators) {
                    if (!entity.isDead()) {
                        entity.remove();
                    }
                }
            }
        }.runTaskLater(plugin, 6L * 20);

    }

    private void sendBoast(Evoker evoker) {
        evoker.getWorld().playSound(evoker.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, 1f, 0.8f);

        for (Player player : evoker.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(evoker.getLocation()) <= 60 * 60) {
                player.sendMessage("§e" + evoker.getName() + ": \"This world shall know pain...\"");
            }
        }
    }
}
