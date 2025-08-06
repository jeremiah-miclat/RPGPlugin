package github.eremiyuh.rPGPlugin.manager;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class RavagerSkillManager {

    private final JavaPlugin plugin;
    private final Map<UUID, Long> lastUsed = new HashMap<>();
    private final Set<UUID> reflecting = new HashSet<>();
    private final Set<UUID> frozen = new HashSet<>();
    private final long COOLDOWN_MS = 30_000;
    private final String targetWorld = "world_rpg";

    public RavagerSkillManager(JavaPlugin plugin) {
        this.plugin = plugin;
        startCooldownTick();       // Show pre-skill message
        startAutoTriggerLoop();    // Automatically activate skill
    }

    public void tryActivate(Ravager ravager) {
        if (!ravager.getWorld().getName().equals(targetWorld)) return;

        UUID id = ravager.getUniqueId();
        long now = System.currentTimeMillis();

        long last = lastUsed.getOrDefault(id, 0L);
        if (now - last < COOLDOWN_MS) return;

        lastUsed.put(id, now); // reset cooldown
        activateSkill(ravager);
    }

    private void activateSkill(Ravager ravager) {
        UUID id = ravager.getUniqueId();
        reflecting.add(id);
        frozen.add(id);

        ravager.setAI(false);

        sayNearby(ravager, "§4" + ravager.getName() + " roars: \"COME, FEED ME YOUR WEAKNESS!\"", 60);

        new BukkitRunnable() {
            @Override
            public void run() {
                ravager.setAI(true);
                reflecting.remove(id);
                frozen.remove(id);

                sayNearby(ravager, "§7" + ravager.getName() + " unleashes fiery rage!", 30);

                Location ravagerLoc = ravager.getLocation().clone();
                ravagerLoc.setY(0); // flat distance

                for (Player player : ravager.getWorld().getPlayers()) {
                    Location playerLoc = player.getLocation().clone();
                    playerLoc.setY(0);

                    if (ravagerLoc.distanceSquared(playerLoc) <= 30 * 30) {
                        Location center = player.getLocation();
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                Location fireLoc = center.clone().add(dx, 0, dz);
                                fireLoc.getBlock().setType(Material.FIRE);
                            }
                        }

                        double maxXZ = Math.max(Math.abs(ravagerLoc.getX()), Math.abs(ravagerLoc.getZ()));
                        int damage = (int) (maxXZ / 100.0);
                        if (damage > 200) {
                            damage = (int) (Objects.requireNonNull(ravager.getAttribute(Attribute.ATTACK_DAMAGE)).getValue() * 1.2);
                        }

                        player.damage(damage);
                        player.setVelocity(player.getVelocity().setY(2.2));
                        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
                        player.spawnParticle(Particle.EXPLOSION, player.getLocation().add(0, 1, 0), 30, 0.5, 1, 0.5, 0.1);
                    }
                }
            }
        }.runTaskLater(plugin, 20L * 5); // 5s delay
    }

    private void startCooldownTick() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();

                for (UUID id : new HashSet<>(lastUsed.keySet())) {
                    Entity entity = Bukkit.getEntity(id);
                    if (!(entity instanceof Ravager ravager) || !ravager.isValid()) {
                        cleanup(id);
                        continue;
                    }

                    if (!ravager.getWorld().getName().equals(targetWorld)) continue;

                    long last = lastUsed.getOrDefault(id, 0L);
                    long timeSinceLast = now - last;

                    long timeUntilReady = COOLDOWN_MS - timeSinceLast;

                    if (timeUntilReady <= 5000 && timeUntilReady > 4000) {
                        sayNearby(ravager, "§6" + ravager.getName() + " growls: \"I feel the power returning...\"", 60);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // every second
    }

    private void startAutoTriggerLoop() {
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorld(targetWorld);
                if (world == null) return;

                long now = System.currentTimeMillis();

                for (LivingEntity entity : world.getLivingEntities()) {
                    if (!(entity instanceof Ravager ravager)) continue;

                    UUID id = ravager.getUniqueId();

                    // Only initialize cooldown once
                    lastUsed.putIfAbsent(id, now - (long) (Math.random() * COOLDOWN_MS));

                    long last = lastUsed.getOrDefault(id, 0L);
                    if (now - last >= COOLDOWN_MS) {
                        tryActivate(ravager);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // every second
    }

    private void sayNearby(LivingEntity entity, String message, double radius) {
        Location origin = entity.getLocation();
        for (Player player : entity.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(origin) <= radius * radius) {
                player.sendMessage(message);
            }
        }
    }

    public boolean isReflecting(UUID id) {
        return reflecting.contains(id);
    }

    public boolean isFrozen(UUID id) {
        return frozen.contains(id);
    }

    private void cleanup(UUID id) {
        lastUsed.remove(id);
        reflecting.remove(id);
        frozen.remove(id);
    }
}
