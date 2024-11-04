package github.eremiyuh.rPGPlugin.commandswithgui;

import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

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
        UserProfile profile = profileManager.getProfile(player.getName());

        if (profile == null) {
            player.sendMessage("You do not have a profile.");
            return true;
        }

        openClassInfoGUI(player, profile);
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
            currencyLore.add("§bDiamonds: §7" + profile.getCurrency("diamond"));  // Light Blue for Diamond
            currencyLore.add("§aEmeralds: §7" + profile.getCurrency("emerald"));   // Green for Emerald
            currencyLore.add("§fIron: §7" + profile.getCurrency("iron"));          // White for Iron
            currencyLore.add("§9Lapis: §7" + profile.getCurrency("lapis"));        // Dark Blue for Lapis
            currencyLore.add("§6Gold: §7" + profile.getCurrency("gold"));          // Gold for Gold
            currencyMeta.setLore(currencyLore);

            currencyIcon.setItemMeta(currencyMeta);
        }

        // Place the single currency item in the GUI
        gui.setItem(6, currencyIcon);

        // Add attribute point items to the GUI
        createAttributePointItems(gui, player, profile);

        // Open the GUI for the player
        player.openInventory(gui);
    }


    private ItemStack createPlayerHead(Player player, UserProfile profile) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();

        if (skullMeta != null) {
            skullMeta.setOwningPlayer(player);
            skullMeta.setDisplayName("§aYour Class: " + profile.getChosenClass());

            // Set lore to show class and attributes
            setPlayerHeadLore(skullMeta, profile);

            playerHead.setItemMeta(skullMeta);
        }

        return playerHead;
    }

    private void setPlayerHeadLore(SkullMeta skullMeta, UserProfile profile) {
        List<String> lore = new ArrayList<>();

        // Add class name and attribute values
        lore.add("§7Attributes:");
        lore.add("§7Strength: " + getAttributeValue(profile, "strength"));
        lore.add("§7Agility: " + getAttributeValue(profile, "agility"));
        lore.add("§7Dexterity: " + getAttributeValue(profile, "dexterity"));
        lore.add("§7Intelligence: " + getAttributeValue(profile, "intelligence"));
        lore.add("§7Vitality: " + getAttributeValue(profile, "vitality"));
        lore.add("§7Luck: " + getAttributeValue(profile, "luck"));

        // Add remaining points, total allocated points for chosen class, and overall allocated points
        lore.add("§eRemaining Points: " + profile.getCurrentAttributePoints());
        lore.add("§eAllocated Points (" + profile.getChosenClass() +"): " + getTotalPointsForChosenClass(profile));
        lore.add("§eTotal Allocated Points: " + profile.getTotalAllocatedPoints());
        lore.add("§eElement: " + profile.getSelectedElement());
        lore.add("§eSkill: " + profile.getSelectedSkill());
        lore.add("§eRace: " + profile.getSelectedRace());
        lore.add("§eTeam: " + profile.getTeam());
        lore.add("§eTeamMates: " + profile.getTeamMembers());
        lore.add("§ePVP: " + profile.isPvpEnabled());
        lore.add("§eClaimPoints: " + profile.getClaimPoints());
        lore.add("§ePotion: " + profile.getPotion());
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
                meta.setLore(Arrays.asList(
                        "§7Current value: §f" + value,
                        "§7Click to add points.",
                        "§7Remaining Points: §f" + profile.getCurrentAttributePoints()
                ));
                item.setItemMeta(meta);
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
                            player.sendMessage("Your ms now is: " + player.getWalkSpeed());
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
                            player.sendMessage("Invalid attribute selected.");
                            break;
                    }

                    playerStatBuff.onClassSwitchOrAttributeChange(player);
                    profileManager.saveProfile(player.getName());
                    openClassInfoGUI(player, profile);  // Refresh the GUI
                } else {
                    player.sendMessage("You have no remaining points to allocate.");
                }
            }
        }
    }
}
