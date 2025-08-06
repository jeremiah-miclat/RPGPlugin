package github.eremiyuh.rPGPlugin.manager;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class ZombieSkillManager {

    private final JavaPlugin plugin;
    private final String targetWorld = "world_rpg";
    private final Map<UUID, Long> lastUsed = new HashMap<>();
    private final long cooldown = 60_000; // 60 seconds

    public ZombieSkillManager(JavaPlugin plugin) {
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
                    if (!(entity instanceof Zombie zombie)) continue;
                    if (!zombie.isValid() || zombie.isDead()) continue;
                    if (zombie.getCustomName() == null) continue;

                    String name = zombie.getCustomName();
                    if (!name.contains("World Boss") || name.contains("Clone")) continue;

                    UUID id = zombie.getUniqueId();
                    long last = lastUsed.getOrDefault(id, -1L);

                    if (last == -1L) {
                        // First time seeing this zombie, randomize initial delay
                        long initialOffset = (long) (Math.random() * cooldown);
                        lastUsed.put(id, now - initialOffset);
                        continue;
                    }

                    long elapsed = now - last;
                    if (elapsed >= cooldown) {
                        spawnClones(zombie);

                        // Desync next use with a small random offset (0–5s)
                        long nextOffset = (long) (Math.random() * 5000);
                        lastUsed.put(id, now + nextOffset);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // Run every second
    }

    private void spawnClones(Zombie original) {
        World world = original.getWorld();
        Location base = original.getLocation();
        Vector[] offsets = {
                new Vector(1, 0, 0),
                new Vector(-1, 0, 0),
                new Vector(0, 0, 1),
                new Vector(0, 0, -1)
        };

        double maxHealth = original.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
        double currentHealth = original.getHealth();
        double attackDamage = 5.0;
        AttributeInstance damageAttr = original.getAttribute(Attribute.ATTACK_DAMAGE);
        if (damageAttr != null) {
            attackDamage = damageAttr.getBaseValue();
        }

        List<Zombie> clones = new ArrayList<>();

        for (Vector offset : offsets) {
            Location spawnLoc = base.clone().add(offset);
            spawnLoc.setY(world.getHighestBlockYAt(spawnLoc) + 1);

            Zombie clone = (Zombie) world.spawnEntity(spawnLoc, EntityType.ZOMBIE);

            clone.setCustomName(original.getCustomName() + " Clone");
            clone.setCustomNameVisible(true);
            clone.setBaby(original.isBaby());
            clone.setPersistent(false);
            clone.setRemoveWhenFarAway(false);

            // Set health
            AttributeInstance cloneHealth = clone.getAttribute(Attribute.MAX_HEALTH);
            if (cloneHealth != null) {
                cloneHealth.setBaseValue(maxHealth);
                clone.setHealth(Math.min(currentHealth, maxHealth));
            }

            // Set attack damage
            AttributeInstance cloneAttack = clone.getAttribute(Attribute.ATTACK_DAMAGE);
            if (cloneAttack != null) {
                cloneAttack.setBaseValue(attackDamage);
            }

            // Copy equipment
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                clone.getEquipment().setItem(slot, original.getEquipment().getItem(slot));
            }

            clones.add(clone);
        }

        // Remove clones after 10 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Zombie clone : clones) {
                    if (!clone.isDead()) {
                        clone.remove();
                    }
                }
            }
        }.runTaskLater(plugin, 10 * 20); // 10 seconds later
    }
}
