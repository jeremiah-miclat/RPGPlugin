package github.eremiyuh.rPGPlugin.methods;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.*;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class DamageAbilities {

    private RPGPlugin plugin;

    public DamageAbilities(RPGPlugin plugin) {
        this.plugin = plugin;
    }

    public void summonFireArrowBarrage(UserProfile profile, Location location, LivingEntity target, double arrowDamage) {

        double heightOffset = 6.0;
        Location targetLocation = target.getLocation();



        // Check if the entity has the fire resistance potion effect
        if (target.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {

            target.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);

            if (target instanceof Player) target.sendMessage("Your fire resistance effect has been removed!");

        }

        // Calculate the starting point for the 5x5 area
        double startX = targetLocation.getX() - 2.0;
        double startZ = targetLocation.getZ() - 2.0;

        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                // Calculate spawn location based on grid position, 6 blocks above the target
                Location spawnLocation = new Location(location.getWorld(), startX + x, targetLocation.getY() + heightOffset, startZ + z);
                Arrow arrow = location.getWorld().spawn(spawnLocation, Arrow.class);
                arrow.setShooter(Bukkit.getPlayer(profile.getPlayerName()));
                // Apply downward velocity
                arrow.setVelocity(new Vector(0, -.01, 0));
//                arrow.setDamage(arrowDamage);
                // Tag arrow with metadata for recognition in handleLongRangeDamage
                arrow.setMetadata("FireArrowBarrage", new FixedMetadataValue(plugin, true));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!arrow.isDead() && arrow.isValid()) {
                        arrow.remove();
                    }
                }, 160L);
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            }
        }
    }

    public void summonFreezeArrowBarrage(UserProfile profile, Location location, LivingEntity target, double arrowDamage) {

        double heightOffset = 6.0;
        Location targetLocation = target.getLocation();

        // Calculate the starting point for the 5x5 area
        double startX = targetLocation.getX() - 2.0; // 5 blocks wide (centered around target)
        double startZ = targetLocation.getZ() - 2.0; // 5 blocks deep

        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                // Calculate spawn location based on grid position, 6 blocks above the target
                Location spawnLocation = new Location(location.getWorld(), startX + x, targetLocation.getY() + heightOffset, startZ + z);
                Arrow arrow = location.getWorld().spawn(spawnLocation, Arrow.class);
                arrow.setShooter(Bukkit.getPlayer(profile.getPlayerName()));
                // Apply downward velocity
                arrow.setVelocity(new Vector(0, -.01, 0)); // Straight down
//                arrow.setDamage(arrowDamage);

                // Tag arrow with metadata for recognition in handleLongRangeDamage
                arrow.setMetadata("FreezeArrowBarrage", new FixedMetadataValue(plugin, true));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!arrow.isDead() && arrow.isValid()) {
                        arrow.remove();
                    }
                }, 160L);
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            }
        }

    }

    public void summonNauseaArrowBarrage(UserProfile profile, Location location, LivingEntity target, double arrowDamage) {

        double heightOffset = 6.0;
        Location targetLocation = target.getLocation();

        // Calculate the starting point for the 5x5 area
        double startX = targetLocation.getX() - 2.0; // 5 blocks wide (centered around target)
        double startZ = targetLocation.getZ() - 2.0; // 5 blocks deep

        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                // Calculate spawn location based on grid position, 6 blocks above the target
                Location spawnLocation = new Location(location.getWorld(), startX + x, targetLocation.getY() + heightOffset, startZ + z);
                Arrow arrow = location.getWorld().spawn(spawnLocation, Arrow.class);
                // Apply downward velocity
                arrow.setVelocity(new Vector(0, -.01, 0)); // Straight down
//                arrow.setDamage(arrowDamage);
                arrow.setShooter(Bukkit.getPlayer(profile.getPlayerName()));
                // Tag arrow with metadata for recognition in handleLongRangeDamage
                arrow.setMetadata("WeaknessArrowBarrage", new FixedMetadataValue(plugin, true));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (!arrow.isDead() && arrow.isValid()) {
                        arrow.remove();
                    }
                }, 160L);
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            }
        }

    }

}
