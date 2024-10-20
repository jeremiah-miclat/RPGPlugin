package github.eremiyuh.rPGPlugin.commands;

import github.eremiyuh.rPGPlugin.manager.PlayerClassManager;
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
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.Map;

public class CheckClassCommand implements CommandExecutor, Listener {
    private final PlayerClassManager playerClassManager;

    // Constructor that accepts PlayerClassManager
    public CheckClassCommand(PlayerClassManager playerClassManager) {
        this.playerClassManager = playerClassManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can check their class!");
            return true;
        }

        Player player = (Player) sender;

        // Check if the player has selected a class
        String playerClass = playerClassManager.loadPlayerClass(player);
        if (playerClass != null) {
            openClassInfoGUI(player, playerClass); // Open the GUI with class info
        } else {
            player.sendMessage("You haven't selected a class yet.");
        }

        return true;
    }

    private void openClassInfoGUI(Player player, String playerClass) {
        // Create a new inventory (GUI) with 27 slots, titled "Your Class Info"
        Inventory gui = Bukkit.createInventory(null, 27, "Your Class Info");

        // Create the player's head item to represent the player's class
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
        skullMeta.setOwningPlayer(player); // Set the player's head
        skullMeta.setDisplayName("§aYour Class: " + playerClass); // Green text for class

        // Fetch the player's attributes from the PlayerClassManager
        Map<String, Integer> attributes = playerClassManager.getPlayerAttributes(player);
        int strength = attributes.get("strength");
        int agility = attributes.get("agility");
        int dexterity = attributes.get("dexterity");
        int intelligence = attributes.get("intelligence");
        int vitality = attributes.get("vitality");
        int attributePoints = playerClassManager.getAllocatedAttributePoints(player); // Get remaining attribute points
        int remainingPoints = playerClassManager.getRemainingAttributePoints(player);
        // Add class and attribute details as lore (hover text) for the head item
        skullMeta.setLore(Arrays.asList(
                "§7Class: §f" + playerClass,
                "§7Strength: §f" + strength,
                "§7Agility: §f" + agility,
                "§7Dexterity: §f" + dexterity,
                "§7Intelligence: §f" + intelligence,
                "§7Vitality: §f" + vitality,
                "§7Allocated Attribute Points: §f" + attributePoints, // Display attribute points
                "§RRemaining Attribute Points: §f" + remainingPoints // Display attribute points
        ));
        playerHead.setItemMeta(skullMeta);

        // Set the player's head in the middle slot of the GUI (slot 13)
        gui.setItem(13, playerHead);

        // Create icons for spending points on attributes
        createAttributePointItems(gui, player);

        // Open the GUI for the player
        player.openInventory(gui);
    }

    private void createAttributePointItems(Inventory gui, Player player) {
        String[] attributes = {"Strength", "Agility", "Dexterity", "Intelligence", "Vitality"};
        int[] slots = {10, 11, 12, 14, 15}; // Slots for the attribute icons

        for (int i = 0; i < attributes.length; i++) {
            ItemStack item = new ItemStack(Material.EMERALD); // Use emerald as the item
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§a" + attributes[i]); // Green text for attribute names
            meta.setLore(Arrays.asList(
                    "§7Click to spend a point on " + attributes[i] + ".",
                    "§7Currently: §f" + playerClassManager.getPlayerAttributes(player).get(attributes[i].toLowerCase()), // Current value
                    "§7Remaining Points: §f" + playerClassManager.getRemainingAttributePoints(player) // Show remaining points
            ));
            item.setItemMeta(meta);
            gui.setItem(slots[i], item);
        }
    }

    // Event handler to cancel clicks in the GUI
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Your Class Info")) { // Check if the clicked inventory is the class info GUI
            event.setCancelled(true); // Prevent item movement

            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.EMERALD) {
                Player player = (Player) event.getWhoClicked();
                String attribute = event.getCurrentItem().getItemMeta().getDisplayName().replace("§a", ""); // Get attribute name

                playerClassManager.allocateAttributePoint(player, attribute.toLowerCase());
                openClassInfoGUI(player, playerClassManager.loadPlayerClass(player)); // Refresh the GUI to show updated points
            }
        }
    }
}
