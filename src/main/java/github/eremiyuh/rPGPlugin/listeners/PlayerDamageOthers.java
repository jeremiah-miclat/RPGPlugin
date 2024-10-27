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
    public void onEntityDamageOverTime(EntityDamageEvent event) {
        if (event.getEntity() instanceof Monster || event.getEntity() instanceof IronGolem || event.getEntity() instanceof Wolf) {

            // Get the entity and damage cause
            LivingEntity entity = (LivingEntity) event.getEntity();
            EntityDamageEvent.DamageCause cause = event.getCause();

            // Check if the damage cause is one of the specified types (poison, fire, drowning, freezing, etc.)
            if (cause == EntityDamageEvent.DamageCause.POISON ||
                    cause == EntityDamageEvent.DamageCause.FIRE_TICK ||
                    cause == EntityDamageEvent.DamageCause.DROWNING ||
                    cause == EntityDamageEvent.DamageCause.FREEZE ||
                    cause == EntityDamageEvent.DamageCause.WITHER ||
                    cause == EntityDamageEvent.DamageCause.LAVA ||
                    cause == EntityDamageEvent.DamageCause.SUFFOCATION ||
                    cause == EntityDamageEvent.DamageCause.FALL ||
                    cause == EntityDamageEvent.DamageCause.FALLING_BLOCK ||
                    cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
                    cause == EntityDamageEvent.DamageCause.LIGHTNING ||
                    cause == EntityDamageEvent.DamageCause.THORNS
            )
            {

                if (entity.hasMetadata("initialExtraHealth")) {
                    // Retrieve extra health attribute from entity's metadata or custom attribute
                    double extraHealth = entity.getMetadata("initialExtraHealth").getFirst().asDouble();

                    if (extraHealth > 0) {
                        double damage = event.getDamage();

                        if (extraHealth - damage > 0) {
                            entity.setMetadata("initialExtraHealth", new FixedMetadataValue(plugin, extraHealth - damage));
                            event.setDamage(0);
                        } else {
                            // Calculate excess damage
                            double excessDamage = damage - extraHealth;
                            entity.setHealth(Math.max(0, entity.getHealth() - excessDamage)); // Ensure health doesn't drop below 0

                            // Clear the extra health metadata as it has been depleted
                            entity.setMetadata("initialExtraHealth", new FixedMetadataValue(plugin, 0.0)); // Reset extra health to 0

                            event.setDamage(excessDamage);
                        }
                    }
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
    private double applyStatsToDamage(double baseDamage, UserProfile damagerProfile, Player player, EntityDamageByEntityEvent event) {
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

        // melee
        if (event.getDamager() instanceof Player) {
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


        // bow
        if (event.getDamager() instanceof Arrow) {

            //archers
            if (damagerProfile.getChosenClass().equalsIgnoreCase("archer")) {
                statDmg += (dex*.02);
                if (damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 1")) {
                    elementalDamage += 3;
                    elementalDamage += (intel*.01);
                } else {
                    elementalDamage += 2 + (intel*.01);
                }
            }else if (damagerProfile.getChosenClass().equalsIgnoreCase("swordsman")) {
                statDmg +=(dex*.005) ;
                elementalDamage+=(2+ (intel * 0.01));
            }
            else if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                statDmg += (dex*.005);
                elementalDamage+=(4+ (intel * 0.01));
            }


        }

        // thrown potions
        if (event.getDamager() instanceof ThrownPotion) {

            //alchemists
            if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                if (damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 1")) {
                    elementalDamage += 6;
                    elementalDamage += (intel*.01);
                } else {
                    elementalDamage += 4 + (intel*.01);
                }
            }else if (damagerProfile.getChosenClass().equalsIgnoreCase("swordsman")) {
                elementalDamage+=(2+ (intel * 0.005));
            }
            else if (damagerProfile.getChosenClass().equalsIgnoreCase("archer")) {
                elementalDamage+=(2+ (intel * 0.005));
            }


        }

        double calculatedDamage = baseDamage + statDmg + elementalDamage;

        // Critical Hit System
        double critChance = 0;
        if (damagerProfile.getChosenClass().equalsIgnoreCase("swordsman")){
            critChance += ((luk / 10) * 0.1 + 0.001);
        }
        if (damagerProfile.getChosenClass().equalsIgnoreCase("archer")){
            dex += ((dex / 10) * 0.1 + 0.001);
        }

        else {
            critChance += ((luk / 20) * 0.1 + 0.001);
        }
        double critDmgMultiplier = 1.1 + (dex * 0.01);
        if (damagerProfile.getChosenClass().equalsIgnoreCase("archer")){
            dex += ((dex / 10) * 0.1 + 0.001);
            critDmgMultiplier += 1.1 + (dex * 0.01);
        }
        boolean isCrit = Math.random() < critChance;

        if (isCrit) {
            calculatedDamage *= critDmgMultiplier;
        }
        player.sendMessage("Base dmg: " + baseDamage + ". stat str: " + statDmg + "." +
                "Elemental dmg: " + elementalDamage
                );

        return calculatedDamage;
    }

    // Method to apply damage considering extra health
    private double applyDamageWithExtraHealth(LivingEntity target, double calculatedDamage, double extraHealth, Player player) {
        player.giveExp((int)calculatedDamage);
        // Check if the calculated damage is less than the extra health
        if (extraHealth - calculatedDamage > 0) {
            // All damage is absorbed by extra health
            // Update the extra health value
            target.setMetadata("initialExtraHealth", new FixedMetadataValue(plugin, extraHealth - calculatedDamage));
            player.sendMessage("Target's remaining health: " + (int)(target.getHealth() + target.getMetadata("initialExtraHealth").getFirst().asDouble() - calculatedDamage));
            return 0; // Extra health absorbs all damage
        } else {
            // Calculate excess damage
            double excessDamage = calculatedDamage - extraHealth;

            // Apply excess damage to the target's health
            target.setHealth(Math.max(0, target.getHealth() - excessDamage)); // Ensure health doesn't drop below 0

            // Clear the extra health metadata as it has been depleted
            target.setMetadata("initialExtraHealth", new FixedMetadataValue(plugin, 0.0)); // Reset extra health to 0

            return excessDamage; // Return the excess damage applied
        }
    }

    // Method to apply extra health and return adjusted damage
    private double applyExtraHealthAndDamage(LivingEntity target, double calculatedDamage, Player player) {
        if (target instanceof Monster || target instanceof IronGolem || target instanceof Wolf) {
            initializeExtraAttributes(target, player); // Ensure metadata is initialized
        }

        // Retrieve and apply existing extra health
        double initialExtraHealth = 0;

        if (target.hasMetadata("initialExtraHealth")) {
            initialExtraHealth += target.getMetadata("initialExtraHealth").get(0).asDouble();
        }

        return applyDamageWithExtraHealth(target, calculatedDamage, initialExtraHealth, player);
    }




    // Method to initialize extra health and extra damage metadata
    private void initializeExtraAttributes(LivingEntity entity, Player player) {
        if (!entity.hasMetadata("extraHealthApplied")) {
            // Calculate extra health and damage
            Location location = entity.getLocation();
            double extraHealth = calculateExtraAttributes(location,entity);
            double extraDamage = extraHealth * 0.01; // Convert to damage (10% of extra health)

            // Store in metadata
            entity.setMetadata("initialExtraHealth", new FixedMetadataValue(plugin, extraHealth));
            entity.setMetadata("extraDamage", new FixedMetadataValue(plugin, extraDamage));
            entity.setMetadata("extraHealthApplied", new FixedMetadataValue(plugin, true)); // Mark as applied

            // Inform player (if applicable)
            if (player != null) {
                player.sendMessage("Mob initialized with extra health: " + extraHealth + " and extra damage: " + extraDamage);
                player.sendMessage("jump strenght: "+ entity.getAttribute(Attribute.GENERIC_JUMP_STRENGTH).getValue());
                player.sendMessage("jump safe dist: "+ entity.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE).getValue());
            }
              }
    }

    // Method to calculate extra attributes (used for both extra health and damage)
    private double calculateExtraAttributes(Location targetLocation, LivingEntity entity) {
        double maxCoord = Math.max(Math.abs(targetLocation.getBlockX()), Math.abs(targetLocation.getBlockZ()));
        double extraHealth = (maxCoord / 100) * 10; // 100% health for every 100 blocks

        // 5% chance to add +1000% extra health
        if (Math.random() < 0.5) {
            extraHealth += (extraHealth * 10); // Add 1000% health

            // Set the entity name to display as a boss with health bar
            String bossName = "Lvl " + (int) (Math.floor(maxCoord) /100) + " Boss " + entity.getType().name() +
                    " Health: " + (int) (entity.getHealth() + extraHealth);

            // Apply the custom name to the entity
            entity.setCustomName(bossName);
            entity.setCustomNameVisible(true);
            // Set extra movement speed if the attribute is available
            if (entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null) {
                double newSpeed = Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getBaseValue() * 1.5; // Increase speed by 50%
                Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(newSpeed);
            }

            // Set extra jump strength if applicable (for horses or similar entities that support it)
            if (entity.getAttribute(Attribute.GENERIC_JUMP_STRENGTH) != null) {
                double newJumpStrength = Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_JUMP_STRENGTH)).getBaseValue() * 3;
                Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_JUMP_STRENGTH)).setBaseValue(newJumpStrength);
            }

            // Set extra jump strength if applicable (for horses or similar entities that support it)
            if (entity.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE) != null) {
                double newSafeJumpDist = Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_JUMP_STRENGTH)).getBaseValue() * 3;
                Objects.requireNonNull(entity.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE)).setBaseValue(entity.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE).getBaseValue()*3);
            }
        }

        return extraHealth; // Use as basis for both health and extra damage
    }



}
