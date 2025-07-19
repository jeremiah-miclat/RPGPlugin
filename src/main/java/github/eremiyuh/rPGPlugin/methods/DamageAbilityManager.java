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

    // Ability mapping
    private final Map<Predicate<UserProfile>, DamConsumer<UserProfile, Location, LivingEntity, Double>> abilityActions;

    // Cooldown tracking (5 seconds per player name)
    private final Map<String, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_MILLIS = 5000;

    public DamageAbilityManager(RPGPlugin plugin) {
        this.abilityPerms = new PlayerAbilityPerms();
        this.playerAbility = new DamageAbilities(plugin);

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
                (profile, location, target, damage) -> playerAbility.summonNauseaArrowBarrage(profile, location, target, damage)
        );
    }

    public void applyDamageAbility(UserProfile attackerProfile, LivingEntity victim, Location attackerLoc, Location victimLoc, double damage) {
        if (attackerProfile == null || attackerProfile.getPlayerName() == null) return;

        String name = attackerProfile.getPlayerName();
        long now = System.currentTimeMillis();

        // Check cooldown
        if (cooldowns.containsKey(name)) {
            long lastUsed = cooldowns.get(name);
            if (now - lastUsed < COOLDOWN_MILLIS) {
                return; // still on cooldown
            }
        }

        // Not on cooldown, apply first matching ability
        for (Map.Entry<Predicate<UserProfile>, DamConsumer<UserProfile, Location, LivingEntity, Double>> entry : abilityActions.entrySet()) {
            if (entry.getKey().test(attackerProfile)) {
                entry.getValue().accept(attackerProfile, victimLoc, victim, damage);
                cooldowns.put(name, now); // apply cooldown
                break; // trigger only one ability
            }
        }
    }
}

@FunctionalInterface
interface DamConsumer<T, U, V, W> {
    void accept(T t, U u, V v, W w);
}
