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

                                // Update the inventory (optional, to reflect new builder level)
                                openSkillsGui(player, profile);

                                // Provide feedback to the player
                                player.sendMessage(ChatColor.GREEN + "Your Builder skill has been increased to level " + newBuilderLevel + "!");
                            } else {
                                player.sendMessage(ChatColor.RED + "You need " + requiredObsi + " obsidian to make this purchase.");
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

                                // Update the inventory (optional, to reflect new fisherman level)
                                openSkillsGui(player, profile);

                                // Provide feedback to the player
                                player.sendMessage(ChatColor.GREEN + "Your Fisherman skill has been increased to level " + newFishermanLevel + "!");
                            } else {
                                player.sendMessage(ChatColor.RED + "You need " + requiredHeart + " heart of the sea to make this purchase.");
                            }
                        }
                    }
                }


            }
        }
    }

}
