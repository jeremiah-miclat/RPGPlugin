package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.inventory.ItemStack;
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
    public void onRaidEvent(RaidTriggerEvent event) {
        if (!event.getWorld().getName().contains("world_rpg")) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onMobPickUpItem(EntityPickupItemEvent event) {
        String world = Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName();
        if (!world.contains("_rpg")) {
            return;
        }

        if (!(event.getEntity() instanceof Monster)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void armorStandSpawn(EntitySpawnEvent event) {
        String world = Objects.requireNonNull(event.getLocation().getWorld()).getName();

        if (!world.contains("_rpg")) {
            return;
        }
        if (!(event.getEntity() instanceof ArmorStand)) {
            return;
        }

        if (event.getEntity() instanceof  ArmorStand armorStand) {
            armorStand.setCollidable(false);
            armorStand.setInvisible(true);
            armorStand.setGravity(false);
            armorStand.setInvulnerable(true);
            armorStand.setPersistent(false);
            armorStand.setMarker(true);
        }

    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {

        String world = Objects.requireNonNull(event.getLocation().getWorld()).getName();

        if (!world.contains("_rpg")) {
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

        if (event.getEntity() instanceof Pillager villager) {
            String name = villager.getCustomName();
            if (name != null && name.contains("tester-player")) {
                villager.setCustomName("Dummy - Players");
                villager.setCustomNameVisible(true);
                Objects.requireNonNull(villager.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(10000000);
                villager.setHealth(10000000);
                villager.setAI(false);
                villager.setRemoveWhenFarAway(false);
                // Create full Netherite armor with Prot IV
                ItemStack helmet = new ItemStack(Material.NETHERITE_HELMET);
                ItemStack chestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
                ItemStack leggings = new ItemStack(Material.NETHERITE_LEGGINGS);
                ItemStack boots = new ItemStack(Material.NETHERITE_BOOTS);

                // Add Protection IV to each
                helmet.addEnchantment(Enchantment.PROTECTION, 4);
                chestplate.addEnchantment(Enchantment.PROTECTION, 4);
                leggings.addEnchantment(Enchantment.PROTECTION, 4);
                boots.addEnchantment(Enchantment.PROTECTION, 4);

                // Give the armor
                villager.getEquipment().setHelmet(helmet);
                villager.getEquipment().setChestplate(chestplate);
                villager.getEquipment().setLeggings(leggings);
                villager.getEquipment().setBoots(boots);

                // Prevent armor from dropping when it dies
                villager.getEquipment().setHelmetDropChance(0f);
                villager.getEquipment().setChestplateDropChance(0f);
                villager.getEquipment().setLeggingsDropChance(0f);
                villager.getEquipment().setBootsDropChance(0f);
                return;
            }
            if (name != null && name.contains("tester-monster")) {
                villager.setCustomName("Dummy - Monsters");
                villager.setCustomNameVisible(true);
                Objects.requireNonNull(villager.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(10000000);
                villager.setHealth(10000000);
                villager.setAI(false);
                villager.setRemoveWhenFarAway(false);
                return;
            }
        }

        if (event.getEntity() instanceof Monster monster) {
            initializeExtraAttributes(monster);

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
        if (entity instanceof Monster) {
            Location location = entity.getLocation();
            calculateExtraAttributes(location, entity);

            if (entity instanceof Pillager pillager)  {
                if (pillager.getCustomName().contains("Dummy")) return;
            }

            setHealthIndicator(entity);
        }
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

        if (entity instanceof PigZombie wither) {
            wither.setPersistent(true);
            entity.setRemoveWhenFarAway(false);
        }

        if (entity instanceof PiglinBrute wither) {
            wither.setPersistent(true);
            entity.setRemoveWhenFarAway(false);
        }

        if (entity instanceof Evoker) {
            entity.setPersistent(true);
            entity.setRemoveWhenFarAway(false);
        }

        if (entity instanceof Vindicator) {
            entity.setPersistent(false);
            entity.setRemoveWhenFarAway(true);
        }


        if (entity instanceof Ravager) {
            entity.setPersistent(true);
            entity.setRemoveWhenFarAway(false);
        }

        int lvl = (int) (Math.floor(maxCoord) / 100);
        if (entity.getWorld().getName().contains("_br")) {
            String worldName = entity.getWorld().getName();
            String[] parts = worldName.split("_");
            lvl = Integer.parseInt(parts[parts.length - 1]);
        }
        String normalName = ChatColor.GREEN + "Lvl " + lvl + " " + entity.getType().name();
        entity.setCustomName(normalName);
        double customMaxHealth =entity.getHealth() + lvl+20;


        double extraHealth = lvl*5+entity.getHealth();

        if (lvl > 199) {
            int extraMultiplier = ((lvl - 200) / 10) * 20; // increase 1000 every 10 levels above 199
            int multiplier = 20 + extraMultiplier;
            extraHealth += lvl * multiplier;
        }

        double customDamage = (lvl*.5) + Objects.requireNonNull(entity.getAttribute(Attribute.ATTACK_DAMAGE)).getValue();

        if (lvl>199) {
            customDamage = (lvl*.7)+ Objects.requireNonNull(entity.getAttribute(Attribute.ATTACK_DAMAGE)).getValue();
        }

        // First, check for the purple world boss (0.1% chance)
        if (Math.random() < .0003 || entity instanceof PiglinBrute || entity instanceof PigZombie || entity instanceof Warden || entity instanceof Wither || entity instanceof ElderGuardian || entity instanceof Ravager || entity instanceof Evoker) { //0.0003
            extraHealth += lvl * 800;
            if (lvl > 199) {
                int extraMultiplier = ((lvl - 200) / 10) * 1000; // increase 1000 every 10 levels above 199
                int multiplier = 1200 + extraMultiplier;
                extraHealth += lvl * multiplier;
            }
            setBossAttributes(entity, maxCoord, "World Boss", ChatColor.DARK_PURPLE);
            entity.setGlowing(true);
            Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(extraHealth);
            entity.setHealth(extraHealth);

            if (!(entity instanceof Warden)) {
                Objects.requireNonNull(entity.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(customDamage+(customDamage*.5));
            } else {
                Objects.requireNonNull(entity.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(customDamage);
            }
            entity.setPersistent(true);
            entity.setCustomNameVisible(true);
            entity.addPotionEffect(new PotionEffect((PotionEffectType.REGENERATION),Integer.MAX_VALUE,1,true,true));
            return;
        }

        // Only check for red boss (1% chance) if not already a purple boss
        if (Math.random() < .003 ) { //0.003
            extraHealth += lvl * 100;
            if (lvl > 199) {
                int extraMultiplier = ((lvl - 200) / 10) * 200; // increase 1000 every 10 levels above 199
                int multiplier = 200 + extraMultiplier;
                extraHealth += lvl * multiplier;
            }
            setBossAttributes(entity, maxCoord, "Boss", ChatColor.RED);
            Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(extraHealth);
            entity.setHealth(extraHealth);
            Objects.requireNonNull(entity.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(customDamage+(customDamage*.2));

            entity.setCustomNameVisible(true);
            return;
        }


        if (Math.random() < 0.03 || entity instanceof Vindicator) {
            extraHealth += lvl * 10;
            if (lvl > 199) {
                int extraMultiplier = ((lvl - 200) / 10) * 100; // increase 1000 every 10 levels above 199
                int multiplier = 100 + extraMultiplier;
                extraHealth += lvl * multiplier;
            }
            setBossAttributes(entity, maxCoord, "Leader", ChatColor.YELLOW);
            Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(extraHealth);
            entity.setHealth(extraHealth);
            Objects.requireNonNull(entity.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(customDamage+(customDamage*.1) );
            entity.setCustomNameVisible(true);
            return;
        }

        // If neither of the boss conditions are met, set standard attributes
        Objects.requireNonNull(entity.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(extraHealth);
        entity.setHealth(extraHealth);

        if (entity instanceof Skeleton || entity instanceof Stray || entity instanceof Pillager) {
            customDamage+=customDamage;
        }

        if (entity instanceof Spider) {
            customDamage*=.8;
        }

        Objects.requireNonNull(entity.getAttribute(Attribute.ATTACK_DAMAGE)).setBaseValue(customDamage);

    }

    private void setBossAttributes(LivingEntity entity, double maxCoord, String type, ChatColor color) {
        int lvl = (int) (Math.floor(maxCoord) / 100);
        if (entity.getWorld().getName().contains("_br")) {
            String worldName = entity.getWorld().getName();
            String[] parts = worldName.split("_");
            lvl = Integer.parseInt(parts[parts.length - 1]);
        }


        String bossName = color + "Lvl " + lvl + " " + type + " " + entity.getType().name();
        entity.setCustomName(bossName);


        // Update health indicator
        setHealthIndicator(entity);

        // Calculate speed and jump multipliers based on maxCoord
        double speedMultiplier =  (0.000075 * maxCoord); // Continuous increase for speed
        double jumpMultiplier = 1 + (0.00015 * maxCoord);  // Continuous increase for jump

        // Set extra movement speed if the attribute is available
//        if (entity.getAttribute(Attribute.MOVEMENT_SPEED) != null) {
//            double baseSpeed = Objects.requireNonNull(entity.getAttribute(Attribute.MOVEMENT_SPEED)).getBaseValue();
//            double newSpeed = baseSpeed * (1+ (speedMultiplier/4)); // Apply the calculated multiplier
//            Objects.requireNonNull(entity.getAttribute(Attribute.MOVEMENT_SPEED)).setBaseValue(newSpeed+.02);
//        }

        // Set extra jump strength if applicable
//        if (entity.getAttribute(Attribute.JUMP_STRENGTH) != null) {
//            double baseJumpStrength = Objects.requireNonNull(entity.getAttribute(Attribute.JUMP_STRENGTH)).getBaseValue();
//            double newJumpStrength = baseJumpStrength * jumpMultiplier; // Apply the calculated multiplier
//            Objects.requireNonNull(entity.getAttribute(Attribute.JUMP_STRENGTH)).setBaseValue(newJumpStrength);
//        }

        // Set extra safe fall distance if applicable
//        if (entity.getAttribute(Attribute.SAFE_FALL_DISTANCE) != null) {
//            double baseSafeFallDistance = Objects.requireNonNull(entity.getAttribute(Attribute.SAFE_FALL_DISTANCE)).getBaseValue();
//            Objects.requireNonNull(entity.getAttribute(Attribute.SAFE_FALL_DISTANCE)).setBaseValue(baseSafeFallDistance * jumpMultiplier + 1);
//        }
    }

    @EventHandler
    public void onEvokerSpell(EntitySpellCastEvent event) {
        if (event.getEntity() instanceof Evoker evoker && evoker.getWorld().getName().contains("rpg"))  {
            if (event.getSpell() == Spellcaster.Spell.SUMMON_VEX) {
                event.setCancelled(true); // Cancel only Vex summon
            }
        }
    }

    @EventHandler
    public void transformCanceller(EntityTransformEvent event) {
        if (event.getEntity().getWorld().getName().contains("_rpg")) {
            event.setCancelled(true);
        }
    }


}
