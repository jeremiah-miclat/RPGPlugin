package github.eremiyuh.rPGPlugin.manager;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class WardenSkillManager {

    private final JavaPlugin plugin;
    private final Map<UUID, Long> lastUsed = new HashMap<>();
    private final Set<UUID> boasted = new HashSet<>();
    private final long cooldown = 60_000; // 60 seconds

    public WardenSkillManager(JavaPlugin plugin) {
        this.plugin = plugin;
        startSkillLoop();
    }

    private void startSkillLoop() {
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorld("world_rpg");
                if (world == null) return;

                long now = System.currentTimeMillis();

                for (LivingEntity entity : world.getLivingEntities()) {
                    if (!(entity instanceof Warden warden)) continue;
                    if (!warden.isValid() || warden.isDead()) continue;

                    UUID id = warden.getUniqueId();
                    long last = lastUsed.getOrDefault(id, -1L);

                    if (last == -1L) {
                        // First time seeing this Warden – apply random desync offset
                        long randomDelay = (long) (Math.random() * cooldown);
                        lastUsed.put(id, now - randomDelay);
                        continue;
                    }

                    long elapsed = now - last;

                    if (elapsed >= cooldown) {
                        activateSkill(warden);
                        long offset = (long) (Math.random() * 5000); // 0–5s offset
                        lastUsed.put(id, now + offset);
                        boasted.remove(id); // Reset boast flag
                    } else if (elapsed >= cooldown - 10_000 && !boasted.contains(id)) {
                        sendBoast(warden);
                        boasted.add(id);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // every second
    }

    private void activateSkill(Warden warden) {
        Location center = warden.getLocation();
        double radius = 10;

        // Visual/audio effects
        center.getWorld().playSound(center, Sound.ENTITY_WARDEN_ROAR, 2f, 1f);
        center.getWorld().spawnParticle(Particle.SONIC_BOOM, center, 1);
        center.getWorld().spawnParticle(Particle.DRAGON_BREATH, center, 100, 3, 1, 3, 0.1);

        Vector direction = center.getDirection().normalize();
        List<Player> affectedPlayers = new ArrayList<>();

        for (Player player : warden.getWorld().getPlayers()) {
            Location playerLoc = player.getLocation().clone();
            Location wardenLoc = center.clone();
            playerLoc.setY(0);
            wardenLoc.setY(0);

            if (playerLoc.distanceSquared(wardenLoc) <= radius * radius) {
                Location target = center.clone().add(direction).add(0, 1, 0);
                player.teleport(target);
                player.playSound(target, Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f, 1.0f);
                player.sendMessage("§c" + warden.getName() + ": \"COME TO ME!\"");
                affectedPlayers.add(player);
            }
        }

        // Choose random teleported player for extra effect
        if (!affectedPlayers.isEmpty()) {
            Player chosen = affectedPlayers.get(new Random().nextInt(affectedPlayers.size()));
            chosen.sendMessage("§4" + warden.getName() + " glares at you...");
            chosen.getWorld().spawnParticle(Particle.LARGE_SMOKE, chosen.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0);
            // Optional: apply debuff or special status effect
        }
    }

    private void sendBoast(Warden warden) {
        Location center = warden.getLocation();
        warden.getWorld().playSound(center, Sound.ENTITY_WARDEN_AMBIENT, 1f, 0.6f);

        for (Player player : warden.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(center) <= 900) { // within 30 blocks
                player.sendMessage("§5" + warden.getName() + ": \"Yes Hi Hello... Goodmorning... mmmm...\"");
            }
        }
    }
}
