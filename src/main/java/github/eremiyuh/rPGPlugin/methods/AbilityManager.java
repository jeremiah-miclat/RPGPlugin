package github.eremiyuh.rPGPlugin.methods;

import github.eremiyuh.rPGPlugin.profile.UserProfile;
import github.eremiyuh.rPGPlugin.perms.PlayerAbilityPerms;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AbilityManager {

    // Instance of PlayerAbilityPerms for checking ability permissions
    private PlayerAbilityPerms abilityPerms;
    private Abilities playerAbility;

    // Mapping of abilities to their corresponding check and action
    private Map<Predicate<UserProfile>, Consumer<Location>> abilityActions;

    public AbilityManager(JavaPlugin plugin) {
        this.abilityPerms = new PlayerAbilityPerms();
        this.playerAbility = new Abilities(plugin);

        // Initialize the ability action map
        abilityActions = new HashMap<>();
        abilityActions.put(
                profile -> abilityPerms.canSummonFire(profile),
                location -> playerAbility.summonFireOnTarget(location)
        );
        abilityActions.put(
                profile -> abilityPerms.canSummonSnowPowder(profile),
                location -> playerAbility.summonPowderSnowOnTarget(location)
        );
    }

    // Apply ability based on the profile's abilities and victim's location
    public void applyAbility(UserProfile attackerProfile, Entity victim, Location attackerLoc, Location victimLoc) {
        // Loop through the mapped abilities and apply the relevant effect if the profile permits it
        for (Map.Entry<Predicate<UserProfile>, Consumer<Location>> entry : abilityActions.entrySet()) {
            Predicate<UserProfile> checkAbility = entry.getKey();
            Consumer<Location> applyEffect = entry.getValue();

            // If the ability check passes, apply the corresponding effect
            if (checkAbility.test(attackerProfile)) {
                applyEffect.accept(victimLoc);
            }
        }
    }
}
