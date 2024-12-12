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
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

public class MonsterInitializerLabyrinth implements Listener {

    private final RPGPlugin plugin;
    private final Random random = new Random();



    public MonsterInitializerLabyrinth(RPGPlugin plugin) {
        this.plugin = plugin;

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

        // Count the number of players in world_rpg
        int playersInRPGWorld = (int) Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getWorld().getName().equals("world_labyrinth2"))
                .count();

        // Calculate the spawn limit based on players in world_rpg
        int mobLimit = Math.min(playersInRPGWorld * 30, 200);

        // Count the number of mobs already in the world
        int currentMobCount = countMobsInWorld(event.getLocation().getWorld());

        // Cancel the spawn if the cached mob count exceeds the limit
        if (currentMobCount >= mobLimit) {
            event.getEntity().setAI(false);
            event.setCancelled(true);
            return;
        }

        // Initialize the mob
        if (event.getEntity() instanceof Monster) {
            Monster mob = (Monster) event.getEntity();

            initializeExtraAttributes(mob);

        }
    }


    private int countMobsInWorld(World world) {
        int mobCount = 0;

        // Loop through all entities in the world and count the mobs
        for (LivingEntity entity : world.getLivingEntities()) {
            if (!entity.hasMetadata("extraHealth") && !(entity instanceof Player)) {
                entity.remove();
            }

            if (!(entity instanceof Player) && !entity.hasMetadata("worldboss")
                    && entity.hasMetadata("extraHealth")
            ) {
                mobCount++;
            }
        }

        return mobCount;
    }

    private boolean isPlayerNearby(Location location, double radius) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(location.getWorld()) && player.getLocation().distance(location) <= radius) {
                return true;
            }
        }
        return false;
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
        // Retrieve the total health (current health + extra health)
        double totalHealth = entity.getHealth();

        // Format the health indicator (e.g., [99] for health 99)
        String healthIndicator = ChatColor.YELLOW + " [" + (int) totalHealth + "]";

        // Get the existing custom name from metadata, or use the default entity type if no custom name is set
        String customName = entity.hasMetadata("customName") ? entity.getMetadata("customName").get(0).asString() : entity.getType().name();


        // Set the updated custom name with the new health indicator
        entity.setCustomName(customName + healthIndicator);
    }


    // Method to calculate extra attributes (used for both extra health and damage)
    private void calculateExtraAttributes(Location targetLocation, LivingEntity entity) {
        int maxY = 250; // Starting height
        int minY = 3; // Minimum height
        double baseHealth = entity.getHealth(); // Base health at max height
        double healthIncrement = 480; // Increment per layer

        // Ensure the y-coordinate is within the range
        int yCoord = Math.min(maxY, Math.max(minY, targetLocation.getBlockY()));


        double customMaxHealth = ((1 + (double) (maxY - yCoord) / 6) * healthIncrement)*1001;
        Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(customMaxHealth);


        // Calculate extra health based on the y-coordinate
        double extraHealth = baseHealth + ((1 + (double) (maxY - yCoord) / 6) * healthIncrement);
        entity.setMetadata("extraHealth", new FixedMetadataValue(plugin, extraHealth));
        double customDamage = Math.min(extraHealth/10, customMaxHealth);
        extraHealth += entity.getHealth();
        entity.setMetadata("customDamage", new FixedMetadataValue(plugin, customDamage));
        // Determine the level based on the y-coordinate (1 level every 6 blocks)
        int lvl = (1 + (maxY - yCoord) / 6)*5;

        // Set default name for normal monsters
        String normalName = ChatColor.GREEN + "Lvl " + lvl + " " + entity.getType().name();


        entity.setMetadata("customName", new FixedMetadataValue(plugin, normalName));

        if (entity instanceof PiglinBrute) {
            extraHealth *= 1000; // Add 10000% health
            setBossAttributes(entity, targetLocation.getBlockY(), "World Boss", ChatColor.LIGHT_PURPLE, lvl);
            entity.setMetadata("worldboss", new FixedMetadataValue(plugin, true));
            entity.setMetadata("lvl", new FixedMetadataValue(plugin, lvl));
            entity.setHealth(Math.min(extraHealth, customMaxHealth));
            entity.setMetadata("extraHealth", new FixedMetadataValue(plugin, extraHealth));
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(customDamage*3);
            entity.setCustomNameVisible(true);
            entity.setGlowing(true);
            entity.setRemoveWhenFarAway(false);
            entity.setPersistent(true);
            return;
        }

        // World Boss scaling logic
        if (Math.random() < 0.0003) {
            extraHealth *= 1000; // Add 10000% health
            setBossAttributes(entity, targetLocation.getBlockY(), "World Boss", ChatColor.DARK_PURPLE, lvl);
            entity.setMetadata("worldboss", new FixedMetadataValue(plugin, true));
            entity.setMetadata("lvl", new FixedMetadataValue(plugin, lvl));
            entity.setHealth(Math.min(extraHealth, customMaxHealth));
            entity.setMetadata("extraHealth", new FixedMetadataValue(plugin, extraHealth));
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(customDamage*3);
            entity.setCustomNameVisible(true);
            entity.setGlowing(true);
            entity.setRemoveWhenFarAway(false);
            entity.setPersistent(true);
            return;
        }


        // Boss scaling logic
        if (Math.random() < .01) {
            extraHealth *= 100; // Add 1000% health
            setBossAttributes(entity, targetLocation.getBlockY(), "Boss", ChatColor.RED, lvl);
            entity.setMetadata("boss", new FixedMetadataValue(plugin, true));
            entity.setMetadata("lvl", new FixedMetadataValue(plugin, lvl));
            entity.setHealth(Math.min(extraHealth, customMaxHealth));
            entity.setMetadata("extraHealth", new FixedMetadataValue(plugin, extraHealth));
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(customDamage*2);
            entity.setRemoveWhenFarAway(false);
            return;
        }

        // Boss scaling logic
        if (Math.random() < .05) {
            extraHealth *= 10; // Add 1000% health
            setBossAttributes(entity, targetLocation.getBlockY(), "Boss", ChatColor.YELLOW, lvl);
            entity.setMetadata("boss", new FixedMetadataValue(plugin, true));
            entity.setMetadata("lvl", new FixedMetadataValue(plugin, lvl));
            entity.setHealth(Math.min(extraHealth, customMaxHealth));
            entity.setMetadata("extraHealth", new FixedMetadataValue(plugin, extraHealth));
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(customDamage*2);
            return;
        }


        entity.setHealth(Math.min(extraHealth, customMaxHealth));
        Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(customDamage*.7);


    }


    private void setBossAttributes(LivingEntity entity, double maxCoord, String type, ChatColor color, int level) {
        // Set the boss name based on the Y-level (maxCoord) and other parameters
        String bossName = color + "Lvl " + level + " " + type + " " + entity.getType().name();
        entity.setMetadata("customName", new FixedMetadataValue(plugin, bossName));

        // Calculate speed multiplier based on the floor (Y-level)
        // At Y-level 3, multiplier = 5; at Y-level 251, multiplier = 1
        double speedMultiplier = 1 + 4 * (251 - maxCoord) / 248.0;  // Interpolates the speed multiplier between 5 and 1

        // Calculate jump multiplier, scaled similarly to speed (e.g., 1.5 times the speed multiplier)
        double jumpMultiplier = speedMultiplier * 1.5;

        // Set extra movement speed if the attribute is available
        if (entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null) {
            double baseSpeed = Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getBaseValue();
            double newSpeed = baseSpeed * speedMultiplier; // Apply the calculated speed multiplier
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(newSpeed);
        }

        // Set extra jump strength if applicable
        if (entity.getAttribute(Attribute.GENERIC_JUMP_STRENGTH) != null) {
            double baseJumpStrength = Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_JUMP_STRENGTH)).getBaseValue();
            double newJumpStrength = baseJumpStrength * jumpMultiplier; // Apply the calculated jump multiplier
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_JUMP_STRENGTH)).setBaseValue(newJumpStrength);
        }

        // Set extra safe fall distance if applicable
        if (entity.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE) != null) {
            double baseSafeFallDistance = Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE)).getBaseValue();
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE)).setBaseValue(baseSafeFallDistance * jumpMultiplier + 1);
        }
    }

}
