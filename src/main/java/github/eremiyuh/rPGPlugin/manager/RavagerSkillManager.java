package github.eremiyuh.rPGPlugin.manager;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
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
        ravager.setInvulnerable(true);

        sayToNearbyPlayers(ravager, "§4" + ravager.getName() + " roars: \"COME, FEED ME YOUR WEAKNESS!\"", 60);

        new BukkitRunnable() {
            @Override
            public void run() {
                ravager.setAI(true);
                ravager.setInvulnerable(false);
                activeReflect.remove(id);
                frozen.remove(id);
                sayToNearbyPlayers(ravager, "§7" + ravager.getName() + " ...", 60);
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
                        lastUsed.remove(id);
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
}
