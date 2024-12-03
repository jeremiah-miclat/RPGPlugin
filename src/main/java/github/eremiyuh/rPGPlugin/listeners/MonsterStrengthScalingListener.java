package github.eremiyuh.rPGPlugin.listeners;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import github.eremiyuh.rPGPlugin.RPGPlugin;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class MonsterStrengthScalingListener implements Listener {


    public Map<LivingEntity, Double> extraHealthMap = new HashMap<>();


    @EventHandler
    public void onMonsterSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();

        // Check if entity is a monster, iron golem, or villager
        if (entity instanceof Monster || entity instanceof IronGolem || entity instanceof Villager || entity instanceof Wolf) {
            int x = Math.abs(entity.getLocation().getBlockX());
            int z = Math.abs(entity.getLocation().getBlockZ());

            // Calculate scaling factor based on distance from origin (x and z only)
            int scalingFactor = Math.max(Math.abs(x), Math.abs(z)) / 10; // Scaling increases by 10% per 100 blocks

            // Increase health
            AttributeInstance maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (maxHealth != null) {
                double baseMaxHealth = maxHealth.getBaseValue();
                double newMaxHealth = baseMaxHealth * (1 + 0.1 * scalingFactor);
                double extraHealth = newMaxHealth;
                extraHealthMap.put(entity, extraHealth); // Store extra health separately
            }

            // Increase damage if the entity can deal damage
            AttributeInstance attackDamage = entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
            if (attackDamage != null) {
                double newDamage = attackDamage.getBaseValue() * (1 + 0.1 * scalingFactor);
                attackDamage.setBaseValue(newDamage);
            }
        }
    }

    // Getter for the extraHealthMap
    public Map<LivingEntity, Double> getExtraHealthMap() {
        return extraHealthMap; // Return an unmodifiable copy
    }

//    @EventHandler
//    public void onEntityDamage(EntityDamageByEntityEvent event) {
//        if (event.getDamager() instanceof Player player) {
//            LivingEntity entity = (LivingEntity) event.getEntity();
//
//            // Check if the entity has extra health
//            if (extraHealthMap.containsKey(entity)) {
//                double currentHealth = entity.getHealth();
//                double extraHealth = extraHealthMap.get(entity);
//                player.sendMessage("Current Health: " + currentHealth + ", Extra Health: " + extraHealth);
//
//                // Check if entity is a Monster
//                if (!(entity instanceof Monster)) return;
//
//                // Check if entity has extra health
//                if (extraHealth > 0) {
//                    double damage = event.getDamage();
//
//                    // If health after damage is less than or equal to zero, use extra health
//                    if (currentHealth - damage <= 0) {
//                        // Prevent death, reset health to max, reduce extra health by remaining damage
//                        entity.setHealth(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
//                        extraHealthMap.put(entity, extraHealth - damage);
//                        event.setDamage(-1); // Cancel the damage event
//                    }
//                } else {
//                    // If there's no extra health left, remove the entity from the map
//                    extraHealthMap.remove(entity);
//                }
//            }
//        }
//
//
//}

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        extraHealthMap.remove(event.getEntity());
    }

    @EventHandler
    public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {

        extraHealthMap.remove(event.getEntity());
    }
}


