package github.eremiyuh.rPGPlugin.perms;

import github.eremiyuh.rPGPlugin.profile.UserProfile;

public class PlayerBuffPerms {

    public static boolean canLifeSteal(UserProfile profile) {
        // Conditions for Alchemist, Archer, or Swordsman with Fire element and Skill 1
        return (profile.getChosenClass().equalsIgnoreCase("Swordsman"))
                && profile.getSelectedSkill().equalsIgnoreCase("Skill 2");
    }

    public static boolean canReduceDmg(UserProfile profile) {
        // Conditions for Alchemist, Archer, or Swordsman with Fire element and Skill 1
        return (profile.getChosenClass().equalsIgnoreCase("Swordsman"))
                && profile.getSelectedSkill().equalsIgnoreCase("Skill 3");
    }

}
