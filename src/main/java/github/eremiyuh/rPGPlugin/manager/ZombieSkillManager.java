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
                long now = System.currentTimeMillis();

                for (World world : Bukkit.getWorlds()) {
                    if (!world.getName().contains("_rpg")) continue; // Only RPG worlds

                    for (LivingEntity entity : world.getLivingEntities()) {
                        EntityType type = entity.getType();
                        if (type != EntityType.ZOMBIE &&
                                type != EntityType.ZOMBIE_VILLAGER &&
                                type != EntityType.ZOMBIFIED_PIGLIN) continue;

                        if (!entity.isValid() || entity.isDead()) continue;
                        if (entity.getCustomName() == null) continue;

                        String name = entity.getCustomName();
                        if (!name.contains("World Boss") || name.contains("Clone")) continue;

                        UUID id = entity.getUniqueId();
                        long last = lastUsed.getOrDefault(id, -1L);

                        if (last == -1L) {
                            // Randomize first use
                            long initialOffset = (long) (Math.random() * cooldown);
                            lastUsed.put(id, now - initialOffset);
                            continue;
                        }

                        if (now - last >= cooldown) {
                            spawnClones(entity);
                            long nextOffset = (long) (Math.random() * 5000);
                            lastUsed.put(id, now + nextOffset);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // Runs every second
    }

    private void spawnClones(LivingEntity original) {
        World world = original.getWorld();
        Location base = original.getLocation();
        Vector[] offsets = {
                new Vector(1, 0, 0),
                new Vector(-1, 0, 0),
                new Vector(0, 0, 1),
                new Vector(0, 0, -1)
        };



        List<LivingEntity> clones = new ArrayList<>();
        for (Vector offset : offsets) {
            Location spawnLoc = base.clone().add(offset);
            spawnLoc.setY(world.getHighestBlockYAt(spawnLoc) + 1);

            LivingEntity clone = (LivingEntity) world.spawnEntity(spawnLoc, original.getType());
//            clone.setCustomName(original.getCustomName() + " Clone");
            clone.setCustomNameVisible(true);
            clone.setPersistent(false);
            clone.setRemoveWhenFarAway(true);




            // Copy equipment
//            for (EquipmentSlot slot : EquipmentSlot.values()) {
//                clone.getEquipment().setItem(slot, original.getEquipment().getItem(slot));
//            }

            clones.add(clone);
        }

        // Remove after 10 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                for (LivingEntity clone : clones) {
                    if (!clone.isDead()) {
                        clone.remove();
                    }
                }
            }
        }.runTaskLater(plugin, 10 * 20);
    }
}
