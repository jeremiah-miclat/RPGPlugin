package github.eremiyuh.rPGPlugin.listeners;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Particle;

public class ArrowHitListener implements Listener {


    public ArrowHitListener(RPGPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow arrow) {


            if (arrow.hasMetadata("FreezeArrowBarrage")) {

                // Check if the arrow hit a living entity
                if (event.getHitEntity() instanceof LivingEntity target) {
                    // Apply a freeze effect (slowness) to the target
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 5)); // 100 ticks of slowness
                    spawnSnowParticles(target.getLocation());
                }
            }

            if (arrow.hasMetadata("WeaknessArrowBarrage")) {

                // Check if the arrow hit a living entity
                if (event.getHitEntity() instanceof LivingEntity target) {
                    // Apply a freeze effect (slowness) to the target
                    target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 5)); // 100 ticks of slowness
                    spawnWaterParticles(target.getLocation());
                }
            }
        }
    }



    private void spawnSnowParticles(Location location) {
        double radius = 1.5; // Radius for scattering
        int numberOfParticles = 30; // Increase the number for more particles

        for (int i = 0; i < numberOfParticles; i++) {
            // Scatter particles in a wider area
            double offsetX = (Math.random() * 2 - 1) * radius;
            double offsetY = (Math.random() * 2 - 1) * radius;
            double offsetZ = (Math.random() * 2 - 1) * radius;

            // Spawn the particle
            location.getWorld().spawnParticle(Particle.SNOWFLAKE,
                    location.getX() + offsetX,
                    location.getY() + offsetY,
                    location.getZ() + offsetZ,
                    1,
                    0, 0, 0, 0.1);
        }

    }

    private void spawnWaterParticles(Location location) {
        double radius = 1.5; // Radius for scattering
        int numberOfParticles = 30; // Increase the number for more particles

        for (int i = 0; i < numberOfParticles; i++) {
            // Scatter particles in a wider area
            double offsetX = (Math.random() * 2 - 1) * radius;
            double offsetY = (Math.random() * 2 - 1) * radius;
            double offsetZ = (Math.random() * 2 - 1) * radius;

            // Spawn the particle
            location.getWorld().spawnParticle(Particle.FALLING_WATER,
                    location.getX() + offsetX,
                    location.getY() + offsetY,
                    location.getZ() + offsetZ,
                    1,
                    0, 0, 0, 0.1);
        }

    }
}
