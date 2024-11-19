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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getLogger;

public class MonsterInitializerLabyrinth implements Listener {

    private final RPGPlugin plugin;
    private final Random random = new Random();



    public MonsterInitializerLabyrinth(RPGPlugin plugin) {
        this.plugin = plugin;

    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        String world = Objects.requireNonNull(event.getLocation().getWorld()).getName();
        if (!world.equals("world_labyrinth")) {
            return;
        }

        Location spawnLocation = event.getLocation();
        if (!isPlayerOnSameYLevel(spawnLocation)) {
            event.setCancelled(true);
            return;
        }


        if (event.getEntity() instanceof Phantom) {
            event.setCancelled(true);
        }

        if (event.getEntity() instanceof Monster || event.getEntity() instanceof Wolf || event.getEntity() instanceof IronGolem) {
            LivingEntity mob = event.getEntity();
            initializeExtraAttributes(mob);

        }
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
                if (Math.abs(entityY - playerY) <= 1) {
                    return true;
                }
            }
        }
        return false;
    }

    // Method to initialize extra health and extra damage metadata
    private void initializeExtraAttributes(LivingEntity entity) {

        Location location = entity.getLocation();
        double extraHealth = calculateExtraAttributes(location, entity);
        double extraDamage = extraHealth * 0.1; // Convert to damage (10% of extra health)

        // Store in metadata
        entity.setMetadata("initialExtraHealth", new FixedMetadataValue(plugin, extraHealth));
        entity.setMetadata("extraDamage", new FixedMetadataValue(plugin, extraDamage));

        // Initialize the list of players who have attacked this entity
        List<String> attackerList = new ArrayList<>();
        entity.setMetadata("attackerList", new FixedMetadataValue(plugin, attackerList));

    }

    // Method to calculate extra attributes (used for both extra health and damage)
    private double calculateExtraAttributes(Location targetLocation, LivingEntity entity) {
        int maxY = 306; // Starting height
        int minY = -63; // Minimum height
        double baseHealth = 3000; // Base health at max height
        double healthIncrement = 3000; // Increment per layer

        // Ensure the y-coordinate is within the range
        int yCoord = Math.min(maxY, Math.max(minY, targetLocation.getBlockY()));

        // Calculate extra health based on the y-coordinate
        double extraHealth = baseHealth + ((maxY - yCoord) * healthIncrement);

        // Determine the level based on the y-coordinate (1 level every 6 blocks)
        int lvl = 1 + (maxY - yCoord) / 6;

        // Set default name for normal monsters
//        String normalName = ChatColor.GREEN + "Lvl " + lvl + " " + entity.getType().name();
//        entity.setCustomName(normalName);
//        entity.setCustomNameVisible(true);

        // Boss scaling logic
        if (Math.random() < .005) {
            extraHealth *= 10; // Add 1000% health
            setBossAttributes(entity, targetLocation.getBlockY(), "Boss", ChatColor.RED);
            entity.setMetadata("boss", new FixedMetadataValue(plugin, true));
            entity.setMetadata("lvl", new FixedMetadataValue(plugin, lvl));
        }

        // World Boss scaling logic
        if (Math.random() < 0.0005) {
            extraHealth *= 100; // Add 10000% health
            setBossAttributes(entity, targetLocation.getBlockY(), "World Boss", ChatColor.DARK_PURPLE);
            entity.setMetadata("worldboss", new FixedMetadataValue(plugin, true));
            entity.setMetadata("lvl", new FixedMetadataValue(plugin, lvl));
        }

        return extraHealth; // Use as a basis for both health and extra damage
    }


    private void setBossAttributes(LivingEntity entity, double maxCoord, String type, ChatColor color) {
        String bossName = color + "Lvl " + (int) (Math.floor(maxCoord) / 100) + " " + type + " " + entity.getType().name();
        entity.setCustomName(bossName);
        entity.setCustomNameVisible(true);
        entity.setRemoveWhenFarAway(false);
//        entity.setPersistent(true);

        // Calculate speed and jump multipliers based on maxCoord
        double speedMultiplier = 1 + (0.000075 * maxCoord); // Continuous increase for speed
        double jumpMultiplier = 1 + (0.00015 * maxCoord);  // Continuous increase for jump

        // Set extra movement speed if the attribute is available
        if (entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null) {
            double baseSpeed = Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getBaseValue();
            double newSpeed = baseSpeed * speedMultiplier; // Apply the calculated multiplier
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(newSpeed);
        }

        // Set extra jump strength if applicable
        if (entity.getAttribute(Attribute.GENERIC_JUMP_STRENGTH) != null) {
            double baseJumpStrength = Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_JUMP_STRENGTH)).getBaseValue();
            double newJumpStrength = baseJumpStrength * jumpMultiplier; // Apply the calculated multiplier
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_JUMP_STRENGTH)).setBaseValue(newJumpStrength);
        }

        // Set extra safe fall distance if applicable
        if (entity.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE) != null) {
            double baseSafeFallDistance = Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE)).getBaseValue();
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE)).setBaseValue(baseSafeFallDistance * jumpMultiplier+1);
        }
    }

    // Ensure normal monsters are green
    private void setNormalMonsterName(LivingEntity entity) {

    }



}
