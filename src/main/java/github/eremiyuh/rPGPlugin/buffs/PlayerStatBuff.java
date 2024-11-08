package github.eremiyuh.rPGPlugin.buffs;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class PlayerStatBuff {

    private final PlayerProfileManager profileManager;

    public PlayerStatBuff(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    /**
     * Retrieve total Vitality and Agility values from player's equipment.
     */
    private double[] getEquipStats(Player player) {
        double equipVitality = 0;
        double equipAgility = 0;

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
                        } else if (lore.startsWith("Agility: ")) {
                            equipAgility += parseLoreValue(lore);
                        }
                    }
                }
            }
        }
        return new double[]{equipVitality, equipAgility};
    }

    /**
     * Calculates max health based on the player's Vitality attribute.
     */
    private double calculateMaxHealth(UserProfile profile, Player player) {
        double baseHealth = 20.0; // Base health in half-hearts (20 = 10 hearts)
        double healthPerVitality = .2;

        double[] equipStats = getEquipStats(player);
        double equipVitality = equipStats[0];

        // Calculate base health based on class and vitality
        double classVitality = switch (profile.getChosenClass().toLowerCase()) {
            case "archer" -> profile.getArcherClassInfo().getVit();
            case "swordsman" -> profile.getSwordsmanClassInfo().getVit();
            case "alchemist" -> profile.getAlchemistClassInfo().getVit();
            default -> profile.getDefaultClassInfo().getVit();
        };
        if (profile.getChosenClass().equalsIgnoreCase("alchemist")) {
            equipVitality*=1.1;
        }

        return baseHealth + healthPerVitality * (classVitality + equipVitality);
    }

    /**
     * Calculates movement speed based on the player's Agility attribute.
     */
    private double calculateSpeed(UserProfile profile, Player player) {
        double baseSpeed = 0.2; // Default Minecraft player speed
        double maxAgilityBonus = 0.3; // Cap agility contribution at 0.3, total max speed = 0.5
        double speedPerAgility = 0.0002;

        double[] equipStats = getEquipStats(player);
        double equipAgility = equipStats[1];

        // Calculate agility-based bonus speed based on class and agility
        double classAgility = switch (profile.getChosenClass().toLowerCase()) {
            case "archer" -> profile.getArcherClassInfo().getAgi();
            case "swordsman" -> profile.getSwordsmanClassInfo().getAgi();
            case "alchemist" -> profile.getAlchemistClassInfo().getAgi();
            default -> profile.getDefaultClassInfo().getAgi();
        };

        if (profile.getChosenClass().equalsIgnoreCase("alchemist")) {
            equipAgility*=1.1;
        }

        double agilityBonus = speedPerAgility * (classAgility + equipAgility);
        agilityBonus = Math.min(agilityBonus, maxAgilityBonus);
        return Math.min(baseSpeed + agilityBonus, 0.5);
    }

    /**
     * Updates the player's max health and movement speed based on RPG attributes.
     */
    public void updatePlayerStatsToRPG(Player player) {
        UserProfile profile = profileManager.getProfile(player.getName());
        if (profile == null) return;

        // Set max health based on vitality
        AttributeInstance maxHealthAttr = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert maxHealthAttr != null;
        double newMaxHealth = calculateMaxHealth(profile, player);
        maxHealthAttr.setBaseValue(newMaxHealth);
        player.setHealth(Math.min(player.getHealth(), newMaxHealth)); // Prevent health from exceeding max

        // Set walk speed based on agility
        double newSpeed = calculateSpeed(profile, player);
        if (newSpeed > 0.4) {
            player.sendMessage("You have reached max ms from agi. Increasing it over 1k points is not recommended");
        }
        player.setWalkSpeed((float) Math.min(newSpeed, 1.0f)); // Cap speed at 1.0 for safety
    }

    /**
     * Resets player's stats to Minecraft's vanilla defaults.
     */
    public void updatePlayerStatsToNormal(Player player) {
        player.setWalkSpeed(0.2f); // Vanilla walk speed

        AttributeInstance maxHealthAttr = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert maxHealthAttr != null;
        maxHealthAttr.setBaseValue(20.0); // Vanilla max health (20 = 10 hearts)
    }

    /**
     * Call this method after any class switch or attribute allocation.
     * @param player The player whose stats are being updated.
     */
    public void onClassSwitchOrAttributeChange(Player player) {
        if (player.getLocation().getWorld().getName().equals("world_rpg") || player.getLocation().getWorld().getName().equals("world_labyrinth")) {
            updatePlayerStatsToRPG(player);
        }
    }

    /**
     * Parses the integer value from a lore string.
     * @param lore The lore text.
     * @return The integer value parsed from the lore.
     */
    private int parseLoreValue(String lore) {
        try {
            return Integer.parseInt(lore.split(": ")[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // Handle parsing errors or missing values
            return 0;
        }
    }
}
