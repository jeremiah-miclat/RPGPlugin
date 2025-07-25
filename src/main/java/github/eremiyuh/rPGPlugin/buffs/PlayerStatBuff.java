package github.eremiyuh.rPGPlugin.buffs;

import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.Map;
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
        int mainhandPower = 0;
        int mainhandSharpness = 0;

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

        // Read enchantment levels from main hand weapon
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        int sharplevel = mainHand.getEnchantmentLevel(Enchantment.SHARPNESS);
        int powerLevel = mainHand.getEnchantmentLevel(Enchantment.POWER);
        mainhandSharpness = sharplevel;
        mainhandPower = powerLevel;

        return new double[]{
                equipVitality,
                equipAgility,
                equipStrength,
                equipDexterity,
                equipIntelligence,
                equipLuck,
                equipFinalHpMultiplier,
                equipStatDmgMultiplier,
                mainhandPower,
                mainhandSharpness
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
                    equipStats[6] *= 1.2;
                    equipStats[7] *= 1.2;
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


            int str = profile.getTempStr();
            int dex = profile.getTempDex();
            int intel = profile.getTempIntel();
            int luk = profile.getTempLuk();
            int vit = profile.getTempVit();
            int agi = profile.getTempAgi();
            int hp= profile.getHpMultiplier()*10;
            int dmg = profile.getStatDmgMultiplier()*10;
            int power = (int) equipStats[8]*20;
            int sharp = (int) equipStats[9]*20;
            double totalStats = str+dex+intel+luk+vit+agi+hp+dmg+power+sharp;



// Divide the total by 100
            double level = totalStats / 100;
            int flooredLevel = (int) level; // or use Math.floor(level) if you're being explicit
            int finalLevel = (int) Math.max(1, (totalStats - 100) / 100 + 1);
            if (finalLevel>profile.getLevel()) {
                profile.setLevel(finalLevel);
            }

            profile.setLs(lifeStealPercent(chosenClass,profile.getSelectedSkill(),player));
            profile.setCrit(critChance(chosenClass,luk,dex, profile.getSelectedSkill()));
            profile.setCritDmg(getCritMultiplier(chosenClass,luk,dex, profile.getSelectedSkill()));

            if (player.getWorld().getName().contains("_rpg")) {
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

    public boolean isHoldingValidMeleeWeapon(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType() == Material.AIR) return false;

        String typeName = mainHand.getType().name();
        return typeName.endsWith("_SWORD") || typeName.endsWith("_AXE") || typeName.equals("TRIDENT") || typeName.equals("MACE");
    }

    public boolean isHoldingValidRangedWeapon(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType() == Material.AIR) return false;

        Material type = mainHand.getType();

        return type == Material.BOW
                || type == Material.CROSSBOW
                || type == Material.TRIDENT;
    }

    public boolean isValidLifestealWeaponM(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType() == Material.AIR) return false;

        String typeName = mainHand.getType().name();
        return typeName.endsWith("_SWORD");
    }

    public boolean isValidLifestealWeaponL(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType() == Material.AIR) return false;

        String typeName = mainHand.getType().name();
        return mainHand.getType() == Material.CROSSBOW;
    }

    public double lifeStealPercent(String chosenClass, String chosenSkill, Player player) {
        if (chosenClass.equalsIgnoreCase("swordsman")
                && chosenSkill.equalsIgnoreCase("skill 2")
                && isValidLifestealWeaponM(player)
        ) {return .1;}
        if (chosenClass.equalsIgnoreCase("archer")
                && chosenSkill.equalsIgnoreCase("skill 3")
                && isValidLifestealWeaponL(player)
        ) {return .05;}
        return 0;
    }

    public double critChance(String chosenClass, int luk, int dex, String chosenSkill) {
        double baseChance;

        switch (chosenClass.toLowerCase()) {
            case "swordsman":
                baseChance = luk * 0.0003;
                break;
            case "archer":
                baseChance = (luk * 0.0003) + (dex * 0.0001);
                if (chosenSkill.equalsIgnoreCase("skill 3")) {
                    baseChance += 0.25; // +25% flat
                }
                break;
            default:
                baseChance = luk * 0.0002;
                break;
        }

        return baseChance;
    }

    public double getCritMultiplier(String classType, double luk, double dex, String chosenSkill) {
        double multiplier = 1.5 + (luk * 0.001);

        if (classType.equalsIgnoreCase("archer") && chosenSkill.equalsIgnoreCase("skill 3")) {
            multiplier += dex * 0.00005;
        }

        return multiplier;
    }

}
