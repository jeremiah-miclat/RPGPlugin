package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.manager.RavagerSkillManager;
import github.eremiyuh.rPGPlugin.methods.DamageAbilityManager;
import github.eremiyuh.rPGPlugin.methods.EffectsAbilityManager;
import github.eremiyuh.rPGPlugin.perms.PlayerBuffPerms;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class DamageListener implements Listener {

    private  final PlayerProfileManager profileManager;
    private final EffectsAbilityManager effectsAbilityManager;
    private  final DamageAbilityManager damageAbilityManager;
    private final RPGPlugin plugin;
    private final Map<UUID, BukkitTask> downedPlayers = new HashMap<>();
    private final RavagerSkillManager manager;
    private final Map<UUID, Long> lastDamageTime = new HashMap<>();

    private final int x1 = -150, z1 = 150;
    private final int x2 = 90, z2 = -110;

    public DamageListener(PlayerProfileManager profileManager, EffectsAbilityManager effectsAbilityManager, DamageAbilityManager damageAbilityManager, RPGPlugin plugin, RavagerSkillManager manager) {
        this.profileManager = profileManager;
        this.effectsAbilityManager = effectsAbilityManager;
        this. damageAbilityManager = damageAbilityManager;
        this.plugin = plugin;
        this.manager = manager;
    }

    Set<UUID> rejoinDowned = new HashSet<>();
    private final Set<UUID> forceDeath = new HashSet<>();
    private final Map<UUID, Long> lastGroundedTime = new HashMap<>();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (downedPlayers.containsKey(player.getUniqueId())) {
            removeDownedState(player);
            player.setHealth(0); // Instant death
        }
    }

//    @EventHandler
//    public void onPlayerQuit(PlayerQuitEvent event) {
//        Player player = event.getPlayer();
//        UUID uuid = player.getUniqueId();
//
//        if (downedPlayers.containsKey(uuid)) {
//            BukkitTask task = downedPlayers.remove(uuid);
//            if (task != null) task.cancel();
//
//            removeDownedState(player);
//            rejoinDowned.add(uuid); // Mark as was downed
//        }
//    }
//
//    @EventHandler
//    public void onPlayerJoin(PlayerJoinEvent event) {
//        Player player = event.getPlayer();
//        UUID uuid = player.getUniqueId();
//
//        if (rejoinDowned.contains(uuid)) {
//            setDownedState(player); // Reapply effects
//            // Re-schedule the death
//            BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
//                removeDownedState(player);
//                player.setHealth(0);
//            }, 20L * 60);
//
//            downedPlayers.put(uuid, task);
//            rejoinDowned.remove(uuid);
//        }
//    }



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

    @EventHandler (priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageByEntityEvent event) {

        if (!Objects.requireNonNull(event.getDamager().getLocation().getWorld()).getName().equals("world_rpg") && !Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName().contains("world_labyrinth")) {
            // Get the entity that was damaged
            Entity damaged = event.getEntity();
            // Get the damager (the entity dealing the damage)
            Entity damager = event.getDamager();
            if (event.getDamager() instanceof Player player && (player.getAllowFlight() && player.isFlying())) {
                player.sendMessage("damaged cancelled. Disable fly mode");
                event.setCancelled(true);
                return;
            }


            if (damaged instanceof Tameable tameable) {
                if (damager instanceof Player) {
                    AnimalTamer owner = tameable.getOwner();

                    // Check if the tameable has an owner
                    if (owner != null) {
                        if (!(owner instanceof Player) )return;
                        if (Objects.requireNonNull(owner.getName()).equalsIgnoreCase(damager.getName())) return;

                        UserProfile ownerProfile = profileManager.getProfile(owner.getName());
                        UserProfile profile = profileManager.getProfile(damager.getName());
                        if (!(profile.getTeam().equalsIgnoreCase(ownerProfile.getTeam())) && !profile.getTeam().equalsIgnoreCase("none")) return;
                        if ((profile.isPvpEnabled() && ownerProfile.isPvpEnabled())) return;
                        damager.sendMessage("Can't Damage");
                        event.setCancelled(true);

                    }
                }

                if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter) {
                    AnimalTamer owner = tameable.getOwner();

                    // Check if the tameable has an owner
                    if (owner != null) {
                        if (Objects.requireNonNull(owner.getName()).equalsIgnoreCase(damager.getName())) return;
                        if (!(owner instanceof Player) )return;
                        UserProfile ownerProfile = profileManager.getProfile(owner.getName());
                        UserProfile profile = profileManager.getProfile(shooter.getName());
                        if (!(profile.getTeam().equalsIgnoreCase(ownerProfile.getTeam())) && !profile.getTeam().equalsIgnoreCase("none")) return;
                        if ((profile.isPvpEnabled() && ownerProfile.isPvpEnabled())) return;
                        damager.sendMessage("Can't Damage");
                        event.setCancelled(true);
                    }
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

                    int attackerLevel = attackerProfile.getLevel();
                    int damagedLevel = damagedProfile.getLevel();


                    // Check if the difference between the total points is greater than 10
                    if (Math.abs(attackerLevel - damagedLevel) > 10) {
                        event.setCancelled(true);
                        return;
                    }

//                    assert attackerProfile != null;
//                    if (!attackerProfile.isPvpEnabled()) {
//                        event.setCancelled(true);
//                        return;
//                    }
//
//                    assert damagedProfile != null;
//                    if (!damagedProfile.isPvpEnabled()) {
//                        event.setCancelled(true);
//                        return;
//                    }

                    if (!Objects.equals(damagedProfile.getTeam(), "none") && Objects.equals(damagedProfile.getTeam(), attackerProfile.getTeam())) {
                        event.setCancelled(true);
                        return;
                    }

                }


                UserProfile attackerProfile = profileManager.getProfile(attacker.getName());




                if (attackerProfile != null) {
                    try {
                        handleMeleeDamage(attacker,victim,event,damagerLocation,damagedLocation,attackerProfile);



                        if ((event.getEntity() instanceof Ravager ravager)) {

                            if (manager.isReflecting(ravager.getUniqueId())) {
                                Vector knockbackDirection = attacker.getLocation().toVector().subtract(damaged.getLocation().toVector()).normalize();
                                knockbackDirection.multiply(1.5);
                                knockbackDirection.setY(0.5);
                                attacker.setVelocity(knockbackDirection);
                                attacker.damage(event.getDamage());
                                attacker.sendMessage("§cYou were struck by " + ravager.getName() + "'s retaliation!");
                                event.setDamage(0);
                            }

                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                if (victim instanceof Monster && !(victim.hasMetadata("attackerList"))) {


                    List<String> attackerList = new ArrayList<>();
                    victim.setMetadata("attackerList", new FixedMetadataValue(plugin, attackerList));

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



                    if (damaged instanceof Player damagedPlayer) {

                        if (attacker.getName().equalsIgnoreCase(damagedPlayer.getName())) {
                            event.setCancelled(true);
                            return;
                        }

                        UserProfile attackerProfile = profileManager.getProfile(attacker.getName());
                        UserProfile damagedProfile = profileManager.getProfile(damagedPlayer.getName());



                        if (attackerProfile == null || damagedProfile == null) {
                            attacker.sendMessage("Your profile or target's profile can not be found. contact developer to fix files");
                            damaged.sendMessage("Your profile or dude's profile can not be found. contact developer to fix files");
                            event.setCancelled(true);
                            return;
                        }


                        int attackerLevel = attackerProfile.getLevel();
                        int damagedLevel = damagedProfile.getLevel();


                        // Check if the difference between the total points is greater than 10
                        if (Math.abs(attackerLevel - damagedLevel) > 10) {
                            event.setCancelled(true);
                            return;
                        }

//                        assert attackerProfile != null;
//                        if (!attackerProfile.isPvpEnabled()) {
//                            event.setCancelled(true);
//                            return;
//                        }
//
//                        assert damagedProfile != null;
//                        if (!damagedProfile.isPvpEnabled()) {
//                            event.setCancelled(true);
//                            return;
//                        }

                        if (!Objects.equals(damagedProfile.getTeam(), "none") && Objects.equals(damagedProfile.getTeam(), attackerProfile.getTeam())) {
                            event.setCancelled(true);
                            return;
                        }



                    }




                    UserProfile attackerProfile = profileManager.getProfile(attacker.getName());



                    if (victim instanceof Monster && !(victim.hasMetadata("attackerList"))) {

                        List<String> attackerList = new ArrayList<>();
                        victim.setMetadata("attackerList", new FixedMetadataValue(plugin, attackerList));

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
                    if (attackerProfile != null) {
                      try {
                          handleLongRangeDamage(attacker,victim,event,damagerLocation,damagedLocation,attackerProfile,attackerProfile.getLongDmg());
                          if ((event.getEntity() instanceof Ravager ravager)) {

                              if (manager.isReflecting(ravager.getUniqueId())) {
                                  Vector knockbackDirection = attacker.getLocation().toVector().subtract(damaged.getLocation().toVector()).normalize();
                                  knockbackDirection.multiply(1.5);
                                  knockbackDirection.setY(0.5);
                                  attacker.setVelocity(knockbackDirection);
                                  attacker.damage(event.getDamage());
                                  attacker.sendMessage("§cYou were struck by " + ravager.getName() + "'s retaliation!");
                                  event.setDamage(0);
                              }

                          }


                      } catch (Exception e) {
                          throw new RuntimeException(e);
                      }
                    }
                }




                if (projectile instanceof ThrownPotion && projectile.getShooter() instanceof Player attacker ) {




                    if (damaged instanceof Player damagedPlayer) {

                        UserProfile attackerProfile = profileManager.getProfile(attacker.getName());
                        UserProfile damagedProfile = profileManager.getProfile(damagedPlayer.getName());

                        if (attackerProfile == null || damagedProfile == null) {
                            attacker.sendMessage("Your profile or target's profile can not be found. contact developer to fix files");
                            damaged.sendMessage("Your profile or dude's profile can not be found. contact developer to fix files");
                            event.setCancelled(true);
                            return;
                        }

                        int attackerLevel = attackerProfile.getLevel();
                        int damagedLevel = damagedProfile.getLevel();


                        // Check if the difference between the total points is greater than 10
                        if (Math.abs(attackerLevel - damagedLevel) > 10) {
                            event.setCancelled(true);
                            return;
                        }

//                        assert attackerProfile != null;
//                        if (!attackerProfile.isPvpEnabled()) {
//                            event.setCancelled(true);
//                            return;
//                        }
//
//                        assert damagedProfile != null;
//                        if (!damagedProfile.isPvpEnabled()) {
//                            event.setCancelled(true);
//                            return;
//                        }

                        if (!Objects.equals(damagedProfile.getTeam(), "none") && Objects.equals(damagedProfile.getTeam(), attackerProfile.getTeam())) {
                            event.setCancelled(true);
                            return;
                        }

                    }


                    UserProfile attackerProfile = profileManager.getProfile(attacker.getName());
                    if (victim instanceof Monster && !(victim.hasMetadata("attackerList"))) {

                        List<String> attackerList = new ArrayList<>();
                        victim.setMetadata("attackerList", new FixedMetadataValue(plugin, attackerList));

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
                    if (attackerProfile != null) {
                        if (attackerProfile.getChosenClass().equalsIgnoreCase("alchemist") && attacker.getName().equals(victim.getName())) {event.setCancelled(true); return;}
                        handleLongRangeDamage(attacker,victim,event,damagerLocation,damagedLocation,attackerProfile, attackerProfile.getSplashDmg());

                        if ((event.getEntity() instanceof Ravager ravager)) {

                            if (manager.isReflecting(ravager.getUniqueId())) {
                                Vector knockbackDirection = attacker.getLocation().toVector().subtract(damaged.getLocation().toVector()).normalize();
                                knockbackDirection.multiply(1.5);
                                knockbackDirection.setY(0.5);
                                attacker.setVelocity(knockbackDirection);
                                attacker.damage(event.getDamage());
                                attacker.sendMessage("§cYou were struck by " + ravager.getName() + "'s retaliation!");
                                event.setDamage(0);
                            }

                        }

//                        if ((damaged instanceof Warden || damaged instanceof Evoker || damaged instanceof Ravager) && Math.random() < 0.05) {
//                            Location aloc = attacker.getLocation().clone();
//                            ((Monster) damaged).attack(attacker);
//                            Vector knockbackDirection = attacker.getLocation().toVector().subtract(damaged.getLocation().toVector()).normalize();
//                            knockbackDirection.multiply(1.5);
//                            knockbackDirection.setY(0.5);
//                            attacker.setVelocity(knockbackDirection);
//                            damaged.teleport(aloc.add(0, 0, 0));
//
//
//                        }


                    }
                }

            }





            // DAMAGE FROM MOBS



            if (event.getDamager() instanceof Monster mob) {



                   ItemStack weapon =  mob.getEquipment().getItemInMainHand();


                    double mobDamage = Objects.requireNonNull(mob.getAttribute(Attribute.ATTACK_DAMAGE)).getValue();



                    mobDamage += event.getDamage();

                    int sharplevel = weapon.getEnchantmentLevel(Enchantment.SHARPNESS);

                    if (sharplevel > 0) {
                        mobDamage*=1+(.1*sharplevel);
                    }

                    if (damaged instanceof Player player) {
                        UserProfile playerProfile = profileManager.getProfile(player.getName());




                        mobDamage *= playerProfile.getDurability() == 0 ? 2 : 1;




                        if (mob instanceof Creeper) {
                            event.setDamage((mobDamage+event.getDamage()));
                            return;
                        }

//                        final double remainingHealth = player.getHealth() - (event.getDamage()+mobDamage);
//                        if (remainingHealth <= 0) {
//                            // Use the same downed state logic as in onFatalDamage
//                            player.setHealth(0.1);
//                            event.setDamage(0);
//                            player.sendMessage("on mob. the damage was "+ event.getDamage()+mobDamage);
//                            player.sendMessage("on mob. the f damage was "+ event.getFinalDamage()+mobDamage);
//                            player.sendMessage("on mob. remaining h "+ remainingHealth);
//                            player.sendMessage("on mob. current h "+ player.getHealth());
//                            setDownedState(player);
//
//                            // Schedule death
//                            BukkitTask deathTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
//                                if (downedPlayers.containsKey(player.getUniqueId())) {
//                                    removeDownedState(player);
//                                    player.setHealth(0);
//                                }
//                            }, 20L * 60); // 60s
//
//                            downedPlayers.put(player.getUniqueId(), deathTask);
//
//                            return;
//                        }
//                        if (mob instanceof Vex) {
//                            mobDamage = .5;
//                        }



                        event.setDamage(mobDamage);

                        return;
                    }



                    event.setDamage((mobDamage));

            }

            if (event.getDamager() instanceof Projectile projectile) {
                // Check for custom damage metadata
                if (projectile.getShooter() instanceof Monster mob) {
                    double customDamage = Objects.requireNonNull(mob.getAttribute(Attribute.ATTACK_DAMAGE)).getBaseValue();
                    customDamage += event.getDamage();
                    ItemStack weapon =  mob.getEquipment().getItemInMainHand();
                    int sharplevel = weapon.getEnchantmentLevel(Enchantment.POWER);

                    if (sharplevel > 0) {
                        customDamage*=1+(.1*sharplevel);
                    }
                    if (damaged instanceof Player player) {
                        if (mob instanceof Wither && new Random().nextInt(100) < 10) {
                            Location playerLoc = player.getLocation();
                            Vector behind = playerLoc.getDirection().normalize().multiply(-1);
                            Location spawnLoc = playerLoc.clone().add(behind).add(0.5, 0, 0.5);
                            player.getWorld().spawnEntity(spawnLoc, EntityType.WITHER_SKELETON);
                        }




                        UserProfile playerProfile = profileManager.getProfile(player.getName());

                        customDamage *= playerProfile.getDurability() == 0 ? 2 : 1;


//                            if (playerProfile.getChosenClass().equalsIgnoreCase("alchemist")) {
//                                customDamage *= 1.2;
//                            }


                        event.setDamage(customDamage);

                        return;
                    }
                    event.setDamage(customDamage);
                }
            }

            if (event.getDamager() instanceof ThrownPotion projectile) {
                // Check for custom damage metadata
                if (projectile.getShooter() instanceof Monster mob) {
                    double customDamage = Objects.requireNonNull(mob.getAttribute(Attribute.ATTACK_DAMAGE)).getBaseValue();
                    customDamage+=event.getDamage();
                    if (damaged instanceof Player player) {
                        UserProfile playerProfile = profileManager.getProfile(player.getName());

                        customDamage *= playerProfile.getDurability() == 0 ? 2 : 1;




                        event.setDamage(customDamage);

                        return;
                    }
                    event.setDamage(customDamage);
                }
            }



        }




    }

    private String getSkeletonRandomInsult() {
        String[] insults = {
                "You're not even worth my arrows!",
                "I've seen zombies with more brains!",
                "Go back to the noob cave!",
                "You're one hit away from respawn!"
        };
        return insults[new Random().nextInt(insults.length)];
    }

    private String getVindicatorRandomInsult() {
        String[] insults = {
                "I'll split you like firewood!",
                "This axe has your name on it.",
                "You call *that* armor?",
                "Your screams will echo in the mansion!",
                "You're softer than a slime block!",
                "Did you forget your sword at home?",
                "One swing is all it takes...",
                "Is that fear I smell?",
                "You should’ve stayed in creative mode!",
                "Even an Illusioner could beat you blind!"
        };
        return insults[new Random().nextInt(insults.length)];
    }

    private boolean hasTotem(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        return mainHand.getType() == Material.TOTEM_OF_UNDYING || offHand.getType() == Material.TOTEM_OF_UNDYING;
    }

    public void updateLastGrounded(Player player) {
        lastGroundedTime.put(player.getUniqueId(), System.currentTimeMillis());
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onFatalDamage(EntityDamageEvent event) {

        // ✅ Exit early if event already cancelled by other plugin or mechanics
        if (event.isCancelled()) return;



        if (!(event.getEntity() instanceof Player player)) return;

        double currentHealth = player.getHealth();
        double finalDamage = event.getFinalDamage();

        if ((currentHealth - finalDamage) <= 0) {
            if (hasTotem(player)) return;
            // ✅ If player is already downed, prevent repeated triggering
            if (downedPlayers.containsKey(player.getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            // ⛑️ Down the player instead of dying
            player.setHealth(0.1);  // minimal value, not zero
//            player.sendMessage("on fatal. the damage was "+ event.getDamage());
            event.setDamage(0);     // prevent actual death
            player.chat( "I'm down. I need help");
            setDownedState(player); // your custom logic

            // ⏳ Schedule forced death after 60 seconds
            BukkitTask deathTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (downedPlayers.containsKey(player.getUniqueId())) {
                    removeDownedState(player);
                    player.setHealth(0); // force death
                }
            }, 20L * 60); // 60 seconds

            // Store the task for later cancellation if revived
            downedPlayers.put(player.getUniqueId(), deathTask);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onEntityTakeDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && !player.getWorld().getName().contains("_rpg")) {
            long now = System.currentTimeMillis();
            UUID uuid = player.getUniqueId();

            // If last damage was less than 1 second ago, cancel this damage
            if (lastDamageTime.containsKey(uuid)) {
                long lastTime = lastDamageTime.get(uuid);
                if (now - lastTime < 1000) { // 1000 ms = 1 second
                    event.setCancelled(true);
                    return;
                }
            }

            lastDamageTime.put(uuid, now);
        }

        if (!Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName().equals("world_rpg") && !Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName().contains("world_labyrinth")) {
            return;
        }

        if (event.getEntity() instanceof Ravager ravager) {
            if (manager.isFrozen(ravager.getUniqueId())) {
                event.setDamage(0);
                return;
            }
        }


        if (event.getCause() == EntityDamageEvent.DamageCause.SONIC_BOOM
                && event.getEntity() instanceof LivingEntity
                && event.getDamageSource() instanceof Warden warden) {

            // Reuse the modified attack damage from the initialized Warden
            double modifiedDamage = Objects.requireNonNull(
                    warden.getAttribute(Attribute.ATTACK_DAMAGE)
            ).getValue();

            double boostedDamage = modifiedDamage * 1.2;

            // Apply it as the Sonic Boom damage
            event.setDamage(boostedDamage);
        }





        if (!(event.getEntity() instanceof LivingEntity) && !(event.getEntity() instanceof  Monster)) {
            return;
        }

        if (event.getDamageSource().getCausingEntity() instanceof Player player1 && (player1.getAllowFlight() && player1.isFlying())) {
            player1.sendMessage("damaged cancelled. Disable fly mode");
            event.setCancelled(true);
            return;
        }


        double damage = event.getDamage();





        resetHealthIndicator((LivingEntity) event.getEntity(), damage);
        if (event.getDamageSource().getCausingEntity() instanceof Player damager && !event.isCancelled()) {
            UserProfile damagerProfile = profileManager.getProfile(damager.getName());
            double finalDamage = event.getFinalDamage();
            try {
                if (damagerProfile == null || damager.isDead()) {
                    System.err.println("Attacker or damagerProfile is null.");
                    return;
                }

                if (damagerProfile.getLs()>0) {

                    AttributeInstance maxHealthAttribute = damager.getAttribute(Attribute.MAX_HEALTH);
                    if (maxHealthAttribute == null) {
                        System.err.println("Attacker does not have the GENERIC_MAX_HEALTH attribute.");
                        return;
                    }

                    double maxHealth = maxHealthAttribute.getValue();
                    double currentHealth = damager.getHealth();
                    if (currentHealth <= 0) {
                        System.err.println("Attacker is dead or has invalid health.");
                        return;
                    }

                    if (finalDamage <= 0) {
                        System.err.println("Final damage is non-positive, no lifesteal will be applied.");
                        return;
                    }
//                    double lifestealAmount = finalDamage * damagerProfile.getLs();
//                    double newHealth = Math.min(currentHealth + lifestealAmount, maxHealth);
//                    damager.setHealth(newHealth);
//                    spawnFloatingHologram(damager.getLocation(), ((int) lifestealAmount+""), damager.getWorld(), "#65fe08");
                    double lifestealAmount = finalDamage * damagerProfile.getLs();
                    double healAmount = Math.min(lifestealAmount, maxHealth - currentHealth);

                    if (healAmount >= 1.0) {
                        damager.setHealth(currentHealth + healAmount);
                        spawnFloatingHologram(damager.getLocation(), Math.max(1,(int) healAmount) + "", damager.getWorld(), "#65fe08");
                    }
                }
            } catch (Exception e) {
                System.err.println("An error occurred in the lifesteal logic.");
                e.printStackTrace();
            }

        }
        if (event.getEntity() instanceof Player player && event.getDamageSource().getCausingEntity() instanceof Monster monster) {
            UserProfile playerProfile = profileManager.getProfile(player.getName());
            playerProfile.setDurability(Math.max(0,playerProfile.getDurability()-1));
            if (playerProfile.isBossIndicator() && playerProfile.getDurability() <= 0) {
                player.sendMessage("Durability depleted. You will receive more damage. /sdw to turn off this warning");
            }
            double agility = playerProfile.getTempAgi();
            int lvl = 1; // Default to level 1

            String name = monster.getName(); // Or monster.getCustomName() if set that way
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("Lvl\\s*(\\d+)").matcher(name);
            if (matcher.find()) {
                lvl = Integer.parseInt(matcher.group(1));
            } else {
                lvl = 1; // fallback if no level is found in name
            }


            double evadeChance;
            double agiPerLevel = agility / lvl;

            if (agiPerLevel < 25.0) {
                evadeChance = (agiPerLevel / 25.0) * 80.0;
            } else if (agiPerLevel < 50.0) {
                double bonus = ((agiPerLevel - 25.0) / 25.0) * 20.0; // 20% from 25 to 50
                evadeChance = 80.0 + bonus;
            } else {
                evadeChance = 100.0;
            }

            if (Math.random() * 100 < evadeChance) {
                // Evade successful
                event.setCancelled(true);

                spawnFloatingHologram(player.getLocation(), "Evade", player.getWorld(), "#ff0004");
                player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation().add(0, 1, 0), 3);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.6f, 1.2f);
                return;
            }

            if (monster instanceof Spider) {
                // 10% chance to apply Nausea
                if (new Random().nextInt(100) < 10) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 2)); // 3s
                }

            }

            if (monster instanceof Blaze) {
                if (player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {

                    player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                    player.sendMessage("Your fire resistance effect has been removed by Blaze.");
                }
            }

            if (monster instanceof Zombie) {
                if (new Random().nextInt(100) < 10) {

                    player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 2));

                }
            }

            if (monster instanceof Skeleton && new Random().nextInt(100) < 10) {
                Skeleton skeleton = (Skeleton) monster;

                // Get the direction the skeleton is facing
                Vector knockbackDirection = skeleton.getLocation().getDirection().normalize().multiply(3.5); // Horizontal speed
                knockbackDirection.setY(0.5); // Optional upward force

                // Apply velocity to knock the player away in the direction the skeleton is facing
                player.setVelocity(knockbackDirection);

                // Get custom name with formatting
                String displayName = skeleton.getCustomName() != null ? skeleton.getCustomName() : ChatColor.GRAY + "Skeleton";

                // Send a random insult
                player.sendMessage(displayName + ChatColor.RED + ": " + getSkeletonRandomInsult());
            }

            if (monster instanceof Vindicator) {
                if (new Random().nextInt(100) < 5){
                    playerProfile.setDurability(Math.max(0,playerProfile.getDurability()-1000));
                    String displayName = monster.getCustomName() != null ? monster.getCustomName() : ChatColor.GRAY + "Vindicator";
                    player.sendMessage(displayName + ChatColor.RED + ": " + getVindicatorRandomInsult());
                    player.sendMessage(ChatColor.RED + "Vindicator reduced your durability by 1000");
                }
                event.setDamage(1);
            }

            // Apply Frenzy trait bonus
            if (playerProfile.getAbyssTrait().equalsIgnoreCase("Frenzy")) {
                event.setDamage(event.getDamage() * 2);
            }
        }

        if (!event.isCancelled()) {
            Entity entity = event.getEntity();
            Location loc = entity.getLocation();
            World world = entity.getWorld();
            String color = (entity instanceof Player) ? "#ff0004" : "#AA00FF";

            spawnFloatingHologram(loc, (Math.max((int) event.getFinalDamage(),1)) + "", world, color);
        }


    }


    private void handleMeleeDamage(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, Location damagerLocation, Location damagedLocation, UserProfile damagerProfile) {

        if (downedPlayers.containsKey(attacker.getUniqueId())) {
            event.setCancelled(true);
            attacker.sendMessage(org.bukkit.ChatColor.RED + "Can't attack");
            return;
        }

        double baseDamage = event.getDamage();
        ItemStack weapon = attacker.getInventory().getItemInMainHand();



        // Fire element check: Disable fire if not selected
        if (!damagerProfile.getSelectedElement().equalsIgnoreCase("fire")
                && (weapon.containsEnchantment(Enchantment.FLAME) || weapon.containsEnchantment(Enchantment.FIRE_ASPECT))) {
            event.getEntity().setFireTicks(0); // Cancel fire ticks
        }
        double damageWithStats = applyStatsToDamage(damagerProfile.getMeleeDmg()+baseDamage, damagerProfile, attacker, event);
        if (event.isCancelled()) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.THORNS) {
            if (!damagerProfile.getChosenClass().equalsIgnoreCase("swordsman") ||
                    !damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
                event.setCancelled(true);
                return;
            }
        }






        // Apply extra health and set final damage
        double finalDamage = damageWithStats;





        // Swordsman-specific ability if holding a sword
        if (event.getCause() != EntityDamageEvent.DamageCause.THORNS && (damagerProfile.getChosenClass().equalsIgnoreCase("swordsman") && (weapon.getType().toString().endsWith("_SWORD") || weapon.getType().toString().endsWith("_AXE")))) {

            effectsAbilityManager.applyAbility(damagerProfile, target, damagerLocation, damagedLocation);
            if (target instanceof Monster mob && (damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 2") || damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 3"))) {
                mob.setTarget(attacker);
            }
        }

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

        if (event.getCause() == EntityDamageEvent.DamageCause.THORNS && damagerProfile.getChosenClass().equalsIgnoreCase("swordsman") && damagerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
            event.setDamage(finalDamage*.3);
            return;
        }


        float attackCooldown = attacker.getAttackCooldown();
            finalDamage *= attackCooldown;

        if (hasBeenAirborneTooLong(attacker, 5000) && !isStandingOnSolidBlock(attacker)) {
            attacker.sendMessage("§e⚠ Damage is reduced because you have been in the air, on water, or on lava for too long.");
            finalDamage *= 0.1; // reduce to 10%
        }

        if (event.getEntity() instanceof Player player) {
            UserProfile playerProfile = profileManager.getProfile(player.getName());
            finalDamage *= playerProfile.getDurability() == 0 ? 2 : 1;
        }

        event.setDamage(finalDamage
//                *dmgReductionMultiplier
        );
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        if (downedPlayers.containsKey(player.getUniqueId())) {
            event.setCancelled(true);

        }
    }

    @EventHandler
    public void onDownedPlayerJump(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!downedPlayers.containsKey(player.getUniqueId())) return;

        double fromY = event.getFrom().getY();
        double toY = event.getTo().getY();

        // If the Y increased and it's not a simple walk up a slope
        if (toY > fromY && (toY - fromY) > 0.419) { // typical jump is ~0.42
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTargetDownPlayer(EntityTargetEvent event) {
        if (!(event.getEntity() instanceof Monster && event.getTarget() instanceof Player player)) return;

        if (downedPlayers.containsKey(player.getUniqueId())) {
                event.setCancelled(true);
            }

    }

    @EventHandler
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        if (downedPlayers.containsKey(player.getUniqueId())) {
            event.setCancelled(true); // Prevent the item from actually changing

            Bukkit.getScheduler().runTask(plugin, () -> killPlayerImmediately(player)); // Defer to next tick
        }
    }
    @EventHandler
    public void onFoodConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if (downedPlayers.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    private void killPlayerImmediately(Player player) {
        if (!downedPlayers.containsKey(player.getUniqueId())) return;

        removeDownedState(player);

//
//        forceDeath.add(player.getUniqueId()); // ✅ tell the damage event to skip downing

        player.sendMessage("§cYou gave up while downed.");
        player.setHealth(0); // Triggers EntityDamageEvent again
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (downedPlayers.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (downedPlayers.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

//    @EventHandler
//    public void onBucketUse(PlayerBucketEvent event) {
//        if (downedPlayers.containsKey(event.getPlayer().getUniqueId())) {
//            event.setCancelled(true);
//        }
//    }

//    @EventHandler
//    public void onProjectileLaunch(ProjectileLaunchEvent event) {
//        if (event.getEntity().getShooter() instanceof Player player &&
//                downedPlayers.containsKey(player.getUniqueId())) {
//            event.setCancelled(true);
//        }
//    }

//    @EventHandler
//    public void onTeleport(PlayerTeleportEvent event) {
//        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL &&
//                downedPlayers.containsKey(event.getPlayer().getUniqueId())) {
//            event.setCancelled(true);
//        }
//    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        // If not downed, let them run commands
        if (!downedPlayers.containsKey(player.getUniqueId())) return;

        // Cancel any command (starts with '/')
        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "You can't use commands");
    }

//    private void startRevivalCheck(Player downedPlayer) {
//        final UUID uuid = downedPlayer.getUniqueId();
//        final int requiredTime = 5; // seconds
//        final int radius = 2;
//
//        final BukkitTask[] taskRef = new BukkitTask[1];
//
//        taskRef[0] = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
//            int ticksNear = 0;
//
//            @Override
//            public void run() {
//                if (!downedPlayers.containsKey(uuid)) {
//                    taskRef[0].cancel();
//                    return;
//                }
//
//                boolean someoneNearby = false;
//                UserProfile downedProfile = profileManager.getProfile(downedPlayer.getName());
//
//                for (Player other : downedPlayer.getWorld().getPlayers()) {
//                    if (other.equals(downedPlayer)) continue;
//                    if (downedPlayers.containsKey(other.getUniqueId())) continue;
//                    UserProfile otherProfile = profileManager.getProfile(other.getName());
//                    if (!downedProfile.getTeam().equalsIgnoreCase(otherProfile.getTeam()) || downedProfile.getTeam().equalsIgnoreCase("none")) continue;
//
//                    if (other.getLocation().distance(downedPlayer.getLocation()) <= radius) {
//                        someoneNearby = true;
//
//                        // ❤️ Show particle
//                        downedPlayer.getWorld().spawnParticle(
//                                Particle.HEART,
//                                downedPlayer.getLocation().add(0, 1, 0),
//                                1
//                        );
//                        break;
//                    }
//                }
//
//                if (someoneNearby) {
//                    ticksNear++;
//
//                    // ⏳ Show progress
//                    downedPlayer.spigot().sendMessage(
//                            net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
//                            new net.md_5.bungee.api.chat.TextComponent("§eReviving... §a" + ticksNear + "§7/§c5")
//                    );
//
//                    if (ticksNear >= requiredTime) {
//                        taskRef[0].cancel();
//                        revivePlayer(downedPlayer);
//                    }
//                } else {
//                    ticksNear = 0;
//
//                    // 👀 Waiting message
//                    downedPlayer.spigot().sendMessage(
//                            net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
//                            new net.md_5.bungee.api.chat.TextComponent("§7Waiting for teammate...")
//                    );
//                }
//            }
//        }, 0L, 20L); // every second
//    }

    private void startRevivalCheck(Player downedPlayer) {
        final UUID uuid = downedPlayer.getUniqueId();
        final int requiredTime = 5; // seconds needed to revive
        final int radius = 2;

        final BukkitTask[] taskRef = new BukkitTask[1];

        taskRef[0] = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int ticksNear = 0;

            @Override
            public void run() {
                if (!downedPlayers.containsKey(uuid)) {
                    taskRef[0].cancel();
                    return;
                }

                boolean someoneNearby = false;
                Player revivingPlayer = null;

                UserProfile downedProfile = profileManager.getProfile(downedPlayer.getName());

                for (Player other : downedPlayer.getWorld().getPlayers()) {
                    if (other.equals(downedPlayer)) continue;
                    if (downedPlayers.containsKey(other.getUniqueId())) continue;

                    UserProfile otherProfile = profileManager.getProfile(other.getName());
                    if (!downedProfile.getTeam().equalsIgnoreCase(otherProfile.getTeam()) ||
                            downedProfile.getTeam().equalsIgnoreCase("none")) continue;

                    if (other.getLocation().distance(downedPlayer.getLocation()) <= radius) {
                        revivingPlayer = other;
                        someoneNearby = true;

                        // ❤️ Show heart particle
                        downedPlayer.getWorld().spawnParticle(
                                Particle.HEART,
                                downedPlayer.getLocation().add(0, 1, 0),
                                1
                        );
                        break;
                    }
                }

                if (someoneNearby) {
                    ticksNear++;

                    // ⏳ Downed player sees revive progress
                    downedPlayer.sendActionBar(Component.text("§eReviving... §a" + ticksNear + "§7/§c" + requiredTime));

                    // ✅ Reviver sees who they’re reviving
                    if (revivingPlayer != null) {
                        revivingPlayer.sendActionBar(Component.text("§aReviving §e" + downedPlayer.getName() + " §a" + ticksNear + "§7/§c" + requiredTime));
                    }

                    if (ticksNear >= requiredTime) {
                        taskRef[0].cancel();
                        revivePlayer(downedPlayer);
                    }

                } else {
                    ticksNear = 0;

                    // 👀 Downed player sees waiting message
                    downedPlayer.sendActionBar(Component.text("§7Waiting for teammate..."));
                }
            }
        }, 0L, 20L); // Run every second
    }




    private void revivePlayer(Player player) {
//        BukkitTask task = downedPlayers.remove(player.getUniqueId());
//        if (task != null) task.cancel();
        player.setFoodLevel(6);
        player.setHealth(6);
        removeDownedState(player);
        Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " was revived");
 // 3 hearts
        player.sendMessage("§aYou were revived!");
    }



    private void setDownedState(Player player) {
        startRevivalCheck(player); // Start revival check task
        player.setInvulnerable(true);
        player.setSneaking(true);
        player.setFoodLevel(0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 60, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 60, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 60, 5, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 60, 255, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 60, 255, false, false));

        player.sendTitle(
                "§cYOU ARE DOWN! DON'T MOVE! WAIT FOR YOUR TEAM",
                "§7or Switch item in hand to give up early.",
                10, 60, 10
        );
        player.sendMessage(
                "§cYOU ARE DOWN! DON'T MOVE! WAIT FOR YOUR TEAM",
                "§7or Switch item in hand to give up early."

        );
    }


    private void removeDownedState(Player player) {
        UUID uuid = player.getUniqueId();

        // Cancel the scheduled death task if it exists
        BukkitTask task = downedPlayers.remove(uuid);
        if (task != null) {
            task.cancel();
        }
        downedPlayers.remove(player.getUniqueId());
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        player.removePotionEffect(PotionEffectType.GLOWING);
        player.removePotionEffect(PotionEffectType.WITHER);
        player.removePotionEffect(PotionEffectType.WEAKNESS);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.setInvulnerable(false);
        player.setSneaking(false);

    }



    // PvE Long Range Damage
    private void handleLongRangeDamage(Player attacker, LivingEntity target, EntityDamageByEntityEvent event, Location damagerLocation, Location damagedLocation, UserProfile damagerProfile, double damage) {
        double baseDamage = event.getDamage();
        ItemStack weapon = attacker.getInventory().getItemInMainHand();

        // Fire element check: Disable fire if not selected
        if (!damagerProfile.getSelectedElement().equalsIgnoreCase("fire")
                && (weapon.containsEnchantment(Enchantment.FLAME) || weapon.containsEnchantment(Enchantment.FIRE_ASPECT))) {
            event.getEntity().setFireTicks(0); // Cancel fire ticks
        }
        // Apply stats based on class for non-default players
        double damageWithStats = applyStatsToDamage(baseDamage+damage, damagerProfile, attacker, event);

        if (event.isCancelled()) return;







        // Apply extra health and set final damage
        double finalDamage = damageWithStats;

        // Alchemist class - Check if using thrown potion
        if (damagerProfile.getChosenClass().equalsIgnoreCase("alchemist") && event.getDamager() instanceof ThrownPotion) {


            effectsAbilityManager.applyAbility(damagerProfile, target, damagerLocation, damagedLocation);

        }

        // Archer class - Check if using bow
        if (damagerProfile.getChosenClass().equalsIgnoreCase("archer") && event.getDamager() instanceof Arrow arrow) {
            effectsAbilityManager.applyAbility(damagerProfile, target, damagerLocation, damagedLocation);

            if (!arrow.hasMetadata("WeaknessArrowBarrage") && !arrow.hasMetadata("FireArrowBarrage") && !arrow.hasMetadata("FreezeArrowBarrage")) {
                double archerDex = damagerProfile.getArcherClassInfo().getDex();
                double baseChance = 0.30; // 10% base chance
                double dexModifier = 0.00007;
                double totalChance = Math.min(1.0, baseChance + (archerDex * dexModifier));

// Generate a random value between 0.0 and 1.0
                if (Math.random() < totalChance) {
                    damageAbilityManager.applyDamageAbility(damagerProfile, target, damagerLocation, damagedLocation, finalDamage);
                }
            }


        }





        if (event.getEntity() instanceof Player victim) {
            UserProfile victimProfile = profileManager.getProfile(victim.getName());
            if (victimProfile==null) return;
            if (damagerProfile.getSelectedElement().equalsIgnoreCase("fire")) {finalDamage=finalDamage*1.1;}
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

        if (event.getDamager() instanceof  Arrow arrow && arrow.getShooter() instanceof Player player) {
            if (player.getInventory().getItemInMainHand().getType() == Material.BOW) {

                Location shooterLoc = attacker.getLocation();
                double distance = shooterLoc.distance(damagedLocation);
                if (distance <= 1) {
                    finalDamage *= 0.05;
                } else if (distance <= 2) {
                    finalDamage *= 0.1;
                } else if (distance <= 3) {
                    finalDamage *= 0.4;
                } else if (distance <= 4) {
                    finalDamage *= 0.7;
                } else if (distance <= 5) {
                    finalDamage *= 0.7;
                } else if (distance <= 6) {
                    finalDamage *= 0.7;
                } else if (distance <= 7) {
                    finalDamage *= 0.7;
                } else if (distance <= 8) {
                    finalDamage *= 0.8;
                } else if (distance <= 9) {
                    finalDamage *= 0.9;
                } else if (distance <= 10) {
                    finalDamage *= 1;
                } else if (distance <= 20 && damagerProfile.getChosenClass().equalsIgnoreCase("archer")) {
                    finalDamage = finalDamage * 1.5;
                } else if (distance <= 40 && damagerProfile.getChosenClass().equalsIgnoreCase("archer")) {
                    finalDamage = finalDamage * 2;
                } else if (distance > 41 && damagerProfile.getChosenClass().equalsIgnoreCase("archer")) {
                    finalDamage = finalDamage * 3;
                }
            }

        }


        if (hasBeenAirborneTooLong(attacker, 5000) && !isStandingOnSolidBlock(attacker)) {
            attacker.sendMessage("§e⚠ Damage is reduced because you have been in the air, on water, or on lava for too long.");
            finalDamage *= 0.1; // reduce to 10%
        }


        if (event.getEntity() instanceof Player player) {
            UserProfile playerProfile = profileManager.getProfile(player.getName());

            finalDamage *= playerProfile.getDurability() == 0 ? 2 : 1;
        }
        event.setDamage(finalDamage);
    }

    @EventHandler
    public void onArrowLaunch(ProjectileLaunchEvent event) {
        if (Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName().equals("world_rpg") || Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName().contains("world_labyrinth")) {
            if (event.getEntity() instanceof Arrow arrow) {
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            }
        }
    }

    // Method to apply stats to damage
    private double applyStatsToDamage(double calculatedDamage, UserProfile damagerProfile, Player player, EntityDamageByEntityEvent event) {

        if (!damagerProfile.isLoggedIn()) {
            event.setCancelled(true);
            return 0;
        }

        // Critical Hit System
        double critChance = damagerProfile.getCrit();
        double critDmgMultiplier = damagerProfile.getCritDmg();


        if (event.getEntity() instanceof Player player1) {
            UserProfile player1Profile = profileManager.getProfile(player1.getName());
            int p1Luk=player1Profile.getTempLuk();


            critChance = Math.max(0, critChance - (p1Luk * 0.0002));

        }

        boolean isCrit = Math.random() < critChance;

// Evasion system (PvP only, and only applies if it's NOT a crit)
        if (!isCrit && event.getEntity() instanceof Player defender) {
            UserProfile defenderProfile = profileManager.getProfile(defender.getName());
            double attackerDex = damagerProfile.getTempDex();
            double defenderAgi = defenderProfile.getTempAgi();

            double evadeChance = 50.0 + ((defenderAgi - attackerDex) * 0.25);
            evadeChance = Math.max(5.0, Math.min(evadeChance, 95.0));

            if (Math.random() * 100 < evadeChance) {
                // Evade successful
                event.setCancelled(true);
                spawnFloatingHologram(defender.getLocation(), "Evade", defender.getWorld(), "#ff0004");
                defender.getWorld().spawnParticle(Particle.SWEEP_ATTACK, defender.getLocation().add(0, 1, 0), 3);
                defender.getWorld().playSound(defender.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.6f, 1.2f);
                return 0; // damage is negated due to evasion
            }
        }

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
        return calculatedDamage;
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
            if (entity.getAttribute(Attribute.MOVEMENT_SPEED) != null) {
                double newSpeed = Objects.requireNonNull(entity.getAttribute(Attribute.MOVEMENT_SPEED)).getBaseValue() * 1.5; // Increase speed by 50%
                Objects.requireNonNull(entity.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(newSpeed);
            }

            // Set extra jump strength if applicable (for horses or similar entities that support it)
            if (entity.getAttribute(Attribute.JUMP_STRENGTH) != null) {
                double newJumpStrength = Objects.requireNonNull(entity.getAttribute(Attribute.JUMP_STRENGTH)).getBaseValue() * 3;
                Objects.requireNonNull(entity.getAttribute(Attribute.JUMP_STRENGTH)).setBaseValue(newJumpStrength);
            }

            // Set extra jump strength if applicable (for horses or similar entities that support it)
            if (entity.getAttribute(Attribute.SAFE_FALL_DISTANCE) != null) {
                double newSafeJumpDist = Objects.requireNonNull(entity.getAttribute(Attribute.JUMP_STRENGTH)).getBaseValue() * 3;
                Objects.requireNonNull(entity.getAttribute(Attribute.SAFE_FALL_DISTANCE)).setBaseValue(entity.getAttribute(Attribute.SAFE_FALL_DISTANCE).getBaseValue()*3);
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
            if (entity.getAttribute(Attribute.MOVEMENT_SPEED) != null) {
                double newSpeed = Objects.requireNonNull(entity.getAttribute(Attribute.MOVEMENT_SPEED)).getBaseValue() * 1.5; // Increase speed by 50%
                Objects.requireNonNull(entity.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(newSpeed);
            }

            // Set extra jump strength if applicable (for horses or similar entities that support it)
            if (entity.getAttribute(Attribute.JUMP_STRENGTH) != null) {
                double newJumpStrength = Objects.requireNonNull(entity.getAttribute(Attribute.JUMP_STRENGTH)).getBaseValue() * 3;
                Objects.requireNonNull(entity.getAttribute(Attribute.JUMP_STRENGTH)).setBaseValue(newJumpStrength);
            }

            // Set extra jump strength if applicable (for horses or similar entities that support it)
            if (entity.getAttribute(Attribute.SAFE_FALL_DISTANCE) != null) {
                double newSafeJumpDist = Objects.requireNonNull(entity.getAttribute(Attribute.JUMP_STRENGTH)).getBaseValue() * 3;
                Objects.requireNonNull(entity.getAttribute(Attribute.SAFE_FALL_DISTANCE)).setBaseValue(entity.getAttribute(Attribute.SAFE_FALL_DISTANCE).getBaseValue()*3);
            }
        }

        return extraHealth; // Use as basis for both health and extra damage
    }

    // Method to spawn the floating hologram above the monster
    private void spawnFloatingHologram(Location location, String text, World world, String hexColor) {
        ArmorStand armorStand = (ArmorStand) world.spawnEntity(location.clone().add(0, 1, 0), EntityType.ARMOR_STAND);

        // Convert hex color to Minecraft §x§R§R§G§G§B§B format
        String coloredText = applyHexColor(hexColor, text);

        armorStand.setCustomName(coloredText);
        armorStand.setCustomNameVisible(true);
        moveHologramUpwards(armorStand);
    }

    private String applyHexColor(String hex, String message) {
        if (!hex.startsWith("#") || hex.length() != 7) return message;

        StringBuilder colorBuilder = new StringBuilder("§x");
        for (char c : hex.substring(1).toCharArray()) {
            colorBuilder.append('§').append(c);
        }
        return colorBuilder + message;
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
        double totalHealth = entity.getHealth();
        double totalRemainingHealth = Math.max(0, totalHealth - damage);

        String healthIndicator = ChatColor.YELLOW + " [" + (int) totalRemainingHealth + "]"; // Format health

        String customName = entity.getCustomName();

        // Remove old health indicator if it exists
        if (customName != null && customName.contains("[")) {
            int healthIndex = customName.indexOf('[');
            customName = customName.substring(0, healthIndex).trim(); // Remove old health indicator
        }

        // Set the updated custom name with the new health indicator
        entity.setCustomName(customName + healthIndicator);
    }

    public static double getTotalStatsFromEquipment(Player player, String chosenClass) {
        double totalStats = 0.0;

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
                        double statValue = 0.0;
                        if (lore.startsWith("Strength: ")) {
                            statValue = parseLoreValue(lore) ;
                        } else if (lore.startsWith("Dexterity: ")) {
                            statValue = parseLoreValue(lore) ;
                        } else if (lore.startsWith("Intelligence: ")) {
                            statValue = parseLoreValue(lore) ;
                        } else if (lore.startsWith("Luck: ")) {
                            statValue = parseLoreValue(lore);
                        } else if (lore.startsWith("Agility: ")) {
                            statValue = parseLoreValue(lore) ;

                        }
                        else if (lore.startsWith("Vitality: ")) {
                            statValue = parseLoreValue(lore) ;
                        }
                        else if (lore.contains("StatDamage%: ")) {
                            statValue = parseLoreValue(lore);
                        }
                        else if (lore.contains("HP%: ")) {
                            statValue = parseLoreValue(lore);
                        }

                        // Apply class modifier if applicable
                        statValue = applyClassModifier(statValue, chosenClass, lore);
                        totalStats += statValue;
                    }
                }
            }
        }

        return totalStats;
    }

    private static double applyClassModifier(double value, String chosenClass, String loreType) {
        if (chosenClass.equalsIgnoreCase("alchemist") &&
                (loreType.startsWith("Strength: ") || loreType.startsWith("Dexterity: ") ||
                        loreType.startsWith("Intelligence: ") || loreType.startsWith("Agility: "))) {
            return Math.round(1.2 * value);
        } else if (chosenClass.equalsIgnoreCase("alchemist") &&
                (loreType.startsWith("Luck: ") || loreType.contains("StatDamage%: "))) {
            return Math.round(1.2 * value);
        } else {
            return value;
        }
    }

    public static double getTotalLuckFromEquipment(Player player, String chosenClass) {
        double totalLuck = 0.0;

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
                        if (lore.startsWith("Luck: ")) {
                            double luckValue = parseLoreValue(lore);

                            // Apply class modifier if applicable
                            if (chosenClass.equalsIgnoreCase("alchemist")) {
                                luckValue = Math.round(1.2 * luckValue);
                            }

                            totalLuck += luckValue;
                        }
                    }
                }
            }
        }

        return totalLuck;
    }

    private static int parseLoreValue(String lore) {
        try {
            return Integer.parseInt(lore.split(": ")[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // Handle parsing errors or missing values
            return 0;
        }
    }

    public boolean hasBeenAirborneTooLong(Player player, long thresholdMillis) {
        long last = lastGroundedTime.getOrDefault(player.getUniqueId(), 0L);
        return System.currentTimeMillis() - last > thresholdMillis;
    }

    private boolean isSupportedGround(Material type) {
        String name = type.name();
        return name.endsWith("SLAB")
                || name.endsWith("STAIRS")
                || name.endsWith("FENCE")
                || name.endsWith("WALL")
                || name.endsWith("CARPET")
                || name.endsWith("PRESSURE_PLATE")
                || name.endsWith("SNOW")
                || name.endsWith("PANE")
                || name.endsWith("CAULDRON")
                || name.endsWith("SCAFFOLDING");
    }

    private boolean isStandingOnSolidBlock(Player player) {
        Block blockBelow = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        Material type = blockBelow.getType();

        if (type.isSolid()) return true;

        return isSupportedGround(type);
    }


}



