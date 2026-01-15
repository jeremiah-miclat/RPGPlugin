package github.eremiyuh.rPGPlugin.commandswithgui;

import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DecimalFormat;
import java.util.*;

public class CheckClassCommand implements CommandExecutor, Listener {

    private final PlayerProfileManager profileManager;
    private final PlayerStatBuff playerStatBuff;

    public CheckClassCommand(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
        this.playerStatBuff = new PlayerStatBuff(profileManager);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }

        Player player = (Player) sender;
        String targetPlayerName = args.length == 0 ? player.getName() : args[0]; // Get the player name from args or use sender's own name
        if (!profileManager.profileExist(targetPlayerName)) {
            player.sendMessage("Player " + targetPlayerName + " does not have a profile.");
            return true;}
        UserProfile profile = profileManager.getProfile(targetPlayerName);

        if (profile == null) {
            player.sendMessage("Player " + targetPlayerName + " does not have a profile.");
            return true;
        }

        if (targetPlayerName.equals(sender.getName())) {
            openClassInfoGUI(player, profile);
            return true;
        }

        Player viewedPlayer = Bukkit.getPlayer(targetPlayerName);

        if (sender.getName().equalsIgnoreCase("Toguwrong")) {
            openOthersInfoGUI(player, profile, viewedPlayer);
            return true;
        }

        if (!targetPlayerName.equals(sender.getName()) && sender.getName().equalsIgnoreCase("eremiyuh")) {
            openOthersInfoGUI(player, profile, viewedPlayer);
        }

        if (!targetPlayerName.equals(sender.getName())) {
            if (!profile.getIsPublic()) {
                sender.sendMessage(  ChatColor.RED + targetPlayerName + "'s profile is hidden");
                return true;}
            openOthersInfoGUI(player, profile, viewedPlayer);
        }


//        if (viewedPlayer != null && !viewedPlayer.isOnline()) {
//            profileManager.removeProfileOnMemory(targetPlayerName);
//        }

        return true;
    }


    private void openClassInfoGUI(Player player, UserProfile profile) {
        Inventory gui = Bukkit.createInventory(null, 27, "Your Class Info");

        // Player head (center top)
        ItemStack playerHead = createPlayerHead(player, profile);
        gui.setItem(4, playerHead);

        // === CURRENCY ICON ===
        ItemStack currencyIcon = new ItemStack(Material.DIAMOND);
        ItemMeta currencyMeta = currencyIcon.getItemMeta();

        if (currencyMeta != null) {
            currencyMeta.setDisplayName("§eCurrencies");

            List<String> currencyLore = new ArrayList<>();
            currencyLore.add("§bDiamonds: §7" + formatNumber(profile.getCurrency("diamond")));
            currencyLore.add("§aEmeralds: §7" + formatNumber(profile.getCurrency("emerald")));
            currencyLore.add("§fIron: §7" + formatNumber(profile.getCurrency("iron")));
            currencyLore.add("§cCopper: §7" + formatNumber(profile.getCurrency("copper")));
            currencyLore.add("§9Lapis: §7" + formatNumber(profile.getCurrency("lapis")));
            currencyLore.add("§6Gold: §7" + formatNumber(profile.getCurrency("gold")));
            currencyLore.add("§dNetherite: §7" + formatNumber(profile.getCurrency("netherite")));

            currencyMeta.setLore(currencyLore);
            currencyIcon.setItemMeta(currencyMeta);
        }

        gui.setItem(6, currencyIcon);

        // === COMBAT / STATUS ICON ===
        ItemStack combatIcon = new ItemStack(Material.BOOK);
        ItemMeta combatMeta = combatIcon.getItemMeta();

        if (combatMeta != null) {
            combatMeta.setDisplayName("§eSTATUS:");

            List<String> combatLore = new ArrayList<>();
            combatLore.add("§eStamina: §7" + formatNumber(profile.getStamina()));
            combatLore.add("§eDurability: §7" + formatNumber(profile.getDurability()));
            combatLore.add("§eEnder Pearls: §7" + formatNumber(profile.getEnderPearl()));
            combatLore.add("§eAbyss Points: §7" + formatNumber(profile.getAbysspoints()));
            combatLore.add("§ePotion: §7" + formatNumber(profile.getPotion()));
            combatLore.add("§eActivity Points: §7" + formatNumber(profile.getCurrency("activitypoints")));

            combatMeta.setLore(combatLore);
            combatIcon.setItemMeta(combatMeta);
        }

        gui.setItem(2, combatIcon);

        // Attribute points (handled by external method)
        createAttributePointItems(gui, player, profile);

        // Show the GUI
        player.openInventory(gui);
    }


    private void openOthersInfoGUI(Player player, UserProfile profile, Player viewedPlayer) {
        Inventory gui = Bukkit.createInventory(null, 27, "Player Class Info");

        // Create player's head item to represent their class
        ItemStack playerHead = createPlayerHead(viewedPlayer, profile);
        gui.setItem(4, playerHead);

        // === CURRENCY ICON ===
        ItemStack currencyIcon = new ItemStack(Material.DIAMOND);
        ItemMeta currencyMeta = currencyIcon.getItemMeta();

        if (currencyMeta != null) {
            currencyMeta.setDisplayName("§eCurrencies");

            List<String> currencyLore = new ArrayList<>();
            currencyLore.add("§bDiamonds: §7" + formatNumber(profile.getCurrency("diamond")));
            currencyLore.add("§aEmeralds: §7" + formatNumber(profile.getCurrency("emerald")));
            currencyLore.add("§fIron: §7" + formatNumber(profile.getCurrency("iron")));
            currencyLore.add("§cCopper: §7" + formatNumber(profile.getCurrency("copper")));
            currencyLore.add("§9Lapis: §7" + formatNumber(profile.getCurrency("lapis")));
            currencyLore.add("§6Gold: §7" + formatNumber(profile.getCurrency("gold")));
            currencyLore.add("§dNetherite: §7" + formatNumber(profile.getCurrency("netherite")));

            currencyMeta.setLore(currencyLore);
            currencyIcon.setItemMeta(currencyMeta);
        }

        gui.setItem(6, currencyIcon);

        // === COMBAT/STATUS ICON ===
        ItemStack combatIcon = new ItemStack(Material.BOOK);
        ItemMeta combatMeta = combatIcon.getItemMeta();

        if (combatMeta != null) {
            combatMeta.setDisplayName("§eSTATUS:");

            List<String> combatLore = new ArrayList<>();
            combatLore.add("§eStamina: §7" + formatNumber(profile.getStamina()));
            combatLore.add("§eDurability: §7" + formatNumber(profile.getDurability()));
            combatLore.add("§eEnder Pearl: §7" + formatNumber(profile.getEnderPearl()));
            combatLore.add("§eAbyss Point: §7" + formatNumber(profile.getAbysspoints()));
            combatLore.add("§ePotion: §7" + formatNumber(profile.getPotion()));
            combatLore.add("§eActivity Points: §7" + formatNumber(profile.getCurrency("activitypoints")));
            combatLore.add("§eClaimPoints: §7" + formatNumber(profile.getClaimPoints()));

            if (!profile.getTeam().equalsIgnoreCase("none") && !profile.getTeam().equalsIgnoreCase(profile.getPlayerName())) {
                UserProfile teamOwnerProfile = profileManager.getProfile(profile.getTeam());
                combatLore.add("§eTeamMates: " + teamOwnerProfile.getTeamMembers());
            } else {
                combatLore.add("§eTeamMates: " + profile.getTeamMembers());
            }

            combatMeta.setLore(combatLore);
            combatIcon.setItemMeta(combatMeta);
        }

        gui.setItem(2, combatIcon);

        // Open the GUI
        player.openInventory(gui);
    }

    private String formatNumber(double number) {
        String suffix;
        double value;

        if (number >= 1_000_000_000_000.0) {
            value = number / 1_000_000_000_000.0;
            suffix = "T";
        } else if (number >= 1_000_000_000.0) {
            value = number / 1_000_000_000.0;
            suffix = "B";
        } else if (number >= 1_000_000.0) {
            value = number / 1_000_000.0;
            suffix = "M";
        } else {
            // No formatting needed under 1000
            return (number == Math.floor(number)) ? String.format("%.0f", number) : String.format("%.2f", number);
        }

        // Remove trailing .00 if it's a whole number
        if (value == Math.floor(value)) {
            return String.format("%.0f%s", value, suffix);
        } else {
            return String.format("%.2f%s", value, suffix);
        }
    }





    private ItemStack createPlayerHead(Player player, UserProfile profile) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();

        if (skullMeta != null) {
            skullMeta.setOwningPlayer(player);
            skullMeta.setDisplayName("§aPlayer Name: " + profile.getPlayerName());

            // Set lore to show class and attributes
            setPlayerHeadLore(skullMeta, profile);

            playerHead.setItemMeta(skullMeta);
        }

        return playerHead;
    }

    private void setPlayerHeadLore(SkullMeta skullMeta, UserProfile profile) {
        List<String> lore = new ArrayList<>();



        // Add class name and attribute values
        lore.add("§ePlayer Class: " + profile.getChosenClass() + " Lvl " + profile.getLevel());
        lore.add("§eBP level(from current gears & stats): " + profile.getArLvl());
        lore.add("§eAbyss Trait: " + profile.getAbyssTrait());
        lore.add("§7Attributes:");
        lore.add("§7Strength: " + profile.getTempStr());
        lore.add("§7Agility: " + profile.getTempAgi());
        lore.add("§7Dexterity: " + profile.getTempDex());
        lore.add("§7Intelligence: " + profile.getTempIntel());
        lore.add("§7Vitality: " + profile.getTempVit());
        lore.add("§7Luck: " + profile.getTempLuk());
        lore.add("§7MaxHP: " + ((int) calculateMaxHealth(profile)));
        lore.add("§7HP%: " + profile.getHpMultiplier());
        lore.add("§7StatDmg%: " + profile.getStatDmgMultiplier());
        DecimalFormat df = new DecimalFormat("#.##");
        lore.add("§7Melee Dmg from stats: " + (int)profile.getMeleeDmg() );
        lore.add("§7Longrange Dmg from stats: " + (int) profile.getLongDmg() );
        lore.add("§7Splashpotion Dmg from stats: " + (int) profile.getSplashDmg());
        lore.add("§7Crit chance: " + df.format(profile.getCrit() * 100) + "%");
        lore.add("§7Crit chance Res%: " + df.format(profile.getCritResist() * 100) + "%");
        lore.add("§7CritDmg Bonus: " + (int) profile.getCritDmg());
//        lore.add("§7CritDmg Res%: " + df.format(profile.getCritResist2() * 100) + "%");
        lore.add("§7Lifesteal%: " + df.format(profile.getLs() * 100) + "%");
        lore.add("§7AttackSpeed Bonus: " + String.format("%.1f%%", calculateBonusAttackSpeed(profile) * 100));
        lore.add("§7Ranged/Splash bonus from agi: +" + ((double) profile.getTempAgi() /100)*2);
        lore.add("§7Flee: " + profile.getFlee());
        lore.add("§7Hit: " + profile.getHit());
        lore.add("§7CD reduction: -" + (int) profile.getCdr());
        // Add remaining points, total allocated points for chosen class, and overall allocated points
        lore.add("§eRemaining Points: " + profile.getCurrentAttributePoints());
        lore.add("§eAllocated Points (" + profile.getChosenClass() +"): " + getTotalPointsForChosenClass(profile));
        lore.add("§eTotal Allocated Points: " + profile.getTotalAllocatedPoints());
        lore.add("§eElement: " + profile.getSelectedElement());
        lore.add("§eSkill: " + profile.getSelectedSkill());
//        lore.add("§eRace: " + profile.getSelectedRace());
        lore.add("§eTeam: " + profile.getTeam());
        lore.add("§ePVP: " + profile.isPvpEnabled());

        // Set the lore to the skull meta
        skullMeta.setLore(lore);
    }

    private int getTotalPointsForChosenClass(UserProfile profile) {
        switch (profile.getChosenClass().toLowerCase()) {
            case "archer":
                return profile.getTotalArcherAllocatedPoints();
            case "swordsman":
                return profile.getTotalSwordsmanAllocatedPoints();
            case "alchemist":
                return profile.getTotalAlchemistAllocatedPoints();
            default:
                return 0;
        }
    }

    private double calculateMaxHealth(UserProfile profile) {
        double baseHealth = 20.0;
        double healthPerVitality = 10.0;
        double healthPerStrength = 5.0; // +5 HP per 100 strength

        double vitality = profile.getTempVit() / 100.0;
        double strength = profile.getTempStr() / 100.0;
        double healthPerLevel = (Math.max(0,profile.getArLvl()-1))*8;
        double hpMultiplier = 1.0 + (profile.getHpMultiplier() * 0.01);

        return (baseHealth + healthPerLevel
                + (healthPerVitality * vitality)
                + (healthPerStrength * strength)) * hpMultiplier;
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

    private void createAttributePointItems(Inventory gui, Player player, UserProfile profile) {
        String[] attributes = {"Strength", "Agility", "Dexterity", "Intelligence", "Vitality", "Luck"};
        int[] slots = {10, 11, 12, 14, 15, 16};
        Material[] materials = {
                Material.IRON_SWORD,    // Strength
                Material.FEATHER,       // Agility
                Material.BOW,           // Dexterity
                Material.BOOK,          // Intelligence
                Material.GOLDEN_APPLE,  // Vitality
                Material.NETHER_STAR     // Luck
        };

        for (int i = 0; i < attributes.length; i++) {
            String attributeName = attributes[i];
            int value = getAttributeValue(profile, attributeName);

            // Use a different icon for each attribute
            ItemStack item = new ItemStack(materials[i]);
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                meta.setDisplayName("§a" + attributeName);

                // Initialize lore
                List<String> lore = new ArrayList<>();
                lore.add("§7Current Value: §f" + value);
                lore.add("§7Click to add points.");
                lore.add("§7Remaining Points: §f" + profile.getCurrentAttributePoints());

                // Add specific lore for items
                if (item.getType() == Material.IRON_SWORD) {
                    lore.add("Increases damage by .04 for melee attacks. ");
                    lore.add("Increases 5 hp per 100 points.");
                }

                if (item.getType() == Material.FEATHER) {
                    lore.add("Increases attack speed or projectiles damage.");
                    lore.add("Increases flee rate.");
                    lore.add("• Critical Resist: Up to 25% at 50 AGI per level.");
                }

                if (item.getType() == Material.BOW) {
                    lore.add("Increases arrow damage by .04.");
                    lore.add("Increases melee and splash damage slightly.");
                    lore.add("Increases hit rate.");
                    lore.add("dexPerLevel = DEX/BPLevel; scales from (dexPerBPLevel ÷ 20) × 15");
                    lore.add("up to 29s cap at dexPerBPLevel ≥ 40");
                }

                if (item.getType() == Material.BOOK) {
                    lore.add("Increases splash potion damage by .04 for pvp and .08 for pve.");
                    lore.add("Increases non-splash potion damage by .04 when using Skill 1");
                    lore.add("Boosts elemental effects durations and amplifiers.");
                    lore.add("Boosts potion healing for Alchemists.");
                }

                if (item.getType() == Material.GOLDEN_APPLE) {
                    lore.add("Increases HP by .1.");
                    lore.add("Use /healthscale to rescale health to always display as 10 hearts.");
                    lore.add("Provides slight damage reduction.");
                    lore.add("• Critical Resist: Up to 25% at 50 VIT per level.");
                }

                if (item.getType() == Material.NETHER_STAR) {
                    lore.add("Main stat for increasing critical chance and critical damage.");
                    lore.add("Main stat for reducing enemy critical chance in PVP.");
                    lore.add("• Critical Chance: Up to 50% at 50 LUK per bp level");
                    lore.add("• Critical Damage Bonus: 24 + (luk / 75) * 12");
                    lore.add("• Critical Resist: Up to 25% at 50 LUK per level.");
                }


                // Apply lore and other settings
                meta.setLore(lore);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES); // Optional: hide default tooltip
                item.setItemMeta(meta);

                // Add item to GUI
                gui.setItem(slots[i], item);
            }

        }
    }


    private int getAttributeValue(UserProfile profile, String attribute) {
        UserProfile.ClassAttributes classAttributes = switch (profile.getChosenClass().toLowerCase()) {
            case "archer" -> profile.getArcherClassInfo();
            case "swordsman" -> profile.getSwordsmanClassInfo();
            case "alchemist" -> profile.getAlchemistClassInfo();
            default -> profile.getDefaultClassInfo();
        };

        // Get the current class attributes based on the player's chosen class

        // Return the attribute value based on the specified attribute name
        return switch (attribute.toLowerCase()) {
            case "strength" -> classAttributes.getStr();
            case "agility" -> classAttributes.getAgi();
            case "dexterity" -> classAttributes.getDex();
            case "intelligence" -> classAttributes.getIntel();
            case "vitality" -> classAttributes.getVit();
            case "luck" -> classAttributes.getLuk();
            default -> 0; // Return 0 if an invalid attribute is specified
        };
    }


    @org.bukkit.event.EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Your Class Info")) {
            event.setCancelled(true);

            if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null) {
                Player player = (Player) event.getWhoClicked();
                UserProfile profile = profileManager.getProfile(player.getName());

                if (Objects.equals(profile.getChosenClass(), "default")) {
                    player.sendMessage("You cannot allocate points while in the default class.");
                    return;
                }

                double allocatedPoints = 0;

                if (profile.getChosenClass().equalsIgnoreCase("archer")) allocatedPoints = profile.getTotalArcherAllocatedPoints();
                if (profile.getChosenClass().equalsIgnoreCase("alchemist")) allocatedPoints = profile.getTotalAlchemistAllocatedPoints();
                if (profile.getChosenClass().equalsIgnoreCase("swordsman")) allocatedPoints = profile.getTotalSwordsmanAllocatedPoints();

                if (allocatedPoints >= 20000) {
                    player.sendMessage("Max attribute allocation per class reached.");
                    return;
                }

                if (profile != null && profile.getCurrentAttributePoints() > 0) {
                    String attributeName = event.getCurrentItem().getItemMeta().getDisplayName().replace("§a", "").toLowerCase();
                    switch (attributeName) {
                        case "strength":
                            profile.addAttributePoints("str", 1);
                            player.sendMessage("You added a point to Strength!");
                            break;
                        case "agility":
                            profile.addAttributePoints("agi", 1);
                            player.sendMessage("You added a point to Agility!");

                            break;
                        case "dexterity":
                            profile.addAttributePoints("dex", 1);
                            player.sendMessage("You added a point to Dexterity!");
                            break;
                        case "intelligence":
                            profile.addAttributePoints("intel", 1);
                            player.sendMessage("You added a point to Intelligence!");
                            break;
                        case "vitality":
                            profile.addAttributePoints("vit", 1);
                            player.sendMessage("You added a point to Vitality!");
                            break;
                        case "luck":
                            profile.addAttributePoints("luk", 1);
                            player.sendMessage("You added a point to Luck!");
                            break;
                        default:
                            break;
                    }

                    playerStatBuff.onClassSwitchOrAttributeChange(player);
//                    player.sendMessage(ChatColor.BLUE + "Your BP Level is: " + profile.getArLvl());
                    openClassInfoGUI(player, profile);  // Refresh the GUI
                } else {
                    player.sendMessage("You have no remaining points to allocate.");
                }
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onOthersInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Player Class Info")) {
            event.setCancelled(true);
        }
    }
    private double calculatePierce(double attackerDex, int defenderLvl) {
        double pierceChance;
        double dexPerLevel = attackerDex / defenderLvl;

        if (dexPerLevel < 25.0) {
            pierceChance = (dexPerLevel / 25.0) * 80.0;
        } else if (dexPerLevel < 50.0) {
            double bonus = ((dexPerLevel - 25.0) / 25.0) * 20.0;
            pierceChance = 80.0 + bonus;
        } else {
            pierceChance = 100.0;
        }
        return pierceChance;
    }


    private double calculateBonusAttackSpeed(UserProfile profile) {
        double agility = profile.getTempAgi();
        double lvl = profile.getArLvl();

        if (lvl <= 0) return 0;

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
}
