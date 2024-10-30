package github.eremiyuh.rPGPlugin.methods;

import github.eremiyuh.rPGPlugin.profile.UserProfile;
import github.eremiyuh.rPGPlugin.perms.PlayerAbilityPerms;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class EffectsAbilityManager {

    private PlayerAbilityPerms abilityPerms;
    private EffectAbilities playerAbility;

    // Updated to map abilities with UserProfile
    private Map<Predicate<UserProfile>, EamConsumer<UserProfile, Location, LivingEntity>> abilityActions;

    public EffectsAbilityManager(JavaPlugin plugin) {
        this.abilityPerms = new PlayerAbilityPerms();
        this.playerAbility = new EffectAbilities(plugin);

        // Initialize the ability action map with UserProfile
        abilityActions = new HashMap<>();
        abilityActions.put(
                profile -> abilityPerms.canSummonFireUponHit(profile),
                (profile, location, target) -> playerAbility.burnTarget(profile, location, target)
        );
        abilityActions.put(
                profile -> abilityPerms.canFreezeOnHit(profile),
                (profile, location, target) -> playerAbility.freezeTarget(profile, location, target)
        );

        abilityActions.put(
                profile -> abilityPerms.canApplyNausea(profile),
                (profile, location, target) -> playerAbility.applyNausea(profile, location, target)
        );

        abilityActions.put(
                profile -> abilityPerms.canApplyWeakness(profile),
                (profile, location, target) -> playerAbility.applyWeakness(profile, location, target)
        );
    }

    // Apply ability based on the profile's abilities and victim's location
    public void applyAbility(UserProfile attackerProfile, LivingEntity victim, Location attackerLoc, Location victimLoc) {
        for (Map.Entry<Predicate<UserProfile>, EamConsumer<UserProfile, Location, LivingEntity>> entry : abilityActions.entrySet()) {
            Predicate<UserProfile> checkAbility = entry.getKey();
            EamConsumer<UserProfile, Location, LivingEntity> applyEffect = entry.getValue();

            // Apply effect based on profile
            if (checkAbility.test(attackerProfile)) {
                applyEffect.accept(attackerProfile, victimLoc, victim);
            }
        }
    }
}

// TriConsumer is a functional interface for methods that take 3 parameters
@FunctionalInterface
interface EamConsumer<T, U, V> {
    void accept(T t, U u, V v);
}
