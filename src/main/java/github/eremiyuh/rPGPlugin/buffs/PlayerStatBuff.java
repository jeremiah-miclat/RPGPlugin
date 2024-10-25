package github.eremiyuh.rPGPlugin.buffs;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;


public class PlayerStatBuff  {

    private final PlayerProfileManager profileManager;

    public PlayerStatBuff(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    /**
     * Calculates max health based on the player's Vitality (vit) attribute.
     */
    private double calculateMaxHealth(UserProfile profile) {
        double baseHealth = 20.0; // base health in half-hearts (20 = 10 hearts)
        double healthPerVitality = .1; // each vitality point adds one full heart

        switch (profile.getChosenClass().toLowerCase()) {
            case "archer":
                baseHealth += healthPerVitality * profile.getArcherClassInfo().getVit();
                break;
            case "swordsman":
                baseHealth += healthPerVitality * profile.getSwordsmanClassInfo().getVit();
                break;
            case "alchemist":
                baseHealth += healthPerVitality * profile.getAlchemistClassInfo().getVit();
                break;
            default:
                // If no class-specific info is found, use a default vitality value
                baseHealth += healthPerVitality * profile.getDefaultClassInfo().getVit();
                break;
        }

        return baseHealth;
    }

    /**
     * Calculates movement speed based on the player's Agility (agi) attribute.
     */
    private double calculateSpeed(UserProfile profile) {
        double baseSpeed = 0.2; // Default Minecraft player speed
        double maxAgilityBonus = 0.3; // Cap agility contribution at 0.3, total max speed = 0.5
        double speedPerAgility = 0.0002;

        // Calculate agility-based bonus speed
        double agilityBonus = switch (profile.getChosenClass().toLowerCase()) {
            case "archer" -> speedPerAgility * profile.getArcherClassInfo().getAgi();
            case "swordsman" -> speedPerAgility * profile.getSwordsmanClassInfo().getAgi();
            case "alchemist" -> speedPerAgility * profile.getAlchemistClassInfo().getAgi();
            default -> speedPerAgility * profile.getDefaultClassInfo().getAgi();
        };

        // Cap the agility-based bonus at maxAgilityBonus to prevent excess speed
        agilityBonus = Math.min(agilityBonus, maxAgilityBonus);

        // Add the agility bonus to base speed and cap final speed

        return Math.min(baseSpeed + agilityBonus, 0.5);
    }


    /**
     * Updates the player's max health and movement speed based on their attributes.
     */
    public void updatePlayerStats(Player player) {
        UserProfile profile = profileManager.getProfile(player.getName());
        if (profile == null) return;

        // Set max health based on vitality using GENERIC_MAX_HEALTH attribute
        AttributeInstance maxHealthAttr = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealthAttr != null) {
            double newMaxHealth = calculateMaxHealth(profile);
            maxHealthAttr.setBaseValue(newMaxHealth);
            player.setHealth(Math.min(player.getHealth(), newMaxHealth)); // Ensure health isn't above max
        }

        // Set walk speed based on agility
        double newSpeed = calculateSpeed(profile);
        if (newSpeed>.4) {player.sendMessage("You have reached max ms from agi. Increasing it over 1k points is not recommended");}
        player.setWalkSpeed((float) Math.min(newSpeed, 1.0f)); // Cap speed at 1.0 for safety
    }


    /**
     * Call this method after any class switch or attribute allocation.
     * @param player The player whose stats are being updated.
     */
    public void onClassSwitchOrAttributeChange(Player player) {
        updatePlayerStats(player);
    }

}
