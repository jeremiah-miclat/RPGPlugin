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

        // Create player's head item to represent their class
        ItemStack playerHead = createPlayerHead(player, profile);
        gui.setItem(4, playerHead);

        // Create a single currency icon to show all player's currencies
        ItemStack currencyIcon = new ItemStack(Material.DIAMOND);
        ItemMeta currencyMeta = currencyIcon.getItemMeta();

        if (currencyMeta != null) {
            currencyMeta.setDisplayName("§eCurrencies");

            // Set lore to show each currency balance with color coding
            List<String> currencyLore = new ArrayList<>();
            currencyLore.add("§bDiamonds: §7" + (int) profile.getCurrency("diamond"));
            currencyLore.add("§aEmeralds: §7" + (int) profile.getCurrency("emerald"));
            currencyLore.add("§fIron: §7" + (int) profile.getCurrency("iron"));
            currencyLore.add("§cCopper: §7" + (int) profile.getCurrency("copper"));
            currencyLore.add("§9Lapis: §7" + (int) profile.getCurrency("lapis"));
            currencyLore.add("§6Gold: §7" + (int) profile.getCurrency("gold"));
            currencyLore.add("§dNetherite: §7" +  (int)profile.getCurrency("netherite"));

            currencyMeta.setLore(currencyLore);

            currencyIcon.setItemMeta(currencyMeta);
        }

        // Place the single currency item in the GUI
        gui.setItem(6, currencyIcon);

        // Add attribute point items to the GUI
        createAttributePointItems(gui, player, profile);

        // Create a single combat icon to show all player's currencies
        ItemStack combatIcon = new ItemStack(Material.BOOK);
        ItemMeta combatMeta = combatIcon.getItemMeta();

        if (combatMeta != null) {
            combatMeta.setDisplayName("§eSTATUS: ");

            // Set lore to show each currency balance with color coding
            List<String> combatLore = new ArrayList<>();
            combatLore.add("§eStamina: §7" + profile.getStamina());
            combatLore.add("§eDurability: §7" + profile.getDurability());
            combatLore.add("§eEnder Pearls: §7" + profile.getEnderPearl());
            combatLore.add("§eAbyss Points: " + (int) (profile.getAbysspoints()));
            combatLore.add("§ePotion: §7" + profile.getPotion());
            combatLore.add("§eActivity Points: §7" + (int) profile.getCurrency("activitypoints"));



            combatMeta.setLore(combatLore);

            combatIcon.setItemMeta(combatMeta);
        }

        gui.setItem(2, combatIcon);

        // Open the GUI for the player
        player.openInventory(gui);
    }

    private void openOthersInfoGUI(Player player, UserProfile profile, Player viewedPlayer) {
        Inventory gui = Bukkit.createInventory(null, 27, "Player Class Info");

        // Create player's head item to represent their class
        ItemStack playerHead = createPlayerHead(viewedPlayer, profile);
        gui.setItem(4, playerHead);

        // Create a single currency icon to show all player's currencies
        ItemStack currencyIcon = new ItemStack(Material.DIAMOND);
        ItemMeta currencyMeta = currencyIcon.getItemMeta();

        if (currencyMeta != null) {
            currencyMeta.setDisplayName("§eCurrencies");

            // Set lore to show each currency balance with color coding
            List<String> currencyLore = new ArrayList<>();
            currencyLore.add("§bDiamonds: §7" + (int) profile.getCurrency("diamond"));
            currencyLore.add("§aEmeralds: §7" + (int) profile.getCurrency("emerald"));
            currencyLore.add("§fIron: §7" + (int) profile.getCurrency("iron"));
            currencyLore.add("§cCopper: §7" + (int) profile.getCurrency("copper"));
            currencyLore.add("§9Lapis: §7" + (int) profile.getCurrency("lapis"));
            currencyLore.add("§6Gold: §7" + (int) profile.getCurrency("gold"));
            currencyLore.add("§dNetherite: §7" +  (int)profile.getCurrency("netherite"));

            currencyMeta.setLore(currencyLore);

            currencyIcon.setItemMeta(currencyMeta);
        }

        // Place the single currency item in the GUI
        gui.setItem(6, currencyIcon);

        // Add attribute point items to the GUI
//        createAttributePointItems(gui, player, profile);

        // Create a single combat icon to show all player's currencies
        ItemStack combatIcon = new ItemStack(Material.BOOK);
        ItemMeta combatMeta = combatIcon.getItemMeta();

        if (combatMeta != null) {
            combatMeta.setDisplayName("§eSTATUS: ");

            // Set lore to show each currency balance with color coding
            List<String> combatLore = new ArrayList<>();
            combatLore.add("§eStamina: §7" + profile.getStamina());
            combatLore.add("§eDurability: §7" + profile.getDurability());
            combatLore.add("§eEnder Pearl: §7" + profile.getEnderPearl());
            combatLore.add("§eAbyss Point: " + (int) (profile.getAbysspoints()));
            combatLore.add("§ePotion: §7" + profile.getPotion());
            combatLore.add("§eActivity Points: §7" + (int) profile.getCurrency("activitypoints"));
            combatLore.add("§eClaimPoints: " + profile.getClaimPoints());
            if (!profile.getTeam().equalsIgnoreCase("none") && !profile.getTeam().equalsIgnoreCase(profile.getPlayerName())) {
                UserProfile teamOwnerProfile = profileManager.getProfile(profile.getTeam());
                combatLore.add("§eTeamMates: " + teamOwnerProfile.getTeamMembers());
            } else{
                combatLore.add("§eTeamMates: " + profile.getTeamMembers());
            }

            combatMeta.setLore(combatLore);

            combatIcon.setItemMeta(combatMeta);
        }

        gui.setItem(2, combatIcon);

        // Open the GUI for the player
        player.openInventory(gui);
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
        lore.add("§eAbyss Trait: " + profile.getAbyssTrait());
        lore.add("§7Attributes:");
        lore.add("§7Strength: " + profile.getTempStr());
        lore.add("§7Agility: " + profile.getTempAgi());
        lore.add("§7Dexterity: " + profile.getTempDex());
        lore.add("§7Intelligence: " + profile.getTempIntel());
        lore.add("§7Vitality: " + profile.getTempVit());
        lore.add("§7Luck: " + profile.getTempLuk());
        lore.add("§7MaxHP: " + (calculateMaxHealth(profile)));
        lore.add("§7HP%: " + profile.getHpMultiplier());
        lore.add("§7StatDmg%: " + profile.getStatDmgMultiplier());
        DecimalFormat df = new DecimalFormat("#.##");

        lore.add("§7Crit%: " + df.format(profile.getCrit() * 100) + "%");
        lore.add("§7CritDmg%: " + df.format(profile.getCritDmg() * 100) + "%");
        lore.add("§7Lifesteal%: " + df.format(profile.getLs() * 100) + "%");

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
        double healthPerVitality = 20.0;

        double vitality = (double) profile.getTempVit() / 100.0;
        double hpMultiplier = 1.0 + (profile.getHpMultiplier() * 0.01);

        return (baseHealth + (healthPerVitality * vitality)) * hpMultiplier;
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
                    lore.add("Increases damage for melee attacks.");
                }

                if (item.getType() == Material.FEATHER) {
                    lore.add("Increases movement speed.");
                    lore.add("To reach maximum movement speed, your total Agility (class stat + equipment stat) must be 8,000.");
                }

                if (item.getType() == Material.BOW) {
                    lore.add("Increases arrow damage.");
                    lore.add("Slightly increases critical chance and critical damage for Archers.");
                }

                if (item.getType() == Material.BOOK) {
                    lore.add("Increases damage from thrown potions.");
                    lore.add("Damage stats for Skill 1 users.");
                    lore.add("Boosts potion effects, healing, and damage for Alchemists.");
                }

                if (item.getType() == Material.GOLDEN_APPLE) {
                    lore.add("Increases HP.");
                    lore.add("Use /healthscale to rescale health to always display as 10 hearts.");
                }

                if (item.getType() == Material.NETHER_STAR) {
                    lore.add("Main stat for increasing critical chance and critical damage.");
                    lore.add("Main stat for decreasing critical chance of players during PVP.");
                    lore.add("Requires 4,000 to 5,000 points to reach 100% critical chance vs. Mobs.");
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

                if (allocatedPoints > 10000) {
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
}
