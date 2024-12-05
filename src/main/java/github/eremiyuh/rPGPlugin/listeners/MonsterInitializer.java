package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.*;
import java.util.List;

public class MonsterInitializer implements Listener {

    private final RPGPlugin plugin;
    private final Random random = new Random();




    public MonsterInitializer(RPGPlugin plugin) {
        this.plugin = plugin;
    }




    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        String world = Objects.requireNonNull(event.getLocation().getWorld()).getName();

        if (!world.equals("world_rpg")) {
            return;
        }

        // Count the number of players in world_rpg
        int playersInRPGWorld = (int) Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getWorld().getName().equals("world_rpg"))
                .count();

        // Calculate the spawn limit based on players in world_rpg
        int mobLimit = Math.min(playersInRPGWorld * 10, 150);

        // Count the number of mobs already in the world
        int currentMobCount = countMobsInWorld(event.getLocation().getWorld());


        // Cancel the spawn if the mob limit is reached
        if (currentMobCount >= mobLimit) {
            event.setCancelled(true);
        }

        if (!isPlayerOnSameYLevel(event.getLocation())) {
            event.setCancelled(true);
            return;
        }

        if ((event.getEntity() instanceof Animals || event.getEntity() instanceof Phantom)
                && !(event.getEntity() instanceof Spider) && !(event.getEntity() instanceof CaveSpider)) {
            event.setCancelled(true);

            return;
        }

        if (event.getEntity() instanceof Monster monster) {
            initializeExtraAttributes(monster);

        }
    }

    private int countMobsInWorld(World world) {
        int mobCount = 0;

        // Loop through all entities in the world and count the mobs
        for (LivingEntity entity : world.getLivingEntities()) {
            if (entity instanceof Monster && !entity.hasMetadata("extraHealth")) {
                entity.remove();
            }

            if (entity instanceof Monster && !entity.hasMetadata("worldboss")
                && entity.hasMetadata("extraHealth")
            ) {
                mobCount++;
            }
        }

        return mobCount;
    }




    private boolean isPlayerOnSameYLevel(Location location) {
        int entityY = location.getBlockY();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(location.getWorld())) {
                int playerY = player.getLocation().getBlockY();
                if (Math.abs(entityY - playerY) <= 3) {
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

        // Set extra health as metadata
//        double baseHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
//        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(baseHealth + extraHealth);
//        entity.setHealth(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

        // Store metadata for extra health and damage


        List<String> attackerList = new ArrayList<>();
        entity.setMetadata("attackerList", new FixedMetadataValue(plugin, attackerList));





        setHealthIndicator(entity);
    }

    private void setHealthIndicator(LivingEntity entity) {
        double totalHealth = entity.getHealth();

        // Format the health indicator (e.g., [99] for health 99)
        String healthIndicator = ChatColor.YELLOW + " [" + (int) totalHealth + "]";

        // Get the existing custom name from metadata, or use the default entity type if no custom name is set
        String customName = entity.hasMetadata("customName") ? entity.getMetadata("customName").get(0).asString() : entity.getType().name();


        // Set the updated custom name with the new health indicator
        entity.setCustomName(customName + healthIndicator);
    }

    private void calculateExtraAttributes(Location targetLocation, LivingEntity entity) {
        double maxCoord = Math.max(Math.abs(targetLocation.getBlockX()), Math.abs(targetLocation.getBlockZ()));

        int lvl = (int) (Math.floor(maxCoord) / 100);
        String normalName = ChatColor.GREEN + "Lvl " + lvl + " " + entity.getType().name();
        entity.setCustomName(normalName);
        entity.setMetadata("extraHealth", new FixedMetadataValue(plugin, maxCoord));
        entity.setMetadata("customName", new FixedMetadataValue(plugin, normalName));


        double customMaxHealth = maxCoord*101;

        Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(customMaxHealth);
        double extraHealth = Math.max(Math.abs(entity.getLocation().getX()), Math.abs(entity.getLocation().getZ()));


        double customDamage = Math.min(extraHealth/10, customMaxHealth);


        if (Math.random() < .09) { //.005
            extraHealth = (extraHealth * 10); // Add 1000% health
            setBossAttributes(entity, maxCoord, "Boss", ChatColor.RED);
            entity.setMetadata("boss", new FixedMetadataValue(plugin, true));
            entity.setMetadata("lvl", new FixedMetadataValue(plugin, lvl));
            entity.setMetadata("extraHealth", new FixedMetadataValue(plugin, extraHealth));
//            entity.setGlowing(true);
            entity.setHealth(Math.min(extraHealth, customMaxHealth));
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(customDamage*2);
            entity.setMetadata("customDamage", new FixedMetadataValue(plugin, customDamage*2));
            return;
        }

        if (Math.random() < .001) { //.0005
            extraHealth = (extraHealth * 100); // Add 10000% health
            setBossAttributes(entity, maxCoord, "World Boss", ChatColor.DARK_PURPLE);
            entity.setMetadata("worldboss", new FixedMetadataValue(plugin, true));
            entity.setMetadata("lvl", new FixedMetadataValue(plugin, lvl));
            entity.setMetadata("extraHealth", new FixedMetadataValue(plugin, extraHealth));
            entity.setGlowing(true);
            entity.setHealth(Math.min(extraHealth, customMaxHealth));
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(customDamage*3);
            entity.setMetadata("customDamage", new FixedMetadataValue(plugin, customDamage*3));
            entity.setRemoveWhenFarAway(false);
            entity.setPersistent(true);
            entity.setCustomNameVisible(true);
            return;
        }

        entity.setHealth(Math.min(extraHealth, customMaxHealth));
        entity.setMetadata("customDamage", new FixedMetadataValue(plugin, customDamage));
        Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(customDamage);

    }

    private void setBossAttributes(LivingEntity entity, double maxCoord, String type, ChatColor color) {
        String bossName = color + "Lvl " + (int) (Math.floor(maxCoord) / 100) + " " + type + " " + entity.getType().name();
        entity.setCustomName(bossName);


        entity.setMetadata("customName", new FixedMetadataValue(plugin, bossName));

        // Update health indicator
        setHealthIndicator(entity);

        // Calculate speed and jump multipliers based on maxCoord
        double speedMultiplier = 1 + (0.000075 * maxCoord); // Continuous increase for speed
        double jumpMultiplier = 1 + (0.00015 * maxCoord);  // Continuous increase for jump

        // Set extra movement speed if the attribute is available
        if (entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null) {
            double baseSpeed = Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getBaseValue();
            double newSpeed = baseSpeed * speedMultiplier; // Apply the calculated multiplier
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(newSpeed+.05);
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
            Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE)).setBaseValue(baseSafeFallDistance * jumpMultiplier + 1);
        }
    }



    // Method to spawn the floating hologram above the monster
    private void spawnFloatingHologram(Location location, String text, World world,
                                       net.md_5.bungee.api.ChatColor color) {
        // Create the ArmorStand at the given location
        ArmorStand armorStand = (ArmorStand) world.spawnEntity(location.clone().add(0, 1, 0), EntityType.ARMOR_STAND);

        String coloredText = color + text;

        // Set up the hologram text
        armorStand.setCustomName(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', coloredText));
        armorStand.setCustomNameVisible(true);

        // Make the ArmorStand invisible and disable its gravity to simulate a floating text
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true); // Small size and no hitbox

        // Create a task to move the hologram upwards
        moveHologramUpwards(armorStand);
    }

    // Method to move the hologram upwards
    private void moveHologramUpwards(ArmorStand armorStand) {
        new BukkitRunnable() {
            private double offsetY = 0;

            @Override
            public void run() {
                // Move the hologram upwards
                armorStand.teleport(armorStand.getLocation().add(0, 0.1, 0)); // Move up by 0.1 blocks

                // After moving upwards by a certain amount, stop the task (you can adjust this)
                if (offsetY >= 2.0) {
                    this.cancel(); // Stop the task after moving the hologram upwards by 2 blocks
                    armorStand.remove(); // Optionally remove the hologram after the animation is complete
                }

                offsetY += 0.1;
            }
        }.runTaskTimer(plugin, 0L, 1L); // Runs every tick (1/20th of a second)
    }

    private void broadcastFromSeizonSMP(String message) {
        Bukkit.getServer().getOnlinePlayers().forEach(player ->
                player.sendMessage(ChatColor.GOLD + "[Seizon SMP] " + ChatColor.RESET + message));
    }
}
