package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.methods.AbilityManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Map;
import java.util.Objects;

public class PlayerDamageOthers implements Listener {

    private  final PlayerProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final RPGPlugin plugin;


    public PlayerDamageOthers(PlayerProfileManager profileManager, AbilityManager abilityManager,RPGPlugin plugin) {
        this.profileManager = profileManager;
        this.abilityManager = abilityManager;
        this.plugin = plugin;
    }


    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        // Get the entity that was damaged
        Entity damaged = event.getEntity();

        // Get the damager (the entity dealing the damage)
        Entity damager = event.getDamager();

        // Get the location of the damaged entity
        Location damagedLocation = damaged.getLocation();

        // Get the location of the damager entity (if damager exists)
        Location damagerLocation = damager.getLocation();

        // Pve melee
        if (damager instanceof Player attacker && damaged instanceof LivingEntity victim && !(damaged instanceof Player))
        {


            UserProfile attackerProfile = profileManager.getProfile(attacker.getName());
            if (attackerProfile!=null) {
                handleMeleePveDamage(attacker,victim,event,damagerLocation,damagedLocation,attackerProfile);
            }
        }

        //PVE with bows and potion
        if (damager instanceof Projectile && damaged instanceof LivingEntity victim && !(damaged instanceof Player) ) {
            Projectile projectile = (Projectile) event.getDamager();
            ProjectileSource shooter = projectile.getShooter();

            // pve with bows
            if (projectile instanceof Arrow && shooter instanceof Player attacker) {
                UserProfile attackerProfile = profileManager.getProfile(attacker.getName());
                if (attackerProfile != null) {
                    handleLongRangePveDamage(attacker,victim,event,damagerLocation,damagedLocation,attackerProfile);
                }
            }

            //PVE with thrown instant damage
            if (projectile instanceof ThrownPotion && projectile.getShooter() instanceof Player attacker) {
                UserProfile attackerProfile = profileManager.getProfile(attacker.getName());
                if (attackerProfile != null) {
                    handleLongRangePveDamage(attacker,victim,event,damagerLocation,damagedLocation,attackerProfile);
                }
            }
        }


        if (damager instanceof LivingEntity angryMob && !(damager instanceof Player) && damaged instanceof Player damagedPLayer) {
                UserProfile damagedProfile = profileManager.getProfile(damagedPLayer.getName());
                if (damagedProfile.getChosenClass().equalsIgnoreCase("default")
                    || damagedProfile.getRPG().equalsIgnoreCase("off")
                ) {
                    event.setDamage(event.getDamage());
                } else {
                    initializeExtraAttributes(angryMob,damagedPLayer);

                    // Apply extra damage if present
                    if (angryMob.hasMetadata("extraDamage")) {
                        double extraDamage = angryMob.getMetadata("extraDamage").get(0).asDouble();
                        event.setDamage(event.getDamage() + extraDamage); // Apply stored extra damage
                        damagedPLayer.sendMessage("Extra damage from mob: " + extraDamage);
                    }
                }

        }


    }

    @EventHandler
    public void onPlayerFreezeDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (event.getCause() == EntityDamageEvent.DamageCause.FREEZE) {

                UserProfile profile = profileManager.getProfile(player.getName());
                if (profile.getSelectedElement().equalsIgnoreCase("ice")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    // pve melee damage
    private void handleMeleePveDamage(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, Location damagerLocation, Location damagedLocation, UserProfile damagerProfile) {
        // Check if the player is in the default class
        if (damagerProfile.getChosenClass().equalsIgnoreCase("default") || damagerProfile.getRPG().equalsIgnoreCase("off")) {
            event.setDamage(event.getDamage()); // Keep default Minecraft combat
            return;
        }

//        MonsterStrengthScalingListener monsterListener = plugin.getMonsterStrengthScalingListener();
//        Map<LivingEntity, Double> extraHealthMap = monsterListener.getExtraHealthMap();
        ItemStack weapon = attacker.getInventory().getItemInMainHand();

        // Fire element check: Disable fire if not selected
        if (!damagerProfile.getSelectedElement().equalsIgnoreCase("fire")
                && (weapon.containsEnchantment(Enchantment.FLAME) || weapon.containsEnchantment(Enchantment.FIRE_ASPECT))) {
            event.getEntity().setFireTicks(0); // Cancel fire ticks
        }



        double baseDamage = event.getDamage();

        // Apply stats based on class for non-default players
        double damageWithStats = applyStatsToDamage(baseDamage, damagerProfile, attacker, event);

//        // Calculate base and attribute-based damage
//        double damage = event.getDamage();
//        double strDmg = damagerProfile.getSwordsmanClassInfo().getStr() * 0.01;
//        double elementalDamage = 2.0 + (damagerProfile.getSwordsmanClassInfo().getIntel() * 0.01);
//        double newDmg = damage + strDmg + elementalDamage;
//
//        // Critical Hit System
//        double critChance = (damagerProfile.getSwordsmanClassInfo().getLuk() / 10) * 0.1 + 0.05;
//        double critDmgMultiplier = 1.1 + (damagerProfile.getSwordsmanClassInfo().getDex() * 0.01);
//        boolean isCrit = Math.random() < critChance;
//
//        if (isCrit) {
//            newDmg *= critDmgMultiplier;
//            attacker.sendMessage("Critical hit! Damage multiplied by " + critDmgMultiplier + ". Damage dealt: " + newDmg);
//            target.getWorld().playSound(damagerLocation, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
//            target.getWorld().playSound(damagedLocation, Sound.ENTITY_PLAYER_ATTACK_CRIT, 10, 1);
//        }
//
//        // Apply extra health check for all non-default classes
//        if (extraHealthMap.containsKey(target)) {
//            double currentHealth = target.getHealth();
//            double extraHealth = extraHealthMap.get(target);
//            double remainingHealth = extraHealth - newDmg;
//
//            if (remainingHealth > 0) {
//                extraHealthMap.put(target, remainingHealth);
//                event.setDamage(0); // Cancel the event damage
//                attacker.sendMessage("Current Health: " + currentHealth + ", Extra Health: " + extraHealth);
//                return;
//            } else {
//                extraHealthMap.remove(target);
//                event.setDamage(newDmg); // Apply remaining damage
//                return;
//            }
//        }

        // Swordsman-specific ability if holding a sword
        if (damagerProfile.getChosenClass().equalsIgnoreCase("swordsman") && weapon.getType().toString().endsWith("_SWORD")) {
            abilityManager.applyAbility(damagerProfile, target, damagerLocation, damagedLocation);
        }

        // Apply extra health and set final damage
        double finalDamage = applyExtraHealthAndDamage(target, damageWithStats, attacker);
        event.setDamage(finalDamage);
    }


    // PvE Long Range Damage
    private void handleLongRangePveDamage(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, Location damagerLocation, Location damagedLocation, UserProfile damagerProfile) {

        // Check if the player is in the default class
        if (damagerProfile.getChosenClass().equalsIgnoreCase("default")
                || damagerProfile.getRPG().equalsIgnoreCase("off")
        ) {
            event.setDamage(event.getDamage()); // Keep default Minecraft combat
            return;
        }

        ItemStack weapon = attacker.getInventory().getItemInMainHand();

        // Fire element check: Disable fire if not selected
        if (!damagerProfile.getSelectedElement().equalsIgnoreCase("fire")
                && (weapon.containsEnchantment(Enchantment.FLAME) || weapon.containsEnchantment(Enchantment.FIRE_ASPECT))) {
            event.getEntity().setFireTicks(0); // Cancel fire ticks
        }

//        MonsterStrengthScalingListener monsterListener = plugin.getMonsterStrengthScalingListener();
//        Map<LivingEntity, Double> extraHealthMap = monsterListener.getExtraHealthMap();

        double baseDamage = event.getDamage();

        // Apply stats based on class for non-default players
        double damageWithStats = applyStatsToDamage(baseDamage, damagerProfile, attacker, event);

        // Archer class - Check if using bow
        if (damagerProfile.getChosenClass().equalsIgnoreCase("archer") && event.getDamager() instanceof Arrow) {
            attacker.sendMessage("Archer class bonus applied");
            abilityManager.applyAbility(damagerProfile, target, damagerLocation, damagedLocation);
        }

        // Alchemist class - Check if using thrown potion
        if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist") && event.getDamager() instanceof ThrownPotion) {
            attacker.sendMessage("Alchemist class bonus applied");
            abilityManager.applyAbility(damagerProfile, target, damagerLocation, damagedLocation);
        }

        // Apply extra health and set final damage
        double finalDamage = applyExtraHealthAndDamage(target, damageWithStats, attacker);
        event.setDamage(finalDamage);
    }

    // Method to apply stats to damage
    private double applyStatsToDamage(double baseDamage, UserProfile damagerProfile, Player player, Event event) {
        double str = 0, dex = 0, intel = 0, luk = 0;

        // Get stats based on the player's chosen class
        if (damagerProfile.getChosenClass().equalsIgnoreCase("archer")) {
            str = damagerProfile.getArcherClassInfo().getStr();
            dex = damagerProfile.getArcherClassInfo().getDex();
            intel = damagerProfile.getArcherClassInfo().getIntel();
            luk = damagerProfile.getArcherClassInfo().getLuk();
        } else if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
            str = damagerProfile.getAlchemistClassInfo().getStr(); // Assume you have a similar method for Alchemist stats
            dex = damagerProfile.getAlchemistClassInfo().getDex();
            intel = damagerProfile.getAlchemistClassInfo().getIntel();
            luk = damagerProfile.getAlchemistClassInfo().getLuk();
        }
        else if (damagerProfile.getChosenClass().equalsIgnoreCase("swordsman")) {
            str = damagerProfile.getAlchemistClassInfo().getStr(); // Assume you have a similar method for Alchemist stats
            dex = damagerProfile.getAlchemistClassInfo().getDex();
            intel = damagerProfile.getAlchemistClassInfo().getIntel();
            luk = damagerProfile.getAlchemistClassInfo().getLuk();
        }

        // Damage calculation based on class stats
        double statDmg = 0;
        double elementalDamage = 0;
        if (event instanceof EntityDamageByEntityEvent) {
            if (damagerProfile.getChosenClass().equalsIgnoreCase("swordsman")) {
                statDmg += str*.01;
                if (damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 1") && player.getInventory().getItemInMainHand().getType().toString().endsWith("_SWORD")) {
                    elementalDamage += (4 + (intel * 0.01));
                } else {
                    elementalDamage += (2 + (intel * 0.01));
                }
            } else if (damagerProfile.getChosenClass().equalsIgnoreCase("archer")) {
                statDmg +=(str*.005) ;
                elementalDamage+=(2+ (intel * 0.01));
            }
            else if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                statDmg += (str*.005);
                elementalDamage+=(4+ (intel * 0.01));
            }
        }



        double calculatedDamage = baseDamage + statDmg + elementalDamage;

        // Critical Hit System
        double critChance = 0;
        if (damagerProfile.getChosenClass().equalsIgnoreCase("swordsman")){
            critChance += ((luk / 10) * 0.1 + 0.001);
        } else {
            critChance += ((luk / 20) * 0.1 + 0.001);
        }
        double critDmgMultiplier = 1.1 + (dex * 0.01);
        boolean isCrit = Math.random() < critChance;

        if (isCrit) {
            calculatedDamage *= critDmgMultiplier;
        }
        player.sendMessage("Base dmg: " + baseDamage + ". With stats applied: " + calculatedDamage + ".");

        return calculatedDamage;
    }

    // Method to apply extra health and return adjusted damage
    private double applyExtraHealth(LivingEntity target, double calculatedDamage, Player player) {
        // Check if extra health has already been applied
        if (target.hasMetadata("extraHealthApplied")) {
            // If extra health has been applied, retrieve the stored initial extra health value
            double initialExtraHealth = target.getMetadata("initialExtraHealth").get(0).asDouble();
            // Apply damage considering the existing extra health
            return applyDamageWithExtraHealth(target, calculatedDamage, initialExtraHealth, player);
        }

        // Get the target's location and calculate extra health
        Location targetLocation = target.getLocation();
        double extraHealth = calculateExtraHealth(targetLocation);

        // Store the initial extra health value in metadata
        target.setMetadata("initialExtraHealth", new FixedMetadataValue(plugin, extraHealth));
        target.setMetadata("extraHealthApplied", new FixedMetadataValue(plugin, true)); // Mark as applied

        // Send current health and extra health information
        player.sendMessage("Health: " + target.getHealth() + ". Extra health applied: " + extraHealth);

        // Apply damage considering the calculated extra health
        return applyDamageWithExtraHealth(target, calculatedDamage, extraHealth, player);
    }

    // Method to apply damage considering extra health
    private double applyDamageWithExtraHealth(LivingEntity target, double calculatedDamage, double extraHealth, Player player) {
        // Check if the calculated damage is less than the extra health
        if (extraHealth - calculatedDamage > 0) {
            // All damage is absorbed by extra health
            // Update the extra health value
            target.setMetadata("initialExtraHealth", new FixedMetadataValue(plugin, extraHealth - calculatedDamage));
            player.sendMessage("Damage absorbed by extra health. Remaining extra health: " + (extraHealth - calculatedDamage));
            return 0; // Extra health absorbs all damage
        } else {
            // Calculate excess damage
            double excessDamage = calculatedDamage - extraHealth;

            // Apply excess damage to the target's health
            target.setHealth(Math.max(0, target.getHealth() - excessDamage)); // Ensure health doesn't drop below 0

            // Clear the extra health metadata as it has been depleted
            target.setMetadata("initialExtraHealth", new FixedMetadataValue(plugin, 0.0)); // Reset extra health to 0
            player.sendMessage("Extra health depleted. Excess damage applied to target's health: " + excessDamage);

            return excessDamage; // Return the excess damage applied
        }
    }

    // Method to apply extra health and return adjusted damage
    private double applyExtraHealthAndDamage(LivingEntity target, double calculatedDamage, Player player) {
        initializeExtraAttributes(target, player); // Ensure metadata is initialized

        // Retrieve and apply existing extra health
        double initialExtraHealth = target.getMetadata("initialExtraHealth").get(0).asDouble();
        return applyDamageWithExtraHealth(target, calculatedDamage, initialExtraHealth, player);
    }


    // Method to calculate extra health based on target location
    private double calculateExtraHealth(Location targetLocation) {
        double maxCoord = Math.max(Math.abs(targetLocation.getBlockX()), Math.abs(targetLocation.getBlockZ()));
        double extraHealth = (maxCoord / 100) * 100; // 100% health for every 100 blocks
        double extraDamage = (maxCoord/100) * .1;
        // 5% chance to apply +1000% health
        if (Math.random() < 0.05) {
            extraHealth += (extraHealth * 10); // Add 1000% health
            extraDamage += extraDamage*10;
        }

        return extraHealth; // Return calculated extra health
    }


    // Method to initialize extra health and extra damage metadata
    private void initializeExtraAttributes(LivingEntity entity, Player player) {
        if (!entity.hasMetadata("extraHealthApplied")) {
            // Calculate extra health and damage
            Location location = entity.getLocation();
            double extraHealth = calculateExtraAttributes(location);
            double extraDamage = extraHealth * 0.1; // Convert to damage (10% of extra health)

            // Store in metadata
            entity.setMetadata("initialExtraHealth", new FixedMetadataValue(plugin, extraHealth));
            entity.setMetadata("extraDamage", new FixedMetadataValue(plugin, extraDamage));
            entity.setMetadata("extraHealthApplied", new FixedMetadataValue(plugin, true)); // Mark as applied

            // Inform player (if applicable)
            if (player != null) {
                player.sendMessage("Mob initialized with extra health: " + extraHealth + " and extra damage: " + extraDamage);
            }
        }
    }

    // Method to calculate extra attributes (used for both extra health and damage)
    private double calculateExtraAttributes(Location targetLocation) {
        double maxCoord = Math.max(Math.abs(targetLocation.getBlockX()), Math.abs(targetLocation.getBlockZ()));
        double extraHealth = (maxCoord / 100) * 100; // 100% health for every 100 blocks

        // 5% chance to add +1000% extra health
        if (Math.random() < 0.05) {
            extraHealth += (extraHealth * 10); // Add 1000% health
        }

        return extraHealth; // Use as basis for both health and extra damage
    }


}
