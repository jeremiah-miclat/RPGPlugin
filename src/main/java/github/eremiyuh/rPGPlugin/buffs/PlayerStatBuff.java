package github.eremiyuh.rPGPlugin.buffs;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
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
        double equipFinalHpMultiplier = 0;

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
                        } else if (lore.contains("HP%: ")) {

                            equipFinalHpMultiplier += parseLoreValue(lore);
                        }
                    }
                }
            }
        }
        return new double[]{equipVitality, equipAgility, equipFinalHpMultiplier};
    }

    /**
     * Calculates max health based on the player's Vitality attribute.
     */
    private double calculateMaxHealth(UserProfile profile, Player player) {
        double baseHealth = 20.0;
        double healthPerVitality = 20;

        double[] equipStats = getEquipStats(player);
        double equipVitality = equipStats[0]/100;
        double finalHpMultiplier = 1 + (equipStats[2]*.01);

        // Calculate base health based on class and vitality
        double classVitality = switch (profile.getChosenClass().toLowerCase()) {
            case "archer" -> (double) profile.getArcherClassInfo().getVit() /100;
            case "swordsman" -> (double) profile.getSwordsmanClassInfo().getVit() /100;
            case "alchemist" -> (double) profile.getAlchemistClassInfo().getVit() /100;
            default -> (double) profile.getDefaultClassInfo().getVit() /100;
        };
        if (profile.getChosenClass().equalsIgnoreCase("alchemist")) {
            equipVitality*=1.2;
        }
        return (baseHealth + (healthPerVitality * (classVitality + equipVitality)))*finalHpMultiplier;
    }

    /**
     * Calculates movement speed based on the player's Agility attribute.
     */
    private double calculateSpeed(UserProfile profile, Player player) {
        double baseSpeed = 0.2;
        double maxAgilityBonus = 1;
        double speedPerAgility = 0.0001;

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
            equipAgility*=1.2;
        }

        double agilityBonus = speedPerAgility * (classAgility + equipAgility);
        agilityBonus = Math.min(agilityBonus, maxAgilityBonus);
        return Math.min(baseSpeed + agilityBonus, 1);
    }

    /**
     * Updates the player's max health and movement speed based on RPG attributes.
     */
    public void updatePlayerStatsToRPG(Player player) {


        try {
            UserProfile profile = profileManager.getProfile(player.getName());
            if (profile == null) return;

            // Set max health based on vitality
            AttributeInstance maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
            assert maxHealthAttr != null;
            double newMaxHealth = calculateMaxHealth(profile, player);
            maxHealthAttr.setBaseValue(newMaxHealth);
            player.setHealth(Math.min(player.getHealth(), newMaxHealth)); // Prevent health from exceeding max

            // Set walk speed based on agility
            double newSpeed = calculateSpeed(profile, player);
            if (newSpeed >= 1) {
                TextComponent speedWarning = Component.text("You have reached max ms from agi. Agi max: 8000").color(TextColor.color(255,0,0));
                player.sendMessage(speedWarning);
            }

            player.setWalkSpeed((float) Math.min(newSpeed, 1.0f));
        }  catch (Exception e) {
            // Handle exceptions gracefully, log the error, etc.
            e.printStackTrace();
        }
    }

    /**
     * Resets player's stats to Minecraft's vanilla defaults.
     */
    public void updatePlayerStatsToNormal(Player player) {
        player.setWalkSpeed(0.2f); // Vanilla walk speed

        AttributeInstance maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
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
