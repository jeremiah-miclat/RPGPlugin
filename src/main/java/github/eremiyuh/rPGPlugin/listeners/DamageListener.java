package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.methods.DamageAbilityManager;
import github.eremiyuh.rPGPlugin.methods.EffectsAbilityManager;
import github.eremiyuh.rPGPlugin.perms.PlayerBuffPerms;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DamageListener implements Listener {

    private  final PlayerProfileManager profileManager;
    private final EffectsAbilityManager effectsAbilityManager;
    private  final DamageAbilityManager damageAbilityManager;
    private final RPGPlugin plugin;


    private final int x1 = -150, z1 = 150;
    private final int x2 = 90, z2 = -110;

    public DamageListener(PlayerProfileManager profileManager, EffectsAbilityManager effectsAbilityManager, DamageAbilityManager damageAbilityManager, RPGPlugin plugin) {
        this.profileManager = profileManager;
        this.effectsAbilityManager = effectsAbilityManager;
        this. damageAbilityManager = damageAbilityManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onEquipDamage(PlayerItemDamageEvent event) {
        if (!event.getPlayer().getWorld().getName().contains("rpg") && !event.getPlayer().getWorld().getName().contains("labyrinth")) {
            return;
        }

        if (event.getItem().getType() == Material.SHIELD) {
            UserProfile profile = profileManager.getProfile(event.getPlayer().getName());
            profile.setDurability(Math.max(0,profile.getDurability()-1));
        }

        event.setCancelled(true);
    }

    private boolean isInNoSpawnArea(int x, int z) {
        return x >= Math.min(x1, x2) && x <= Math.max(x1, x2) &&
                z >= Math.min(z1, z2) && z <= Math.max(z1, z2);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {

        if (!Objects.requireNonNull(event.getDamager().getLocation().getWorld()).getName().equals("world_rpg") && !Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName().contains("world_labyrinth")) {
            // Get the entity that was damaged
            Entity damaged = event.getEntity();
            // Get the damager (the entity dealing the damage)
            Entity damager = event.getDamager();
            if (event.getDamager() instanceof Player player && player.getAllowFlight()) {
                player.sendMessage("damaged cancelled. Disable fly mode");
                event.setCancelled(true);
                return;
            }


            if (damaged instanceof Tameable tameable) {
                AnimalTamer owner = tameable.getOwner();

                // Check if the tameable has an owner
                if (owner != null && damager instanceof Player player) {
                    UserProfile ownerProfile = profileManager.getProfile(player.getName());
                    UserProfile profile = profileManager.getProfile(player.getName());
                    if ((profile.getTeam().equalsIgnoreCase(ownerProfile.getTeam())) && !profile.getTeam().equalsIgnoreCase("none")) return;
                    if (!(profile.isPvpEnabled() && ownerProfile.isPvpEnabled())) return;
                    event.setCancelled(true);
                }
            }



            // Get the location of the damaged entity
            Location damagedLocation = damaged.getLocation();
            // Get the location of the damager entity (if damager exists)
            Location damagerLocation = damager.getLocation();
            if (damager instanceof Player attacker && damaged instanceof Player damagedPlayer) {

                UserProfile attackerProfile = profileManager.getProfile(attacker.getName());
                UserProfile damagedProfile = profileManager.getProfile(damaged.getName());

                if (attackerProfile == null || damagedProfile == null) {
                    attacker.sendMessage("Your profile or target's profile can not be found. contact developer to fix files");
                    damaged.sendMessage("Your profile or dude's profile can not be found. contact developer to fix files");
                    event.setCancelled(true);
                    return;
                }

                assert attackerProfile != null;
                if (!attackerProfile.isPvpEnabled()) {
                    event.setCancelled(true);
                    return;
                }

                assert damagedProfile != null;
                if (!damagedProfile.isPvpEnabled()) {
                    event.setCancelled(true);
                    return;
                }

                if (!Objects.equals(damagedProfile.getTeam(), "none") && Objects.equals(damagedProfile.getTeam(), attackerProfile.getTeam())) {
                    event.setCancelled(true);
                    return;
                }

            }

            if (damager instanceof Projectile projectile && damaged instanceof Player damagedPlayer && projectile.getShooter() instanceof Player shooter) {
                //bow
                UserProfile shooterProfile = profileManager.getProfile(shooter.getName());
                UserProfile damagedProfile = profileManager.getProfile(damaged.getName());

                if (shooterProfile == null || damagedProfile == null) {
                    shooter.sendMessage("Your profile or target's profile can not be found. contact developer to fix files");
                    damaged.sendMessage("Your profile or dude's profile can not be found. contact developer to fix files");
                    event.setCancelled(true);
                    return;
                }

                assert shooterProfile != null;
                if (!shooterProfile.isPvpEnabled()) {
                    event.setCancelled(true);
                    return;
                }

                assert damagedProfile != null;
                if (!damagedProfile.isPvpEnabled()) {
                    event.setCancelled(true);
                    return;
                }
                if (!Objects.equals(damagedProfile.getTeam(), "none") && Objects.equals(damagedProfile.getTeam(), shooterProfile.getTeam())) {
                    event.setCancelled(true);
                    return;
                }

            }

            if (damager instanceof Projectile projectile && damaged instanceof Player damagedPlayer
                    && projectile instanceof ThrownPotion thrownPotion && thrownPotion.getShooter() instanceof Player thrower) {

                UserProfile throwerProfile = profileManager.getProfile(thrower.getName());
                UserProfile damagedProfile = profileManager.getProfile(damaged.getName());

                if (throwerProfile == null || damagedProfile == null) {
                    thrower.sendMessage("Your profile or target's profile can not be found. contact developer to fix files");
                    damaged.sendMessage("Your profile or dude's profile can not be found. contact developer to fix files");
                    event.setCancelled(true);
                    return;
                }

                assert throwerProfile != null;
                if (!throwerProfile.isPvpEnabled()) {
                    event.setCancelled(true);
                    return;
                }

                assert damagedProfile != null;
                if (!damagedProfile.isPvpEnabled()) {
                    event.setCancelled(true);
                    return;
                }

                if (!Objects.equals(damagedProfile.getTeam(), "none") && Objects.equals(damagedProfile.getTeam(), throwerProfile.getTeam())) {
                    event.setCancelled(true);
                    return;
                }


            }

        }


        if (Objects.requireNonNull(event.getDamager().getLocation().getWorld()).getName().equals("world_rpg") || Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName().contains("world_labyrinth")) {
            Location loc = event.getEntity().getLocation();

            // Check if the world name contains "Labyrinth" and coordinates match
            if (loc.getWorld().getName().contains("labyrinth")) {
                int x = loc.getBlockX();
                int z = loc.getBlockZ();


                // Check if the coordinates are within the specified range
                if ((x >= -23 && x <= -6 && z >= -38 && z <= -34) && event.getEntity() instanceof Monster) {
                    // Cancel the damage
                    event.setCancelled(true);
                    return;
                }

                // Check if the coordinates are within the specified range
                if ((x >= -23 && x <= -17 && z >= -38 && z <= -34) && event.getEntity() instanceof Player) {
                    // Cancel the damage
                    event.setCancelled(true);
                    return;
                }
            }


            if (!(event.getEntity() instanceof LivingEntity) && !(event.getEntity() instanceof  Monster)) {
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





            if (damager instanceof Player attacker && damaged instanceof LivingEntity victim)
            {




                if (damaged instanceof Player damagedPlayer) {

                    UserProfile attackerProfile = profileManager.getProfile(attacker.getName());
                    UserProfile damagedProfile = profileManager.getProfile(damagedPlayer.getName());


                    if (attackerProfile == null || damagedProfile == null) {
                        attacker.sendMessage("Your profile or target's profile can not be found. contact developer to fix files");
                        damaged.sendMessage("Your profile or dude's profile can not be found. contact developer to fix files");
                        event.setCancelled(true);
                        return;
                    }

                    assert attackerProfile != null;
                    if (!attackerProfile.isPvpEnabled()) {
                        event.setCancelled(true);
                        return;
                    }

                    assert damagedProfile != null;
                    if (!damagedProfile.isPvpEnabled()) {
                        event.setCancelled(true);
                        return;
                    }

                    if (!Objects.equals(damagedProfile.getTeam(), "none") && Objects.equals(damagedProfile.getTeam(), attackerProfile.getTeam())) {
                        event.setCancelled(true);
                        return;
                    }

                }


                UserProfile attackerProfile = profileManager.getProfile(attacker.getName());


                if (event.getCause() == EntityDamageEvent.DamageCause.THORNS && !attackerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {

                    event.setDamage(4);
                    return;
                }

                if (event.getCause() == EntityDamageEvent.DamageCause.THORNS &&  attackerProfile.getChosenClass().equalsIgnoreCase("swordsman") && !attackerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {

                    event.setDamage(4);
                    return;
                }

                if (attackerProfile != null) {
                    try {
                        handleMeleeDamage(attacker,victim,event,damagerLocation,damagedLocation,attackerProfile);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                if (victim instanceof Monster) {
                    if (!victim.hasMetadata("extraHealth")) {
                        victim.setHealth(0);
                        attacker.sendMessage("Server removed monster rewards when it restarted");
                        return;
                    }
                }

                if (victim.hasMetadata("attackerList")) {
                    List<String> attackerList = (List<String>) victim.getMetadata("attackerList").get(0).value();

                    String attackerName = attacker.getName();
                    assert attackerList != null;
                    if (!attackerList.contains(attackerName)) {
                        attackerList.add(attackerName);
                        victim.setMetadata("attackerList", new FixedMetadataValue(plugin, attackerList)); // Update metadata
                    }
                }
            }


            if (damager instanceof Projectile && damaged instanceof LivingEntity victim) {

                Projectile projectile = (Projectile) event.getDamager();
                ProjectileSource shooter = projectile.getShooter();



                if (projectile instanceof Arrow && shooter instanceof Player attacker) {
                    Location loc2 = attacker.getLocation();

                    // Check if the world name contains "Labyrinth" and coordinates match
                    if (loc2.getWorld().getName().contains("labyrinth")) {
                        int x = loc2.getBlockX();
                        int z = loc2.getBlockZ();


                        // Check if the coordinates are within the specified range
                        if (x >= -23 && x <= -17 && z >= -38 && z <= -34) {
                            // Cancel the damage
                            event.setCancelled(true);
                            return;
                        }
                    }

                    if (damaged instanceof Player damagedPlayer) {

                        UserProfile attackerProfile = profileManager.getProfile(attacker.getName());
                        UserProfile damagedProfile = profileManager.getProfile(damagedPlayer.getName());

                        if (attackerProfile == null || damagedProfile == null) {
                            attacker.sendMessage("Your profile or target's profile can not be found. contact developer to fix files");
                            damaged.sendMessage("Your profile or dude's profile can not be found. contact developer to fix files");
                            event.setCancelled(true);
                            return;
                        }

                        assert attackerProfile != null;
                        if (!attackerProfile.isPvpEnabled()) {
                            event.setCancelled(true);
                            return;
                        }

                        assert damagedProfile != null;
                        if (!damagedProfile.isPvpEnabled()) {
                            event.setCancelled(true);
                            return;
                        }

                        if (!Objects.equals(damagedProfile.getTeam(), "none") && Objects.equals(damagedProfile.getTeam(), attackerProfile.getTeam())) {
                            event.setCancelled(true);
                            return;
                        }



                    }




                    UserProfile attackerProfile = profileManager.getProfile(attacker.getName());



                    if (victim.hasMetadata("attackerList")) {

                        List<String> attackerList = (List<String>) victim.getMetadata("attackerList").get(0).value();

                        String attackerName = attacker.getName();
                        if (!attackerList.contains(attackerName)) {
                            attackerList.add(attackerName);
                            victim.setMetadata("attackerList", new FixedMetadataValue(plugin, attackerList)); // Update metadata
                        }
                    }
                    if (attackerProfile != null) {
                      try {
                          handleLongRangeDamage(attacker,victim,event,damagerLocation,damagedLocation,attackerProfile);
                      } catch (Exception e) {
                          throw new RuntimeException(e);
                      }
                    }
                }

                if (projectile instanceof ThrownPotion && projectile.getShooter() instanceof Player attacker ) {

                    Location loc2 = attacker.getLocation();

                    // Check if the world name contains "Labyrinth" and coordinates match
                    if (loc2.getWorld().getName().contains("labyrinth")) {
                        int x = loc2.getBlockX();
                        int z = loc2.getBlockZ();


                        // Check if the coordinates are within the specified range
                        if (x >= -23 && x <= -17 && z >= -38 && z <= -34) {
                            // Cancel the damage
                            event.setCancelled(true);
                            return;
                        }
                    }

                    if (damaged instanceof Player damagedPlayer) {

                        UserProfile attackerProfile = profileManager.getProfile(attacker.getName());
                        UserProfile damagedProfile = profileManager.getProfile(damagedPlayer.getName());

                        if (attackerProfile == null || damagedProfile == null) {
                            attacker.sendMessage("Your profile or target's profile can not be found. contact developer to fix files");
                            damaged.sendMessage("Your profile or dude's profile can not be found. contact developer to fix files");
                            event.setCancelled(true);
                            return;
                        }

                        assert attackerProfile != null;
                        if (!attackerProfile.isPvpEnabled()) {
                            event.setCancelled(true);
                            return;
                        }

                        assert damagedProfile != null;
                        if (!damagedProfile.isPvpEnabled()) {
                            event.setCancelled(true);
                            return;
                        }

                        if (!Objects.equals(damagedProfile.getTeam(), "none") && Objects.equals(damagedProfile.getTeam(), attackerProfile.getTeam())) {
                            event.setCancelled(true);
                            return;
                        }

                    }


                    UserProfile attackerProfile = profileManager.getProfile(attacker.getName());
                    if (victim.hasMetadata("attackerList")) {
                        List<String> attackerList = (List<String>) victim.getMetadata("attackerList").get(0).value();

                        String attackerName = attacker.getName();
                        if (!attackerList.contains(attackerName)) {
                            attackerList.add(attackerName);
                            victim.setMetadata("attackerList", new FixedMetadataValue(plugin, attackerList)); // Update metadata
                        }
                    }
                    if (attackerProfile != null) {
                        if (attackerProfile.getChosenClass().equalsIgnoreCase("alchemist") && attacker.getName().equals(victim.getName())) {event.setCancelled(true); return;}
                        handleLongRangeDamage(attacker,victim,event,damagerLocation,damagedLocation,attackerProfile);
                    }
                }

            }





            // DAMAGE FROM MOBS



            if (event.getDamager() instanceof Monster mob) {

                // Check if the entity has the fire resistance potion effect
                if (mob instanceof Blaze && damaged instanceof LivingEntity target && target.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {

                    target.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);

                    if (target instanceof Player) target.sendMessage("Your fire resistance effect has been removed by Blaze.");
                }

                   ItemStack weapon =  mob.getEquipment().getItemInMainHand();


                    double mobDamage = Objects.requireNonNull(mob.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).getValue();

                    if (mob instanceof Creeper) {
                        mobDamage *=2;
                    }

                    mobDamage += event.getDamage();

                    int sharplevel = weapon.getEnchantmentLevel(Enchantment.SHARPNESS);

                    if (sharplevel > 0) {
                        mobDamage*=1+(.1*sharplevel);
                    }

                    if (damaged instanceof Player player) {
                        UserProfile playerProfile = profileManager.getProfile(player.getName());
                        playerProfile.setDurability(Math.max(0,playerProfile.getDurability()-1));
                        if (playerProfile.isBossIndicator() && playerProfile.getDurability() <= 0) {
                            player.sendMessage("Durability depleted. You will receive more damage. /sdw to turn off this warning");
                        }
                        mobDamage *= playerProfile.getDurability() == 0 ? 2 : 1;


                        if (event.getEntity() instanceof Player) {

//                            if (playerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
//                                mobDamage *= 1.2;
//                            }
                            if (playerProfile.getChosenClass().equalsIgnoreCase("swordsman") && playerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
                                mobDamage *= .3;
                            }
                            if (playerProfile.getChosenClass().equalsIgnoreCase("swordsman") && !playerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
                                mobDamage *= .8;
                            }
                        }


                        event.setDamage(mobDamage);

                        return;
                    }



                    event.setDamage((mobDamage));

            }

            if (event.getDamager() instanceof Projectile projectile) {
                // Check for custom damage metadata
                if (projectile.getShooter() instanceof Monster mob && mob.hasMetadata("customDamage")) {
                    double customDamage = mob.getMetadata("customDamage").get(0).asDouble();
                    customDamage += event.getDamage();
                    ItemStack weapon =  mob.getEquipment().getItemInMainHand();
                    int sharplevel = weapon.getEnchantmentLevel(Enchantment.POWER);

                    if (sharplevel > 0) {
                        customDamage*=1+(.1*sharplevel);
                    }
                    if (damaged instanceof Player player) {
                        UserProfile playerProfile = profileManager.getProfile(player.getName());
                        playerProfile.setDurability(Math.max(0,playerProfile.getDurability()-1));
                        if (playerProfile.isBossIndicator() && playerProfile.getDurability() <= 0) {
                            player.sendMessage("Durability depleted. You will receive more damage. /sdw to turn off this warning");
                        }
                        customDamage *= playerProfile.getDurability() == 0 ? 2 : 1;


//                            if (playerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
//                                customDamage *= 1.2;
//                            }
                            if (playerProfile.getChosenClass().equalsIgnoreCase("swordsman") && playerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
                                customDamage *= .3;
                            }
                            if (playerProfile.getChosenClass().equalsIgnoreCase("swordsman") && !playerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
                                customDamage *= .8;
                            }

                        event.setDamage(customDamage);

                        return;
                    }
                    event.setDamage(customDamage);
                }
            }

            if (event.getDamager() instanceof ThrownPotion projectile) {
                // Check for custom damage metadata
                if (projectile.getShooter() instanceof Monster mob && mob.hasMetadata("customDamage")) {
                    double customDamage = mob.getMetadata("customDamage").get(0).asDouble();
                    customDamage+=event.getDamage();
                    if (damaged instanceof Player player) {
                        UserProfile playerProfile = profileManager.getProfile(player.getName());
                        playerProfile.setDurability(Math.max(0,playerProfile.getDurability()-1));
                        if (playerProfile.isBossIndicator() && playerProfile.getDurability() <= 0) {
                            player.sendMessage("Durability depleted. You will receive more damage. /sdw to turn off this warning");
                        }
                        customDamage *= playerProfile.getDurability() == 0 ? 2 : 1;
                        customDamage*=.3;

//                            if (playerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
//                                customDamage *= 1.2;
//                            }
                            if (playerProfile.getChosenClass().equalsIgnoreCase("swordsman") && playerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
                                customDamage *= .3;
                            }
                            if (playerProfile.getChosenClass().equalsIgnoreCase("swordsman") && !playerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
                                customDamage *= .8;
                            }

                        event.setDamage(customDamage);

                        return;
                    }
                    event.setDamage(customDamage);
                }
            }
        }




    }

    @EventHandler
    public void entityTarget(EntityTargetLivingEntityEvent event) {
        if (!Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName().equals("world_rpg") && !Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName().contains("world_labyrinth")) {
            return;
        }
        if (event.getEntity() instanceof Monster mob) {
            if (!mob.hasMetadata("extraHealth")) {
                mob.remove();
            }
        }
    }



    @EventHandler (priority = EventPriority.HIGH)
    public void onEntityTakeDamage(EntityDamageEvent event) {
        if (!Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName().equals("world_rpg") && !Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName().contains("world_labyrinth")) {
            return;
        }

        Location loc = event.getEntity().getLocation();

        if (loc.getWorld().getName().contains("labyrinth")) {
            int x = loc.getBlockX();
            int z = loc.getBlockZ();


            // Check if the coordinates are within the specified range
            if ((x >= -23 && x <= -6 && z >= -38 && z <= -34) && event.getEntity() instanceof Monster) {
                // Cancel the damage
                event.setCancelled(true);
                return;
            }

            // Check if the coordinates are within the specified range
            if ((x >= -23 && x <= -17 && z >= -38 && z <= -34) && event.getEntity() instanceof Player) {
                // Cancel the damage
                event.setCancelled(true);
                return;
            }
        }

        if (!(event.getEntity() instanceof LivingEntity) && !(event.getEntity() instanceof  Monster)) {
            return;
        }
//
//        if (event.getEntity() instanceof Monster monsterWithoutMeta && !monsterWithoutMeta.hasMetadata("extraHealth")) {
//            monsterWithoutMeta.remove();
//            return;
//        }
//
        if (event.getDamageSource().getCausingEntity() instanceof Player player && player.getAllowFlight()) {
            player.sendMessage("damaged cancelled. Disable fly mode");
            event.setCancelled(true);
            return;
        }




        double damage = event.getDamage();
        // Create floating text using an ArmorStand
//        spawnFloatingHologram(event.getEntity().getLocation(), ((int) damage)+"", event.getEntity().getWorld(), net.md_5.bungee.api.ChatColor.RED);

        // Update the health indicator

        if (event.getEntity().hasMetadata("extraHealth")) {
            resetHealthIndicator((LivingEntity) event.getEntity(), damage);
        }







    }


    private void handleMeleeDamage(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, Location damagerLocation, Location damagedLocation, UserProfile damagerProfile) {
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
            if (damagerProfile.getSelectedElement().equalsIgnoreCase("fire")) {finalDamage=finalDamage*1.1;}
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




//        double rawDmg = event.getDamage();
//        double finalDmg = event.getFinalDamage();
//
//        double dmgReductionMultiplier = 0;
//
//        if (rawDmg > 0) {
//            // Only calculate dmgReductionMultiplier if rawDmg is greater than 0
//            if (finalDmg > 0) {
//                dmgReductionMultiplier = finalDmg / rawDmg;
//            }
//        }
//
//
//        if (Double.isNaN(dmgReductionMultiplier)) {
//            dmgReductionMultiplier = 0;
//        }
        if (event.getEntity() instanceof Player player) {
            UserProfile playerProfile = profileManager.getProfile(player.getName());


            playerProfile.setDurability(Math.max(0,playerProfile.getDurability()-1));
            if (playerProfile.isBossIndicator() && playerProfile.getDurability() <= 0) {
                player.sendMessage("Durability depleted. You will receive more damage. /sdw to turn off this warning");
            }
            finalDamage *= playerProfile.getDurability() == 0 ? 2 : 1;


//            if (playerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
//                finalDamage *= 1.2;
//            }
            if (playerProfile.getChosenClass().equalsIgnoreCase("swordsman") && playerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
                finalDamage *= .3;
            }
            if (playerProfile.getChosenClass().equalsIgnoreCase("swordsman") && !playerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
                finalDamage *= .8;
            }
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.THORNS && damagerProfile.getChosenClass().equalsIgnoreCase("swordsman") && damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {

            event.setDamage(finalDamage*.1);
            return;
        }

        int sharpnessLevel = weapon.getEnchantmentLevel(Enchantment.SHARPNESS);

        if (sharpnessLevel > 0) {
            finalDamage = finalDamage * (1+(0.1*sharpnessLevel));
        }

        if (!weapon.getType().toString().endsWith("_SWORD")
                && !weapon.getType().toString().endsWith("_AXE")
                && !weapon.getType().toString().endsWith("_MACE")
                && !weapon.getType().toString().endsWith("_TRIDENT")
        ) {
            finalDamage *= .2;
        }

        // Swordsman-specific ability if holding a sword
        if (damagerProfile.getChosenClass().equalsIgnoreCase("swordsman") && weapon.getType().toString().endsWith("_SWORD")) {

            effectsAbilityManager.applyAbility(damagerProfile, target, damagerLocation, damagedLocation);
            if (target instanceof Monster mob && (damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 2") || damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 3"))) {
                mob.setTarget(attacker);
            }
        }

        try {
            if (attacker == null || damagerProfile == null) {
                System.err.println("Attacker or damagerProfile is null.");
                return;
            }

            if (PlayerBuffPerms.canLifeSteal(damagerProfile)
                    && attacker.getInventory().getItemInMainHand().getType().toString().endsWith("_SWORD")) {

                AttributeInstance maxHealthAttribute = attacker.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (maxHealthAttribute == null) {
                    System.err.println("Attacker does not have the GENERIC_MAX_HEALTH attribute.");
                    return;
                }

                double maxHealth = maxHealthAttribute.getValue();
                double currentHealth = attacker.getHealth();
                if (currentHealth <= 0) {
                    System.err.println("Attacker is dead or has invalid health.");
                    return;
                }

                if (finalDamage <= 0) {
                    System.err.println("Final damage is non-positive, no lifesteal will be applied.");
                    return;
                }

                double lifestealAmount = finalDamage * 0.1; // 10% lifesteal
                double newHealth = Math.min(currentHealth + lifestealAmount, maxHealth);
                attacker.setHealth(newHealth);
            }
        } catch (Exception e) {
            System.err.println("An error occurred in the lifesteal logic.");
            e.printStackTrace();
        }

        if (event.getEntity() instanceof Player player) {
            finalDamage /=1.5;
        }

        // Attack cooldown mechanic (charge multiplier)
        float attackCooldown = attacker.getAttackCooldown();  // 0 = no cooldown, 1 = max cooldown

        // Apply cooldown scaling to the final damage
        finalDamage *= attackCooldown;

        event.setDamage(finalDamage
//                *dmgReductionMultiplier
        );
    }


    // PvE Long Range Damage
    private void handleLongRangeDamage(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, Location damagerLocation, Location damagedLocation, UserProfile damagerProfile) {



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
                finalDamage *= 0.7;
            } else if (distance <= 8) {
                finalDamage *= 0.8;
            } else if (distance <= 9) {
                finalDamage *= 0.9;
            } else if (distance <= 10) {
                finalDamage *= 1;
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




        if (event.getEntity() instanceof Player player) {
            UserProfile playerProfile = profileManager.getProfile(player.getName());

            playerProfile.setDurability(Math.max(0,playerProfile.getDurability()-1));
            if (playerProfile.isBossIndicator() && playerProfile.getDurability() <= 0) {
                player.sendMessage("Durability depleted. You will receive more damage. /sdw to turn off this warning");
            }
            finalDamage *= playerProfile.getDurability() == 0 ? 2 : 1;
//            if (playerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
//                finalDamage *= 1.2;
//            }
            if (playerProfile.getChosenClass().equalsIgnoreCase("swordsman") && playerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
                finalDamage *= .3;
            }
            if (playerProfile.getChosenClass().equalsIgnoreCase("swordsman") && !playerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
                finalDamage *= .8;
            }
        }

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
//            if (victimProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
//                finalDamage=finalDamage*1.2;
//            }
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


        // Alchemist class - Check if using thrown potion
        if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist") && event.getDamager() instanceof ThrownPotion) {
            event.setDamage(finalDamage*1.2);
            effectsAbilityManager.applyAbility(damagerProfile, target, damagerLocation, damagedLocation);

            if (attacker.getWorld().getName().contains("labyrinth") && target instanceof Monster mob) {
                if (Math.random() < 0.05) {
                    Location attackerLocation = attacker.getLocation();
                    Vector direction = attackerLocation.getDirection().normalize(); // Get the attacker's direction
                    double distance = 0.5; // Set the distance you want the mob to appear in front of the attacker
                    Location teleportLocation = attackerLocation.add(direction.multiply(distance));

                    // Adjust Y to avoid the mob spawning inside the ground
                    teleportLocation.setY(attackerLocation.getY());

                    mob.teleport(teleportLocation);
                }

            }

            return;
        }

        // Archer class - Check if using bow
        if (damagerProfile.getChosenClass().equalsIgnoreCase("archer") && event.getDamager() instanceof Arrow arrow) {
            effectsAbilityManager.applyAbility(damagerProfile, target, damagerLocation, damagedLocation);

            if (!arrow.hasMetadata("WeaknessArrowBarrage") && !arrow.hasMetadata("FireArrowBarrage") && !arrow.hasMetadata("FreezeArrowBarrage")) {
                double archerDex = damagerProfile.getArcherClassInfo().getDex();
                double baseChance = 0.10; // 10% base chance
                double dexModifier = .00004; // 0.004 per Dexterity
                double totalChance = Math.min(.5,baseChance + (archerDex * dexModifier));

// Generate a random value between 0.0 and 1.0
                if (Math.random() < totalChance) {
                    damageAbilityManager.applyDamageAbility(damagerProfile, target, damagerLocation, damagedLocation, finalDamage);
                }
            }


        }

        int powerLevel = weapon.getEnchantmentLevel(Enchantment.POWER);

        if (powerLevel > 0) {
            finalDamage = finalDamage * (1+0.1*powerLevel);
        }

        if (event.getEntity() instanceof Player player) {
            finalDamage /=1.5;
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
                                str += (Math.round(1.2*parseLoreValue(lore)));
                            }
                            else {
                                str += parseLoreValue(lore);
                            }


                        } else if (lore.startsWith("Dexterity: ")) {
                            if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                                dex += (Math.round(1.2*parseLoreValue(lore)));
                            }
                            else {
                                dex += parseLoreValue(lore);
                            }
                        } else if (lore.startsWith("Intelligence: ")) {
                            if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                                intel += (Math.round(1.2*parseLoreValue(lore)));
                            }
                            else {
                                intel += parseLoreValue(lore);
                            }
                        } else if (lore.startsWith("Luck: ")) {
                            if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                                luk += (Math.round(1.2*parseLoreValue(lore)));
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
            statDmg+=str*1;
            if (damagerProfile.getChosenClass().equalsIgnoreCase("swordsman")) {
                if (damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 1") && player.getInventory().getItemInMainHand().getType().toString().endsWith("_SWORD")) {
                    elementalDamage += (intel * 1.2);
                }
            }
        }


        // bow
        if (event.getDamager() instanceof Arrow) {
            statDmg+=dex*1;
            //archers
            if (damagerProfile.getChosenClass().equalsIgnoreCase("archer")) {

                if (damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 2")) {
                    elementalDamage += (intel*1);
                }

                if (damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 1")) {
                    elementalDamage += (intel*1.2);
                }
            }
        }

        // thrown potions
        if (event.getDamager() instanceof ThrownPotion) {
            elementalDamage += intel * 1;
            //alchemists
            if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
                if (damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 1")) {
                    elementalDamage += (intel*.2);
                }
            }
        }

        double calculatedDamage = baseDamage + statDmg + elementalDamage;

        // Critical Hit System
        double critChance = 0;
        if (damagerProfile.getChosenClass().equalsIgnoreCase("swordsman")){
            critChance += (luk * 0.0003);
        }
        else {
            critChance += (luk * 0.0002);
        }
        if (damagerProfile.getChosenClass().equalsIgnoreCase("archer")){
            critChance += (dex * 0.0001);
        }
        double critDmgMultiplier = 1.5 + (luk * 0.001);
        if (damagerProfile.getChosenClass().equalsIgnoreCase("archer") && damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")){
            critChance += 0.25;
            critDmgMultiplier += dex*0.00005;
        }

        if (event.getEntity() instanceof Player player1) {
            UserProfile player1Profile = profileManager.getProfile(player1.getName());
            int p1Luk=0;

            if (player1Profile.getChosenClass().equalsIgnoreCase("swordsman")) {
                p1Luk = player1Profile.getSwordsmanClassInfo().getLuk();
            }
            if (player1Profile.getChosenClass().equalsIgnoreCase("archer")) {
                p1Luk = player1Profile.getArcherClassInfo().getLuk();
            }
            if (player1Profile.getChosenClass().equalsIgnoreCase("alchemist")) {
                p1Luk = player1Profile.getAlchemistClassInfo().getLuk();
            }

            critChance -= Math.max(0,p1Luk*0.0002);

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





        if (damagerProfile.getStamina() <= 0) {
            damagerProfile.setStamina(0);
            if (damagerProfile.isBossIndicator()) {
                player.sendMessage("Stamina depleted. You will deal less damage. /sdw to turn off this warning");
            }
            calculatedDamage /= 2;
        } else {
            damagerProfile.setStamina(damagerProfile.getStamina() - 1);
        }

        if (damagerProfile.getDurability() <= 0) {
            damagerProfile.setDurability(0);
            if (damagerProfile.isBossIndicator()) {
                player.sendMessage("Durability depleted. You will take more damage. /sdw to turn off this warning");
            }
        } else {
            damagerProfile.setDurability(damagerProfile.getDurability() - 1);
        }


        String selectedElement = damagerProfile.getSelectedElement(); // Get the selected element (e.g., fire)

        // If the selected element is "fire", and we want to check if the target is affected by Slowness
//        if (selectedElement.equalsIgnoreCase("fire") && event.getEntity() instanceof LivingEntity target && target.hasPotionEffect(PotionEffectType.SLOWNESS)) {
//                spawnFloatingHologram(event.getEntity().getLocation(), "Melt", player.getWorld(), ChatColor.AQUA);
//        }
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

        }

        return  calculatedDamage;
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

    // Method to spawn the floating hologram above the monster
    private void spawnFloatingHologram(Location location, String text, World world,
                                       ChatColor color) {
        // Create the ArmorStand at the given location
        ArmorStand armorStand = (ArmorStand) world.spawnEntity(location.clone().add(0, 1, 0), EntityType.ARMOR_STAND);

        String coloredText = color + text;

        // Set up the hologram text
        armorStand.setCustomName(ChatColor.translateAlternateColorCodes('&', coloredText));
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


    private void resetHealthIndicator(LivingEntity entity, double damage) {
        // Retrieve the total health (current health + extra health)
        double totalHealth = entity.getHealth();

        int totalRemainingHealth = (int) Math.floor(totalHealth - damage);

        if (totalRemainingHealth< 0) {
            totalRemainingHealth = 0;
        }

        // Format the health indicator (e.g., [99] for health 99)
        String healthIndicator = org.bukkit.ChatColor.YELLOW + " [" + totalRemainingHealth + "]";

        // Get the existing custom name from metadata, or use the default entity type if no custom name is set
        String customName = entity.hasMetadata("customName") ? entity.getMetadata("customName").get(0).asString() : entity.getType().name();

        // Set the updated custom name with the new health indicator
        entity.setCustomName(customName + healthIndicator);

    }



}



