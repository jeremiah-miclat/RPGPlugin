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
        double healthPerVitality = 10.0;
        double healthPerStrength = 5.0; // +5 HP per 100 strength

        double bonusHPFromS3 = 0;

        double newhealth = 0;

        if (profile.getChosenClass().contains("sword") && profile.getSelectedSkill().contains("3")) {
            bonusHPFromS3 += 2 * profile.getArLvl();
        }

        double vitality = profile.getTempVit() / 100.0;
        double strength = profile.getTempStr() / 100.0;
        double healthPerLevel = (Math.max(0,profile.getArLvl()-1))*8;
        double hpMultiplier = 1.0 + (profile.getHpMultiplier() * 0.01);



        newhealth = (bonusHPFromS3 + baseHealth + healthPerLevel
                + (healthPerVitality * vitality)
                + (healthPerStrength * strength)) * hpMultiplier;

        if (profile.getAbyssTrait().equalsIgnoreCase("Bloodlust")) newhealth*=.9;
        else if (profile.getAbyssTrait().equalsIgnoreCase("Fortress")) {
            newhealth*=2;
        }
        if (profile.getSelectedElement().contains("water")) newhealth*=1.1;

        return newhealth;

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

    private double calculateBonusAttackSpeed(UserProfile profile) {
        double agility = profile.getTempAgi();
        double lvl = profile.getArLvl();

        if (lvl <= 0) return 0;
        if (agility <= 0) return 0;
        double agiPerLvl = agility / lvl;
        double bonus;

        if (agiPerLvl <= 50) {
            // Linear from 0 → 50 AGI/lvl maps to 0 → 1 bonus
            bonus = agiPerLvl / 50.0;
        } else if (agiPerLvl <= 100) {
            // Linear from 50 → 100 AGI/lvl maps to 1 → 3 bonus
            bonus = 1.0 + ((agiPerLvl - 50) / 50.0) * 2.0;
        } else {
            // Cap at 3
            bonus = 3.0;
        }

        return bonus;
    }

    public void applyAttackSpeedBonus(Player player, UserProfile profile) {
        AttributeInstance attr = player.getAttribute(Attribute.ATTACK_SPEED);
        if (attr == null) return;

        double currentSpeed = attr.getDefaultValue(); // Includes base + modifiers
        double bonusMultiplier = 1.0 + calculateBonusAttackSpeed(profile);

        attr.setBaseValue(currentSpeed * bonusMultiplier);
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
            String selectedSkill = profile.getSelectedSkill();
            String chosenTrait = profile.getAbyssTrait();
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
            int hp= profile.getHpMultiplier();
            int dmg = profile.getStatDmgMultiplier();
            int power = (int) equipStats[8];
            int sharp = (int) equipStats[9];
            double totalStats = str+dex+intel+luk+vit+agi;
//            double bonusLvlFromDmg = calculateBPLevelMBonusFromStatDmg(str,intel,dex,dmg,sharp,power);
//            double bonusLvlFromHp = calculateBPLevelMBonusFromHP(vit,hp, profile.getArLvl());
//            double bonusLvlFromEnchant = calculateBPLevelMBonusFromEnchant(sharp,power);


            double level = (totalStats / 100);
            int tempLvl = (int) Math.ceil(level); // always round UP

            int finalLevel = (int) Math.max(1, ((profile.getTotalAllocatedPoints()+profile.getCurrentAttributePoints()) - 100) / 100 + 1);
            profile.setArLvl(Math.max(1,profile.getTotalAllocatedPoints()/100));
            if (finalLevel>profile.getLevel()) {
                profile.setLevel(finalLevel);
                player.sendMessage(ChatColor.GOLD + "You are now level "+ finalLevel);
            }
            boolean validRangedWeapon = isHoldingValidRangedWeapon(player);
            profile.setLs(lifeStealPercent(chosenClass,profile.getSelectedSkill(),player,chosenTrait));
            double bonusCrit = 0;
            if (profile.getSelectedElement().equalsIgnoreCase("ice")) bonusCrit+=.1;
            profile.setCrit(critChance(chosenClass,luk,dex, profile.getSelectedSkill(),chosenTrait,validRangedWeapon,profile.getArLvl())+bonusCrit);
            profile.setCritResist(calculateCritResist(luk,agi,vit, profile.getArLvl()));
            profile.setCritDmg(getCritMultiplier(chosenClass,luk,dex, profile.getSelectedSkill(),chosenTrait,validRangedWeapon,profile.getArLvl()));

            double meleeDmg = 0;
            double longDmg=0;
            double splashDmg= (double) intel /100*8;
            splashDmg+=((double) dex /100)*2;
            splashDmg+=((double) agi /100)*2;

            if (isHoldingValidMeleeWeapon(player)) {
                meleeDmg+=((double) str /100)*4;
                meleeDmg+=((double) dex /100)*2;

                if (selectedSkill.contains("1") && chosenClass.contains("sword")) {
                    meleeDmg+=((double) intel /100)*4;
                }
                meleeDmg+=dmgBonusFromEnchant(player);
                meleeDmg*=dmgMultiplierFromCE(profile);

            }

            if (validRangedWeapon) {
                longDmg+=((double) dex /100)*4;
                longDmg+=((double) agi /100)*2;
                if (chosenClass.contains("archer")) {
                    if(selectedSkill.contains("1")) longDmg+=((double) intel /100)*4;
                    else if (selectedSkill.contains("2")) longDmg+=((double) intel /100)*4;
                }
                longDmg+=dmgBonusFromEnchant(player);
                longDmg*=dmgMultiplierFromCE(profile);

//                longDmg*=dmgMultiplierFromAgi(profile);
            }

            if (chosenClass.contains("alche")) {
                splashDmg+=dmgBonusFromEnchant(player);
                splashDmg*=dmgMultiplierFromCE(profile);
//                splashDmg*=dmgMultiplierFromAgi(profile);
            }

            if (profile.getAbyssTrait().equalsIgnoreCase("Fortress")) {
                meleeDmg *=.8;
                longDmg*=.8;
                splashDmg*=.8;
            } else if (profile.getAbyssTrait().equalsIgnoreCase("Frenzy")) {
                meleeDmg *=2;
                meleeDmg +=8;
                longDmg*=2;
                longDmg+=8;
                splashDmg*=2;
                splashDmg+=8;
            }

            if (profile.getSelectedElement().contains("fire")) {
                meleeDmg *=1.1;
                longDmg*=1.1;
                splashDmg*=1.1;
            }

            profile.setMeleeDmg(meleeDmg);
            profile.setLongDmg(longDmg);
            profile.setSplashDmg(splashDmg);
            profile.setCdr(getCooldownReduction(dex, profile.getArLvl()));
            profile.setCritResist2(calculateCritDmgNegate(agi,vit,luk,hp));
            profile.setFlee(calculateFlee(agi));
            profile.setHit(calculateHitRate(dex));

            if (player.getWorld().getName().contains("_rpg")) {
                // Update player health
                AttributeInstance maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
                if (maxHealthAttr != null) {
                    double newMaxHealth = calculateMaxHealth(profile);
                    maxHealthAttr.setBaseValue(newMaxHealth);
                    player.setHealth(Math.min(player.getHealth(), newMaxHealth));
                }

                applyAttackSpeedBonus(player,profile);

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
//        player.setWalkSpeed(0.2f); // Vanilla walk speed
        AttributeInstance attr = player.getAttribute(Attribute.ATTACK_SPEED);
        assert attr != null;
        attr.setBaseValue(attr.getDefaultValue());

        AttributeInstance maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        assert maxHealthAttr != null;
        maxHealthAttr.setBaseValue(20.0); // Vanilla max health (20 = 10 hearts)
    }

    /**
     * Call this method after any class switch or attribute allocation.
     * @param player The player whose stats are being updated.
     */
    public void onClassSwitchOrAttributeChange(Player player) {
        if (!player.getLocation().getWorld().getName().contains("_rpg")) {
            updatePlayerStatsToNormal(player);
        }
        updatePlayerStatsToRPG(player);
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

    public double lifeStealPercent(String chosenClass, String chosenSkill, Player player, String chosenTrait) {
        double lifeSteal = 0;
        if (chosenTrait.equalsIgnoreCase("Bloodlust")) {lifeSteal+=.05;}
        if (chosenClass.equalsIgnoreCase("swordsman")
                && chosenSkill.equalsIgnoreCase("skill 2")
                && isValidLifestealWeaponM(player)
        ) {lifeSteal += .1;}
        if (chosenClass.equalsIgnoreCase("archer")
                && chosenSkill.equalsIgnoreCase("skill 3")
                && isValidLifestealWeaponL(player)
        ) {lifeSteal += .05;}
        return lifeSteal;
    }

    public double critChance(String chosenClass, int luk, int dex,
                             String chosenSkill, String chosenTrait,
                             boolean validRangedWeapon, int bpLvl) {
        if (bpLvl <= 0) return 0;

        double lukPerLvl = (double) luk / bpLvl;

        // Linear scaling: 50 lukPerLvl = 50% chance, capped at 50%
        double baseChance = lukPerLvl / 100.0;   // scales linearly
        if (baseChance > 0.5) baseChance = 0.5; // max 50%

        // Archer bonus
        if (chosenClass.equalsIgnoreCase("Archer") && validRangedWeapon
                && chosenSkill.contains("3")) {
            baseChance += 0.1;
        }

        // Trait penalty
        if (chosenTrait.equalsIgnoreCase("Gamble")) {
            baseChance -= 0.25;
        }

        // Clamp between 0% and 100%
        baseChance = Math.max(0, Math.min(1, baseChance));

        return baseChance;
    }


    public double getCritMultiplier(String classType, double luk, double dex, String chosenSkill, String chosenTrait, boolean validRangedWeapon, int bpLvl) {
        if (bpLvl <= 0) return 0;



        double bonus = 24 + (luk / 75) * 12.0;

        if (chosenTrait.equalsIgnoreCase("Gamble")) bonus*=1.5;
        if (chosenTrait.equalsIgnoreCase("Frenzy")) bonus*=2;

        return bonus;
    }

    public double dmgBonusFromEnchant(Player player) {
        double bonus = 0;

        ItemStack weapon = player.getEquipment().getItemInMainHand();

        int sharplevel = weapon.getEnchantmentLevel(Enchantment.SHARPNESS);
        int powerlevel  = weapon.getEnchantmentLevel(Enchantment.POWER);
        bonus += (sharplevel+powerlevel)*4;

        return bonus;
    }

    public double dmgMultiplierFromCE(UserProfile profile) {
        double multiplier = 1;

        double statDmg = profile.getStatDmgMultiplier();
        multiplier+=statDmg*.01;

        return multiplier;
    }

    public double dmgMultiplierFromAgi(UserProfile profile) {
        double lvl = profile.getArLvl();
        double agi = profile.getTempAgi();

        if (lvl <= 0) return 1.0; // safety check

        double agiPerLvl = agi / lvl;
        double multiplier;

        if (agiPerLvl <= 50) {
            // Linear scaling from 1.0 → 2.0 at 50/lvl
            multiplier = 1.0 + (agiPerLvl / 50.0);
        } else if (agiPerLvl <= 100) {
            // Linear scaling from 2.0 → 4.0 at 100/lvl
            multiplier = 2.0 + ((agiPerLvl - 50) / 50.0) * 2.0;
        } else {
            // Cap at 4.0
            multiplier = 4.0;
        }

        return multiplier;
    }

    private double calculateEvasion(double agility, int level) {
        double evadeChance;
        double agiPerLevel = agility / level;

        if (agiPerLevel < 25.0) {
            evadeChance = (agiPerLevel / 25.0) * 80.0;
        } else if (agiPerLevel < 50.0) {
            double bonus = ((agiPerLevel - 25.0) / 25.0) * 20.0; // 20% from 25 to 50
            evadeChance = 80.0 + bonus;
        } else {
            evadeChance = 100.0;
        }
        return evadeChance;
    }

    public double getCooldownReduction(int dex, int level) {
        double dexPerLevel = (double) dex / level;
        double reduction;

        if (dexPerLevel >= 40) {
            reduction = 29.0;
        } else if (dexPerLevel >= 20) {

            reduction = 15.0 + ((dexPerLevel - 20) / 20.0) * 14.0;
        } else {

            reduction = (dexPerLevel / 20.0) * 15.0;
        }
        return reduction;
    }

    public double calculateCritResist(int luk,int agi,int vit, int bpLvl) {
        if (bpLvl <= 0) return 0;

        double lukPerLvl = (double) (luk+agi+vit) / bpLvl;

        // 50 lukPerLvl = 0.25 resist, capped at 0.25
        double resist = Math.min((lukPerLvl / 50.0) * 0.25, 0.25);

        return resist;
    }

    public double calculateCritDmgNegate(int agi,int vit, int luk, int hp) {
        double result = 0;
        result += agi+vit+luk + hp;
        if (result>0) {
            return (result* 0.001);
        }
        else {
            return 0;
        }
    }

    public double calculateHitRate(int dex) {
        return  (double) dex /50;
    }

    public  double calculateFlee(int agi) {
        return  (double) agi /100;
    }

//    public double calculateBPLevelMBonusFromStatDmg(int str, int intel, int dex, int statDmg,int sharp,int power) {
//        double totalStats = str + intel + dex;
//        double base = (totalStats / 100.0)+sharp+power;
//        double multiplier = 1.0 + (statDmg / 100.0);
//        if (multiplier>1) return ((base * multiplier)-base)*.4;
//        return 0;
//
//    }
//
//    public double calculateBPLevelMBonusFromHP(int vit, int hp, int bpLvl) {
//        double base = ((double) vit / 100.0) + bpLvl;
//        double multiplier = 1.0 + (hp / 100.0);
//        if (multiplier>1) return ((base * multiplier)-base)*.1;
//        return 0;
//    }
//
//    public double calculateBPLevelMBonusFromEnchant(int sharp, int power) {
//        return  sharp+power;
//    }
}
