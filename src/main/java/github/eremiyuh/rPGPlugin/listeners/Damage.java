package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.methods.AbilityManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Damage implements Listener {

    private  final PlayerProfileManager profileManager;
    private final AbilityManager abilityManager;

    public Damage(PlayerProfileManager profileManager, AbilityManager abilityManager) {
        this.profileManager = profileManager;
        this.abilityManager = abilityManager;
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
        if (damager instanceof Projectile && damaged instanceof LivingEntity victim && !(damaged instanceof Player)) {
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


        if (damager instanceof LivingEntity angryMob && !(damager instanceof Player)) {

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
    private void handleMeleePveDamage(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, Location damagerLocation, Location damagedLocation
            , UserProfile damagerProfile) {

        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        if (!damagerProfile.getSelectedElement().equalsIgnoreCase("fire") && (weapon.containsEnchantment(Enchantment.FLAME) ||weapon.containsEnchantment(Enchantment.FIRE_ASPECT) ))
        {
            event.getEntity().setFireTicks(0);
        }

        if (damagerProfile.getChosenClass().equalsIgnoreCase("swordsman")) {
            abilityManager.applyAbility(damagerProfile,target,damagerLocation,damagedLocation);
            double damage =  event.getDamage();
            event.setDamage(damage);
        }
    }

    // PvE Long Range Damage
    private void handleLongRangePveDamage(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, Location damagerLocation, Location damagedLocation
            , UserProfile damagerProfile
    ) {

        ItemStack weapon = attacker.getInventory().getItemInMainHand();

        // Fire element check: Disable fire if not selected
        if (!damagerProfile.getSelectedElement().equalsIgnoreCase("fire")
                && (weapon.containsEnchantment(Enchantment.FLAME) || weapon.containsEnchantment(Enchantment.FIRE_ASPECT))) {
            event.getEntity().setFireTicks(0); // Cancel fire ticks
        }

        // Archer class
        if (damagerProfile.getChosenClass().equalsIgnoreCase("archer") && event.getDamager() instanceof Arrow) {
            attacker.sendMessage("Archer class bonus applied");

            abilityManager.applyAbility(damagerProfile, target, damagerLocation, damagedLocation);
            double damage = event.getDamage();

            double agi = damagerProfile.getArcherClassInfo().getAgi();
            double str = damagerProfile.getArcherClassInfo().getStr();
            double dex = damagerProfile.getArcherClassInfo().getDex();
            double intel = damagerProfile.getArcherClassInfo().getIntel();
            double luk = damagerProfile.getArcherClassInfo().getLuk();
            double vit = damagerProfile.getArcherClassInfo().getVit();

            // damage from str
            double strDmg = str * .01;

            // Elemental Damage
            double elementalDamage = 2.0 + (intel * .01) ;

            // new dmg
            double newDmg = damage + strDmg + elementalDamage;

            // Critical Hit System
            double critChance = ((luk/100) * 0.1) + .5;
            double critDmgMultiplier = 1.1 + (dex * 0.01);
            boolean isCrit = Math.random() < critChance;
            if (isCrit) {
                event.setDamage(newDmg * critDmgMultiplier);
                attacker.sendMessage("Critical hit! Damage multiplied by " + critDmgMultiplier + "Damage dealt " + newDmg*critDmgMultiplier);
                target.getWorld().playSound(damagerLocation, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                target.getWorld().playSound(damagedLocation, Sound.ENTITY_PLAYER_ATTACK_CRIT, 10, 1);
            } else {
                // Apply the final damage to the event
                event.setDamage(newDmg);

                attacker.sendMessage("Not critical. Dealt damage is: " + newDmg);
            }
        }

        // Alchemist class
        if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist") && event.getDamager() instanceof ThrownPotion) {
            attacker.sendMessage("Alchemist class bonus applied");
            double damage = event.getDamage();
            abilityManager.applyAbility(damagerProfile, target, damagerLocation, damagedLocation);
            event.setDamage(damage);
        }
    }

    // splash potion event damage to mobs handler
    private void handlePlayerPotionDamageToMobs(Player attacker, LivingEntity victim, PotionSplashEvent event, Location damagerLocation, Location damagedLocation, UserProfile attackerProfile) {
        abilityManager.applyAbility(attackerProfile,victim,damagerLocation,damagedLocation);
        double intensity = event.getIntensity(victim);
        attacker.sendMessage("you dealt " + intensity + " damage");

    }

}
