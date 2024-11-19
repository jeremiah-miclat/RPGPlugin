package github.eremiyuh.rPGPlugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class SwordCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public SwordCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is an admin or player
        if (!(sender instanceof Player admin)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        // Check if the sender is an admin
        if (!admin.isOp()) { // Assuming you have a permission node for admins
            admin.sendMessage("You do not have permission to use this command.");
            return true;
        }

        // Check if sender's name matches allowed admin (optional restriction)
        if (!admin.getName().equals("Eremiyuh")) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        // Ensure correct number of arguments
        if (args.length != 4) {
            admin.sendMessage("Usage: /give <playername> <item_name> <lore_name> <value>");
            return true;
        }

        // Parse the arguments
        String targetName = args[0]; // Player who will receive the item
        String itemName = args[1];   // Item type (e.g., diamond_sword)
        String loreName = args[2];   // Lore name (e.g., Agility)
        String valueString = args[3]; // Lore value (e.g., 1000)

        // Try to parse the value to an integer
        int value;
        try {
            value = Integer.parseInt(valueString);
        } catch (NumberFormatException e) {
            admin.sendMessage("Invalid value. Please enter a valid integer for the lore.");
            return true;
        }

        // Find the target player
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            admin.sendMessage("Player " + targetName + " not found or is offline.");
            return true;
        }

        // Determine the item type from the item name
        Material itemMaterial;
        try {
            itemMaterial = Material.valueOf(itemName.toUpperCase()); // Convert the item name to Material
        } catch (IllegalArgumentException e) {
            admin.sendMessage("Invalid item name. Please use a valid item name (e.g., diamond_sword).");
            return true;
        }

        // Create the item
        ItemStack item = new ItemStack(itemMaterial);
        ItemMeta meta = item.getItemMeta();

        // Set the display name and lore
        if (meta != null) {
            meta.setDisplayName(itemName.replace('_', ' ')); // Optional: Use the item name as display name
            String lore = loreName + ": " + value;  // e.g., "Agility: 1000"
            meta.setLore(Arrays.asList(lore)); // Set lore

            // Add a custom tag to identify this item
            NamespacedKey key = new NamespacedKey(plugin, "special_sword");
            meta.getPersistentDataContainer().set(key, org.bukkit.persistence.PersistentDataType.STRING, "unique_value");

            // Apply the updated meta to the item
            item.setItemMeta(meta);
        }

        // Give the item to the target player
        targetPlayer.getInventory().addItem(item);
        targetPlayer.sendMessage("You have been given a " + itemName + " with " + loreName + " " + value + ".");
        admin.sendMessage("You have given " + targetName + " a " + itemName + " with " + loreName + " " + value + ".");

        return true;
    }
}
