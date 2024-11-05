package github.eremiyuh.rPGPlugin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PotionSplashEvent;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.LivingEntity;

import java.util.*;

public class AlchemistThrowPotion implements Listener {
    private final PlayerProfileManager profileManager;
    private final JavaPlugin plugin;

    private static final int INTENSITY_CAP = 3;

    private static final Set<PotionEffectType> POSITIVE_EFFECTS = new HashSet<>() {{
        add(PotionEffectType.SPEED);
        add(PotionEffectType.JUMP_BOOST);
        add(PotionEffectType.NIGHT_VISION);
        add(PotionEffectType.INVISIBILITY);
        add(PotionEffectType.FIRE_RESISTANCE);
        add(PotionEffectType.SLOW_FALLING);
        add(PotionEffectType.WATER_BREATHING);
        add(PotionEffectType.REGENERATION);
        add(PotionEffectType.STRENGTH);
        add(PotionEffectType.LUCK);
        add(PotionEffectType.RESISTANCE);
    }};

    private static final Set<PotionEffectType> NEGATIVE_EFFECTS = new HashSet<>() {{
        add(PotionEffectType.WEAKNESS);
        add(PotionEffectType.POISON);
        add(PotionEffectType.SLOWNESS);
        add(PotionEffectType.WITHER);
        // Ensure these effects are appropriate for your design
    }};

    private static final Set<PotionEffectType> HEAL_EFFECTS = new HashSet<>() {{
        add(PotionEffectType.REGENERATION);
        add(PotionEffectType.INSTANT_HEALTH);
    }};

    public AlchemistThrowPotion(PlayerProfileManager profileManager, JavaPlugin plugin) {
        this.profileManager = profileManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onDoctorThrewPotion(PotionSplashEvent event) {

        if (!Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName().equals("world_rpg")) {
            return;
        }

        if (event.getEntity().getShooter() instanceof Player thrower) {
            UserProfile throwerProfile = profileManager.getProfile(thrower.getName());

            if (thrower.getAllowFlight()) {
                thrower.sendMessage("You are flying, don't waste potions");
                event.setCancelled(true);
                return;
            }

            if (throwerProfile != null && "alchemist".equalsIgnoreCase(throwerProfile.getChosenClass()) && throwerProfile.getSelectedSkill().equalsIgnoreCase("skill 2")) {
                double intel = throwerProfile.getAlchemistClassInfo() != null ? throwerProfile.getAlchemistClassInfo().getIntel() : 0;


                for (PotionEffect effect : event.getPotion().getEffects()) {
                    int baseIntensity = effect.getAmplifier();
                    int baseDuration = effect.getDuration();

                    boolean isPositiveEffect = POSITIVE_EFFECTS.contains(effect.getType());
                    boolean isNegativeEffect = NEGATIVE_EFFECTS.contains(effect.getType());

                    int intensity = 0;
                    int duration = 0;

                    if (isPositiveEffect) {
                        intensity = (int) (intel * 0.003);
                        duration = (int) (intel * 0.6);
                    } else if (isNegativeEffect) {
                        intensity = (int) (intel * 0.003);
                        duration = (int) (intel * 0.03);
                    } else {
                        continue;
                    }

                    for (LivingEntity target : event.getAffectedEntities()) {
                        thrower.sendMessage("Your name: " + thrower.getName() + ", Target: " + target.getName());

                        int finalIntensity = baseIntensity + intensity;
                        int finalDuration = baseDuration + duration;
                        target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, finalIntensity, true, true));


                    }
                }


                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (LivingEntity target : event.getAffectedEntities()) {
                        for (PotionEffect effect : event.getPotion().getEffects()) {
                            boolean isPositiveEffect = POSITIVE_EFFECTS.contains(effect.getType());
                            boolean isNegativeEffect = NEGATIVE_EFFECTS.contains(effect.getType());

                        if (thrower.getName().equals(target.getName()) && isNegativeEffect) {
                           target.removePotionEffect(effect.getType());
                        }

                        if (target instanceof Player targetPlayer && isNegativeEffect) {
                            UserProfile targetProfile = profileManager.getProfile(targetPlayer.getName());
                            if (throwerProfile.getTeam().equals(targetProfile.getTeam())) {
                                targetPlayer.removePotionEffect(effect.getType());
                            }
                        }
                            if (target instanceof Player targetPlayer && isPositiveEffect && !throwerProfile.getTeam().equals("none")) {
                                UserProfile targetProfile = profileManager.getProfile(targetPlayer.getName());
                                if (!throwerProfile.getTeam().equals(targetProfile.getTeam())) {
                                    targetPlayer.removePotionEffect(effect.getType());
                                }
                            }

                            if (target instanceof Monster monster && isPositiveEffect ) {
                                monster.removePotionEffect(effect.getType());
                            }



                        }

                    }
                    }, 1L);
            }

            if (throwerProfile != null && "alchemist".equalsIgnoreCase(throwerProfile.getChosenClass())&& throwerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
                double intel = throwerProfile.getAlchemistClassInfo() != null ? throwerProfile.getAlchemistClassInfo().getIntel() : 0;

                for (PotionEffect effect : event.getPotion().getEffects()) {
                    int baseIntensity = effect.getAmplifier();
                    int baseDuration = effect.getDuration();

                    boolean isPositiveEffect = HEAL_EFFECTS.contains(effect.getType());


                    int intensity = 0;
                    int duration = 0;

                    // Calculate intensity and duration adjustments based on effect type
                    if (isPositiveEffect) {
                        intensity = (int) (intel * 0.005);
                        duration = Math.min(INTENSITY_CAP, (int) (intel * 0.002));
                    }
                    // Apply the effect to targets
                    for (LivingEntity target : event.getAffectedEntities()) {
                        if (!(target instanceof Monster) && isPositiveEffect) {
                            // Adjust intensity and duration and apply the effect to the target
                            int finalIntensity = baseIntensity * (1 + intensity); // Adjusted intensity
                            int finalDuration = baseDuration * (1 + duration);    // Adjusted duration
                            target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, finalIntensity, true, true));
                        }
                    }
                }

            }


        }

    }
}
