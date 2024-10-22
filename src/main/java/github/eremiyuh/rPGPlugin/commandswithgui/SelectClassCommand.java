package github.eremiyuh.rPGPlugin.commandswithgui;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.manager.PlayerProfileManager;
import github.eremiyuh.rPGPlugin.profile.UserProfile;
import org.bukkit.Bukkit;
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

import java.util.ArrayList;
import java.util.List;

public class SelectClassCommand implements CommandExecutor, Listener {

    private final RPGPlugin plugin;
    private final PlayerProfileManager profileManager;

    public SelectClassCommand(RPGPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            openClassSelectionGUI(player);
            return true;
        }
        return false;
    }

    private void openClassSelectionGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, "Select Your Class");

        // Create icons for each class
        ItemStack swordsmanIcon = createClassIcon(Material.DIAMOND_SWORD, "Swordsman", "A fierce warrior skilled in melee combat.");
        ItemStack archerIcon = createClassIcon(Material.BOW, "Archer", "A master of ranged combat and precision.");
        ItemStack alchemistIcon = createClassIcon(Material.POTION, "Alchemist", "A wise potion maker and supporter.");

        // Add icons to the inventory
        gui.setItem(2, swordsmanIcon);
        gui.setItem(4, archerIcon);
        gui.setItem(6, alchemistIcon);

        player.openInventory(gui);
    }

    private ItemStack createClassIcon(Material material, String className, String loreText) {
        ItemStack icon = new ItemStack(material);
        ItemMeta meta = icon.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(className);
            // Set lore
            List<String> lore = new ArrayList<>();
            lore.add(loreText);
            meta.setLore(lore);
            // Hide attributes
            meta.setUnbreakable(true); // Optional: To hide item attributes visually
            icon.setItemMeta(meta);
        }
        return icon;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Select Your Class")) {
            event.setCancelled(true); // Prevent interactions
            Player player = (Player) event.getWhoClicked();

            // Check which item was clicked
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String displayName = clickedItem.getItemMeta().getDisplayName();
                UserProfile profile = profileManager.getProfile(player.getName());

                // Check cooldown
                long currentTime = System.currentTimeMillis();
//                long cooldownPeriod = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
                long cooldownPeriod = 1000; // 24 hours in milliseconds
                if (currentTime - profile.getLastClassSelection() < cooldownPeriod) {
                    long timeLeft = cooldownPeriod - (currentTime - profile.getLastClassSelection());
                    player.sendMessage("You must wait " + (timeLeft / 1000 / 60) + " minutes before choosing a class again.");
                    return; // Exit if still in cooldown
                }

                if (profile != null) {
                    // Update class and save profile
                    switch (displayName) {
                        case "Swordsman":
                            profile.setChosenClass("Swordsman");
                            break;
                        case "Archer":
                            profile.setChosenClass("Archer");
                            break;
                        case "Alchemist":
                            profile.setChosenClass("Alchemist");
                            break;
                    }
                    profile.setLastClassSelection(currentTime); // Update last selection time
                    profileManager.saveProfile(player.getName());
                    player.sendMessage("You have selected the " + profile.getChosenClass() + " class!");
                }

                // Close the inventory after selection
                player.closeInventory();
            }
        }
    }
}
