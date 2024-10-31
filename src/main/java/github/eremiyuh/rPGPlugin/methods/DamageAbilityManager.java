package github.eremiyuh.rPGPlugin.methods;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import github.eremiyuh.rPGPlugin.perms.PlayerAbilityPerms;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class DamageAbilityManager {

    private final PlayerAbilityPerms abilityPerms;
    private final DamageAbilities playerAbility;

    // Map abilities with UserProfile, taking four parameters including damage
    private final Map<Predicate<UserProfile>, DamConsumer<UserProfile, Location, LivingEntity, Double>> abilityActions;

    public DamageAbilityManager(RPGPlugin plugin) {
        this.abilityPerms = new PlayerAbilityPerms();
        this.playerAbility = new DamageAbilities(plugin);

        // Initialize the ability action map with UserProfile
        abilityActions = new HashMap<>();
        abilityActions.put(
                profile -> abilityPerms.canSummonFireArrowBarrage(profile),
                (profile, location, target, damage) -> playerAbility.summonFireArrowBarrage(profile, location, target, damage)
        );
        abilityActions.put(
                profile -> abilityPerms.canSummonFreezeArrowBarrage(profile),
                (profile, location, target, damage) -> playerAbility.summonFreezeArrowBarrage(profile, location, target, damage)
        );

        abilityActions.put(
                profile -> abilityPerms.canSummonWeaknessArrowBarrage(profile),
                (profile, location, target, damage) -> playerAbility.summonWeaknessArrowBarrage(profile, location, target, damage)
        );

    }

    // Apply ability based on the profile's abilities and victim's location
    public void applyDamageAbility(UserProfile attackerProfile, LivingEntity victim, Location attackerLoc, Location victimLoc, double damage) {
        for (Map.Entry<Predicate<UserProfile>, DamConsumer<UserProfile, Location, LivingEntity, Double>> entry : abilityActions.entrySet()) {
            Predicate<UserProfile> checkAbility = entry.getKey();
            DamConsumer<UserProfile, Location, LivingEntity, Double> applyEffect = entry.getValue();

            // Apply effect based on profile
            if (checkAbility.test(attackerProfile)) {
                applyEffect.accept(attackerProfile, victimLoc, victim, damage);
            }
        }
    }
}

// QuadConsumer is a functional interface for methods that take 4 parameters
@FunctionalInterface
interface DamConsumer<T, U, V, W> {
    void accept(T t, U u, V v, W w);
}
