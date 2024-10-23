package github.eremiyuh.rPGPlugin.perms;

import github.eremiyuh.rPGPlugin.profile.UserProfile;

public class PlayerAbilityPerms {

    public boolean canSummonFire(UserProfile profile) {
        return profile.getChosenClass().equalsIgnoreCase("Alchemist") &&
                profile.getSelectedElement().equalsIgnoreCase("Fire") &&
                profile.getSelectedSkill().equalsIgnoreCase("Skill 1");
    }

    public boolean canSummonSnowPowder(UserProfile profile) {
        return profile.getChosenClass().equalsIgnoreCase("Alchemist") &&
                profile.getSelectedElement().equalsIgnoreCase("Ice") &&
                profile.getSelectedSkill().equalsIgnoreCase("Skill 2");
    }
}
