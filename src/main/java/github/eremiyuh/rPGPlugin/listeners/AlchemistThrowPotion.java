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
        if (event.getEntity().getShooter() instanceof Player thrower) {
            UserProfile throwerProfile = profileManager.getProfile(thrower.getName());

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
                        intensity = Math.min(INTENSITY_CAP, (int) (intel * 0.003));
                        duration = Math.min(INTENSITY_CAP, (int) (intel * 0.003));

                    } else if (isNegativeEffect) {
                        intensity = Math.min(INTENSITY_CAP, (int) (intel * 0.002));
                        duration = Math.min(INTENSITY_CAP, (int) (intel * 0.002));
                    }

                    // Apply the effect to targets
                    for (LivingEntity target : event.getAffectedEntities()) {
                        if (target != null) {
                            int finalIntensity = baseIntensity * intensity; // Add to the original intensity
                            int finalDuration = baseDuration * duration; // Add to the original duration
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

                    if (isPositiveEffect) {
                        intensity = Math.min(INTENSITY_CAP, (int) (intel * 0.003));
                        duration = Math.min(INTENSITY_CAP, (int) (intel * 0.003));

                    }

                    for (LivingEntity target : event.getAffectedEntities()) {
                        int finalIntensity = baseIntensity * intensity; // Add to the original intensity
                        int finalDuration = baseDuration * duration; // Add to the original duration

                        if (target instanceof Player targetPlayer) {
                           if (target.getName().equals(thrower.getName())) {
                               target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, finalIntensity, true, true));
                               thrower.sendMessage("healed self");
                           }
                           UserProfile targetPlayerProfile = profileManager.getProfile(targetPlayer.getName());
                           String throwerTeam = throwerProfile.getTeam();
                           String targetTeam = targetPlayerProfile.getTeam();

                           if (!Objects.equals(throwerTeam, "none") && throwerTeam.equals(targetTeam)) {
                               target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, finalIntensity, true, true));
                               thrower.sendMessage("healed target teammate");
                           }
                        }


                        if (!(target instanceof Player)) {
                            target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, finalIntensity, true, true));
                            thrower.sendMessage("healed target");
                        }
                    }
                }
                event.setCancelled(true);
            }


        }
    }
}
