package github.eremiyuh.rPGPlugin.methods;


import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import github.eremiyuh.rPGPlugin.perms.PlayerAbilityPerms;


public class AbilityManager {

    // Instance of PlayerAbilityPerms for checking ability permissions
    private PlayerAbilityPerms abilityPerms;
    private Abilities playerAbility;

    public AbilityManager() {
        this.abilityPerms = new PlayerAbilityPerms();
        this.playerAbility = new Abilities();
    }

    public void applyAbility(UserProfile attackerProfile, Entity victim, Location attackerLoc, Location victimLoc){
        if (abilityPerms.canSummonFire(attackerProfile)) {
            playerAbility.summonFireOnTarget(victimLoc);
        }
        if (abilityPerms.canSummonSnowPowder(attackerProfile)) {
            playerAbility.summonPowderSnowOnTarget(victimLoc);
        }
    }
}
