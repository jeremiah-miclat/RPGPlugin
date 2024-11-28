package github.eremiyuh.rPGPlugin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPotionEffectEvent;
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

    private static final int INTENSITY_CAP = 10;

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
        if (event.getEntity().getShooter() instanceof Player thrower  && thrower.getAllowFlight()) {
            thrower.sendMessage("Disable fly");
            event.setCancelled(true);
            return;
        }


        if (!Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName().equals("world_rpg") && !Objects.requireNonNull(event.getEntity().getLocation().getWorld()).getName().contains("labyrinth")) {
            return;
        }
        try {
            if (event.getEntity().getShooter() instanceof Player thrower) {
                UserProfile throwerProfile = profileManager.getProfile(thrower.getName());



                if (throwerProfile != null && "alchemist".equalsIgnoreCase(throwerProfile.getChosenClass()) && throwerProfile.getSelectedSkill().equalsIgnoreCase("skill 2")) {
                    double intel = throwerProfile.getAlchemistClassInfo() != null ? throwerProfile.getAlchemistClassInfo().getIntel() : 0;

                    for (PotionEffect effect : event.getPotion().getEffects()) {
                        int baseIntensity = effect.getAmplifier();
                        int baseDuration = effect.getDuration();

                        boolean isPositiveEffect = POSITIVE_EFFECTS.contains(effect.getType());
                        boolean isNegativeEffect = NEGATIVE_EFFECTS.contains(effect.getType());

                        double intensity = 0;
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

                            if (effect.getType() == PotionEffectType.SPEED) {
                                intensity = intensity/10;
                                duration /= 100;
                                int finalIntensity = (int) (baseIntensity + intensity);
                                int finalDuration = baseDuration + duration;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, finalIntensity, true, true));
                            }

                            if (effect.getType() == PotionEffectType.JUMP_BOOST) {
                                intensity = 0;
                                duration /= 100;
                                int finalIntensity = (int) (baseIntensity + intensity);
                                int finalDuration = baseDuration + duration;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, finalIntensity, true, true));
                            }

                            if (effect.getType() == PotionEffectType.STRENGTH) {
                                intensity = intensity*1;
                                duration /= 100;
                                int finalIntensity = (int) (baseIntensity + intensity);
                                int finalDuration = baseDuration + duration;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, finalIntensity, true, true));
                            }

                            if (effect.getType() == PotionEffectType.POISON) {
                                intensity = 0;
                                int finalIntensity = (int) (baseIntensity + intensity);
                                int finalDuration = baseDuration + duration;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, finalIntensity, true, true));
                            }

                            if (effect.getType() == PotionEffectType.WITHER) {
                                intensity = 0;
                                int finalIntensity = (int) (baseIntensity + intensity);
                                int finalDuration = baseDuration + duration;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, finalIntensity, true, true));
                            }

                            if (effect.getType() == PotionEffectType.FIRE_RESISTANCE) {
                                int finalIntensity = (int) (baseIntensity + intensity);
                                int finalDuration = baseDuration + duration;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, finalIntensity, true, true));
                            }

                            if (effect.getType() == PotionEffectType.NIGHT_VISION) {
                                int finalIntensity = (int) (baseIntensity + intensity);
                                int finalDuration = baseDuration + duration;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, finalIntensity, true, true));
                            }

                            if (effect.getType() == PotionEffectType.SLOW_FALLING) {
                                int finalIntensity = (int) (baseIntensity + intensity);
                                int finalDuration = baseDuration + duration;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, finalIntensity, true, true));
                            }

                            if (effect.getType() == PotionEffectType.WATER_BREATHING) {
                                int finalIntensity = (int) (baseIntensity + intensity);
                                int finalDuration = baseDuration + duration;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, finalIntensity, true, true));
                            }

                            if (effect.getType() == PotionEffectType.INVISIBILITY) {
                                int finalIntensity = (int) (baseIntensity + intensity);
                                int finalDuration = baseDuration + duration;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, finalIntensity, true, true));
                            }




                        }
                    }


                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        for (LivingEntity target : event.getAffectedEntities()) {
                            for (PotionEffect effect : event.getPotion().getEffects()) {
                                boolean isPositiveEffect = POSITIVE_EFFECTS.contains(effect.getType());
                                boolean isNegativeEffect = NEGATIVE_EFFECTS.contains(effect.getType());

                                if (thrower.getName().equals(target.getName()) && isNegativeEffect) {
                                    target.removePotionEffect(effect.getType());
                                    target.addPotionEffect(new PotionEffect((PotionEffectType.REGENERATION),60,0,true,true));
                                }

                                if (target instanceof Player targetPlayer && isNegativeEffect) {
                                    UserProfile targetProfile = profileManager.getProfile(targetPlayer.getName());
                                    if (throwerProfile.getTeam().equals(targetProfile.getTeam()) && !throwerProfile.getTeam().equalsIgnoreCase("none")) {
                                        targetPlayer.removePotionEffect(effect.getType());
                                        target.addPotionEffect(new PotionEffect((PotionEffectType.REGENERATION),60,0,true,true));
                                    }
                                }
                                if (target instanceof Player targetPlayer && isPositiveEffect && !throwerProfile.getTeam().equals("none")) {
                                    UserProfile targetProfile = profileManager.getProfile(targetPlayer.getName());
                                    if (!throwerProfile.getTeam().equals(targetProfile.getTeam())) {
                                        targetPlayer.removePotionEffect(effect.getType());
                                        if (targetProfile.isPvpEnabled() && throwerProfile.isPvpEnabled()) target.addPotionEffect(new PotionEffect((PotionEffectType.NAUSEA),200,0,true,true));

                                    }
                                }

                                if (target instanceof Monster monster && isPositiveEffect ) {
                                    monster.removePotionEffect(effect.getType());
                                    target.addPotionEffect(new PotionEffect((PotionEffectType.WITHER),60,0,true,true));
                                }



                            }

                        }
                    }, 1L);
                }

//            if (throwerProfile != null && "alchemist".equalsIgnoreCase(throwerProfile.getChosenClass())&& throwerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
//                double intel = throwerProfile.getAlchemistClassInfo() != null ? throwerProfile.getAlchemistClassInfo().getIntel() : 0;
//
//                for (PotionEffect effect : event.getPotion().getEffects()) {
//                    int baseIntensity = effect.getAmplifier();
//                    int baseDuration = effect.getDuration();
//
//                    boolean isPositiveEffect = HEAL_EFFECTS.contains(effect.getType());
//
//
//                    int intensity = 0;
//                    int duration = 0;
//
//                    // Calculate intensity and duration adjustments based on effect type
//                    if (isPositiveEffect) {
//                        intensity = (int) (intel * 0.005);
//                        duration = Math.min(INTENSITY_CAP, (int) (intel * 0.002));
//                    }
//                    // Apply the effect to targets
//                    for (LivingEntity target : event.getAffectedEntities()) {
//                        if (!(target instanceof Player player)) continue;
//                        UserProfile playerProfile = profileManager.getProfile(player.getName());
//                        if (isPositiveEffect && player.getName().equals(thrower.getName())) {
//                            // Adjust intensity and duration and apply the effect to the target
//                            int finalIntensity = baseIntensity * (1 + intensity); // Adjusted intensity
//                            int finalDuration = baseDuration * (1 + duration);    // Adjusted duration
//                            target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, finalIntensity, true, true));
//                        }
//
//                        if (!(target instanceof Monster) && isPositiveEffect && playerProfile != null && !playerProfile.getTeam().equals("none") && playerProfile.getTeam().equalsIgnoreCase(thrower.getName())) {
//                            int finalIntensity = baseIntensity * (1 + intensity); // Adjusted intensity
//                            int finalDuration = baseDuration * (1 + duration);    // Adjusted duration
//                            target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, finalIntensity, true, true));
//                        }
//
//                    }
//                }
//
//            }
                if (throwerProfile != null && "alchemist".equalsIgnoreCase(throwerProfile.getChosenClass()) && throwerProfile.getSelectedSkill().equalsIgnoreCase("skill 3")) {
                    double intel = throwerProfile.getAlchemistClassInfo() != null ? throwerProfile.getAlchemistClassInfo().getIntel() : 0;

                    for (PotionEffect effect : event.getPotion().getEffects()) {
                        int baseIntensity = effect.getAmplifier();
                        int baseDuration = effect.getDuration();

                        boolean isPositiveEffect = HEAL_EFFECTS.contains(effect.getType());

                        int intensity = Math.max(0, (int) (intel * 0.0005));
                        int duration = Math.max(0, Math.min(INTENSITY_CAP, (int) (intel * 0.002)));

                        for (LivingEntity target : event.getAffectedEntities()) {
                            if (!(target instanceof Player)) continue;

                            Player player = (Player) target;
                            UserProfile playerProfile = profileManager.getProfile(player.getName());

                            boolean applyEffect = false;

                            // Apply effect if the player is the thrower or in the same team
                            if (isPositiveEffect && player.getName().equals(thrower.getName())) {
                                applyEffect = true;
                            }

                            if (isPositiveEffect && playerProfile != null && !playerProfile.getTeam().equals("none") && playerProfile.getTeam().equalsIgnoreCase(throwerProfile.getTeam())) {
                                applyEffect = true;
                            }

                            // Apply the effect if conditions are met
                            if (applyEffect) {
                                int finalIntensity = baseIntensity + (int)(baseIntensity * intensity);
                                int finalDuration = baseDuration + (int)(baseDuration * duration);
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, finalIntensity, true, true));
                            }
                        }
                    }
                }


            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
