package github.eremiyuh.rPGPlugin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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




                        for (LivingEntity target : event.getAffectedEntities()) {
                            if (target.hasMetadata("attackerList")) {
                                List<String> attackerList = (List<String>) target.getMetadata("attackerList").get(0).value();

                                String attackerName = thrower.getName();
                                assert attackerList != null;
                                if (!attackerList.contains(attackerName)) {
                                    attackerList.add(attackerName);
                                    target.setMetadata("attackerList", new FixedMetadataValue(plugin, attackerList)); // Update metadata
                                }
                            }

                            if (effect.getType() == PotionEffectType.SPEED) {

                                duration = (int) (intel/10);

                                int finalDuration = baseDuration + duration + 100;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, 1, true, true));
                            }

                            if (effect.getType() == PotionEffectType.JUMP_BOOST) {

                                duration = (int) (intel/10);

                                int finalDuration = baseDuration + duration + 100;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, 1, true, true));
                            }

                            if (effect.getType() == PotionEffectType.STRENGTH) {
                                intensity = (intel/6);
                                duration = (int) (intel/10);

                                int finalDuration = baseDuration + duration + 100;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, (int) intensity, true, true));
                            }

                            if (effect.getType() == PotionEffectType.POISON) {

                                duration = (int) (intel*10);

                                int finalDuration = baseDuration + duration + 200;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, 1, true, true));
                            }

                            if (effect.getType() == PotionEffectType.WITHER) {

                                duration = (int) (intel*10);

                                int finalDuration = baseDuration + duration + 200;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, 1, true, true));
                            }

                            if (effect.getType() == PotionEffectType.FIRE_RESISTANCE) {
                                duration = (int) (intel/10);
                                int finalDuration = baseDuration + duration + 100;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, baseIntensity, true, true));
                            }

                            if (effect.getType() == PotionEffectType.NIGHT_VISION) {

                                duration = (int) (intel/10);
                                int finalDuration = baseDuration + duration + 100;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, baseIntensity, true, true));
                            }

                            if (effect.getType() == PotionEffectType.SLOW_FALLING) {

                                duration = (int) (intel/10);
                                int finalDuration = baseDuration + duration + 100;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, baseIntensity, true, true));
                            }

                            if (effect.getType() == PotionEffectType.WATER_BREATHING) {
                                duration = (int) (intel/10);
                                int finalDuration = baseDuration + duration + 100;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, baseIntensity, true, true));
                            }

                            if (effect.getType() == PotionEffectType.INVISIBILITY) {

                                duration = (int) (intel/10);
                                int finalDuration = baseDuration + duration + 100;
                                target.addPotionEffect(new PotionEffect(effect.getType(), finalDuration, baseIntensity, true, true));
                            }

                            if (effect.getType() == PotionEffectType.RESISTANCE) {
                                target.addPotionEffect(new PotionEffect(effect.getType(), 100, 1, true, true));
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
                                    target.addPotionEffect(new PotionEffect((PotionEffectType.REGENERATION),100+(int)(intel*10),0,true,true));
                                }

                                if (target instanceof Player targetPlayer && isNegativeEffect) {
                                    UserProfile targetProfile = profileManager.getProfile(targetPlayer.getName());
                                    if (throwerProfile.getTeam().equals(targetProfile.getTeam()) && !throwerProfile.getTeam().equalsIgnoreCase("none")) {
                                        targetPlayer.removePotionEffect(effect.getType());
                                        target.addPotionEffect(new PotionEffect((PotionEffectType.REGENERATION),100+(int)(intel*10),0,true,true));
                                    }
                                    if ((!targetProfile.isPvpEnabled() || !throwerProfile.isPvpEnabled()) && (!throwerProfile.getTeam().equals(targetProfile.getTeam()) ||
                                                    throwerProfile.getTeam().equalsIgnoreCase("none")
                                            )) {
                                        targetPlayer.removePotionEffect(effect.getType());
                                        target.addPotionEffect(new PotionEffect((PotionEffectType.REGENERATION),100,0,true,true));
                                    }
                                }
                                if (target instanceof Player targetPlayer && isPositiveEffect && !throwerProfile.getTeam().equals("none")
                                ) {
                                    UserProfile targetProfile = profileManager.getProfile(targetPlayer.getName());
                                    if (!throwerProfile.getTeam().equals(targetProfile.getTeam())) {
                                        targetPlayer.removePotionEffect(effect.getType());
                                        if (targetProfile.isPvpEnabled() && throwerProfile.isPvpEnabled()) target.addPotionEffect(new PotionEffect((PotionEffectType.NAUSEA),200+(int)(intel/10),0,true,true));

                                    }
                                }

                                if (target instanceof Monster monster && isPositiveEffect ) {
                                    monster.removePotionEffect(effect.getType());
                                    target.addPotionEffect(new PotionEffect((PotionEffectType.WITHER),100+(int)(intel*10),1,true,true));
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
                        int baseDuration = effect.getDuration();

                        boolean isPositiveEffect = HEAL_EFFECTS.contains(effect.getType());

                        int intensity = (int) (intel / 4);


                        for (LivingEntity target : event.getAffectedEntities()) {

                            if (target.hasMetadata("attackerList")) {
                                List<String> attackerList = (List<String>) target.getMetadata("attackerList").get(0).value();

                                String attackerName = thrower.getName();
                                assert attackerList != null;
                                if (!attackerList.contains(attackerName)) {
                                    attackerList.add(attackerName);
                                    target.setMetadata("attackerList", new FixedMetadataValue(plugin, attackerList)); // Update metadata
                                }
                            }

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
                                if (target.getHealth()>0) {
                                    int baseHealing = 4; // Base healing per amplifier level
                                    double healAmount = baseHealing * (intensity+1); // Add 1 because intensity starts at 0

                                    double newHealth = Math.min(target.getHealth() + healAmount, Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
                                    target.setHealth(newHealth);
                                }
                            }
                        }
                    }
                }


            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private boolean isUndead(Entity entity) {
        return switch (entity.getType()) {
            case ZOMBIE, ZOMBIE_VILLAGER, HUSK, DROWNED,
                 SKELETON, STRAY, WITHER_SKELETON, WITHER,
                 PHANTOM, ZOMBIFIED_PIGLIN, SKELETON_HORSE -> true;
            default -> false;
        };
    }

}
