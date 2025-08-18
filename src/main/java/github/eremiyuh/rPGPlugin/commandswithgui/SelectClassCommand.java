package github.eremiyuh.rPGPlugin.commandswithgui;

import github.eremiyuh.rPGPlugin.RPGPlugin;
import github.eremiyuh.rPGPlugin.buffs.PlayerStatBuff;
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
    private final PlayerStatBuff playerStatBuff;
    private final int CLASS_CHANGE_COST = 100; // Cost in diamonds for changing class

    public SelectClassCommand(RPGPlugin plugin, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
        this.playerStatBuff = new PlayerStatBuff(profileManager);
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
        ItemStack alchemistIcon = createClassIcon(Material.POTION, "Alchemist", "A wise potion maker, that applies buff/debuff.");

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
            List<String> lore = new ArrayList<>();
            lore.add(loreText);
            meta.setLore(lore);
            meta.setUnbreakable(true);
            icon.setItemMeta(meta);
        }
        return icon;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Select Your Class")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName() && (event.getSlot() == 4 || event.getSlot() == 2 || event.getSlot() == 6)) {
                String displayName = clickedItem.getItemMeta().getDisplayName();
                UserProfile profile = profileManager.getProfile(player.getName());

                if (profile == null) {
                    player.sendMessage("Profile not found.");
                    return;
                }



                // Check if the player is selecting the same class
                if (profile.getChosenClass().equalsIgnoreCase(displayName)) {
                    player.sendMessage("You are already a " + displayName + ".");
                    return;
                }


                // Update profile with the new class
                profile.setChosenClass(displayName);
                profileManager.saveProfile(player.getName());

                // Apply buffs based on the new class selection
                playerStatBuff.onClassSwitchOrAttributeChange(player);

                player.sendMessage("You have selected the " + displayName + " class!");



//                // Check if the player has enough diamonds to change class
//                if (profile.getDiamond() >= 100 || profile.getChosenClass().equalsIgnoreCase("default")) {
//                    // Deduct diamonds from player
//                    if (!profile.getChosenClass().equalsIgnoreCase("default")) profile.setDiamond(profile.getDiamond()-10);
//
//                    // Update profile with the new class
//                    profile.setChosenClass(displayName);
//                    profileManager.saveProfile(player.getName());
//
//                    // Apply buffs based on the new class selection
//                    playerStatBuff.onClassSwitchOrAttributeChange(player);
//
//                    player.sendMessage("You have selected the " + displayName + " class!");
//                } else {
//                    player.sendMessage("You have " + (int) profile.getDiamond() + "diamond/s");
//                    player.sendMessage("You need at least " + CLASS_CHANGE_COST + " diamonds to change your class.");
//                }

                player.closeInventory();
            }
        }
    }
}
