package github.eremiyuh.rPGPlugin.manager;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class RavagerSkillManager {

    private final JavaPlugin plugin;
    private final Map<UUID, Long> lastUsed = new HashMap<>();
    private final Set<UUID> activeReflect = new HashSet<>();
    private final Set<UUID> frozen = new HashSet<>();
    private final long cooldown = 30_000; // 30 seconds
    private final String targetWorld = "world_rpg";

    public RavagerSkillManager(JavaPlugin plugin) {
        this.plugin = plugin;
        startSkillTick();       // Handles boasting 5s before ready
        startAutoTriggerLoop(); // Automatically activates skill
    }

    public void tryActivate(Ravager ravager) {
        if (!ravager.getWorld().getName().equals(targetWorld)) return;

        UUID id = ravager.getUniqueId();
        long now = System.currentTimeMillis();

        if (lastUsed.containsKey(id) && now - lastUsed.get(id) < cooldown) return;

        lastUsed.put(id, now);
        activateSkill(ravager);
    }

    private void activateSkill(Ravager ravager) {
        UUID id = ravager.getUniqueId();
        activeReflect.add(id);
        frozen.add(id);

        ravager.setAI(false);
//        ravager.setInvulnerable(true);

        sayToNearbyPlayers(ravager, "§4" + ravager.getName() + " roars: \"COME, FEED ME YOUR WEAKNESS!\"", 60);

        new BukkitRunnable() {
            @Override
            public void run() {
                ravager.setAI(true);
//                ravager.setInvulnerable(false);
                activeReflect.remove(id);
                frozen.remove(id);
                sayToNearbyPlayers(ravager, "§7" + ravager.getName() + " Fly foo!", 30);


                Location ravagerLocXZ = ravager.getLocation().clone();
                ravagerLocXZ.setY(0); // Ignore vertical difference

                for (Player player : ravager.getWorld().getPlayers()) {
                    Location playerLocXZ = player.getLocation().clone();
                    playerLocXZ.setY(0); // Ignore vertical difference

                    if (ravagerLocXZ.distanceSquared(playerLocXZ) <= 30 * 30) {
                        Location center = player.getLocation();

                        for (int x = -1; x <= 1; x++) {
                            for (int z = -1; z <= 1; z++) {
                                Location fireLoc = center.clone().add(x, 0, z);
                                fireLoc.getBlock().setType(Material.FIRE);
                            }
                        }
//                        ravager.attack(player);
                        // Toss logic here

                        // Upward launch
                        double maxCoord = Math.max(Math.abs(ravagerLocXZ.getX()), Math.abs(ravagerLocXZ.getZ()));

// Every 100 units = 5 damage
                        int damage = (int) (maxCoord / 100.0);
                        player.damage(damage);

                        player.setVelocity(player.getVelocity().setY(2.2));

                        // Sound
                        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);


                        player.getWorld().spawnParticle(
                                Particle.EXPLOSION,
                                player.getLocation().add(0, 1, 0), // position
                                30,                                // count (more = denser effect)
                                0.5, 1, 0.5,                       // offsetX, offsetY, offsetZ (spread area)
                                0.1                                // extra (speed or size depending on particle)
                        );

                    }
                }

            }
        }.runTaskLater(plugin, 20L * 5); // 5 seconds
    }

    private void startSkillTick() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();

                for (UUID id : new HashSet<>(lastUsed.keySet())) {
                    LivingEntity entity = Bukkit.getEntity(id) instanceof LivingEntity le ? le : null;
                    if (!(entity instanceof Ravager ravager) || !ravager.isValid()) {
                        cleanup(id);
                        continue;
                    }

                    if (!ravager.getWorld().getName().equals(targetWorld)) continue;

                    long last = lastUsed.getOrDefault(id, 0L);
                    long remaining = cooldown - (now - last);
                    if (remaining <= 5000 && remaining > 4000) {
                        sayToNearbyPlayers(ravager, "§6" + ravager.getName() + " growls: \"I feel the power returning...\"", 60);
                    }

                    if (remaining <= 0) {
                        lastUsed.remove(id);
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
                    if (!lastUsed.containsKey(id) || now - lastUsed.get(id) >= cooldown) {
                        tryActivate(ravager);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // every second
    }

    private void sayToNearbyPlayers(LivingEntity entity, String message, double radius) {
        for (Player player : entity.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(entity.getLocation()) <= radius * radius) {
                player.sendMessage(message);
            }
        }
    }

    public boolean isReflecting(UUID id) {
        return activeReflect.contains(id);
    }

    public boolean isFrozen(UUID id) {
        return frozen.contains(id);
    }

    private void cleanup(UUID id) {
        lastUsed.remove(id);
        activeReflect.remove(id);
        frozen.remove(id);
    }
}
