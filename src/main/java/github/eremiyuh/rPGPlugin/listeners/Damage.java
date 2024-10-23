package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.methods.AbilityManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffectType;

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

        if (damager instanceof Player attacker) {
            if (damaged instanceof Player victim) {
                UserProfile attackerProfile = profileManager.getProfile(attacker.getName());
                UserProfile victimProfile = profileManager.getProfile(victim.getName());

                if (attackerProfile != null && victimProfile != null) {
                    boolean attackerPvp = attackerProfile.isPvpEnabled();
                    boolean victimPvp = victimProfile.isPvpEnabled();

                    if (!attackerPvp || !victimPvp){
                        // Cancel the damage event to prevent PvP
                        event.setCancelled(true);

                        // Optionally send messages to both players
                        attacker.sendMessage("PvP is disabled for you or your target.");
                    }

                    String attackerTeam = attackerProfile.getTeam();
                    String victimTeam = victimProfile.getTeam();

                    // If they are on the same team, cancel the damage
                    if (!Objects.equals(attackerTeam, "none") && !Objects.equals(victimTeam, "none") && attackerTeam.equals(victimTeam)) {
                        attacker.sendMessage("You cannot damage players on your own team!");
                        event.setCancelled(true);
                        return; // Exit early as we don’t need to process further
                    }

                    // PvP damage calculation
                    handlePvPDamage(attacker, victim, event, damagerLocation, damagedLocation);
                }




            }
            if (damaged instanceof LivingEntity target && !(damaged instanceof Player)) {
                // PvE damage calculation
                handlePvEDamageToEnvironment(attacker, target, event, damagerLocation, damagedLocation);
            }
        }

        if (damager instanceof LivingEntity angryMob && !(damager instanceof Player)) {
            if (damaged instanceof Player victim) {
                // PvE damage where mob damages player
                handlePvEDamageFromEnvironment(angryMob, victim, event, damagerLocation, damagedLocation);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        Entity hitEntity = event.getHitEntity();
        // pvp event
        if (projectile.getShooter() instanceof Player damager) {
            if (hitEntity instanceof Player victim) {
                UserProfile damagerProfile = profileManager.getProfile(damager.getName());
                UserProfile victimProfile = profileManager.getProfile(victim.getName());

                if (damagerProfile != null && victimProfile != null) {
                    Boolean attackerPvp = damagerProfile.isPvpEnabled();
                    Boolean victimPvp = victimProfile.isPvpEnabled();

                    if (!damagerProfile.isPvpEnabled() || !victimProfile.isPvpEnabled()){
                        // Cancel the damage event to prevent PvP
                        event.setCancelled(true);

                        // Optionally send messages to both players
                        damager.sendMessage("PvP is disabled for you or your target.");
                        victim.sendMessage("PvP is disabled, you cannot be attacked.");
                    }

                    String damagerTeam = damagerProfile.getTeam();
                    String victimTeam = victimProfile.getTeam();

                    // If they are on the same team, cancel the damage
                    if (damagerTeam != null && damagerTeam.equals(victimTeam)) {
                        damager.sendMessage("You cannot damage players on your own team!");
                        event.setCancelled(true);
                        return; // Exit early as we don’t need to process further
                    }

                    Location damagerLocation = damager.getLocation();
                    Location victimLocation = victim.getLocation();

                    if (projectile instanceof Arrow) {
                        damager.sendMessage("Bullseye");
                    }

                    if (projectile instanceof  ThrownPotion) {
                        damager.sendMessage("Bullseye");
                    }



                }

            }
        }

        //pve event
        if (projectile.getShooter() instanceof Player damager) {
            if (hitEntity instanceof LivingEntity mobVictim && !(hitEntity instanceof Player)) {
                UserProfile damagerProfile = profileManager.getProfile(damager.getName());
                Location damagerLoc = damager.getLocation();
                Location mobVictimLoc = mobVictim.getLocation();
                damager.sendMessage(event.getEntity().getName() + " hit");
                handleLongRangePveDamage(damager,mobVictim,event,damagerLoc, mobVictimLoc,damagerProfile);
            }
        }
    }

    @EventHandler
    public void onSplashPotionHit(PotionSplashEvent event) {
        ThrownPotion thrownPotion = event.getPotion();

        boolean isInstantDamage =  thrownPotion.getEffects().stream().anyMatch(effect -> effect.getType().equals(PotionEffectType.INSTANT_DAMAGE));

        if (isInstantDamage &&  thrownPotion.getShooter() instanceof Player thrower) {
            UserProfile throwerProfile =  profileManager.getProfile(thrower.getName());
            for (LivingEntity entity : event.getAffectedEntities()) {
                if (!(entity instanceof Player ) && throwerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                    thrower.sendMessage("you threw potion on a mob");
                    abilityManager.applyAbility(throwerProfile,entity,thrower.getLocation(),entity.getLocation());
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


    // PvP melee Damage
    private void handlePvPMeleeDamage(Player attacker, Player victim, EntityDamageByEntityEvent event, Location damagerLocation, Location damagedLocation, UserProfile attackerProfile, UserProfile victimProfile) {
        double baseDamage = event.getDamage();
        UserProfile playerProfile = profileManager.getProfile(attacker.getName());

        // Get the item in the attacker's main hand
        Material mainHandItem = attacker.getInventory().getItemInMainHand().getType();

        // Using Sword
        if (attackerProfile.getChosenClass().equalsIgnoreCase("swordsman") && mainHandItem.toString().endsWith("_SWORD")) {  // Check if the item is any type of sword
            // Your logic for sword usage here
            attacker.sendMessage("You are using a sword!");
        }

        double modifiedDamage = calculatePvPDamage(attacker, victim, baseDamage);
        event.setDamage(modifiedDamage);
    }



    private void handlePvPDamage(Player attacker, Player victim, EntityDamageByEntityEvent event, Location damagerLocation, Location damagedLocation) {
        double baseDamage = event.getDamage();
        UserProfile playerProfile = profileManager.getProfile(attacker.getName());

        // Get the item in the attacker's main hand
        Material mainHandItem = attacker.getInventory().getItemInMainHand().getType();

        // Using Sword
        if (mainHandItem.toString().endsWith("_SWORD")) {  // Check if the item is any type of sword
            // Your logic for sword usage here
            attacker.sendMessage("You are using a sword!");
        }

        // Using Bow
        if (mainHandItem == Material.BOW) {  // Check if the item is a bow
            // Your logic for bow usage here
            attacker.sendMessage("You are using a bow!");
        }

        // Using Splash Potion
        if (mainHandItem == Material.SPLASH_POTION) {  // Check if the item is a splash potion
            // Your logic for splash potion usage here
            attacker.sendMessage("You are using a splash potion!");
        }

        double modifiedDamage = calculatePvPDamage(attacker, victim, baseDamage);
        event.setDamage(modifiedDamage);
    }

    // PvP ranged Damage
    private void handlePvpProjectileDamage(Player attacker, Player victim, EntityDamageByEntityEvent event, Location damagerLocation, Location damagedLocation) {

    }


    // PvE Damage (Mob vs Player)
    private void handlePvEDamageFromEnvironment(Entity damager, Player victim, EntityDamageByEntityEvent event, Location damagerLocation, Location damagedLocation) {
        double baseDamage = event.getDamage();
        double modifiedDamage = calculatePvEDamageFromEnvironment(damager, victim, baseDamage);
        UserProfile playerProfile = profileManager.getProfile(victim.getName());
        if (playerProfile.canSummonFlowingWater()) {
            summonFlowingWaterCross(damagedLocation);
        }
        event.setDamage(modifiedDamage);
    }

    // PvE melee Damage (Player vs Mob)
    private void handlePvEDamageToEnvironment(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, Location damagerLocation, Location damagedLocation) {
        double baseDamage = event.getDamage();
        double modifiedDamage = calculatePvEDamageToEnvironment(attacker, target, baseDamage);
        UserProfile playerProfile = profileManager.getProfile(attacker.getName());

        // Get the item in the attacker's main hand
        Material mainHandItem = attacker.getInventory().getItemInMainHand().getType();

        // Using Sword
        if (mainHandItem.toString().endsWith("_SWORD")) {  // Check if the item is any type of sword
            // Your logic for sword usage here
            attacker.sendMessage("You are using a sword!");
        }

        attacker.sendMessage((event.getDamager() + " he is"));
        if (event.getDamager() instanceof Projectile projectile) {

            // Check if the projectile was shot by a player
            if (projectile.getShooter() instanceof Player shooter) {
                shooter.sendMessage("You hit someone!");
                // Using Bow
                if (projectile instanceof Arrow) {
                    // Handle arrow damage
                    shooter.sendMessage("You hit someone with an arrow!");
                }

                // Using Potion
                else if (projectile instanceof ThrownPotion) {
                    // Handle splash potion damage
                    shooter.sendMessage("You hit someone with a splash potion!");
                }
            }
        }

    }

    // PvE Long Range Damage
    private void handleLongRangePveDamage(Player attacker, LivingEntity target, ProjectileHitEvent event, Location damagerLocation, Location damagedLocation
        , UserProfile damagerProfile
    ) {

        // archers
        if (damagerProfile.getChosenClass().equalsIgnoreCase("archer") && event.getEntity() instanceof Arrow) {
            attacker.sendMessage("Archer class bonus applied");
        }

        if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist") && event.getEntity() instanceof ThrownPotion) {
            attacker.sendMessage("Alchemist class bonus applied");
        }

    }

    // ELEMENTS SUMMON

    // Example method to summon lava at a location
    private void summonLava(Location location) {
        // Get the block at the location
        Block block = location.getBlock();

        // Check if the block is air, water, or lava before replacing it
        if (block.getType() == Material.AIR || block.getType() == Material.WATER) {
            block.setType(Material.LAVA); // Replaces the block with lava
        }
    }


    // Example method to summon water at a location
    private void summonWater(Location location) {
        location.getBlock().setType(Material.WATER); // Replaces the block with water
    }

    private void summonFlowingWater(Location location) {
        if (location.getBlock().getType() == Material.AIR) { // Check if the block is air
            location.getBlock().setType(Material.WATER); // Set the block to water
            // Set the block data to flowing water
            location.getBlock().setBlockData(Bukkit.createBlockData("minecraft:water[level=7]"));
        }
    }

    // Helper method to set a block as flowing water
    private void setFlowingWater(Location location) {
        if (location.getBlock().getType() == Material.AIR) { // Check if the block is air
            location.getBlock().setType(Material.WATER); // Set the block to water
            // Set the block data to flowing water with level 7 (most flowing)
            location.getBlock().setBlockData(Bukkit.createBlockData("minecraft:water[level=7]"));
        }
    }

    // Summon flowing water in a cross shape around the given location
    private void summonFlowingWaterCross(Location location) {
        // Define the central block as flowing water
        setFlowingWater(location);

        // Set flowing water in a cross pattern (north, south, east, west)
        setFlowingWater(location.clone().add(1, 0, 0)); // Block to the east
        setFlowingWater(location.clone().add(-1, 0, 0)); // Block to the west
        setFlowingWater(location.clone().add(0, 0, 1)); // Block to the south
        setFlowingWater(location.clone().add(0, 0, -1)); // Block to the north
    }

    // PvP damage calculation logic (adjust as necessary)
    private double calculatePvPDamage(Player attacker, Player victim, double baseDamage) {
        double damageModifier = 1.0;
        return baseDamage * damageModifier;
    }

    // PvE damage calculation logic (environment to player)
    private double calculatePvEDamageFromEnvironment(Entity damager, Player victim, double baseDamage) {
        double damageModifier = 1.0;
        return baseDamage * damageModifier;
    }

    // PvE damage calculation logic (player to environment/mob)
    private double calculatePvEDamageToEnvironment(Player attacker, LivingEntity target, double baseDamage) {
        double damageModifier = 1.0;
        return baseDamage * damageModifier;
    }
}
