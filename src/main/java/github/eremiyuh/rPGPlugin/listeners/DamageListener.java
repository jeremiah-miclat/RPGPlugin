package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.methods.DamageAbilityManager;
import github.eremiyuh.rPGPlugin.methods.EffectsAbilityManager;
import github.eremiyuh.rPGPlugin.perms.PlayerBuffPerms;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DamageListener implements Listener {

    private  final PlayerProfileManager profileManager;
    private final EffectsAbilityManager effectsAbilityManager;
    private  final DamageAbilityManager damageAbilityManager;
    private final RPGPlugin plugin;


    public DamageListener(PlayerProfileManager profileManager, EffectsAbilityManager effectsAbilityManager, DamageAbilityManager damageAbilityManager, RPGPlugin plugin) {
        this.profileManager = profileManager;
        this.effectsAbilityManager = effectsAbilityManager;
        this. damageAbilityManager = damageAbilityManager;
        this.plugin = plugin;
    }


    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && player.getAllowFlight()) {
            player.sendMessage("you can't attack while flying");
            event.setCancelled(true);
            return;
        }
        if (!Objects.requireNonNull(event.getDamager().getLocation().getWorld()).getName().equals("world_rpg")) {
            return;
        }
        // Get the entity that was damaged
        Entity damaged = event.getEntity();

        // Get the damager (the entity dealing the damage)
        Entity damager = event.getDamager();



        // Get the location of the damaged entity
        Location damagedLocation = damaged.getLocation();

        // Get the location of the damager entity (if damager exists)
        Location damagerLocation = damager.getLocation();

        // PVP HANDLER

        // Melee PVP
        if (damager instanceof Player attacker && damaged instanceof Player damagedPlayer){

            UserProfile attackerProfile = profileManager.getProfile(attacker.getName());
            UserProfile damagedProfile = profileManager.getProfile(damaged.getName());

            if (attackerProfile==null || damagedProfile==null) {
                attacker.sendMessage("Your profile or target's profile can not be found. contact developer to fix files");
                damaged.sendMessage("Your profile or dude's profile can not be found. contact developer to fix files");
                event.setCancelled(true);
            }

            assert attackerProfile != null;
            if (!attackerProfile.isPvpEnabled()) {
                event.setCancelled(true);
            }

            assert damagedProfile != null;
            if (!damagedProfile.isPvpEnabled()) {
                event.setCancelled(true);
            }

            damagedProfile.setDurability(damagedProfile.getDurability()-1);
            damagedProfile.setStamina(damagedProfile.getStamina()-1);

            // Loop through all armor slots (helmet, chestplate, leggings, boots)
            for (int i = 0; i < damagedPlayer.getInventory().getArmorContents().length; i++) {
                ItemStack armorItem = damagedPlayer.getInventory().getArmorContents()[i];

                if (armorItem != null && armorItem.getType().getMaxDurability() > 0) { // Check if the item has durability
                    ItemMeta meta = armorItem.getItemMeta();
                    if (meta != null) {
                        armorItem.setDurability((short) 0); // Reset durability
                    }
                }
            }


        }
        // PVP with bow
        if (damager instanceof Projectile projectile && damaged instanceof Player damagedPlayer && projectile.getShooter() instanceof Player shooter ){
            //bow
                UserProfile shooterProfile = profileManager.getProfile(shooter.getName());
                UserProfile damagedProfile = profileManager.getProfile(damaged.getName());

                if (shooterProfile==null || damagedProfile==null) {
                    shooter.sendMessage("Your profile or target's profile can not be found. contact developer to fix files");
                    damaged.sendMessage("Your profile or dude's profile can not be found. contact developer to fix files");
                    event.setCancelled(true);
                }

                assert shooterProfile != null;
                if (!shooterProfile.isPvpEnabled()) {
                    event.setCancelled(true);
                }

                assert damagedProfile != null;
                if (!damagedProfile.isPvpEnabled()) {
                    event.setCancelled(true);
                }
            damagedProfile.setDurability(damagedProfile.getDurability()-1);
            damagedProfile.setStamina(damagedProfile.getStamina()-1);

            // Loop through all armor slots (helmet, chestplate, leggings, boots)
            for (int i = 0; i < damagedPlayer.getInventory().getArmorContents().length; i++) {
                ItemStack armorItem = damagedPlayer.getInventory().getArmorContents()[i];

                if (armorItem != null && armorItem.getType().getMaxDurability() > 0) { // Check if the item has durability
                    ItemMeta meta = armorItem.getItemMeta();
                    if (meta != null) {
                        armorItem.setDurability((short) 0); // Reset durability
                    }
                }
            }
        }
        // PVP with Potion
        if (damager instanceof Projectile projectile && damaged instanceof Player damagedPlayer && projectile instanceof ThrownPotion thrownPotion && thrownPotion.getShooter() instanceof Player thrower ){
            //bow
            UserProfile throwerProfile = profileManager.getProfile(thrower.getName());
            UserProfile damagedProfile = profileManager.getProfile(damaged.getName());

            if (throwerProfile==null || damagedProfile==null) {
                thrower.sendMessage("Your profile or target's profile can not be found. contact developer to fix files");
                damaged.sendMessage("Your profile or dude's profile can not be found. contact developer to fix files");
                event.setCancelled(true);
            }

            assert throwerProfile != null;
            if (!throwerProfile.isPvpEnabled()) {
                event.setCancelled(true);
            }

            assert damagedProfile != null;
            if (!damagedProfile.isPvpEnabled()) {
                event.setCancelled(true);
            }

            damagedProfile.setDurability(damagedProfile.getDurability()-1);
            damagedProfile.setStamina(damagedProfile.getStamina()-1);

            // Loop through all armor slots (helmet, chestplate, leggings, boots)
            for (int i = 0; i < damagedPlayer.getInventory().getArmorContents().length; i++) {
                ItemStack armorItem = damagedPlayer.getInventory().getArmorContents()[i];

                if (armorItem != null && armorItem.getType().getMaxDurability() > 0) { // Check if the item has durability
                    ItemMeta meta = armorItem.getItemMeta();
                    if (meta != null) {
                        armorItem.setDurability((short) 0); // Reset durability
                    }
                }
            }

        }



        // END OF PVP HANDLER



        // Pve melee
        if (damager instanceof Player attacker && damaged instanceof LivingEntity victim)
        {
            ItemStack itemInHand = attacker.getInventory().getItemInMainHand();
            ItemMeta meta = itemInHand.getItemMeta();
            if (meta != null) {
                itemInHand.setDurability((short) 0);
            }

            UserProfile attackerProfile = profileManager.getProfile(attacker.getName());

            if (attackerProfile != null) {
                handleMeleePveDamage(attacker,victim,event,damagerLocation,damagedLocation,attackerProfile);
            }
            if (victim.hasMetadata("attackerList")) {
                List<String> attackerList = (List<String>) victim.getMetadata("attackerList").get(0).value();

                // Add the attacker's name to the list if it's not already present
                String attackerName = attacker.getName();
                assert attackerList != null;
                if (!attackerList.contains(attackerName)) {
                    attackerList.add(attackerName);
                    victim.setMetadata("attackerList", new FixedMetadataValue(plugin, attackerList)); // Update metadata
                }
            }
        }

        //PVE with bows and potion
        if (damager instanceof Projectile && damaged instanceof LivingEntity victim) {

            Projectile projectile = (Projectile) event.getDamager();
            ProjectileSource shooter = projectile.getShooter();

            // pve with bows
            if (projectile instanceof Arrow && shooter instanceof Player attacker) {

                UserProfile attackerProfile = profileManager.getProfile(attacker.getName());
                // If the item is not null and has durability (like tools or weapons)
                ItemStack itemInHand = attacker.getInventory().getItemInMainHand();
                ItemMeta meta = itemInHand.getItemMeta();
                if (meta != null) {
                    itemInHand.setDurability((short) 0);

                }
                if (victim.hasMetadata("attackerList")) {
                    // Retrieve the metadata safely
                    @SuppressWarnings("unchecked") // Suppress unchecked cast warning
                    List<String> attackerList = (List<String>) victim.getMetadata("attackerList").get(0).value();

                    String attackerName = attacker.getName();
                    if (!attackerList.contains(attackerName)) {
                        attackerList.add(attackerName);
                        victim.setMetadata("attackerList", new FixedMetadataValue(plugin, attackerList)); // Update metadata
                    }
                } else {
                    // Initialize the attacker list if it doesn't exist
                    List<String> newAttackerList = new ArrayList<>();
                    newAttackerList.add(attacker.getName());
                    victim.setMetadata("attackerList", new FixedMetadataValue(plugin, newAttackerList)); // Set new metadata
                }
                if (attackerProfile != null) {
                    handleLongRangePveDamage(attacker,victim,event,damagerLocation,damagedLocation,attackerProfile);
                }
            }

            //PVE with thrown instant damage
            if (projectile instanceof ThrownPotion && projectile.getShooter() instanceof Player attacker ) {

                UserProfile attackerProfile = profileManager.getProfile(attacker.getName());
                if (victim.hasMetadata("attackerList")) {
                    // Retrieve the metadata safely
                    @SuppressWarnings("unchecked") // Suppress unchecked cast warning
                    List<String> attackerList = (List<String>) victim.getMetadata("attackerList").get(0).value();

                    String attackerName = attacker.getName();
                    if (!attackerList.contains(attackerName)) {
                        attackerList.add(attackerName);
                        victim.setMetadata("attackerList", new FixedMetadataValue(plugin, attackerList)); // Update metadata
                    }
                } else {
                    // Initialize the attacker list if it doesn't exist
                    List<String> newAttackerList = new ArrayList<>();
                    newAttackerList.add(attacker.getName());
                    victim.setMetadata("attackerList", new FixedMetadataValue(plugin, newAttackerList)); // Set new metadata
                }
                if (attackerProfile != null) {
                    handleLongRangePveDamage(attacker,victim,event,damagerLocation,damagedLocation,attackerProfile);
                }
            }
        }

        // monster attacks player
        if (damager instanceof LivingEntity angryMob && !(damager instanceof Player) && damaged instanceof Player damagedPLayer) {

            if (angryMob.hasMetadata("extraDamage")) {
                UserProfile damagedProfile = profileManager.getProfile(damagedPLayer.getName());
                damagedProfile.setDurability(damagedProfile.getDurability()-1);
                damagedProfile.setStamina(damagedProfile.getStamina()-1);

                // Loop through all armor slots (helmet, chestplate, leggings, boots)
                for (int i = 0; i < damagedPLayer.getInventory().getArmorContents().length; i++) {
                    ItemStack armorItem = damagedPLayer.getInventory().getArmorContents()[i];

                    if (armorItem != null && armorItem.getType().getMaxDurability() > 0) { // Check if the item has durability
                        ItemMeta meta = armorItem.getItemMeta();
                        if (meta != null) {
                            armorItem.setDurability((short) 0); // Reset durability
                        }
                    }
                }

                double extraDamage = angryMob.getMetadata("extraDamage").get(0).asDouble();
                if (PlayerBuffPerms.canReduceDmg(damagedProfile)) {
                    event.setDamage((event.getDamage() + extraDamage)/4);
                }
                else if (damagedProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                    event.setDamage((event.getDamage() + extraDamage)*1.2);
                }
                else {
                    if (damagedProfile.getChosenClass().equalsIgnoreCase("swordsman")) {
                        event.setDamage((event.getDamage() + extraDamage)/1.25);
                    } else {
                        event.setDamage((event.getDamage() + extraDamage));
                    }
                }


            }
        }

        if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Monster angryMob && !(damager instanceof Player) && damaged instanceof Player damagedPLayer) {

            // Loop through all armor slots (helmet, chestplate, leggings, boots)
            for (int i = 0; i < damagedPLayer.getInventory().getArmorContents().length; i++) {
                ItemStack armorItem = damagedPLayer.getInventory().getArmorContents()[i];

                if (armorItem != null && armorItem.getType().getMaxDurability() > 0) { // Check if the item has durability
                    ItemMeta meta = armorItem.getItemMeta();
                    if (meta != null) {
                        armorItem.setDurability((short) 0); // Reset durability
                        armorItem.setItemMeta(meta); // Apply changes to the item
                    }
                }
            }

            if (angryMob.hasMetadata("extraDamage")) {
                UserProfile damagedProfile = profileManager.getProfile(damagedPLayer.getName());
                damagedProfile.setDurability(damagedProfile.getDurability()-1);
                damagedProfile.setStamina(damagedProfile.getStamina()-1);
                double extraDamage = angryMob.getMetadata("extraDamage").get(0).asDouble();
                if (PlayerBuffPerms.canReduceDmg(damagedProfile)) {
                    event.setDamage((event.getDamage() + extraDamage)/4);
                }
                else if (damagedProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                    event.setDamage((event.getDamage() + extraDamage)*1.2);
                }
                else {
                    if (damagedProfile.getChosenClass().equalsIgnoreCase("swordsman")) {
                        event.setDamage((event.getDamage() + extraDamage)/1.25);
                    } else {
                        event.setDamage((event.getDamage() + extraDamage));
                    }
                }


            }
        }

        if (damager instanceof ThrownPotion projectile && projectile.getShooter() instanceof Monster angryMob && !(damager instanceof Player) && damaged instanceof Player damagedPLayer) {

            // Loop through all armor slots (helmet, chestplate, leggings, boots)
            for (int i = 0; i < damagedPLayer.getInventory().getArmorContents().length; i++) {
                ItemStack armorItem = damagedPLayer.getInventory().getArmorContents()[i];

                if (armorItem != null && armorItem.getType().getMaxDurability() > 0) { // Check if the item has durability
                    ItemMeta meta = armorItem.getItemMeta();
                    if (meta != null) {
                        armorItem.setDurability((short) 0); // Reset durability
                        armorItem.setItemMeta(meta); // Apply changes to the item
                    }
                }
            }

            if (angryMob.hasMetadata("extraDamage")) {
                UserProfile damagedProfile = profileManager.getProfile(damagedPLayer.getName());
                damagedProfile.setDurability(damagedProfile.getDurability()-1);
                damagedProfile.setStamina(damagedProfile.getStamina()-1);
                double extraDamage = angryMob.getMetadata("extraDamage").get(0).asDouble();
                if (PlayerBuffPerms.canReduceDmg(damagedProfile)) {
                    event.setDamage((event.getDamage() + extraDamage)/4);
                }
                else if (damagedProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                    event.setDamage((event.getDamage() + extraDamage)*1.2);
                }
                else {
                    if (damagedProfile.getChosenClass().equalsIgnoreCase("swordsman")) {
                        event.setDamage((event.getDamage() + extraDamage)/1.25);
                    } else {
                        event.setDamage((event.getDamage() + extraDamage));
                    }
                }


            }
        }


    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onModifiedEntityTakeDamage(EntityDamageEvent event) {
        if (!Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName().equals("world_rpg")) {
            return;
        }

        if (event.getEntity() instanceof Monster || event.getEntity() instanceof IronGolem || event.getEntity() instanceof Wolf) {

            // Get the entity and damage cause
            LivingEntity entity = (LivingEntity) event.getEntity();





            EntityDamageEvent.DamageCause cause = event.getCause();
            // Check if the damage cause is one of the specified types (poison, fire, drowning, freezing, etc.)


                if (entity.hasMetadata("initialExtraHealth")) {

                    if (event.getDamageSource().getCausingEntity() instanceof Player player && player.getAllowFlight()) return;

                    // Retrieve extra health attribute from entity's metadata or custom attribute
                    double extraHealth = entity.getMetadata("initialExtraHealth").getFirst().asDouble();








                    if (extraHealth > 0) {
                        double damage = event.getDamage();

                        if (extraHealth - damage > 0) {
                            entity.setMetadata("initialExtraHealth", new FixedMetadataValue(plugin, extraHealth - damage));
                            if (event.getDamageSource().getCausingEntity() instanceof Player player && (entity.hasMetadata("boss") || entity.hasMetadata("worldboss"))) {
                                UserProfile playerProfile = profileManager.getProfile(player.getName());
                                if (playerProfile.isBossIndicator()) {
                                    // Inside your code
                                    if (extraHealth + ((Creature) event.getEntity()).getHealth() > -1) {
                                        double healthRemaining = (extraHealth + ((Creature) event.getEntity()).getHealth()) - damage;

                                        String message = ChatColor.RED + event.getEntity().getCustomName() +
                                                ChatColor.RESET + ChatColor.GRAY + " health remaining: " +
                                                ChatColor.GREEN + (int) healthRemaining;

                                        player.sendMessage(message);
                                    }
                                }
                            }
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

//    @EventHandler
//    public void onEntityHitByCustomSkill(EntityDamageByEntityEvent event) {
//        if (event.getEntity() instanceof Monster || event.getEntity() instanceof IronGolem || event.getEntity() instanceof Wolf) {
//
//            // Get the entity and damage cause
//            LivingEntity entity = (LivingEntity) event.getEntity();
//
//            // Check if the damage cause is one of the specified types (poison, fire, drowning, freezing, etc.)
//            if (event.getDamager() instanceof Arrow arrow){
//            if (arrow.hasMetadata("FireArrowBarrage") || arrow.hasMetadata("FreezeArrowBarrage") || arrow.hasMetadata("WeaknessArrowBarrage"))
//            {
//                if (entity.hasMetadata("initialExtraHealth")) {
//                    // Retrieve extra health attribute from entity's metadata or custom attribute
//                    double extraHealth = entity.getMetadata("initialExtraHealth").getFirst().asDouble();
//
//                    if (extraHealth > 0) {
//                        double damage = event.getDamage();
//
//                        if (extraHealth - damage > 0) {
//                            entity.setMetadata("initialExtraHealth", new FixedMetadataValue(plugin, extraHealth - damage));
//                            event.setDamage(0);
//                        } else {
//                            // Calculate excess damage
//                            double excessDamage = damage - extraHealth;
//                            entity.setHealth(Math.max(0, entity.getHealth() - excessDamage)); // Ensure health doesn't drop below 0
//
//                            // Clear the extra health metadata as it has been depleted
//                            entity.setMetadata("initialExtraHealth", new FixedMetadataValue(plugin, 0.0)); // Reset extra health to 0
//
//                            event.setDamage(excessDamage);
//                        }
//                    }
//                }
//            }
//            }
//        }
//    }


    // pve melee damage
//    private void handleMeleePveDamage(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, Location damagerLocation, Location damagedLocation, UserProfile damagerProfile) {
//        ItemStack weapon = attacker.getInventory().getItemInMainHand();
//
//        // Fire element check: Disable fire if not selected
//        if (!damagerProfile.getSelectedElement().equalsIgnoreCase("fire")
//                && (weapon.containsEnchantment(Enchantment.FLAME) || weapon.containsEnchantment(Enchantment.FIRE_ASPECT))) {
//            event.getEntity().setFireTicks(0); // Cancel fire ticks
//        }
//
//
//
//        double baseDamage = event.getDamage();
//
//        // Apply stats based on class for non-default players
//        double damageWithStats = applyStatsToDamage(baseDamage, damagerProfile, attacker, event);
//
//        // Swordsman-specific ability if holding a sword
//        if (damagerProfile.getChosenClass().equalsIgnoreCase("swordsman") && weapon.getType().toString().endsWith("_SWORD")) {
//            effectsAbilityManager.applyAbility(damagerProfile, target, damagerLocation, damagedLocation);
//        }
//
//        // Apply extra health and set final damage
//        double finalDamage = applyExtraHealthAndDamage(target, damageWithStats, attacker);
//        event.setDamage(finalDamage);
//    }

    private void handleMeleePveDamage(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, Location damagerLocation, Location damagedLocation, UserProfile damagerProfile) {
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
            effectsAbilityManager.applyAbility(damagerProfile, target, damagerLocation, damagedLocation);
        }

        // Apply extra health and set final damage
        double finalDamage = applyExtraHealthAndDamage(target, damageWithStats, attacker);


        if (event.getEntity() instanceof Player victim) {
            UserProfile victimProfile = profileManager.getProfile(victim.getName());
            if (victimProfile==null) return;
            if (damagerProfile.getSelectedElement().equalsIgnoreCase("fire")) {finalDamage=finalDamage*1.1;}
            if (victimProfile.getChosenClass().equalsIgnoreCase("swordsman") && !victimProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
                finalDamage= finalDamage*.8;
            }
            if (victimProfile.getChosenClass().equalsIgnoreCase("swordsman") && victimProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
                finalDamage= finalDamage*.3;
            }
            if (victimProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                finalDamage=finalDamage*1.2;
            }
            if (damagerProfile.getSelectedElement().equalsIgnoreCase("fire") && victimProfile.getSelectedElement().equalsIgnoreCase("ice")) {
                finalDamage=finalDamage*1.1;
            }
            if (damagerProfile.getSelectedElement().equalsIgnoreCase("ice") && victimProfile.getSelectedElement().equalsIgnoreCase("water")) {
                finalDamage=finalDamage*1.2;
            }
            if (damagerProfile.getSelectedElement().equalsIgnoreCase("water") && victimProfile.getSelectedElement().equalsIgnoreCase("fire")) {
                finalDamage=finalDamage*1.2;
            }
            if (damagerProfile.getSelectedElement().equalsIgnoreCase("fire") && victimProfile.getSelectedElement().equalsIgnoreCase("water")) {
                finalDamage=finalDamage*.8;
            }
            if (damagerProfile.getSelectedElement().equalsIgnoreCase("water") && victimProfile.getSelectedElement().equalsIgnoreCase("ice")) {
                finalDamage=finalDamage*.8;
            }
            if (damagerProfile.getSelectedElement().equalsIgnoreCase("ice") && victimProfile.getSelectedElement().equalsIgnoreCase("fire")) {
                finalDamage=finalDamage*.8;
            }
            if (victimProfile.getSelectedElement().equalsIgnoreCase("none")) {
                finalDamage=finalDamage*1.3;
            }
        }

        // Attack cooldown mechanic (charge multiplier)
        float attackCooldown = attacker.getAttackCooldown();  // 0 = no cooldown, 1 = max cooldown

        // Apply cooldown scaling to the final damage
        finalDamage *= attackCooldown;

        event.setDamage(finalDamage);
    }


    // PvE Long Range Damage
    private void handleLongRangePveDamage(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, Location damagerLocation, Location damagedLocation, UserProfile damagerProfile) {



        ItemStack weapon = attacker.getInventory().getItemInMainHand();

        // Fire element check: Disable fire if not selected
        if (!damagerProfile.getSelectedElement().equalsIgnoreCase("fire")
                && (weapon.containsEnchantment(Enchantment.FLAME) || weapon.containsEnchantment(Enchantment.FIRE_ASPECT))) {
            event.getEntity().setFireTicks(0); // Cancel fire ticks
        }

        double baseDamage = event.getDamage();

        // Apply stats based on class for non-default players
        double damageWithStats = applyStatsToDamage(baseDamage, damagerProfile, attacker, event);



        // Apply extra health and set final damage
        double finalDamage = applyExtraHealthAndDamage(target, damageWithStats, attacker);

        if (event.getEntity() instanceof Player victim) {
            UserProfile victimProfile = profileManager.getProfile(victim.getName());
            if (victimProfile==null) return;

            if (victimProfile.getChosenClass().equalsIgnoreCase("swordsman") && !victimProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
                finalDamage= finalDamage*.8;
            }
            if (victimProfile.getChosenClass().equalsIgnoreCase("swordsman") && victimProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
                finalDamage= finalDamage*.3;
            }
            if (victimProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                finalDamage=finalDamage*1.2;
            }
            if (damagerProfile.getSelectedElement().equalsIgnoreCase("fire") && victimProfile.getSelectedElement().equalsIgnoreCase("ice")) {
                finalDamage=finalDamage*1.2;
            }
            if (damagerProfile.getSelectedElement().equalsIgnoreCase("ice") && victimProfile.getSelectedElement().equalsIgnoreCase("water")) {
                finalDamage=finalDamage*1.2;
            }
            if (damagerProfile.getSelectedElement().equalsIgnoreCase("water") && victimProfile.getSelectedElement().equalsIgnoreCase("fire")) {
                finalDamage=finalDamage*1.2;
            }
            if (damagerProfile.getSelectedElement().equalsIgnoreCase("fire") && victimProfile.getSelectedElement().equalsIgnoreCase("water")) {
                finalDamage=finalDamage*.8;
            }
            if (damagerProfile.getSelectedElement().equalsIgnoreCase("water") && victimProfile.getSelectedElement().equalsIgnoreCase("ice")) {
                finalDamage=finalDamage*.8;
            }
            if (damagerProfile.getSelectedElement().equalsIgnoreCase("ice") && victimProfile.getSelectedElement().equalsIgnoreCase("fire")) {
                finalDamage=finalDamage*.8;
            }
            if (victimProfile.getSelectedElement().equalsIgnoreCase("none")) {
                finalDamage=finalDamage*1.3;
            }
        }

        if (event.getDamager() instanceof  Arrow arrow) {
            Location shooterLoc = attacker.getLocation();
            double distance = shooterLoc.distance(damagedLocation);
            if (distance <= 1) {
                finalDamage *= 0.05;
            } else if (distance <= 2) {
                finalDamage *= 0.1;
            } else if (distance <= 3) {
                finalDamage *= 0.2;
            } else if (distance <= 4) {
                finalDamage *= 0.3;
            } else if (distance <= 5) {
                finalDamage *= 0.4;
            } else if (distance <= 6) {
                finalDamage *= 0.5;
            } else if (distance <= 7) {
                finalDamage *= 0.6;
            } else if (distance <= 8) {
                finalDamage *= 0.7;
            } else if (distance <= 9) {
                finalDamage *= 0.8;
            } else if (distance <= 10) {
                finalDamage *= 0.9;
            }
            else if (distance <= 20 && damagerProfile.getChosenClass().equalsIgnoreCase("archer")) {
                finalDamage = finalDamage * 1.5;
            }
            else if (distance <= 40 && damagerProfile.getChosenClass().equalsIgnoreCase("archer")) {
                finalDamage = finalDamage * 2;
            }
            else if (distance > 41 && damagerProfile.getChosenClass().equalsIgnoreCase("archer")) {
                finalDamage = finalDamage * 3;
            }


        }

        // Archer class - Check if using bow
        if (damagerProfile.getChosenClass().equalsIgnoreCase("archer") && event.getDamager() instanceof Arrow arrow) {
            effectsAbilityManager.applyAbility(damagerProfile, target, damagerLocation, damagedLocation);
            if (!arrow.hasMetadata("WeaknessArrowBarrage") && !arrow.hasMetadata("FireArrowBarrage") && !arrow.hasMetadata("FreezeArrowBarrage")) {
                double archerDex = damagerProfile.getArcherClassInfo().getDex();
                double baseChance = 0.10; // 10% base chance
                double dexModifier = 0.002; // 0.004 per Dexterity
                double totalChance = baseChance + (archerDex * dexModifier);

// Generate a random value between 0.0 and 1.0
                if (Math.random() < totalChance) {
                    damageAbilityManager.applyDamageAbility(damagerProfile, target, damagerLocation, damagedLocation, finalDamage);
                }
            }


        }

        // Alchemist class - Check if using thrown potion
        if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist") && event.getDamager() instanceof ThrownPotion) {

            effectsAbilityManager.applyAbility(damagerProfile, target, damagerLocation, damagedLocation);
        }



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
            str = damagerProfile.getAlchemistClassInfo().getStr();
            dex = damagerProfile.getAlchemistClassInfo().getDex();
            intel = damagerProfile.getAlchemistClassInfo().getIntel();
            luk = damagerProfile.getAlchemistClassInfo().getLuk();
        }
        else if (damagerProfile.getChosenClass().equalsIgnoreCase("swordsman")) {
            str = damagerProfile.getSwordsmanClassInfo().getStr();
            dex = damagerProfile.getSwordsmanClassInfo().getDex();
            intel = damagerProfile.getSwordsmanClassInfo().getIntel();
            luk = damagerProfile.getSwordsmanClassInfo().getLuk();
        }



        // Apply additional stats from item lore
        ItemStack[] equipment = {
                player.getInventory().getHelmet(),
                player.getInventory().getChestplate(),
                player.getInventory().getLeggings(),
                player.getInventory().getBoots(),
                player.getInventory().getItemInMainHand(),
                player.getInventory().getItemInOffHand()
        };



        for (ItemStack item : equipment) {
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasLore()) {
                    for (String lore : Objects.requireNonNull(meta.getLore())) {
                        if (lore.startsWith("Strength: ")) {

                            if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                                str += (Math.round(1.1*parseLoreValue(lore)));
                            }
                            else {
                                str += parseLoreValue(lore);
                            }


                        } else if (lore.startsWith("Dexterity: ")) {
                            if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                                dex += (Math.round(1.1*parseLoreValue(lore)));
                            }
                            else {
                                dex += parseLoreValue(lore);
                            }
                        } else if (lore.startsWith("Intelligence: ")) {
                            if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                                intel += (Math.round(1.1*parseLoreValue(lore)));
                            }
                            else {
                                intel += parseLoreValue(lore);
                            }
                        } else if (lore.startsWith("Luck: ")) {
                            if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                                luk += (Math.round(1.1*parseLoreValue(lore)));
                            }
                            else {
                                luk += parseLoreValue(lore);
                            }
                        }
                    }
                }
            }
        }


        // Damage calculation based on class stats
        double statDmg = 0;
        double elementalDamage = 0;

        // melee
        if (event.getDamager() instanceof Player) {
            if (damagerProfile.getChosenClass().equalsIgnoreCase("swordsman")) {


                if (damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 2") && player.getInventory().getItemInMainHand().getType().toString().endsWith("_SWORD")) {
                    statDmg += str*.2;
                } else {
                    statDmg += str*.1;
                }


                if (damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 1") && player.getInventory().getItemInMainHand().getType().toString().endsWith("_SWORD")) {
                    elementalDamage += (4 + (intel * 0.2));
                } else {
                    elementalDamage += (2 + (intel * 0.1));
                }
            } else if (damagerProfile.getChosenClass().equalsIgnoreCase("archer")) {
                statDmg +=(str*.05) ;
                elementalDamage+=(2+ (intel * 0.1));
            }
            else if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                statDmg += (str*.05);
                elementalDamage+=(4+ (intel * 0.1));
            }
        }


        // bow
        if (event.getDamager() instanceof Arrow) {

            //archers
            if (damagerProfile.getChosenClass().equalsIgnoreCase("archer")) {
                statDmg += (dex*.2);

                if (damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
                    statDmg += (dex*.2);
                }

                if (damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 2")) {
                    statDmg += (dex*.1);
                    elementalDamage += (intel*.1);
                }

                if (damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 1")) {
                    elementalDamage += 4;
                    elementalDamage += (intel*.2);
                } else {
                    elementalDamage += 2 + (intel*.1);
                }
            }else if (damagerProfile.getChosenClass().equalsIgnoreCase("swordsman")) {
                statDmg +=(dex*.05) ;
                elementalDamage+=(2+ (intel * 0.1));
            }
            else if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                statDmg += (dex*.05);
                elementalDamage+=(4+ (intel * 0.1));
            }


        }

        // thrown potions
        if (event.getDamager() instanceof ThrownPotion) {

            //alchemists
            if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                if (damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 1")) {
                    elementalDamage += 6;
                    elementalDamage += (intel*.4);

                } else {
                    elementalDamage += 4 + (intel*.2);
                }
            }else if (damagerProfile.getChosenClass().equalsIgnoreCase("swordsman")) {
                elementalDamage+=(2+ (intel * 0.05));
            }
            else if (damagerProfile.getChosenClass().equalsIgnoreCase("archer")) {
                elementalDamage+=(2+ (intel * 0.05));
            }


        }

        double calculatedDamage = baseDamage + statDmg + elementalDamage;

        // Critical Hit System
        double critChance = 0;
        if (damagerProfile.getChosenClass().equalsIgnoreCase("swordsman")){
            critChance += (luk * 0.00075);
        }
        else {
            critChance += (luk * 0.0005);
        }
        if (damagerProfile.getChosenClass().equalsIgnoreCase("archer")){
            critChance += (dex * 0.00025);
        }
        double critDmgMultiplier = 1.5 + (dex * 0.0001);
        if (damagerProfile.getChosenClass().equalsIgnoreCase("archer") && damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")){
            critChance += 0.25;
            critDmgMultiplier += dex*0.00005;
        }

        boolean isCrit = Math.random() < critChance;

        if (isCrit) {
            calculatedDamage *= critDmgMultiplier;
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.0f);
            event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.0f);
            World world = event.getEntity().getWorld();
            for (int i = 0; i < 50; i++) {
                double xOffset = (Math.random() - 0.5) * 1.5;
                double zOffset = (Math.random() - 0.5) * 1.5;
                double yVelocity = Math.random() * 0.5 + 0.5;

                // Spawn different water-like particles for the splash effect
                world.spawnParticle(
                        Particle.CRIT,
                        event.getEntity().getLocation().clone().add(xOffset, 0, zOffset),
                        1, // Particle count (1 at a time)
                        0, yVelocity, 0, // Upward velocity to make them rise
                        0.05 // Speed of the particle
                );
            }
        }



        if (PlayerBuffPerms.canLifeSteal(damagerProfile)) {
            double lifestealAmount = statDmg * 0.1; // 10% lifesteal
            double maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue(); // Get max health using attribute
            double newHealth = Math.min(player.getHealth() + lifestealAmount, maxHealth); // Avoid exceeding max health
            player.setHealth(newHealth);
        }

        if (damagerProfile.getDurability() < 1 && !damagerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
            calculatedDamage /= 2;
        } else {
            damagerProfile.setDurability(damagerProfile.getDurability() - 1);
        }

        if (damagerProfile.getStamina() < 1) {
            calculatedDamage /= 2;
        } else {
            damagerProfile.setStamina(damagerProfile.getStamina() - 1);
        }

        return calculatedDamage;
    }

    private int parseLoreValue(String lore) {
        try {
            return Integer.parseInt(lore.split(": ")[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // Handle parsing errors or missing values
            return 0;
        }
    }

    // Method to apply extra health and return adjusted damage
    private double applyExtraHealthAndDamage(LivingEntity target, double calculatedDamage, Player player) {
        if (target instanceof Monster || target instanceof IronGolem || target instanceof Wolf) {
//            initializeExtraAttributes(target, player);
        }
//
//        // Retrieve and apply existing extra health
//        double initialExtraHealth = 0;
//
//        if (target.hasMetadata("initialExtraHealth")) {
//            initialExtraHealth += target.getMetadata("initialExtraHealth").get(0).asDouble();
//        }
//
//        return applyDamageWithExtraHealth(target, calculatedDamage, initialExtraHealth, player);
        return  calculatedDamage;
    }




    // Method to initialize extra health and extra damage metadata
    private void initializeExtraAttributes(LivingEntity entity, Player player) {
        if (!entity.hasMetadata("extraHealthApplied")) {
            // Calculate extra health and damage
            Location location = entity.getLocation();
            double extraHealth = calculateExtraAttributes(location,entity);
            double extraDamage = extraHealth * 0.1; // Convert to damage (10% of extra health)

            // Store in metadata
            entity.setMetadata("initialExtraHealth", new FixedMetadataValue(plugin, extraHealth));
            entity.setMetadata("extraDamage", new FixedMetadataValue(plugin, extraDamage));
            entity.setMetadata("extraHealthApplied", new FixedMetadataValue(plugin, true)); // Mark as applied

              }
    }

    // Method to calculate extra attributes (used for both extra health and damage)
    private double calculateExtraAttributes(Location targetLocation, LivingEntity entity) {
        double maxCoord = Math.max(Math.abs(targetLocation.getBlockX()), Math.abs(targetLocation.getBlockZ()));
        double extraHealth = (maxCoord / 100) * 10; // 100% health for every 100 blocks

        // 1% chance to add +1000% extra health
        if (Math.random() < .001) {
            extraHealth += (extraHealth * 10); // Add 1000% health

            // Set the entity name to display as a boss with health bar
            String bossName = "Lvl " + (int) (Math.floor(maxCoord) /100) + " Boss " + entity.getType().name();

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

        // .5% chance to add +10000% extra health
        if (Math.random() < .0005) {
            extraHealth += (extraHealth * 100); // Add 1000% health

            // Set the entity name to display as a boss with health bar
            String bossName = "Lvl " + (int) (Math.floor(maxCoord) /100) + " World Boss " + entity.getType().name();

            // Apply the custom name to the entity
            entity.setCustomName(bossName);
            entity.setPersistent(true);
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
