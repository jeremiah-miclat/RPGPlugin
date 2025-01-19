package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class MonsterInitializerLabyrinth implements Listener {

    private final RPGPlugin plugin;
    private final Random random = new Random();



    public MonsterInitializerLabyrinth(RPGPlugin plugin) {
        this.plugin = plugin;

    }

    @EventHandler
    public void onMobPickUpItem(EntityPickupItemEvent event) {
        String world = Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName();
        if (!world.contains("labyrinth")) {
            return;
        }

        if (!(event.getEntity() instanceof Monster)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        String world = Objects.requireNonNull(event.getLocation().getWorld()).getName();
        if (!world.contains("labyrinth")) {
            return;
        }

        Location spawnLocation = event.getLocation();
        if (!isPlayerOnSameYLevel(spawnLocation) ) {
            if (event.getEntity() instanceof PiglinBrute) return;
            event.setCancelled(true);
            return;
        }

        // Check if the spawn is a monster, not an animal
        if (!(event.getEntity() instanceof Monster)) {
            event.setCancelled(true);
            return;
        }

        if (event.getEntity().getType() == EntityType.PIGLIN) {
            if (!((Piglin) event.getEntity()).isAdult()) {
                event.setCancelled(true);
            }
        }

        if (event.getEntity() instanceof PigZombie zombifiedPiglin) {

            if (!zombifiedPiglin.isAdult()) {

                event.setCancelled(true);
            }
        }

        if (event.getEntity() instanceof Monster) {
            Monster mob = (Monster) event.getEntity();
            mob.setNoDamageTicks(0);
            initializeExtraAttributes(mob);

        }
    }


    private boolean isPlayerOnSameYLevel(Location location) {
        int entityY = location.getBlockY();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(location.getWorld())) {
                int playerY = player.getLocation().getBlockY();
                if (Math.abs(entityY - playerY) <= 2) {
                    return true;
                }
            }
        }
        return false;
    }

    // Method to initialize extra health and extra damage metadata
    private void initializeExtraAttributes(LivingEntity entity) {

        Location location = entity.getLocation();
        calculateExtraAttributes(location, entity);


        // Initialize the list of players who have attacked this entity
        List<String> attackerList = new ArrayList<>();
        entity.setMetadata("attackerList", new FixedMetadataValue(plugin, attackerList));

        setHealthIndicator(entity);
    }

    private void setHealthIndicator(LivingEntity entity) {
        double totalHealth = entity.getHealth();

        // Format the health indicator (e.g., [99] for health 99)
        String healthIndicator = ChatColor.YELLOW + " [" + (int) totalHealth + "]";

        // Get the existing custom name from metadata, or use the default entity type if no custom name is set
        String customName = entity.getCustomName();


        // Set the updated custom name with the new health indicator
        entity.setCustomName(customName + healthIndicator);
    }



    // Method to calculate extra attributes (used for both extra health and damage)
    private void calculateExtraAttributes(Location targetLocation, LivingEntity entity) {
        int maxY = 250; // Starting height
        int minY = 3; // Minimum height
        double baseHealth = entity.getHealth(); // Base health at max height


        if (entity instanceof Warden warden) {
            warden.setPersistent(true);
            entity.setRemoveWhenFarAway(false);
        }

        if (entity instanceof Evoker) {
            entity.setPersistent(true);
            entity.setRemoveWhenFarAway(false);
        }

        if (entity instanceof Ravager) {
            entity.setPersistent(true);
            entity.setRemoveWhenFarAway(false);
        }

        // Ensure the y-coordinate is within the range
        int yCoord = Math.min(maxY, Math.max(minY, targetLocation.getBlockY()));




        // Determine the level based on the y-coordinate (1 level every 6 blocks)
        int lvl = (1 + (maxY - yCoord) / 6)*5;


        // Calculate extra health based on the y-coordinate
        double extraHealth = lvl+20+entity.getHealth();
        double customDamage = lvl*.2 + Objects.requireNonNull(entity.getAttribute(Attribute.ATTACK_DAMAGE)).getValue() + 1;


        // Set default name for normal monsters
        String normalName = ChatColor.GREEN + "Lvl " + lvl + " " + entity.getType().name();


        entity.setCustomName(normalName);


        // World Boss scaling logic
        if (Math.random() < .0003 || entity instanceof Warden || entity instanceof Wither || entity instanceof ElderGuardian || entity instanceof Ravager || entity instanceof Evoker && !(entity instanceof Vex)) {
            setBossAttributes(entity, targetLocation.getBlockY(), "World Boss", ChatColor.DARK_PURPLE, lvl);
            extraHealth += lvl*1500;
            Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(extraHealth+500);
            entity.setHealth(extraHealth);
            Objects.requireNonNull(entity.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(customDamage*2);
            entity.setCustomNameVisible(true);
            entity.setGlowing(true);
            entity.addPotionEffect(new PotionEffect((PotionEffectType.REGENERATION),Integer.MAX_VALUE,1,true,true));
            entity.setPersistent(true);
            return;
        }


        // Boss scaling logic
        if (Math.random() < .003) {
            setBossAttributes(entity, targetLocation.getBlockY(), "Boss", ChatColor.RED, lvl);
            extraHealth += lvl*150;
            Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(extraHealth+500);
            entity.setHealth(extraHealth);
            Objects.requireNonNull(entity.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(customDamage*1.5);

            return;
        }

        // Boss scaling logic
        if (Math.random() < .03) {
            setBossAttributes(entity, targetLocation.getBlockY(), "Leader", ChatColor.YELLOW, lvl);
            extraHealth += lvl*15;
            Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(extraHealth+500);
            entity.setHealth(extraHealth);
            Objects.requireNonNull(entity.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(customDamage * 1.2);
            return;
        }

        Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(extraHealth+500);
        entity.setHealth(extraHealth);
        Objects.requireNonNull(entity.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(customDamage);


    }


    private void setBossAttributes(LivingEntity entity, double maxCoord, String type, ChatColor color, int level) {
        // Set the boss name based on the Y-level (maxCoord) and other parameters
        String bossName = color + "Lvl " + level + " " + type + " " + entity.getType().name();
        entity.setCustomName(bossName);

        // Calculate speed multiplier based on the floor (Y-level)
        // At Y-level 3, multiplier = 5; at Y-level 251, multiplier = 1
        double speedMultiplier = 1 + 4 * (251 - maxCoord) / 248.0;  // Interpolates the speed multiplier between 5 and 1

        // Calculate jump multiplier, scaled similarly to speed (e.g., 1.5 times the speed multiplier)
        double jumpMultiplier = speedMultiplier * 1.5;

        // Set extra movement speed if the attribute is available
        if (entity.getAttribute(Attribute.MOVEMENT_SPEED) != null) {
            double baseSpeed = Objects.requireNonNull(entity.getAttribute(Attribute.MOVEMENT_SPEED)).getBaseValue();
            double newSpeed = baseSpeed * speedMultiplier; // Apply the calculated speed multiplier
            Objects.requireNonNull(entity.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(newSpeed);
        }

        // Set extra jump strength if applicable
        if (entity.getAttribute(Attribute.JUMP_STRENGTH) != null) {
            double baseJumpStrength = Objects.requireNonNull(entity.getAttribute(Attribute.JUMP_STRENGTH)).getBaseValue();
            double newJumpStrength = baseJumpStrength * jumpMultiplier; // Apply the calculated jump multiplier
            Objects.requireNonNull(entity.getAttribute(Attribute.JUMP_STRENGTH)).setBaseValue(newJumpStrength);
        }

        // Set extra safe fall distance if applicable
        if (entity.getAttribute(Attribute.SAFE_FALL_DISTANCE) != null) {
            double baseSafeFallDistance = Objects.requireNonNull(entity.getAttribute(Attribute.SAFE_FALL_DISTANCE)).getBaseValue();
            Objects.requireNonNull(entity.getAttribute(Attribute.SAFE_FALL_DISTANCE)).setBaseValue(baseSafeFallDistance * jumpMultiplier + 1);
        }
    }

}
