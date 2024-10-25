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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
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
        MonsterStrengthScalingListener monsterListener = plugin.getMonsterStrengthScalingListener();
        Map<LivingEntity, Double> extraHealthMap = monsterListener.getExtraHealthMap();
        ItemStack weapon = attacker.getInventory().getItemInMainHand();

        // Fire element check: Disable fire if not selected
        if (!damagerProfile.getSelectedElement().equalsIgnoreCase("fire")
                && (weapon.containsEnchantment(Enchantment.FLAME) || weapon.containsEnchantment(Enchantment.FIRE_ASPECT))) {
            event.getEntity().setFireTicks(0); // Cancel fire ticks
        }

        if (extraHealthMap.containsKey(target)) {
            attacker.sendMessage(target.getHealth()+" left");
        }


        if (damagerProfile.getChosenClass().equalsIgnoreCase("swordsman")) {


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
            double critChance = ((luk/10) * 0.1) + .05;
            double critDmgMultiplier = 1.1 + (dex * 0.01);
            boolean isCrit = Math.random() < critChance;

            if (isCrit) {
                newDmg = newDmg * critDmgMultiplier;
                attacker.sendMessage("Critical hit! Damage multiplied by " + critDmgMultiplier + "Damage dealt " + newDmg*critDmgMultiplier);
                target.getWorld().playSound(damagerLocation, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);
                target.getWorld().playSound(damagedLocation, Sound.ENTITY_PLAYER_ATTACK_CRIT, 10, 1);

            }

            if (extraHealthMap.containsKey(target)) {
                double currentHealth = target.getHealth();
                double extraHealth = extraHealthMap.get(target);
                double mobDmg = Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).getValue();
                if (weapon.getType().toString().endsWith("_SWORD")) {

                    abilityManager.applyAbility(damagerProfile,target,damagerLocation,damagedLocation);
                } else {
                    newDmg=damage;
                }

                // Check if entity has extra health
                if (extraHealth - newDmg > 0) {
                    extraHealthMap.put(target, extraHealth - newDmg);
                        event.setDamage(0); // Cancel the damage event
                    attacker.sendMessage("mob dmg:" + mobDmg);
                    attacker.sendMessage("Current Health: " + currentHealth + ", Extra Health: " + extraHealth);
                        return;

                }
                else {

                    extraHealthMap.put(target, extraHealth - newDmg);
                    extraHealthMap.remove(target);
                    event.setDamage(newDmg);
                    return;
                }

            }



            event.setDamage(newDmg);


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
            double critChance = ((luk/10) * 0.1) + .001;
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

            }
        }

        // Alchemist class
        if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist") && event.getDamager() instanceof ThrownPotion) {
            attacker.sendMessage("Alchemist class bonus applied");

            abilityManager.applyAbility(damagerProfile, target, damagerLocation, damagedLocation);
            double damage = event.getDamage();

            double agi = damagerProfile.getArcherClassInfo().getAgi();
            double str = damagerProfile.getArcherClassInfo().getStr();
            double dex = damagerProfile.getArcherClassInfo().getDex();
            double intel = damagerProfile.getArcherClassInfo().getIntel();
            double luk = damagerProfile.getArcherClassInfo().getLuk();
            double vit = damagerProfile.getArcherClassInfo().getVit();

            // damage from str
            double strDmg = str * .003;

            // Elemental Damage
            double elementalDamage = 4.0 + (intel * .02) ;

            // new dmg
            double newDmg = damage + strDmg + elementalDamage + (intel * .01);

            // Critical Hit System
            double critChance = ((luk/10) * 0.1) + .05;
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

            }
        }
    }

}
