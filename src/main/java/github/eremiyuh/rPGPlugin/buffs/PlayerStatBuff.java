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
        double equipStrength = 0;
        double equipDexterity = 0;
        double equipIntelligence = 0;
        double equipLuck = 0;
        double equipFinalHpMultiplier = 0;
        double equipStatDmgMultiplier = 0;

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
                        if (lore.startsWith("Vitality: ")) equipVitality += parseLoreValue(lore);
                        else if (lore.startsWith("Agility: ")) equipAgility += parseLoreValue(lore);
                        else if (lore.startsWith("Strength: ")) equipStrength += parseLoreValue(lore);
                        else if (lore.startsWith("Dexterity: ")) equipDexterity += parseLoreValue(lore);
                        else if (lore.startsWith("Intelligence: ")) equipIntelligence += parseLoreValue(lore);
                        else if (lore.startsWith("Luck: ")) equipLuck += parseLoreValue(lore);
                        else if (lore.contains("HP%: ")) equipFinalHpMultiplier += parseLoreValue(lore);
                        else if (lore.contains("StatDamage%: ")) equipStatDmgMultiplier += parseLoreValue(lore);
                    }
                }
            }
        }

        return new double[]{
                equipVitality,
                equipAgility,
                equipStrength,
                equipDexterity,
                equipIntelligence,
                equipLuck,
                equipFinalHpMultiplier,
                equipStatDmgMultiplier
        };
    }


    /**
     * Calculates max health based on the player's Vitality attribute.
     */
    private double calculateMaxHealth(UserProfile profile) {
        double baseHealth = 20.0;
        double healthPerVitality = 20.0;

        double vitality = (double) profile.getTempVit() / 100.0;
        double hpMultiplier = 1.0 + (profile.getHpMultiplier() * 0.01);

        return (baseHealth + (healthPerVitality * vitality)) * hpMultiplier;
    }



    /**
     * Calculates movement speed based on the player's Agility attribute.
     */
    private double calculateSpeed(UserProfile profile) {
        double baseSpeed = 0.2;
        double speedPerAgility = 0.0001;
        double maxAgilityBonus = 1.0;

        double agility = profile.getTempAgi();
        double agilityBonus = speedPerAgility * agility;

        agilityBonus = Math.min(agilityBonus, maxAgilityBonus);
        return Math.min(baseSpeed + agilityBonus, 1.0);
    }


    /**
     * Updates the player's max health and movement speed based on RPG attributes.
     */
    public void updatePlayerStatsToRPG(Player player) {
        try {
            UserProfile profile = profileManager.getProfile(player.getName());
            if (profile == null) return;

            profile.resetTemporaryStats(); // Clear temp stats

            double[] equipStats = getEquipStats(player); // [vit, agi, str, dex, int, luk, hpMult, statMult]

            double classStr = 0, classDex = 0, classInt = 0, classLuk = 0, classVit = 0, classAgi = 0;
            String chosenClass = profile.getChosenClass().toLowerCase();

            switch (chosenClass) {
                case "archer" -> {
                    classStr = profile.getArcherClassInfo().getStr();
                    classDex = profile.getArcherClassInfo().getDex();
                    classInt = profile.getArcherClassInfo().getIntel();
                    classLuk = profile.getArcherClassInfo().getLuk();
                    classVit = profile.getArcherClassInfo().getVit();
                    classAgi = profile.getArcherClassInfo().getAgi();
                }
                case "swordsman" -> {
                    classStr = profile.getSwordsmanClassInfo().getStr();
                    classDex = profile.getSwordsmanClassInfo().getDex();
                    classInt = profile.getSwordsmanClassInfo().getIntel();
                    classLuk = profile.getSwordsmanClassInfo().getLuk();
                    classVit = profile.getSwordsmanClassInfo().getVit();
                    classAgi = profile.getSwordsmanClassInfo().getAgi();
                }
                case "alchemist" -> {
                    classStr = profile.getAlchemistClassInfo().getStr();
                    classDex = profile.getAlchemistClassInfo().getDex();
                    classInt = profile.getAlchemistClassInfo().getIntel();
                    classLuk = profile.getAlchemistClassInfo().getLuk();
                    classVit = profile.getAlchemistClassInfo().getVit();
                    classAgi = profile.getAlchemistClassInfo().getAgi();

                    // Bonus agility and vitality for alchemist gear
                    equipStats[0] *= 1.2; // vit
                    equipStats[1] *= 1.2; // agi
                }
            }

            // Set final stats (class + gear) as temporary stats
            profile.setTempStr((int) (classStr + equipStats[2]));
            profile.setTempDex((int) (classDex + equipStats[3]));
            profile.setTempIntel((int) (classInt + equipStats[4]));
            profile.setTempLuk((int) (classLuk + equipStats[5]));
            profile.setTempVit((int) (classVit + equipStats[0]));
            profile.setTempAgi((int) (classAgi + equipStats[1]));

            // Update multipliers
            profile.setHpMultiplier((int) equipStats[6]);
            profile.setStatDmgMultiplier((int) equipStats[7]);

            double totalStats = 0;

// Sum the first 6 normally
            for (int i = 0; i < 6; i++) {
                totalStats += equipStats[i];
            }

// Multiply the last two by 10 before adding
            totalStats += equipStats[6] * 10;
            totalStats += equipStats[7] * 10;

// Divide the total by 100
            double level = totalStats / 100;
            int flooredLevel = (int) level; // or use Math.floor(level) if you're being explicit
            int finalLevel = Math.max(1, flooredLevel); // ensures minimum is 1
            profile.setLevel(finalLevel);



            // Update player health
            AttributeInstance maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
            if (maxHealthAttr != null) {
                double newMaxHealth = calculateMaxHealth(profile);
                maxHealthAttr.setBaseValue(newMaxHealth);
                player.setHealth(Math.min(player.getHealth(), newMaxHealth));
            }

            // Update movement speed
            double newSpeed = calculateSpeed(profile);
            player.setWalkSpeed((float) Math.min(newSpeed, 1.0f));

            // Warn if speed is capped
            if (newSpeed >= 1.0) {
                TextComponent speedWarning = Component.text("You have reached max ms from agi. Agi max: 8000")
                        .color(TextColor.color(255, 0, 0));
                player.sendMessage(speedWarning);
            }

        } catch (Exception e) {
            e.printStackTrace(); // Log any errors
        }
    }


    /**
     * Resets player's stats to Minecraft's vanilla defaults.
     */
    public void updatePlayerStatsToNormal(Player player) {
        updatePlayerStatsToRPG(player);
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
