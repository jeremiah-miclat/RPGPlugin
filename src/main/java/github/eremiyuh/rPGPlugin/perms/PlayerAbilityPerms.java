package github.eremiyuh.rPGPlugin.perms;

import github.eremiyuh.rPGPlugin.profile.UserProfile;

public class PlayerAbilityPerms {

    public boolean canSummonFireUponHit(UserProfile profile) {
        // Conditions for Alchemist, Archer, or Swordsman with Fire element and Skill 1
        return (profile.getChosenClass().equalsIgnoreCase("Alchemist") ||
                profile.getChosenClass().equalsIgnoreCase("Archer") ||
                profile.getChosenClass().equalsIgnoreCase("Swordsman")||
                profile.getChosenClass().equalsIgnoreCase("Rocket"))
                && profile.getSelectedElement().equalsIgnoreCase("Fire")
                && profile.getSelectedSkill().equalsIgnoreCase("Skill 1");
    }

    public boolean canFreezeOnHit(UserProfile profile) {
        // Conditions for Alchemist, Archer, or Swordsman with Ice element and Skill 1
        return (profile.getChosenClass().equalsIgnoreCase("Alchemist") ||
                profile.getChosenClass().equalsIgnoreCase("Archer") ||
                profile.getChosenClass().equalsIgnoreCase("Swordsman")||
                profile.getChosenClass().equalsIgnoreCase("Rocket"))
                && profile.getSelectedElement().equalsIgnoreCase("Ice")
                && profile.getSelectedSkill().equalsIgnoreCase("Skill 1");
    }


    public boolean canApplyNausea(UserProfile profile) {
        // Conditions for Alchemist, Archer, or Swordsman with Ice element and Skill 1
        return (profile.getChosenClass().equalsIgnoreCase("Alchemist") ||
                profile.getChosenClass().equalsIgnoreCase("Archer") ||
                profile.getChosenClass().equalsIgnoreCase("Swordsman") ||
                profile.getChosenClass().equalsIgnoreCase("Rocket"))
                && profile.getSelectedElement().equalsIgnoreCase("Water")
                && profile.getSelectedSkill().equalsIgnoreCase("Skill 1");
    }


    public boolean canApplyWeakness(UserProfile profile) {
        // Conditions for Alchemist, Archer, or Swordsman with Ice element and Skill 1
        return (profile.getChosenClass().equalsIgnoreCase("Alchemist") ||
                profile.getChosenClass().equalsIgnoreCase("Archer") ||
                profile.getChosenClass().equalsIgnoreCase("Swordsman"))
                && profile.getSelectedElement().equalsIgnoreCase("Earth")
                && profile.getSelectedSkill().equalsIgnoreCase("Skill 1");
    }

    public boolean canSummonFireArrowBarrage(UserProfile profile) {
        // Conditions for Alchemist, Archer, or Swordsman with Ice element and Skill 1
        return (profile.getChosenClass().equalsIgnoreCase("Archer"))
                && profile.getSelectedElement().equalsIgnoreCase("Fire")
                && profile.getSelectedSkill().equalsIgnoreCase("Skill 2");
    }

    public boolean canSummonFreezeArrowBarrage(UserProfile profile) {
        // Conditions for Alchemist, Archer, or Swordsman with Ice element and Skill 1
        return (profile.getChosenClass().equalsIgnoreCase("Archer"))
                && profile.getSelectedElement().equalsIgnoreCase("ice")
                && profile.getSelectedSkill().equalsIgnoreCase("Skill 2");
    }

    public boolean canSummonWeaknessArrowBarrage(UserProfile profile) {
        // Conditions for Alchemist, Archer, or Swordsman with Ice element and Skill 1
        return (profile.getChosenClass().equalsIgnoreCase("Archer"))
                && profile.getSelectedElement().equalsIgnoreCase("water")
                && profile.getSelectedSkill().equalsIgnoreCase("Skill 2");
    }

}
