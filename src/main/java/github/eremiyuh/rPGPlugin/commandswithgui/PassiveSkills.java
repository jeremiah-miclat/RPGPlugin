package github.eremiyuh.rPGPlugin.commandswithgui;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PassiveSkills implements CommandExecutor, Listener {
    private final RPGPlugin plugin;
    private final PlayerProfileManager profileManager;

    public PassiveSkills(RPGPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    // When a player enters /selectskill, the GUI will appear
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

        openSkillsGui(player, profile);
        return true;
    }

    // Open the skills GUI for the player
    public void openSkillsGui(Player player, UserProfile profile) {
        Inventory gui = Bukkit.createInventory(null, 9, "Passive Skills");


        //builder
        ItemStack builder = new ItemStack(Material.OBSIDIAN);
        ItemMeta builderMeta = builder.getItemMeta();
        List<String> builderMetalore = new ArrayList<>();
        builderMetalore.add(ChatColor.BLUE + "" + ChatColor.ITALIC + "Builder: " + profile.getBuilder());
        builderMetalore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Bonus activity points per level when placing blocks" );
        builderMetalore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Cost: 10000 activity points, 10 obsidians" );
        builderMeta.setLore(builderMetalore);
        builderMeta.setDisplayName("Builder");
        builder.setItemMeta(builderMeta);
        gui.setItem(0,builder);

        // Fisherman
        ItemStack fisherman = new ItemStack(Material.FISHING_ROD);  // Using a fishing rod as the icon for Fisherman skill
        ItemMeta fishermanMeta = fisherman.getItemMeta();
        List<String> fishermanMetalore = new ArrayList<>();
        fishermanMetalore.add(ChatColor.GREEN + "" + ChatColor.ITALIC + "Fisherman: " + profile.getFisherman());
        fishermanMetalore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Bonus activity point multiplier per level when selling fish");
        fishermanMetalore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Cost: 100000 activity points, 1 heart of the sea");
        fishermanMeta.setLore(fishermanMetalore);
        fishermanMeta.setDisplayName("Fisherman");
        fisherman.setItemMeta(fishermanMeta);
        gui.setItem(1, fisherman);

        // Destroyer or Demolisher
        ItemStack destroyer = new ItemStack(Material.NETHERITE_PICKAXE);  // Using a diamond sword as the icon for Destroyer skill
        ItemMeta destroyerMeta = destroyer.getItemMeta();
        List<String> destroyerLore = new ArrayList<>();
        destroyerLore.add(ChatColor.RED + "" + ChatColor.ITALIC + "Demolisher: " + profile.getDestroyer());
        destroyerLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Bonus activity point per level when breaking blocks");
        destroyerLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Cost: 100000 activity points, 10 TNT");
        destroyerMeta.setLore(destroyerLore);
        destroyerMeta.setDisplayName("Demolisher");
        destroyer.setItemMeta(destroyerMeta);
        gui.setItem(2, destroyer);

        // Hunter
        ItemStack hunter = new ItemStack(Material.BOW);  // Using a bow as the icon for Hunter skill
        ItemMeta hunterMeta = hunter.getItemMeta();
        List<String> hunterLore = new ArrayList<>();
        hunterLore.add(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Hunter: " + profile.getHunter());
        hunterLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Bonus activity point per level when defeating living entities");
        hunterLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Cost: 100000 activity points, 1 wither skeleton skull");
        hunterMeta.setLore(hunterLore);
        hunterMeta.setDisplayName("Hunter");
        hunter.setItemMeta(hunterMeta);
        gui.setItem(3, hunter);

        // Crafter
        ItemStack crafter = new ItemStack(Material.CRAFTING_TABLE);  // Using a crafting table as the icon for Crafter skill
        ItemMeta crafterMeta = crafter.getItemMeta();
        List<String> crafterLore = new ArrayList<>();
        crafterLore.add(ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + "Crafter: " + profile.getCrafter());
        crafterLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Bonus activity point per level when crafting and enchanting");
        crafterLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Cost: 100000 activity points, 10 crafting table");
        crafterMeta.setLore(crafterLore);
        crafterMeta.setDisplayName("Crafter");
        crafter.setItemMeta(crafterMeta);
        gui.setItem(4, crafter);

        // Trader
        ItemStack trader = new ItemStack(Material.EMERALD);  // Using a villager spawn egg as the icon for Trader skill
        ItemMeta traderMeta = trader.getItemMeta();
        List<String> traderLore = new ArrayList<>();
        traderLore.add(ChatColor.GOLD + "" + ChatColor.ITALIC + "Trader: " + profile.getTrader());
        traderLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Bonus activity point per level when trading with villagers");
        traderLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Cost: 100000 activity points, 10 books");
        traderMeta.setLore(traderLore);
        traderMeta.setDisplayName("Trader");
        trader.setItemMeta(traderMeta);
        gui.setItem(5, trader);

        // Open the GUI for the player
        player.openInventory(gui);
    }





    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if the inventory is the skills GUI by checking its title
        if (event.getView().getTitle().equals("Passive Skills")) {
            event.setCancelled(true);  // Prevent interactions such as clicking or dragging

            // Ensure the player who clicked is a Player instance
            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();
                UserProfile profile = profileManager.getProfile(player.getName());

                // Get the clicked item
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.hasItemMeta() && Objects.requireNonNull(clickedItem.getItemMeta()).hasLore()) {
                    ItemMeta itemMeta = clickedItem.getItemMeta();

                    // Check if the clicked item is the builder icon (by lore or name)
                    List<String> lore = itemMeta.getLore();
                    if (lore != null && !lore.isEmpty()) {
                        String firstLine = lore.get(0); // Get the first line of the lore to check if it's the builder skill

                        // If the clicked item is the Builder skill
                        if (firstLine.contains(ChatColor.BLUE + "" + ChatColor.ITALIC + "Builder")) {
                            int requiredObsi = 10;
                            if (profile.getActivitypoints()<10000) {
                                player.sendMessage("Need 10000 active points");
                                return;
                            }

                            if (player.getInventory().containsAtLeast(new ItemStack(Material.OBSIDIAN),requiredObsi)) {
                                // Increment the builder skill in the profile
                                player.getInventory().removeItem(new ItemStack(Material.OBSIDIAN, requiredObsi));
                                int newBuilderLevel = profile.getBuilder() + 1; // Increment builder skill by 1
                                profile.setBuilder(newBuilderLevel); // Update the profile
                                profile.setCurrency("activitypoints", profile.getActivitypoints()-10000);
                                // Update the inventory (optional, to reflect new builder level)
                                openSkillsGui(player, profile);

                                // Provide feedback to the player
                                player.sendMessage(ChatColor.GREEN + "Your Builder skill has been increased to level " + newBuilderLevel + "!");
                            } else {
                                player.sendMessage(ChatColor.RED + "You need " + requiredObsi + " obsidian to upgrade.");
                            }

                        }

                        // If the clicked item is the Fisherman skill
                        if (firstLine.contains(ChatColor.GREEN + "" + ChatColor.ITALIC + "Fisherman")) {
                            int requiredHeart = 1;
                            if (profile.getActivitypoints() < 100000) {
                                player.sendMessage("Need 100000 activity points");
                                return;
                            }

                            if (player.getInventory().containsAtLeast(new ItemStack(Material.HEART_OF_THE_SEA), requiredHeart)) {
                                // Increment the Fisherman skill in the profile
                                player.getInventory().removeItem(new ItemStack(Material.HEART_OF_THE_SEA, requiredHeart));
                                int newFishermanLevel = profile.getFisherman() + 1; // Increment fisherman skill by 1
                                profile.setFisherman(newFishermanLevel); // Update the profile
                                profile.setCurrency("activitypoints", profile.getActivitypoints()-100000);
                                // Update the inventory (optional, to reflect new fisherman level)
                                openSkillsGui(player, profile);

                                // Provide feedback to the player
                                player.sendMessage(ChatColor.GREEN + "Your Fisherman skill has been increased to level " + newFishermanLevel + "!");
                            } else {
                                player.sendMessage(ChatColor.RED + "You need " + requiredHeart + " heart of the sea to upgrade.");
                            }
                        }

                        // If the clicked item is the Demolisher skill
                        if (firstLine.contains(ChatColor.RED + "" + ChatColor.ITALIC + "Demolisher")) {
                            int requiredTNT = 10;
                            if (profile.getActivitypoints() < 100000) {
                                player.sendMessage("Need 100000 activity points");
                                return;
                            }

                            if (player.getInventory().containsAtLeast(new ItemStack(Material.TNT), requiredTNT)) {
                                // Increment the Destroyer skill in the profile
                                player.getInventory().removeItem(new ItemStack(Material.TNT, requiredTNT));
                                int newDestroyerLevel = profile.getDestroyer() + 1; // Increment destroyer skill by 1
                                profile.setDestroyer(newDestroyerLevel); // Update the profile
                                profile.setCurrency("activitypoints", profile.getActivitypoints()-100000);
                                // Update the inventory (optional, to reflect new destroyer level)
                                openSkillsGui(player, profile);

                                // Provide feedback to the player
                                player.sendMessage(ChatColor.RED + "Your Destroyer skill has been increased to level " + newDestroyerLevel + "!");
                            } else {
                                player.sendMessage(ChatColor.RED + "You need " + requiredTNT + " TNT to upgrade.");
                            }
                        }

                        // If the clicked item is the Hunter skill
                        if (firstLine.contains(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Hunter")) {
                            int requiredWSH = 1;
                            if (profile.getActivitypoints() < 100000) {
                                player.sendMessage("Need 100000 activity points");
                                return;
                            }

                            if (player.getInventory().containsAtLeast(new ItemStack(Material.WITHER_SKELETON_SKULL), requiredWSH)) {
                                // Increment the Hunter skill in the profile
                                player.getInventory().removeItem(new ItemStack(Material.WITHER_SKELETON_SKULL, requiredWSH));
                                int newHunterLevel = profile.getHunter() + 1; // Increment hunter skill by 1
                                profile.setHunter(newHunterLevel); // Update the profile
                                profile.setCurrency("activitypoints", profile.getActivitypoints()-100000);
                                // Update the inventory (optional, to reflect new hunter level)
                                openSkillsGui(player, profile);

                                // Provide feedback to the player
                                player.sendMessage(ChatColor.YELLOW + "Your Hunter skill has been increased to level " + newHunterLevel + "!");
                            } else {
                                player.sendMessage(ChatColor.RED + "You need " + requiredWSH + "wither skeleton skull to upgrade.");
                            }
                        }

                        // If the clicked item is the Crafter skill
                        if (firstLine.contains(ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + "Crafter")) {
                            int requiredCraftTable = 10;  // Number of enchanted books required for the upgrade
                            if (profile.getActivitypoints() < 100000) {
                                player.sendMessage("Need 100000 activity points");
                                return;
                            }

                            if (player.getInventory().containsAtLeast(new ItemStack(Material.CRAFTING_TABLE), requiredCraftTable)) {
                                // Increment the Crafter skill in the profile
                                player.getInventory().removeItem(new ItemStack(Material.CRAFTING_TABLE, requiredCraftTable));
                                int newCrafterLevel = profile.getCrafter() + 1; // Increment Crafter skill by 1
                                profile.setCrafter(newCrafterLevel); // Update the profile
                                profile.setCurrency("activitypoints", profile.getActivitypoints()-100000);
                                // Update the inventory (optional, to reflect new crafter level)
                                openSkillsGui(player, profile);

                                // Provide feedback to the player
                                player.sendMessage(ChatColor.BLUE + "Your Crafter skill has been increased to level " + newCrafterLevel + "!");
                            } else {
                                player.sendMessage(ChatColor.RED + "You need " + requiredCraftTable + " crafting table to upgrade.");
                            }
                        }

                        // If the clicked item is the Trader skill
                        if (firstLine.contains(ChatColor.GOLD + "" + ChatColor.ITALIC + "Trader")) {
                            int requiredBooks = 10;
                            if (profile.getActivitypoints() < 100000) {
                                player.sendMessage("Need 100000 activity points");
                                return;
                            }

                            if (player.getInventory().containsAtLeast(new ItemStack(Material.BOOK), requiredBooks)) {
                                // Increment the Trader skill in the profile
                                player.getInventory().removeItem(new ItemStack(Material.BOOK, requiredBooks));
                                int newTraderLevel = profile.getTrader() + 1; // Increment Trader skill by 1
                                profile.setTrader(newTraderLevel); // Update the profile
                                profile.setCurrency("activitypoints", profile.getActivitypoints()-100000);
                                // Update the inventory (optional, to reflect new trader level)
                                openSkillsGui(player, profile);

                                // Provide feedback to the player
                                player.sendMessage(ChatColor.BLUE + "Your Trader skill has been increased to level " + newTraderLevel + "!");
                            } else {
                                player.sendMessage(ChatColor.RED + "You need " + requiredBooks + " books to upgrade.");
                            }
                        }




                    }
                }


            }
        }
    }

}
