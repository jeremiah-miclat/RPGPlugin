package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
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

public class Damage implements Listener {

    private  final PlayerProfileManager profileManager;

    public Damage(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
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
                    String attackerTeam = attackerProfile.getTeam();
                    String victimTeam = victimProfile.getTeam();

                    // If they are on the same team, cancel the damage
                    if (attackerTeam != null && attackerTeam.equals(victimTeam)) {
                        attacker.sendMessage("You cannot damage players on your own team!");
                        event.setCancelled(true);
                        return; // Exit early as we don’t need to process further
                    }
                }



                // PvP damage calculation
                handlePvPDamage(attacker, victim, event, damagerLocation, damagedLocation);
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

    // PvP Damage
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

    // PvE Damage (Player vs Mob)
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

    // method to fire at a location
    private void summonFire(Location location) {
        Block block = location.getBlock();
        if (block.getType() == Material.AIR || block.getType() == Material.WATER) {
            block.setType(Material.FIRE);
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
