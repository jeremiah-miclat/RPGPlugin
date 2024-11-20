package github.eremiyuh.rPGPlugin.methods;

import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectAbilities {

    private JavaPlugin plugin;

    public EffectAbilities(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // Method to summon fire
    public void summonFireOnTarget(UserProfile profile, Location location, LivingEntity target) {
        Block block = location.getBlock();
        if (block.getType() == Material.AIR || block.getType() == Material.WATER || block.getType() == Material.POWDER_SNOW || block.getType() == Material.FIRE) {
            block.setType(Material.FIRE);


            int fireDuration = 100;
            target.setFireTicks(fireDuration);

            target.getWorld().spawnParticle(Particle.FLAME, target.getLocation(), 30, 0.5, 1, 0.5);

            // Remove fire block after 5 seconds
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (block.getType() == Material.FIRE) {
                    block.setType(Material.AIR);
                }
            }, 100L);
        }
    }

    // Method to burn target
    public void burnTarget(UserProfile profile, Location location, LivingEntity target) {
        int userInt = 0;
        String userClass = profile.getChosenClass();

        if (userClass.equalsIgnoreCase("archer")) userInt= profile.getArcherClassInfo().getIntel();
        if (userClass.equalsIgnoreCase("alchemist")) userInt= profile.getArcherClassInfo().getIntel();
        if (userClass.equalsIgnoreCase("swordsman")) userInt= profile.getSwordsmanClassInfo().getIntel();


        int fireDuration =  100 + (int)(userInt *.02); // Extend fire duration based on profile buff

        target.setFireTicks(fireDuration);

        Location targetLocation = target.getLocation();
        World world = target.getWorld();


        for (int i = 0; i < 10; i++) {
            double xOffset = (Math.random() - 0.5) * 1.5;
            double zOffset = (Math.random() - 0.5) * 1.5;
            double yVelocity = Math.random() * 0.5 + 0.5;

            world.spawnParticle(
                    Particle.FLAME,
                    targetLocation.clone().add(xOffset, 0, zOffset),
                    1, // Particle count (1 at a time)
                    0, yVelocity, 0, // Upward velocity to make them rise
                    0.05 // Speed of the particle
            );
        }

    }

    // Method to freeze target
    public void freezeTarget(UserProfile profile, Location location, LivingEntity target) {
        int slownessDuration = 100; // Longer freeze if ice buff

        int userInt = 0;
        String userClass = profile.getChosenClass();

        if (userClass.equalsIgnoreCase("archer")) userInt= profile.getArcherClassInfo().getIntel();
        if (userClass.equalsIgnoreCase("alchemist")) userInt= profile.getArcherClassInfo().getIntel();
        if (userClass.equalsIgnoreCase("swordsman")) userInt= profile.getSwordsmanClassInfo().getIntel();


        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, slownessDuration+ (int)(userInt *.02), 1));

        target.getWorld().spawnParticle(Particle.SNOWFLAKE, target.getLocation(), 10, 0.5, .5, 0.5);
    }

    // Method to apply nausea effect
    public void applyNausea(UserProfile profile, Location sourceLocation, LivingEntity target) {
        int userInt = 0;
        String userClass = profile.getChosenClass();

        if (userClass.equalsIgnoreCase("archer")) userInt= profile.getArcherClassInfo().getIntel();
        if (userClass.equalsIgnoreCase("alchemist")) userInt= profile.getArcherClassInfo().getIntel();
        if (userClass.equalsIgnoreCase("swordsman")) userInt= profile.getSwordsmanClassInfo().getIntel();
        int weaknessDuration = 100+ (int)(userInt *.02); // Duration of the nausea effect in ticks (5 seconds)

        Location targetLocation = target.getLocation();
        World world = target.getWorld();
        target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, weaknessDuration, 1));

        for (int i = 0; i < 50; i++) {
            double xOffset = (Math.random() - 0.5) * 1.5;
            double zOffset = (Math.random() - 0.5) * 1.5;
            double yVelocity = Math.random() * 0.5 + 0.5;

            // Spawn different water-like particles for the splash effect
            world.spawnParticle(
                    Particle.DRIPPING_WATER,
                    targetLocation.clone().add(xOffset, 0, zOffset),
                    1, // Particle count (1 at a time)
                    0, yVelocity, 0, // Upward velocity to make them rise
                    0.05 // Speed of the particle
            );
        }

        // Water drip particles falling down after the burst (more subtle effect)
        for (int i = 0; i < 20; i++) {
            double xOffset = (Math.random() - 0.5);
            double zOffset = (Math.random() - 0.5);
            double yVelocity = -0.1; // Downward slow fall effect

            world.spawnParticle(
                    Particle.DRIPPING_WATER,
                    targetLocation.clone().add(xOffset, 1, zOffset), // Start higher up, like falling water
                    1,
                    0, yVelocity, 0,
                    0.01
            );
        }
    }

    // Method to apply weakness effect
    public void applyWeakness(UserProfile profile, Location sourceLocation, LivingEntity target) {



        int userInt = 0;
        String userClass = profile.getChosenClass();

        if (userClass.equalsIgnoreCase("archer")) userInt= profile.getArcherClassInfo().getIntel();
        if (userClass.equalsIgnoreCase("alchemist")) userInt= profile.getArcherClassInfo().getIntel();
        if (userClass.equalsIgnoreCase("swordsman")) userInt= profile.getSwordsmanClassInfo().getIntel();
        int weaknessDuration = 100+ (int)(userInt *.02); // Duration of the nausea effect in ticks (5 seconds)

        Location targetLocation = target.getLocation();
        World world = target.getWorld();
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, weaknessDuration, 1));

        for (int i = 0; i < 50; i++) {
            double xOffset = (Math.random() - 0.5) * 1.5;
            double zOffset = (Math.random() - 0.5) * 1.5;
            double yVelocity = Math.random() * 0.5 + 0.5;

            // Spawn different water-like particles for the splash effect
            world.spawnParticle(
                    Particle.DRIPPING_WATER,
                    targetLocation.clone().add(xOffset, 0, zOffset),
                    1, // Particle count (1 at a time)
                    0, yVelocity, 0, // Upward velocity to make them rise
                    0.05 // Speed of the particle
            );
        }

        // Water drip particles falling down after the burst (more subtle effect)
        for (int i = 0; i < 20; i++) {
            double xOffset = (Math.random() - 0.5);
            double zOffset = (Math.random() - 0.5);
            double yVelocity = -0.1; // Downward slow fall effect

            world.spawnParticle(
                    Particle.DRIPPING_WATER,
                    targetLocation.clone().add(xOffset, 1, zOffset), // Start higher up, like falling water
                    1,
                    0, yVelocity, 0,
                    0.01
            );
        }
    }



}
