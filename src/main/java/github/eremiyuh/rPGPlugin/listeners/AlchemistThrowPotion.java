package github.eremiyuh.rPGPlugin.listeners;

import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PotionSplashEvent;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AlchemistThrowPotion implements Listener {
    private final PlayerProfileManager profileManager;

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

    public AlchemistThrowPotion(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
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


            if (throwerProfile != null && "alchemist".equalsIgnoreCase(throwerProfile.getChosenClass())&& throwerProfile.getSelectedSkill().equalsIgnoreCase("skill 2")) {
                double intel = throwerProfile.getAlchemistClassInfo() != null ? throwerProfile.getAlchemistClassInfo().getIntel() : 0;

                for (PotionEffect effect : event.getPotion().getEffects()) {
                    int baseIntensity = effect.getAmplifier();
                    int baseDuration = effect.getDuration();

                    boolean isPositiveEffect = POSITIVE_EFFECTS.contains(effect.getType());
                    boolean isNegativeEffect = NEGATIVE_EFFECTS.contains(effect.getType());

                    int intensity = 0;
                    int duration = 0;

                    if (isPositiveEffect) {
                        intensity = Math.min(INTENSITY_CAP, (int) (intel * 0.002));
                        duration = Math.min(INTENSITY_CAP, (int) (intel * 0.002));

                    } else if (isNegativeEffect) {
                        intensity = Math.min(INTENSITY_CAP, (int) (intel * 0.002));
                        duration = Math.min(INTENSITY_CAP, (int) (intel * 0.002));
                    }

                    // Apply the effect to targets
                    for (LivingEntity target : event.getAffectedEntities()) {
                        // Skip applying negative effects to the thrower
                        if (!target.getName().equals(thrower.getName()) && isNegativeEffect) {
                            target.sendMessage("thrower");
                            // Adjust intensity and duration and apply the effect to the target
                            int finalIntensity = baseIntensity * (1 + intensity); // Adjusted intensity
                            int finalDuration = baseDuration * (1 + duration);    // Adjusted duration
                            target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, finalIntensity, true, true));
                        }

                    }

                }
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
