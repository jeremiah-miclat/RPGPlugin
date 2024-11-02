package github.eremiyuh.rPGPlugin.buffs;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;


public class PlayerStatBuff  {

    private final PlayerProfileManager profileManager;

    public PlayerStatBuff(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    /**
     * Calculates max health based on the player's Vitality (vit) attribute.
     */
    private double calculateMaxHealth(UserProfile profile, Player player) {
        double baseHealth = 20.0; // base health in half-hearts (20 = 10 hearts)
        double healthPerVitality = 1; // each vitality point adds one full heart
        double equipVitality = 0;

        ItemStack[] equipment = {
                player.getInventory().getHelmet(),
                player.getInventory().getChestplate(),
                player.getInventory().getLeggings(),
                player.getInventory().getBoots(),
                player.getInventory().getItemInMainHand(),
                player.getInventory().getItemInOffHand()
        };

        for (ItemStack item : equipment) {
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasLore()) {
                    for (String lore : Objects.requireNonNull(meta.getLore())) {
                        if (lore.startsWith("Vitality: ")) {
                            equipVitality += parseLoreValue(lore);
                        }
                    }
                }
            }
        }

        switch (profile.getChosenClass().toLowerCase()) {
            case "archer":
                baseHealth += healthPerVitality * profile.getArcherClassInfo().getVit()+equipVitality;
                break;
            case "swordsman":
                baseHealth += healthPerVitality * profile.getSwordsmanClassInfo().getVit()+equipVitality;
                break;
            case "alchemist":
                baseHealth += healthPerVitality * profile.getAlchemistClassInfo().getVit()+equipVitality;
                break;
            default:
                // If no class-specific info is found, use a default vitality value
                baseHealth += healthPerVitality * profile.getDefaultClassInfo().getVit()+equipVitality;
                break;
        }

        return baseHealth;
    }

    /**
     * Calculates movement speed based on the player's Agility (agi) attribute.
     */
    private double calculateSpeed(UserProfile profile, Player player) {
        double baseSpeed = 0.2; // Default Minecraft player speed
        double maxAgilityBonus = 0.3; // Cap agility contribution at 0.3, total max speed = 0.5
        double speedPerAgility = 0.0002;
        double equipAgility = 0;
        double equipVitality = 0;

        ItemStack[] equipment = {
                player.getInventory().getHelmet(),
                player.getInventory().getChestplate(),
                player.getInventory().getLeggings(),
                player.getInventory().getBoots(),
                player.getInventory().getItemInMainHand(),
                player.getInventory().getItemInOffHand()
        };

        for (ItemStack item : equipment) {
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasLore()) {
                    for (String lore : Objects.requireNonNull(meta.getLore())) {
                        if (lore.startsWith("Agility: ")) {
                            equipAgility += parseLoreValue(lore);
                        }
                    }
                }
            }
        }

        // Calculate agility-based bonus speed
        double agilityBonus = switch (profile.getChosenClass().toLowerCase()) {
            case "archer" -> speedPerAgility * profile.getArcherClassInfo().getAgi() + equipAgility;
            case "swordsman" -> speedPerAgility * profile.getSwordsmanClassInfo().getAgi()+ equipAgility;
            case "alchemist" -> speedPerAgility * profile.getAlchemistClassInfo().getAgi()+ equipAgility;
            default -> speedPerAgility * profile.getDefaultClassInfo().getAgi()+ equipAgility;
        };

        // Cap the agility-based bonus at maxAgilityBonus to prevent excess speed
        agilityBonus = Math.min(agilityBonus, maxAgilityBonus);

        // Add the agility bonus to base speed and cap final speed

        return Math.min(baseSpeed + agilityBonus, 0.5);
    }


    /**
     * Updates the player's max health and movement speed based on their attributes.
     */
    public void updatePlayerStatsToNormal(Player player) {

        UserProfile profile = profileManager.getProfile(player.getName());
        if (profile == null) return;

        // Always start with vanilla stats
        double baseHealth = 20.0; // Vanilla max health (20 = 10 hearts)
        float baseSpeed = 0.2f; // Vanilla walk speed

        player.setWalkSpeed(baseSpeed);

        // Set the player's max health to the base health
        AttributeInstance maxHealthAttr = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert maxHealthAttr != null;
        maxHealthAttr.setBaseValue(baseHealth);




    }



    public void updatePlayerStatsToRPG(Player player) {

        UserProfile profile = profileManager.getProfile(player.getName());
        if (profile == null) return;

        // Always start with vanilla stats
        double baseHealth = 20.0; // Vanilla max health (20 = 10 hearts)
        float baseSpeed = 0.2f; // Vanilla walk speed

        player.setWalkSpeed(baseSpeed);

        // Set the player's max health to the base health
        AttributeInstance maxHealthAttr = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert maxHealthAttr != null;
        maxHealthAttr.setBaseValue(baseHealth);



        // Now apply the RPG calculations

        // Set max health based on vitality using GENERIC_MAX_HEALTH attribute
        double newMaxHealth = calculateMaxHealth(profile, player);
        maxHealthAttr.setBaseValue(newMaxHealth);
        player.setHealth(Math.min(player.getHealth(), newMaxHealth)); // Ensure health isn't above max

        // Set walk speed based on agility
        double newSpeed = calculateSpeed(profile, player);
        if (newSpeed > 0.4) {
            player.sendMessage("You have reached max ms from agi. Increasing it over 1k points is not recommended");
        }
        player.setWalkSpeed((float) Math.min(newSpeed, 1.0f)); // Cap speed at 1.0 for safety

    }

    /**
     * Call this method after any class switch or attribute allocation.
     * @param player The player whose stats are being updated.
     */
    public void onClassSwitchOrAttributeChange(Player player) {
        if (player.getLocation().getWorld().getName().equals("world_rpg")) {
            updatePlayerStatsToRPG(player);
        } else {
            updatePlayerStatsToNormal(player);
        }

    }

    private int parseLoreValue(String lore) {
        try {
            return Integer.parseInt(lore.split(": ")[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // Handle parsing errors or missing values
            return 0;
        }
    }

}
