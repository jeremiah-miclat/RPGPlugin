package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntitySpellCastEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
    public void onMobPickUpItem(EntityPickupItemEvent event) {
        String world = Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName();
        if (!world.contains("world_rpg")) {
            return;
        }

        if (!(event.getEntity() instanceof Monster)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void armorStandSpawn(EntitySpawnEvent event) {
        String world = Objects.requireNonNull(event.getLocation().getWorld()).getName();

        if (!world.equals("world_rpg")) {
            return;
        }
        if (!(event.getEntity() instanceof ArmorStand)) {
            return;
        }

        if (event.getEntity() instanceof  ArmorStand armorStand) {
            armorStand.setInvisible(true);
        }

    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        String world = Objects.requireNonNull(event.getLocation().getWorld()).getName();

        if (!world.equals("world_rpg")) {
            return;
        }
        event.getEntity().setCanPickupItems(false);

//        if (!isPlayerOnSameYLevel(event.getLocation())) {
//            event.setCancelled(true);
//            return;
//        }

        if (
                (event.getEntity() instanceof Animals || event.getEntity() instanceof Phantom) ||
                        (event.getEntity() instanceof Zombie && ((Zombie) event.getEntity()).isBaby())
        ) {
            if (!(event.getEntity() instanceof Spider) && !(event.getEntity() instanceof CaveSpider)) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getEntity() instanceof Monster monster) {
            monster.setNoDamageTicks(0);
            initializeExtraAttributes(monster);

        }

        if (event.getEntity() instanceof Villager villager) {
            Objects.requireNonNull(villager.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(10000);
            villager.setHealth(10000);

        }
    }





    private boolean isPlayerOnSameYLevel(Location location) {
        int entityY = location.getBlockY();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(location.getWorld())) {
                int playerY = player.getLocation().getBlockY();
                if (Math.abs(entityY - playerY) <= 30) {
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

    private void calculateExtraAttributes(Location targetLocation, LivingEntity entity) {
        double maxCoord = Math.max(Math.abs(targetLocation.getBlockX()), Math.abs(targetLocation.getBlockZ()));

        if (entity instanceof Warden warden) {
            warden.setPersistent(true);
            entity.setRemoveWhenFarAway(false);
        }

        if (entity instanceof Wither wither) {
            wither.setPersistent(true);
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

        int lvl = (int) (Math.floor(maxCoord) / 100);
        String normalName = ChatColor.GREEN + "Lvl " + lvl + " " + entity.getType().name();
        entity.setCustomName(normalName);
        double customMaxHealth =entity.getHealth() + lvl+20;


        double extraHealth = lvl*5+entity.getHealth();

        double customDamage = lvl*.05 + Objects.requireNonNull(entity.getAttribute(Attribute.ATTACK_DAMAGE)).getValue() + 2;

        // First, check for the purple world boss (0.1% chance)
        if (Math.random() < .0003 || entity instanceof Warden || entity instanceof Wither || entity instanceof ElderGuardian || entity instanceof Ravager || entity instanceof Evoker && !(entity instanceof Vex)) { //0.0003
            extraHealth += lvl*2000;
            setBossAttributes(entity, maxCoord, "World Boss", ChatColor.DARK_PURPLE);
            entity.setGlowing(true);
            Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(extraHealth+10);
            entity.setHealth(extraHealth);

            Objects.requireNonNull(entity.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(customDamage+(lvl*.95) );
            entity.setPersistent(true);
            entity.setCustomNameVisible(true);
            entity.addPotionEffect(new PotionEffect((PotionEffectType.REGENERATION),Integer.MAX_VALUE,1,true,true));
            return;
        }

        // Only check for red boss (1% chance) if not already a purple boss
        if (Math.random() < .003 ) { //0.003
            extraHealth += lvl*200;
            setBossAttributes(entity, maxCoord, "Boss", ChatColor.RED);
            Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(extraHealth+10);
            entity.setHealth(extraHealth);
            Objects.requireNonNull(entity.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(customDamage+(lvl*.45));

            entity.setCustomNameVisible(true);
            return;
        }


        if (Math.random() < 0.03 || entity instanceof Vindicator) {
            extraHealth += lvl*10;
            setBossAttributes(entity, maxCoord, "Leader", ChatColor.YELLOW);
            Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(extraHealth+10);
            entity.setHealth(extraHealth);
            Objects.requireNonNull(entity.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(customDamage );
            entity.setCustomNameVisible(true);
            return;
        }

        // If neither of the boss conditions are met, set standard attributes
        Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(extraHealth);
        entity.setHealth(extraHealth);
        Objects.requireNonNull(entity.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(customDamage);
    }

    private void setBossAttributes(LivingEntity entity, double maxCoord, String type, ChatColor color) {
        String bossName = color + "Lvl " + (int) (Math.floor(maxCoord) / 100) + " " + type + " " + entity.getType().name();
        entity.setCustomName(bossName);


        // Update health indicator
        setHealthIndicator(entity);

        // Calculate speed and jump multipliers based on maxCoord
        double speedMultiplier =  (0.000075 * maxCoord); // Continuous increase for speed
        double jumpMultiplier = 1 + (0.00015 * maxCoord);  // Continuous increase for jump

        // Set extra movement speed if the attribute is available
        if (entity.getAttribute(Attribute.MOVEMENT_SPEED) != null) {
            double baseSpeed = Objects.requireNonNull(entity.getAttribute(Attribute.MOVEMENT_SPEED)).getBaseValue();
            double newSpeed = baseSpeed * (1+ (speedMultiplier/4)); // Apply the calculated multiplier
            Objects.requireNonNull(entity.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(newSpeed+.02);
        }

        // Set extra jump strength if applicable
        if (entity.getAttribute(Attribute.JUMP_STRENGTH) != null) {
            double baseJumpStrength = Objects.requireNonNull(entity.getAttribute(Attribute.JUMP_STRENGTH)).getBaseValue();
            double newJumpStrength = baseJumpStrength * jumpMultiplier; // Apply the calculated multiplier
            Objects.requireNonNull(entity.getAttribute(Attribute.JUMP_STRENGTH)).setBaseValue(newJumpStrength);
        }

        // Set extra safe fall distance if applicable
        if (entity.getAttribute(Attribute.SAFE_FALL_DISTANCE) != null) {
            double baseSafeFallDistance = Objects.requireNonNull(entity.getAttribute(Attribute.SAFE_FALL_DISTANCE)).getBaseValue();
            Objects.requireNonNull(entity.getAttribute(Attribute.SAFE_FALL_DISTANCE)).setBaseValue(baseSafeFallDistance * jumpMultiplier + 1);
        }
    }

    @EventHandler
    public void onEvokerSpell(EntitySpellCastEvent event) {
        if (event.getEntity() instanceof Evoker evoker && evoker.getWorld().getName().contains("rpg"))  {
            if (event.getSpell() == Spellcaster.Spell.SUMMON_VEX) {
                event.setCancelled(true); // Cancel only Vex summon
            }
        }
    }


}
