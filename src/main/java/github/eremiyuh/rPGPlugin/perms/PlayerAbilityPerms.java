package github.eremiyuh.rPGPlugin.perms;

import github.eremiyuh.rPGPlugin.profile.UserProfile;

public class PlayerAbilityPerms {

    public boolean canSummonFireUponHit(UserProfile profile) {
        String chosenClass = profile.getChosenClass();
        String element = profile.getSelectedElement();
        String skill = profile.getSelectedSkill();

        if (!element.equalsIgnoreCase("Fire")) {
            return false;
        }

        if (chosenClass.equalsIgnoreCase("Swordsman")) {
            return true; // Swordsman always qualifies if element matches
        }

        return (chosenClass.equalsIgnoreCase("Alchemist") ||
                chosenClass.equalsIgnoreCase("Archer") ||
                chosenClass.equalsIgnoreCase("Rocket"))
                && skill.equalsIgnoreCase("Skill 1");
    }

    public boolean canFreezeOnHit(UserProfile profile) {
        String chosenClass = profile.getChosenClass();
        String element = profile.getSelectedElement();
        String skill = profile.getSelectedSkill();

        if (!element.equalsIgnoreCase("Ice")) {
            return false;
        }


        return (chosenClass.equalsIgnoreCase("Alchemist") ||
                chosenClass.equalsIgnoreCase("Archer") ||
                chosenClass.equalsIgnoreCase("Rocket")||
                chosenClass.equalsIgnoreCase("Swordsman"))
                && skill.equalsIgnoreCase("Skill 1");
    }

    public boolean canApplyNausea(UserProfile profile) {
        String chosenClass = profile.getChosenClass();
        String element = profile.getSelectedElement();
        String skill = profile.getSelectedSkill();

        if (!element.equalsIgnoreCase("Water")) {
            return false;
        }


        return (chosenClass.equalsIgnoreCase("Alchemist") ||
                chosenClass.equalsIgnoreCase("Archer") ||
                chosenClass.equalsIgnoreCase("Rocket")||
                chosenClass.equalsIgnoreCase("Swordsman"))
                && skill.equalsIgnoreCase("Skill 1");
    }

    public boolean canApplyWeakness(UserProfile profile) {
        String chosenClass = profile.getChosenClass();
        String element = profile.getSelectedElement();
        String skill = profile.getSelectedSkill();

        if (!element.equalsIgnoreCase("Earth")) {
            return false;
        }

        return (chosenClass.equalsIgnoreCase("Alchemist") ||
                chosenClass.equalsIgnoreCase("Archer") ||
                chosenClass.equalsIgnoreCase("Rocket")||
                chosenClass.equalsIgnoreCase("Swordsman"))
                && skill.equalsIgnoreCase("Skill 1");
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
